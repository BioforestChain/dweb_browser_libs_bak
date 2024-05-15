use std::collections::HashMap;
use std::fs::OpenOptions;
use std::io::Write;
use std::sync::{Mutex, OnceLock};

use objc2::__macro_helpers::Sized;
use objc2::mutability::InteriorMutable;
use objc2::runtime::{NSObject, NSObjectProtocol, ProtocolObject};
use objc2::{declare_class, msg_send_id, ClassType, DeclaredClass};
use objc2::{rc::Id, Message};
use objc2_core_location::{
    kCLLocationAccuracyBest, kCLLocationAccuracyKilometer, CLAuthorizationStatus, CLLocation,
    CLLocationManager, CLLocationManagerDelegate,
};
use objc2_foundation::NSArray;

static LOCATION_MANAGER_HASHMAP: OnceLock<Mutex<HashMap<String, LocationManager>>> =
    OnceLock::new();

#[derive(Debug)]
pub(crate) struct LocationManager {
    clLocationManager: Id<CLLocationManager>,
    locationManagerDelegate: Id<ProtocolObject<dyn CLLocationManagerDelegate>>,
}

unsafe impl Send for LocationManager {}

impl LocationManager {
    fn new(callback: Box<dyn LocationManagerCallback>) -> Self {
        let clLocationManager = unsafe { CLLocationManager::new() };
        let locationManagerDelegate =ProtocolObject::from_id(
            LocationManagerDelegate::new(clLocationManager.clone(), callback),
        );

        unsafe {
            clLocationManager.setDelegate(Some(&*locationManagerDelegate));
        }

        Self {
            clLocationManager: clLocationManager,
            locationManagerDelegate: locationManagerDelegate,
        }
    }
}

struct LocationManagerIvars {
    callback: Box<dyn LocationManagerCallback>,
    clLocationManager: Id<CLLocationManager>
}

declare_class!(
    #[derive(Debug)]
    struct LocationManagerDelegate;

    unsafe impl ClassType for LocationManagerDelegate {
        type Super = NSObject;
        type Mutability = InteriorMutable;
        const NAME: &'static str = "LocationManagerDelegate";
    }

    impl DeclaredClass for LocationManagerDelegate {
        type Ivars = LocationManagerIvars;
    }
);

impl LocationManagerDelegate {
    fn new(clLocationManager: Id<CLLocationManager>, callback: Box<dyn LocationManagerCallback>) -> Id<Self> {
        let this = Self::alloc().set_ivars(LocationManagerIvars { clLocationManager, callback });
        unsafe { msg_send_id![super(this), init] }
    }
}

unsafe impl NSObjectProtocol for LocationManagerDelegate {}

unsafe impl Send for LocationManagerDelegate {}

// unsafe impl RefEncode for LocationManagerDelegate {
//     const ENCODING_REF: Encoding = Encoding::Object;
// }

// unsafe impl Message for LocationManagerDelegate {}

// unsafe impl ClassType for LocationManagerDelegate {
//     type Super = NSObject;
//     type Mutability = InteriorMutable;

//     const NAME: &'static str = "LocationManagerDelegate";

//     fn class() -> &'static AnyClass {
//         todo!()
//     }

//     fn as_super(&self) -> &Self::Super {
//         todo!()
//     }

//     fn as_super_mut(&mut self) -> &mut Self::Super {
//         todo!()
//     }
// }

unsafe impl CLLocationManagerDelegate for LocationManagerDelegate {
    unsafe fn locationManagerDidChangeAuthorization(&self, manager: &CLLocationManager)
    where
        Self: Sized + Message,
    {
        println!(
            "QAQ locationManagerDidChangeAuthorization={}",
            manager.authorizationStatus().0
        );

        write_log(format!(
            "locationManagerDidChangeAuthorization = {}",
            manager.authorizationStatus().0
        ));

        self.ivars()
            .callback
            .on_authorization_status(manager.authorizationStatus().0);
        // self.ivars().mmid
        // {
        //     AUTHORIZATION_STATUS_HASHMAP.get_or_init(Default::default).lock().unwrap().get(k)
        // }
        // _ = self.status_sender.send(manager.authorizationStatus().0);
    }

