package geolocation

actual fun ForeignCallbackTypeLocationManagerCallback.toForeignCallback() : ForeignCallback =
    NativeCallback(this::invoke)