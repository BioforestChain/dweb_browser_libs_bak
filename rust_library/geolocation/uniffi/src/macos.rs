pub use geolocation_macos::LocationManagerCallback;

pub fn location_provider_create(mmid: String, callback: Box<dyn LocationManagerCallback>) {
    geolocation_macos::location_provider_create(mmid, callback)
}

pub fn request_always_authorization(mmid: String) {
    geolocation_macos::request_always_authorization(mmid)
}

pub fn request_when_in_use_authorization(mmid: String) {
    geolocation_macos::request_when_in_use_authorization(mmid)
}

pub fn request_location(mmid: String) {
    geolocation_macos::request_location(mmid)
}

pub fn current_location_authorization_status(mmid: String) -> i32 {
    geolocation_macos::current_location_authorization_status(mmid)
}

pub fn start_updating_location(mmid: String, precise: bool, distance: f64) {
    geolocation_macos::start_updating_location(mmid, precise, distance)
}

pub fn stop_updating_location(mmid: String) {
    geolocation_macos::stop_updating_location(mmid)
}