    unsafe fn locationManager_didUpdateLocations(
        &self,
        manager: &CLLocationManager,
        locations: &NSArray<CLLocation>,
    ) where
        Self: Sized + Message,
    {
        println!(
            "QAQ didUpdateLocations=> status={}, location={}",
            manager.authorizationStatus().0,
            locations.last().unwrap().altitude()
        );

        write_log(format!(
            "didUpdateLocations: status={}, location={}",
            manager.authorizationStatus().0,
            locations.last().unwrap().altitude()
        ));

        if let Some(location) = locations.last() {
            self.ivars().callback.on_location(
                location
                    .horizontalAccuracy()
                    .max(location.verticalAccuracy()),
                location.coordinate().latitude,
                location.coordinate().longitude,
                Some(location.altitude()),
                None,
                match location.course() < 0_f64 {
                    true => None,
                    false => Some(location.course()),
                },
                match location.speed() < 0_f64 {
                    true => None,
                    false => Some(location.speed()),
                },
            );
        }

        // let location = locations.last().unwrap();
        // _ = self.location_sender.send(GeoLocationResult {
        //     accuracy: location
        //         .horizontalAccuracy()
        //         .max(location.verticalAccuracy()),
        //     latitude: location.coordinate().latitude,
        //     longitude: location.coordinate().longitude,
        //     altitude: Some(location.altitude()),
        //     altitudeAccuracy: None,
        //     heading: match location.course() < 0_f64 {
        //         true => None,
        //         false => Some(location.course()),
        //     },
        //     speed: match location.speed() < 0_f64 {
        //         true => None,
        //         false => Some(location.speed()),
        //     },
        // })
    }

    unsafe fn locationManager_didFailWithError(
        &self,
        manager: &CLLocationManager,
        error: &objc2_foundation::NSError,
    ) where
        Self: Sized + Message,
    {
        println!(
            "QAQ didFailWithError {}",
            error.localizedFailureReason().unwrap()
        );
        write_log(format!(
            "didFailWithError => {}",
            error.localizedFailureReason().unwrap()
        ));
    }
}

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

pub(crate) struct GeoLocationResult {
    accuracy: f64,
    latitude: f64,
    longitude: f64,
    altitude: Option<f64>,
    altitudeAccuracy: Option<f64>,
    heading: Option<f64>,
    speed: Option<f64>,
}

pub fn location_manager_create(mmid: String, callback: Box<dyn LocationManagerCallback>) {
    write_log("location_manager_create create before".to_string());
    let location_manager = LocationManager::new(callback);
    {
        LOCATION_MANAGER_HASHMAP
            .get_or_init(Default::default)
            .lock()
            .unwrap()
            .insert(mmid, location_manager);
    }
    write_log("location_manager_create create after".to_string());
}

pub fn current_authorization_status(mmid: String) -> i32 {
    write_log("current_authorization_status start".to_string());
    if let Some(location_manager) = LOCATION_MANAGER_HASHMAP
        .get_or_init(Default::default)
        .lock()
        .unwrap()
        .get(&mmid)
    {
        write_log("current_authorization_status get".to_string());
        unsafe { location_manager.clLocationManager.authorizationStatus().0 }
    } else {
        CLAuthorizationStatus::kCLAuthorizationStatusNotDetermined.0
    }
}

pub fn request_location(mmid: String) {
    write_log("request_location start".to_string());
    if let Some(location_manager) = LOCATION_MANAGER_HASHMAP
        .get_or_init(Default::default)
        .lock()
        .unwrap()
        .get(&mmid)
    {
        write_log("request_location get".to_string());
        unsafe {
            location_manager
                .clLocationManager
                .requestWhenInUseAuthorization()
        };

        write_log("request_location end".to_string());
    }
}

pub fn start_updating_location(mmid: String, precise: bool, distance: f64) {
    write_log("start_updating_location before".to_string());
    if let Some(location_manager) = LOCATION_MANAGER_HASHMAP
        .get_or_init(Default::default)
        .lock()
        .unwrap()
        .get(&mmid)
    {
        write_log("start_updating_location setDesiredAccuracy".to_string());
        unsafe {
            location_manager
                .clLocationManager
                .setDesiredAccuracy(match precise {
                    true => kCLLocationAccuracyBest,
                    false => kCLLocationAccuracyKilometer,
                });
            write_log("start_updating_location setDistanceFilter".to_string());
            location_manager
                .clLocationManager
                .setDistanceFilter(distance);

            write_log("start_updating_location setDelegate start".to_string());
            location_manager
                .clLocationManager
                .setDelegate(Some(&*location_manager.locationManagerDelegate));
            write_log("start_updating_location setDelegate end".to_string());
            write_log("start_updating_location start".to_string());
            location_manager.clLocationManager.startUpdatingLocation();
        };
        write_log("start_updating_location end".to_string());
    }
}

pub fn stop_updating_location(mmid: String) {
    {
        if let Some(location_manager) = LOCATION_MANAGER_HASHMAP
            .get_or_init(Default::default)
            .lock()
            .unwrap()
            .get(&mmid)
        {
            unsafe { location_manager.clLocationManager.stopUpdatingLocation() }
        }
    }

    LOCATION_MANAGER_HASHMAP
        .get_or_init(Default::default)
        .lock()
        .unwrap()
        .remove(&mmid);
}

fn write_log(message: String) {
    let mut file = OpenOptions::new().write(true).append(true).open("/Users/bfs-kingsword09/Library/Application Support/dweb-browser/data/geolocation.sys.dweb/log.txt").unwrap();
    let new_message = format!("rust => {}\n", message);
    _ = file.write_all(new_message.as_bytes());
}
