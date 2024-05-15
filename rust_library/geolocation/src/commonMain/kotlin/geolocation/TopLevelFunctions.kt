package geolocation



fun `locationProviderCreate`(`mmid`: String, `callback`: LocationManagerCallback) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_1966_location_provider_create(FfiConverterString.lower(`mmid`), FfiConverterTypeLocationManagerCallback.lower(`callback`), _status)
}


fun `requestAlwaysAuthorization`(`mmid`: String) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_1966_request_always_authorization(FfiConverterString.lower(`mmid`), _status)
}


fun `requestWhenInUseAuthorization`(`mmid`: String) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_1966_request_when_in_use_authorization(FfiConverterString.lower(`mmid`), _status)
}


fun `requestLocation`(`mmid`: String) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_1966_request_location(FfiConverterString.lower(`mmid`), _status)
}


fun `currentLocationAuthorizationStatus`(`mmid`: String): Int {
    return FfiConverterInt.lift(
    rustCall() { _status ->
    UniFFILib.geolocation_1966_current_location_authorization_status(FfiConverterString.lower(`mmid`), _status)
})
}



fun `startUpdatingLocation`(`mmid`: String, `precise`: Boolean, `distance`: Double) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_1966_start_updating_location(FfiConverterString.lower(`mmid`), FfiConverterBoolean.lower(`precise`), FfiConverterDouble.lower(`distance`), _status)
}


fun `stopUpdatingLocation`(`mmid`: String) =
    
    rustCall() { _status ->
    UniFFILib.geolocation_1966_stop_updating_location(FfiConverterString.lower(`mmid`), _status)
}
