package dev.siro256.objio

import dev.siro256.modelio.Model
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ObjIOTest {
    @Test
    fun testExtension() {
        assertContentEquals(arrayOf("obj"), ObjIO.extension)
    }

    @ParameterizedTest
    @MethodSource("testDataProvider")
    fun testParse(byteArray: ByteArray, model: Model) {
        assertEquals(model, ObjIO.parse(byteArray).getOrThrow())
    }

    /**
     * Exported vertex coords, uv coords, normals, and objects aren't sorted.
     * Thereby, the output([ByteArray]) can't be predicted.
     * However, if the output is correct, the output([Model]) parsed from it should be same as input.
     * Therefore, parse output then compare with input.
     */
    @ParameterizedTest
    @MethodSource("testDataProvider")
    fun testExport(byteArray: ByteArray, model: Model) {
        assertEquals(model, ObjIO.parse(ObjIO.export(model).getOrThrow()).getOrThrow())
    }

    companion object {
        private val case1Obj =
            this::class.java.classLoader.getResourceAsStream("dev/siro256/objio/ObjIOTest/case1.obj")!!.readBytes()
        private val case2Obj =
            this::class.java.classLoader.getResourceAsStream("dev/siro256/objio/ObjIOTest/case2.obj")!!.readBytes()
        private val case3Obj =
            this::class.java.classLoader.getResourceAsStream("dev/siro256/objio/ObjIOTest/case3.obj")!!.readBytes()
        private val case4Obj =
            this::class.java.classLoader.getResourceAsStream("dev/siro256/objio/ObjIOTest/case4.obj")!!.readBytes()
        private val case1ExpectedModel =
            Model(emptyList())
        private val case2ExpectedModel =
            Model(
                listOf(
                    Model.Object("empty", emptyList()),
                    Model.Object(
                        "square",
                        listOf(
                            Model.Face(
                                Optional.of("material"),
                                Model.Vector3f(0.0f, 0.0f, 1.0f),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 0.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.0f, 0.0f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 0.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.5f, 0.0f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.0f, 0.5f))
                                )
                            ),
                            Model.Face(
                                Optional.of("material"),
                                Model.Vector3f(0.0f, 0.0f, 1.0f),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 0.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.5f, 0.0f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.5f, 0.5f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.0f, 0.5f))
                                )
                            )
                        )
                    )
                )
            )
        private val case3ExpectedModel =
            Model(
                listOf(
                    Model.Object(
                        "square1",
                        listOf(
                            Model.Face(
                                Optional.empty(),
                                Model.Vector3f(0.0f, 0.0f, 1.0f),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 0.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.empty()
                                ),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 0.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.empty()
                                ),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.empty()
                                )
                            ),
                            Model.Face(
                                Optional.of("material1"),
                                Model.Vector3f(0.0f, 0.0f, 1.0f),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 0.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.5f, 0.0f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.5f, 0.5f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.0f, 0.5f))
                                )
                            )
                        )
                    ),
                    Model.Object(
                        "square2",
                        listOf(
                            Model.Face(
                                Optional.empty(),
                                Model.Vector3f(0.0f, 1.0f, 0.0f),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.empty()
                                ),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.empty()
                                ),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, -1.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.empty()
                                )
                            ),
                            Model.Face(
                                Optional.of("material2"),
                                Model.Vector3f(0.0f, 1.0f, 0.0f),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.of(Model.Vector2f(1.0f, 0.5f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 1.0f, -1.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.of(Model.Vector2f(1.0f, 1.0f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, -1.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.of(Model.Vector2f(0.5f, 1.0f))
                                )
                            )
                        )
                    )
                )
            )
        private val case4ExpectedModel =
            Model(
                listOf(
                    Model.Object(
                        "正方形1",
                        listOf(
                            Model.Face(
                                Optional.empty(),
                                Model.Vector3f(0.0f, 0.0f, 1.0f),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 0.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.empty()
                                ),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 0.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.empty()
                                ),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.empty()
                                )
                            ),
                            Model.Face(
                                Optional.of("マテリアル1"),
                                Model.Vector3f(0.0f, 0.0f, 1.0f),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 0.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.5f, 0.0f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.5f, 0.5f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 0.0f, 1.0f)),
                                    Optional.of(Model.Vector2f(0.0f, 0.5f))
                                )
                            )
                        )
                    ),
                    Model.Object(
                        "正方形2",
                        listOf(
                            Model.Face(
                                Optional.empty(),
                                Model.Vector3f(0.0f, 1.0f, 0.0f),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.empty()
                                ),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.empty()
                                ),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, -1.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.empty()
                                )
                            ),
                            Model.Face(
                                Optional.of("マテリアル2"),
                                Model.Vector3f(0.0f, 1.0f, 0.0f),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 1.0f, 0.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.of(Model.Vector2f(1.0f, 0.5f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(1.0f, 1.0f, -1.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.of(Model.Vector2f(1.0f, 1.0f))
                                ),
                                Model.Vertex(
                                    Model.Vector3f(0.0f, 1.0f, -1.0f),
                                    Optional.of(Model.Vector3f(0.0f, 1.0f, 0.0f)),
                                    Optional.of(Model.Vector2f(0.5f, 1.0f))
                                )
                            )
                        )
                    )
                )
            )

        /**
         * | objects # | faces # | have material  | have uv        | char type |
         * | :-------- | :------ | :------------- | :------------- | :-------- |
         * | 0         | 0       | no             | no             | ASCII     |
         * | 2         | 0, 2    | no, yes        | no, yes        | ASCII     |
         * | 2         | 2, 2    | no-yes, no-yes | no-yes, no-yes | ASCII     |
         * | 2         | 2, 2    | no-yes, no-yes | no-yes, no-yes | non-ASCII |
         */
        @JvmStatic
        fun testDataProvider() =
            listOf(
                Arguments.of(case1Obj, case1ExpectedModel),
                Arguments.of(case2Obj, case2ExpectedModel),
                Arguments.of(case3Obj, case3ExpectedModel),
                Arguments.of(case4Obj, case4ExpectedModel)
            )
    }
}
