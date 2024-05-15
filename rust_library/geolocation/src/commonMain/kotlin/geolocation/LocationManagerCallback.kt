package geolocation



interface LocationManagerCallback {
    fun `onAuthorizationStatus`(`status`: Int)
    fun `onLocation`(`accuracy`: Double, `latitude`: Double, `longitude`: Double, `altitude`: Double?, `altitudeAccuracy`: Double?, `heading`: Double?, `speed`: Double?)
    
}

object ForeignCallbackTypeLocationManagerCallback {
    @Suppress("TooGenericExceptionCaught")
    fun invoke(handle: Handle, method: Int, args: RustBuffer, outBuf: RustBufferPointer): Int {
        val cb = FfiConverterTypeLocationManagerCallback.lift(handle)
        return when (method) {
            IDX_CALLBACK_FREE -> {
                FfiConverterTypeLocationManagerCallback.drop(handle)
                0
            }
            1 -> {
                try {
                    val buffer = this.`invokeOnAuthorizationStatus`(cb, args)
                    // Success
                    outBuf.setValue(buffer)
                    1
                } catch (e: Throwable) {
                    try {
                        outBuf.setValue(FfiConverterString.lower(e.toString()))
                    } catch (e: Throwable) {
                    }
                    -1
                }
            }
            2 -> {
                try {
                    val buffer = this.`invokeOnLocation`(cb, args)
                    // Success
                    outBuf.setValue(buffer)
                    1
                } catch (e: Throwable) {
                    try {
                        outBuf.setValue(FfiConverterString.lower(e.toString()))
                    } catch (e: Throwable) {
                    }
                    -1
                }
            }
            
            else -> {
                try {
                    outBuf.setValue(FfiConverterString.lower("Invalid Callback index"))
                } catch (e: Throwable) {
                }
                -1
            }
        }
    }

    
    private fun `invokeOnAuthorizationStatus`(kotlinCallbackInterface: LocationManagerCallback, args: RustBuffer): RustBuffer =
        try {
            val buf = checkNotNull(args.toBuffer()) { "No Buffer in RustBuffer; this is a Uniffi bug" }
            kotlinCallbackInterface.`onAuthorizationStatus`(
                    FfiConverterInt.read(buf)
                    )
            .let { emptyRustBuffer() }
                } finally {
            args.free()
        }

    
    private fun `invokeOnLocation`(kotlinCallbackInterface: LocationManagerCallback, args: RustBuffer): RustBuffer =
        try {
            val buf = checkNotNull(args.toBuffer()) { "No Buffer in RustBuffer; this is a Uniffi bug" }
            kotlinCallbackInterface.`onLocation`(
                    FfiConverterDouble.read(buf), 
                    FfiConverterDouble.read(buf), 
                    FfiConverterDouble.read(buf), 
                    FfiConverterOptionalDouble.read(buf), 
                    FfiConverterOptionalDouble.read(buf), 
                    FfiConverterOptionalDouble.read(buf), 
                    FfiConverterOptionalDouble.read(buf)
                    )
            .let { emptyRustBuffer() }
                } finally {
            args.free()
        }

    
}

object FfiConverterTypeLocationManagerCallback: FfiConverterCallbackInterface<LocationManagerCallback>() {
    override fun register(lib: UniFFILib) {
        rustCall() { status ->
            lib.ffi_geolocation_f60a_LocationManagerCallback_init_callback(ForeignCallbackTypeLocationManagerCallback.toForeignCallback(), status)
        }
    }
}

expect fun ForeignCallbackTypeLocationManagerCallback.toForeignCallback() : ForeignCallback