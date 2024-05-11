use geolocation::location_provider_create;
use geolocation::request_when_in_use_authorization;
use geolocation::start_updating_location;
use geolocation::LocationProviderCallback;

#[derive(Debug)]
struct LocationProviderCallbackImpl;
impl LocationProviderCallback for LocationProviderCallbackImpl {
    fn on_authenorization_callback(&self, status: i32) {
        println!("QAQ on_authenorization_callback={}", status);
    }

    fn on_location_callback(
        &self,
        accuracy: f64,
        latitude: f64,
        longitude: f64,
        altitude: Option<f64>,
        altitudeAccuracy: Option<f64>,
        heading: Option<f64>,
        speed: Option<f64>,
    ) {
        println!(
            "QAQ on_location_callback => accuracy={}, latitude={}, longitude={}, altitude={:?}, altitudeAccuracy={:?}, heading={:?}, speed={:?}",
            accuracy, latitude, longitude, altitude, altitudeAccuracy, heading, speed
        );
    }
}

fn main() {
    let mmid = "xxx.test.dweb".to_string();
    let callback = LocationProviderCallbackImpl;
    location_provider_create(mmid.clone(), true, 1.0, Box::new(callback));
    request_when_in_use_authorization(mmid.clone());
    start_updating_location(mmid);
}
