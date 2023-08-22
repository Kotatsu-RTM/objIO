package dev.siro256.objio

import dev.siro256.modelio.Model
import dev.siro256.modelio.ModelIO

object ObjIO : ModelIO {
    override val extension: Array<String> = TODO("Not yet implemented")

    override fun parse(byteArray: ByteArray): Result<Model> {
        TODO("Not yet implemented")
    }

    override fun export(model: Model): Result<ByteArray> {
        TODO("Not yet implemented")
    }
}
