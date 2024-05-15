package geolocation



import com.sun.jna.Library
import com.sun.jna.Native

@Synchronized
private fun findLibraryName(): String {
    val componentName = "geolocation"
    val libOverride = System.getProperty("uniffi.component.$componentName.libraryOverride")
    if (libOverride != null) {
        return libOverride
    }
    return "geolocation"
}

actual object UniFFILib : Library {
    init {
        Native.register(UniFFILib::class.java, findLibraryName())
        FfiConverterTypeLocationManagerCallback.register(this)
        
    }

    @JvmName("ffi_geolocation_1966_LocationManagerCallback_init_callback")
    actual external fun ffi_geolocation_1966_LocationManagerCallback_init_callback(`callbackStub`: ForeignCallback,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_1966_location_provider_create")
    actual external fun geolocation_1966_location_provider_create(`mmid`: RustBuffer,`callback`: ULong,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_1966_request_always_authorization")
    actual external fun geolocation_1966_request_always_authorization(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_1966_request_when_in_use_authorization")
    actual external fun geolocation_1966_request_when_in_use_authorization(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_1966_request_location")
    actual external fun geolocation_1966_request_location(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_1966_current_location_authorization_status")
    actual external fun geolocation_1966_current_location_authorization_status(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Int

    @JvmName("geolocation_1966_start_updating_location")
    actual external fun geolocation_1966_start_updating_location(`mmid`: RustBuffer,`precise`: Byte,`distance`: Double,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_1966_stop_updating_location")
    actual external fun geolocation_1966_stop_updating_location(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("ffi_geolocation_1966_rustbuffer_alloc")
    actual external fun ffi_geolocation_1966_rustbuffer_alloc(`size`: Int,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    @JvmName("ffi_geolocation_1966_rustbuffer_from_bytes")
    actual external fun ffi_geolocation_1966_rustbuffer_from_bytes(`bytes`: ForeignBytes,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    @JvmName("ffi_geolocation_1966_rustbuffer_free")
    actual external fun ffi_geolocation_1966_rustbuffer_free(`buf`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("ffi_geolocation_1966_rustbuffer_reserve")
    actual external fun ffi_geolocation_1966_rustbuffer_reserve(`buf`: RustBuffer,`additional`: Int,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    
}