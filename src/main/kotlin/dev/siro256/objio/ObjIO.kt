package dev.siro256.objio

import dev.siro256.fastset.FastIndexSet
import dev.siro256.modelio.Model
import dev.siro256.modelio.ModelIO

object ObjIO : ModelIO {
    private val VERTEX = Regex("^v ([\\d.e-]+) ([\\d.e-]+) ([\\d.e-]+)$")
    private val NORMAL = Regex("^vn ([\\d.e-]+) ([\\d.e-]+) ([\\d.e-]+)$")
    private val UV = Regex("^vt ([\\d.e-]+) ([\\d.e-]+)$")
    private val OBJECT = Regex("^o (.+)\$")
    private val MATERIAL = Regex("^usemtl (.+)$")
    private val FACE =
        Regex("^f (?<v1>\\d+)/(?<u1>\\d*)/(?<n1>\\d+) (?<v2>\\d+)/(?<u2>\\d*)/(?<n2>\\d+) (?<v3>\\d+)/(?<u3>\\d*)/(?<n3>\\d+)\$")

    override val extension: Array<String> = arrayOf("obj")

    override fun parse(byteArray: ByteArray): Result<Model> {
        val text = byteArray.decodeToString()

        val vertices = mutableListOf<Model.Vector3f>()
        val normals = mutableListOf<Model.Vector3f>()
        val uvs = mutableListOf<Model.Vector2f>()
        val objects = mutableMapOf<String, MutableList<Model.Face>>()

        var lastMatchedObject: String? = null
        var lastMatchedMaterial: String? = null

        text.lines().forEach {
            val vertexMatch = VERTEX.matchEntire(it)
            val normalMatch = NORMAL.matchEntire(it)
            val uvMatch = UV.matchEntire(it)
            val objectMatch = OBJECT.matchEntire(it)
            val materialMatch = MATERIAL.matchEntire(it)
            val faceMatch = FACE.matchEntire(it)

            if (vertexMatch?.groupValues?.size == 4) {
                val values = vertexMatch.groupValues
                vertices.add(Model.Vector3f(values[1].toFloat(), values[2].toFloat(), values[3].toFloat()))
            } else if (normalMatch?.groupValues?.size == 4) {
                val values = normalMatch.groupValues
                normals.add(Model.Vector3f(values[1].toFloat(), values[2].toFloat(), values[3].toFloat()))
            } else if (uvMatch?.groupValues?.size == 3) {
                val values = uvMatch.groupValues
                uvs.add(Model.Vector2f(values[1].toFloat(), values[2].toFloat()))
            } else if (objectMatch?.groupValues?.size == 2) {
                val name = objectMatch.groupValues[1]
                objects[name] = mutableListOf()
                lastMatchedObject = name
                lastMatchedMaterial = null
            } else if (materialMatch?.groupValues?.size == 2) {
                lastMatchedMaterial = materialMatch.groupValues[1]
            } else if (faceMatch != null && faceMatch.groupValues.size >= 7) {
                val v1 = faceMatch.groups["v1"]?.value?.run { toInt() - 1 } ?: return@forEach
                val u1 = faceMatch.groups["u1"]?.value?.toIntOrNull()?.run { this - 1 }
                val n1 = faceMatch.groups["n1"]?.value?.run { toInt() - 1 } ?: return@forEach
                val v2 = faceMatch.groups["v2"]?.value?.run { toInt() - 1 } ?: return@forEach
                val u2 = faceMatch.groups["u2"]?.value?.toIntOrNull()?.run { this - 1 }
                val n2 = faceMatch.groups["n2"]?.value?.run { toInt() - 1 } ?: return@forEach
                val v3 = faceMatch.groups["v3"]?.value?.run { toInt() - 1 } ?: return@forEach
                val u3 = faceMatch.groups["u3"]?.value?.toIntOrNull()?.run { this - 1 }
                val n3 = faceMatch.groups["n3"]?.value?.run { toInt() - 1 } ?: return@forEach

                objects[lastMatchedObject]?.add(
                    Model.Face(
                        lastMatchedMaterial,
                        Model.Vertex(vertices[v1], normals[n1], u1?.let(uvs::get)),
                        Model.Vertex(vertices[v2], normals[n2], u2?.let(uvs::get)),
                        Model.Vertex(vertices[v3], normals[n3], u3?.let(uvs::get))
                    )
                )
            }
        }

        val model = Model(objects.map { Model.Object(it.key, it.value) })

        return Result.success(model)
    }

