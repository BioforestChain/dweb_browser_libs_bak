use std::{
    collections::HashMap,
    ffi::{c_char, CStr},
    os::raw::{c_double, c_int},
    sync::{Mutex, OnceLock},
};
use swift_rs::{swift, Bool, Double, Int, SRString, SwiftArg};

type ClLocationCallbackFn = unsafe extern "C" fn(
    *const c_char,
    c_double,
    c_double,
    c_double,
    *const c_double,
    *const c_double,
    *const c_double,
    *const c_double,
);
pub struct ClLocationCallback(pub ClLocationCallbackFn);
impl<'a> SwiftArg<'a> for ClLocationCallback {
    type ArgType = ClLocationCallbackFn;

    unsafe fn as_arg(&'a self) -> Self::ArgType {
        self.0
    }
}
type AuthorizationStatusCallbackFn = unsafe extern "C" fn(*const c_char, c_int);
pub struct AuthorizationStatusCallback(pub AuthorizationStatusCallbackFn);
impl<'a> SwiftArg<'a> for AuthorizationStatusCallback {
    type ArgType = AuthorizationStatusCallbackFn;

    unsafe fn as_arg(&'a self) -> Self::ArgType {
        self.0
    }
}

#[repr(C)]
pub(crate) struct GeolocationResult {
    accuracy: f64,
    latitude: f64,
    longitude: f64,
    altitude: Option<f64>,
    altitudeAccuracy: Option<f64>,
    heading: Option<f64>,
    speed: Option<f64>,
}

// Init LocationProvider
swift!(pub(crate) fn locationprovider_create(mmid: &SRString, precise: Bool, distance: Double, clLocationCallback: ClLocationCallback, authorizationStatusCallback: AuthorizationStatusCallback));

swift!(pub(crate) fn microModule_requestAlwaysAuthorization(mmid: &SRString));

swift!(pub(crate) fn microModule_requestWhenInUseAuthorization(mmid: &SRString));

swift!(pub(crate) fn microModule_requestLocation(mmid: &SRString));

swift!(pub(crate) fn microModule_currentLocationAuthorizationStatus(mmid: &SRString) -> Int);

swift!(pub(crate) fn microModule_startUpdatingLocation(mmid: &SRString));

swift!(pub(crate) fn microModule_stopUpdatingLocation(mmid: &SRString));

pub trait LocationProviderCallback: Send + Sync + std::fmt::Debug {
    fn on_authenorization_callback(&self, status: i32);
    fn on_location_callback(
        &self,
        accuracy: f64,
        latitude: f64,
        longitude: f64,
        altitude: Option<f64>,
        altitudeAccuracy: Option<f64>,
        heading: Option<f64>,
        speed: Option<f64>,
    );
}

static LOCATION_HASHMAP: OnceLock<Mutex<HashMap<String, Box<dyn LocationProviderCallback>>>> =
    OnceLock::new();

fn c_char_to_string(s: *const c_char) -> String {
    let c_str: &CStr = unsafe { CStr::from_ptr(s) };
    let str_slice: &str = c_str.to_str().unwrap();
    str_slice.to_string()
}

fn c_double_to_option_f64(d: *const c_double) -> Option<f64> {
    if d.is_null() {
        None
    } else {
        unsafe { Some(*d) }
    }
}

pub fn location_provider_create(
    mmid: String,
    precise: bool,
    distance: f64,
    callback: Box<dyn LocationProviderCallback>,
) {
    println!("QAQ start");
    {
        LOCATION_HASHMAP
            .get_or_init(Default::default)
            .lock()
            .unwrap()
            .insert(mmid.clone(), callback);
    }

    unsafe {
        extern "C" fn location_callback(
            mmid: *const c_char,
            accuracy: c_double,
            latitude: c_double,
            longitude: c_double,
            altitude: *const c_double,
            altitudeAccuracy: *const c_double,
            heading: *const c_double,
            speed: *const c_double,
        ) {
            println!("mmid: {}, accuracy: {}", c_char_to_string(mmid), accuracy);
            if let Some(callback) = LOCATION_HASHMAP
                .get_or_init(Default::default)
                .lock()
                .unwrap()
                .get(c_char_to_string(mmid).as_str())
            {
                callback.on_location_callback(
                    accuracy,
                    latitude,
                    longitude,
                    c_double_to_option_f64(altitude),
                    c_double_to_option_f64(altitudeAccuracy),
                    c_double_to_option_f64(heading),
                    c_double_to_option_f64(speed),
                );
            }
        }

        extern "C" fn authorization_status_callback(
            mmid: *const c_char,
            authorization_status: c_int,
        ) {
            println!(
                "mmid: {} authorization_status: {}",
                c_char_to_string(mmid),
                authorization_status
            );
            if let Some(callback) = LOCATION_HASHMAP
                .get_or_init(Default::default)
                .lock()
                .unwrap()
                .get(c_char_to_string(mmid).as_str())
            {
                callback.on_authenorization_callback(authorization_status);
            }
        }

        locationprovider_create(
            &mmid.as_str().into(),
            precise,
            distance,
            ClLocationCallback(location_callback),
            AuthorizationStatusCallback(authorization_status_callback),
        );
    };
    println!("QAQ finish");
}

pub fn request_always_authorization(mmid: String) {
    unsafe { microModule_requestAlwaysAuthorization(&mmid.as_str().into()) }
}

pub fn request_when_in_use_authorization(mmid: String) {
    unsafe { microModule_requestWhenInUseAuthorization(&mmid.as_str().into()) }
}

pub fn request_location(mmid: String) {
    unsafe { microModule_requestLocation(&mmid.as_str().into()) }
}

pub fn current_location_authorization_status(mmid: String) -> i32 {
    let status = unsafe { microModule_currentLocationAuthorizationStatus(&mmid.as_str().into()) };
    status as i32
}

pub fn start_updating_location(mmid: String) {
    println!("QAQ start updating before");
    unsafe { microModule_startUpdatingLocation(&mmid.as_str().into()) }
    println!("QAQ start updating after");
}

pub fn stop_updating_location(mmid: String) {
    println!("QAQ stop updating before");
    unsafe { microModule_stopUpdatingLocation(&mmid.as_str().into()) }
    println!("QAQ stop updating after");
    {
        LOCATION_HASHMAP
            .get_or_init(Default::default)
            .lock()
            .unwrap()
            .remove(mmid.as_str());
    }
}
