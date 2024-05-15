use std::{
    collections::HashMap,
    ffi::{c_char, CStr},
    fs::OpenOptions,
    io::Write,
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
swift!(pub(crate) fn locationprovider_create(mmid: &SRString, clLocationCallback: ClLocationCallback, authorizationStatusCallback: AuthorizationStatusCallback));

swift!(pub(crate) fn microModule_requestAlwaysAuthorization(mmid: &SRString));

swift!(pub(crate) fn microModule_requestWhenInUseAuthorization(mmid: &SRString));

swift!(pub(crate) fn microModule_requestLocation(mmid: &SRString));

swift!(pub(crate) fn microModule_currentLocationAuthorizationStatus(mmid: &SRString) -> Int);

swift!(pub(crate) fn microModule_startUpdatingLocation(mmid: &SRString, precise: Bool, distance: Double));

swift!(pub(crate) fn microModule_stopUpdatingLocation(mmid: &SRString));

pub trait LocationManagerCallback: Send + Sync + std::fmt::Debug {
    fn on_authorization_status(&self, status: i32);
    fn on_location(
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

static LOCATION_HASHMAP: OnceLock<Mutex<HashMap<String, Box<dyn LocationManagerCallback>>>> =
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
    callback: Box<dyn LocationManagerCallback>,
) {
    write_log("QAQ start");
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
            write_log(format!("mmid: {}, accuracy: {}", c_char_to_string(mmid), accuracy).as_str());
            if let Some(callback) = LOCATION_HASHMAP
                .get_or_init(Default::default)
                .lock()
                .unwrap()
                .get(c_char_to_string(mmid).as_str())
            {
                callback.on_location(
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
            write_log(
                format!(
                    "mmid: {} authorization_status: {}",
                    c_char_to_string(mmid),
                    authorization_status
                )
                .as_str(),
            );
            if let Some(callback) = LOCATION_HASHMAP
                .get_or_init(Default::default)
                .lock()
                .unwrap()
                .get(c_char_to_string(mmid).as_str())
            {
                callback.on_authorization_status(authorization_status);
            }
        }

        locationprovider_create(
            &mmid.as_str().into(),
            ClLocationCallback(location_callback),
            AuthorizationStatusCallback(authorization_status_callback),
        );
    };
    write_log("QAQ finish");
}

pub fn request_always_authorization(mmid: String) {
    unsafe { microModule_requestAlwaysAuthorization(&mmid.as_str().into()) }
}

pub fn request_when_in_use_authorization(mmid: String) {
    write_log("QAQ requestWhenInUseAuthorization start");
    unsafe { microModule_requestWhenInUseAuthorization(&mmid.as_str().into()) }
    write_log("QAQ requestWhenInUseAuthorization end");
}

pub fn request_location(mmid: String) {
    unsafe { microModule_requestLocation(&mmid.as_str().into()) }
}

pub fn current_location_authorization_status(mmid: String) -> i32 {
    let status = unsafe { microModule_currentLocationAuthorizationStatus(&mmid.as_str().into()) };
    status as i32
}

pub fn start_updating_location(mmid: String, precise: bool, distance: f64) {
    write_log("QAQ start updating before");
    unsafe { microModule_startUpdatingLocation(&mmid.as_str().into(), precise, distance) }
    write_log("QAQ start updating after");
}

pub fn stop_updating_location(mmid: String) {
    write_log("QAQ stop updating before");
    unsafe { microModule_stopUpdatingLocation(&mmid.as_str().into()) }
    write_log("QAQ stop updating after");
    {
        LOCATION_HASHMAP
            .get_or_init(Default::default)
            .lock()
            .unwrap()
            .remove(mmid.as_str());
    }
}

fn write_log(message: &str) {
    let mut file = OpenOptions::new().write(true).append(true).open("/Users/bfs-kingsword09/Library/Application Support/dweb-browser/data/geolocation.sys.dweb/log.txt").unwrap();
    let new_message = format!("rust => {}\n", message.to_string());
    _ = file.write_all(new_message.as_bytes());
}
