package geolocation



expect object UniFFILib {
    fun ffi_geolocation_45e2_LocationProviderCallback_init_callback(`callbackStub`: ForeignCallback,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_45e2_location_provider_create(`mmid`: RustBuffer,`precise`: Byte,`distance`: Double,`callback`: ULong,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_45e2_request_always_authorization(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_45e2_request_when_in_use_authorization(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_45e2_request_location(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_45e2_current_location_authorization_status(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Int

    fun geolocation_45e2_start_updating_location(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun geolocation_45e2_stop_updating_location(`mmid`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun ffi_geolocation_45e2_rustbuffer_alloc(`size`: Int,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    fun ffi_geolocation_45e2_rustbuffer_from_bytes(`bytes`: ForeignBytes,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    fun ffi_geolocation_45e2_rustbuffer_free(`buf`: RustBuffer,
    _uniffi_out_err: RustCallStatus
    ): Unit

    fun ffi_geolocation_45e2_rustbuffer_reserve(`buf`: RustBuffer,`additional`: Int,
    _uniffi_out_err: RustCallStatus
    ): RustBuffer

    
}