package geolocation



fun `locationProviderCreate`(`mmid`: String, `precise`: Boolean, `distance`: Double, `callback`: LocationProviderCallback) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_45e2_location_provider_create(FfiConverterString.lower(`mmid`), FfiConverterBoolean.lower(`precise`), FfiConverterDouble.lower(`distance`), FfiConverterTypeLocationProviderCallback.lower(`callback`), _status)
}


fun `requestAlwaysAuthorization`(`mmid`: String) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_45e2_request_always_authorization(FfiConverterString.lower(`mmid`), _status)
}


fun `requestWhenInUseAuthorization`(`mmid`: String) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_45e2_request_when_in_use_authorization(FfiConverterString.lower(`mmid`), _status)
}


fun `requestLocation`(`mmid`: String) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_45e2_request_location(FfiConverterString.lower(`mmid`), _status)
}


fun `currentLocationAuthorizationStatus`(`mmid`: String): Int {
    return FfiConverterInt.lift(
    rustCall() { _status ->
    UniFFILib.geolocation_45e2_current_location_authorization_status(FfiConverterString.lower(`mmid`), _status)
})
}



fun `startUpdatingLocation`(`mmid`: String) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_45e2_start_updating_location(FfiConverterString.lower(`mmid`), _status)
}


fun `stopUpdatingLocation`(`mmid`: String) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_45e2_stop_updating_location(FfiConverterString.lower(`mmid`), _status)
}
