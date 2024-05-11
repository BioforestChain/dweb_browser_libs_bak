#[cfg(target_os = "macos")]
#[path = "macos.rs"]
mod os;
pub use os::LocationProviderCallback;

#[cfg(target_os = "windows")]
#[path = "windows.rs"]
mod os;


pub fn location_provider_create(mmid: String, precise: bool, distance: f64, callback: Box<dyn  LocationProviderCallback>) {
    os::location_provider_create(mmid, precise, distance, callback)
}

pub fn request_always_authorization(mmid: String) {
    os::request_always_authorization(mmid)
}

pub fn request_when_in_use_authorization(mmid: String) {
    os::request_when_in_use_authorization(mmid)
}

pub fn request_location(mmid: String) {
    os::request_location(mmid)
}

pub fn current_location_authorization_status(mmid: String) -> i32 {
  os::current_location_authorization_status(mmid)
}

pub fn start_updating_location(mmid: String) {
    os::start_updating_location(mmid)
}

pub fn stop_updating_location(mmid: String) {
    os::stop_updating_location(mmid)
}

uniffi::include_scaffolding!("geolocation");
