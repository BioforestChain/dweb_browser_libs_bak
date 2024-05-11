package geolocation

import okio.Buffer

object FfiConverterOptionalDouble: FfiConverterRustBuffer<Double?> {
    override fun read(buf: Buffer): Double? {
        if (buf.readByte().toInt() == 0) {
            return null
        }
        return FfiConverterDouble.read(buf)
    }

    override fun allocationSize(value: Double?): Int {
        if (value == null) {
            return 1
        } else {
            return 1 + FfiConverterDouble.allocationSize(value)
        }
    }

    override fun write(value: Double?, buf: Buffer) {
        if (value == null) {
            buf.writeByte(0)
        } else {
            buf.writeByte(1)
            FfiConverterDouble.write(value, buf)
        }
    }
}