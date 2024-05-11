package geolocation



interface LocationProviderCallback {
    fun `onAuthenorizationCallback`(`status`: Int)
    fun `onLocationCallback`(`accuracy`: Double, `latitude`: Double, `longitude`: Double, `altitude`: Double?, `altitudeAccuracy`: Double?, `heading`: Double?, `speed`: Double?)
    
}

object ForeignCallbackTypeLocationProviderCallback {
    @Suppress("TooGenericExceptionCaught")
    fun invoke(handle: Handle, method: Int, args: RustBuffer, outBuf: RustBufferPointer): Int {
        val cb = FfiConverterTypeLocationProviderCallback.lift(handle)
        return when (method) {
            IDX_CALLBACK_FREE -> {
                FfiConverterTypeLocationProviderCallback.drop(handle)
                0
            }
            1 -> {
                try {
                    val buffer = this.`invokeOnAuthenorizationCallback`(cb, args)
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
                    val buffer = this.`invokeOnLocationCallback`(cb, args)
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

    
    private fun `invokeOnAuthenorizationCallback`(kotlinCallbackInterface: LocationProviderCallback, args: RustBuffer): RustBuffer =
        try {
            val buf = checkNotNull(args.toBuffer()) { "No Buffer in RustBuffer; this is a Uniffi bug" }
            kotlinCallbackInterface.`onAuthenorizationCallback`(
                    FfiConverterInt.read(buf)
                    )
            .let { emptyRustBuffer() }
                } finally {
            args.free()
        }

    
    private fun `invokeOnLocationCallback`(kotlinCallbackInterface: LocationProviderCallback, args: RustBuffer): RustBuffer =
        try {
            val buf = checkNotNull(args.toBuffer()) { "No Buffer in RustBuffer; this is a Uniffi bug" }
            kotlinCallbackInterface.`onLocationCallback`(
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

object FfiConverterTypeLocationProviderCallback: FfiConverterCallbackInterface<LocationProviderCallback>() {
    override fun register(lib: UniFFILib) {
        rustCall() { status ->
            lib.ffi_geolocation_45e2_LocationProviderCallback_init_callback(ForeignCallbackTypeLocationProviderCallback.toForeignCallback(), status)
        }
    }
}

expect fun ForeignCallbackTypeLocationProviderCallback.toForeignCallback() : ForeignCallback