    override fun export(model: Model): Result<ByteArray> {
        val simplifiedModel = simplifyModel(model)
        val lines = mutableListOf<String>()

        lines.add("""
            # Exported by objIO. https://github.com/Kotatsu-RTM/objIO
            #
            # ${simplifiedModel.coordinates.size} vertices
            # ${simplifiedModel.objects.flatMap { it.faces }.size } triangles
            # ${simplifiedModel.objects.size} objects
            # ${simplifiedModel.materials.size} materials
            
        """.trimIndent())

        simplifiedModel.coordinates.forEach {
            lines.add("v ${it.x} ${it.y} ${it.z}")
        }

        simplifiedModel.normals.forEach {
            lines.add("vn ${it.x} ${it.y} ${it.z}")
        }

        simplifiedModel.uvs.forEach {
            lines.add("vt ${it.x} ${it.y}")
        }

        simplifiedModel.objects.forEach { (name, faces) ->
            fun writeFace(face: SimpleModel.Face) {
                val v1 = face.first.coordinate
                val v2 = face.second.coordinate
                val v3 = face.third.coordinate
                val n1 = face.first.normal
                val n2 = face.second.normal
                val n3 = face.third.normal
                val u1 = face.first.uv ?: ""
                val u2 = face.second.uv ?: ""
                val u3 = face.third.uv ?: ""

                lines.add("f $v1/$u1/$n1 $v2/$u2/$n2 $v3/$u3/$n3")
            }

            lines.add("o $name")

            faces.filter { it.material == null }.forEach(::writeFace)

            var lastMaterial = ""

            faces.filter { it.material != null }.sortedBy { it.material }.forEach {
                if (lastMaterial != it.material) {
                    lastMaterial = it.material!!
                    lines.add("usemtl ${it.material}")
                }
                writeFace(it)
            }
        }

        return Result.success(lines.joinToString(separator = "\n").encodeToByteArray())
    }

    private fun simplifyModel(model: Model): SimpleModel {
        val coordinates = FastIndexSet<SimpleModel.Vector3f>()
        val normals = FastIndexSet<SimpleModel.Vector3f>()
        val uvs = FastIndexSet<SimpleModel.Vector2f>()
        val materials = mutableSetOf<String>()
        val objects =
            model.objects.map { obj ->
                val faces =
                    obj.faces.map { face ->
                        val firstCoordinate = face.first.coordinate.convert()
                        val secondCoordinate = face.second.coordinate.convert()
                        val thirdCoordinate = face.third.coordinate.convert()
                        val firstNormal = face.first.normal.convert()
                        val secondNormal = face.second.normal.convert()
                        val thirdNormal = face.third.normal.convert()
                        val firstUV = face.first.uv?.convert()
                        val secondUV = face.second.uv?.convert()
                        val thirdUV = face.third.uv?.convert()

                        coordinates.addAll(listOf(firstCoordinate, secondCoordinate, thirdCoordinate))
                        normals.addAll(listOf(firstNormal, secondNormal, thirdNormal))
                        uvs.addAll(listOfNotNull(firstUV, secondUV, thirdUV))
                        face.material?.let(materials::add)

                        val first =
                            SimpleModel.Vertex(
                                coordinates.indexOf(firstCoordinate) + 1,
                                normals.indexOf(firstNormal) + 1,
                                if (firstUV != null) uvs.indexOf(firstUV) + 1 else null
                            )
                        val second =
                            SimpleModel.Vertex(
                                coordinates.indexOf(secondCoordinate) + 1,
                                normals.indexOf(secondNormal) + 1,
                                if (secondUV != null) uvs.indexOf(secondUV) + 1 else null
                            )
                        val third =
                            SimpleModel.Vertex(
                                coordinates.indexOf(thirdCoordinate) + 1,
                                normals.indexOf(thirdNormal) + 1,
                                if (thirdUV != null) uvs.indexOf(thirdUV) + 1 else null
                            )

                        SimpleModel.Face(face.material, first, second, third)
                    }

                SimpleModel.Object(obj.name, faces)
            }

        return SimpleModel(coordinates, normals, uvs, materials, objects)
    }

    private fun Model.Vector2f.convert() = SimpleModel.Vector2f(x, y)

    private fun Model.Vector3f.convert() = SimpleModel.Vector3f(x, y, z)

    data class SimpleModel(
        val coordinates: Set<Vector3f>,
        val normals: Set<Vector3f>,
        val uvs: Set<Vector2f>,
        val materials: Set<String>,
        val objects: List<Object>,
    ) {
        data class Object(
            val name: String,
            val faces: List<Face>,
        )

        data class Face(
            val material: String?,
            val first: Vertex,
            val second: Vertex,
            val third: Vertex,
        )

        data class Vertex(
            val coordinate: Int,
            val normal: Int,
            val uv: Int?,
        )

        data class Vector2f(
            val x: Float,
            val y: Float,
        )

        data class Vector3f(
            val x: Float,
            val y: Float,
            val z: Float,
        )
    }
}
