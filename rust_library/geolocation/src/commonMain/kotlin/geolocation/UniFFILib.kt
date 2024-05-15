package geolocation



expect object UniFFILib {
    fun ffi_geolocation_1966_LocationManagerCallback_init_callback(`callbackStub`: ForeignCallback,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_1966_location_provider_create(`mmid`: RustBuffer,`callback`: ULong,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_1966_request_always_authorization(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_1966_request_when_in_use_authorization(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_1966_request_location(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_1966_current_location_authorization_status(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Int

    fun geolocation_1966_start_updating_location(`mmid`: RustBuffer,`precise`: Byte,`distance`: Double,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_1966_stop_updating_location(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun ffi_geolocation_1966_rustbuffer_alloc(`size`: Int,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    fun ffi_geolocation_1966_rustbuffer_from_bytes(`bytes`: ForeignBytes,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    fun ffi_geolocation_1966_rustbuffer_free(`buf`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun ffi_geolocation_1966_rustbuffer_reserve(`buf`: RustBuffer,`additional`: Int,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    
}