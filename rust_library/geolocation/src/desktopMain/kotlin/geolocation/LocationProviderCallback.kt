package geolocation

actual fun ForeignCallbackTypeLocationProviderCallback.toForeignCallback() : ForeignCallback =
    NativeCallback(this::invoke)