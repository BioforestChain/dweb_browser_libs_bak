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
        FfiConverterTypeLocationProviderCallback.register(this)
        
    }

    @JvmName("ffi_geolocation_45e2_LocationProviderCallback_init_callback")
    actual external fun ffi_geolocation_45e2_LocationProviderCallback_init_callback(`callbackStub`: ForeignCallback,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_45e2_location_provider_create")
    actual external fun geolocation_45e2_location_provider_create(`mmid`: RustBuffer,`precise`: Byte,`distance`: Double,`callback`: ULong,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_45e2_request_always_authorization")
    actual external fun geolocation_45e2_request_always_authorization(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_45e2_request_when_in_use_authorization")
    actual external fun geolocation_45e2_request_when_in_use_authorization(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_45e2_request_location")
    actual external fun geolocation_45e2_request_location(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_45e2_current_location_authorization_status")
    actual external fun geolocation_45e2_current_location_authorization_status(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Int

    @JvmName("geolocation_45e2_start_updating_location")
    actual external fun geolocation_45e2_start_updating_location(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("geolocation_45e2_stop_updating_location")
    actual external fun geolocation_45e2_stop_updating_location(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("ffi_geolocation_45e2_rustbuffer_alloc")
    actual external fun ffi_geolocation_45e2_rustbuffer_alloc(`size`: Int,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    @JvmName("ffi_geolocation_45e2_rustbuffer_from_bytes")
    actual external fun ffi_geolocation_45e2_rustbuffer_from_bytes(`bytes`: ForeignBytes,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    @JvmName("ffi_geolocation_45e2_rustbuffer_free")
    actual external fun ffi_geolocation_45e2_rustbuffer_free(`buf`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    @JvmName("ffi_geolocation_45e2_rustbuffer_reserve")
    actual external fun ffi_geolocation_45e2_rustbuffer_reserve(`buf`: RustBuffer,`additional`: Int,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    
}