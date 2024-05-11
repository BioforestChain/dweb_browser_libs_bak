// The Swift Programming Language
// https://docs.swift.org/swift-book

import CoreLocation
import SwiftRs

class LocationProviderManager: NSObject {
    static var shared = LocationProviderManager()
    static let semaphore = DispatchSemaphore(value: 0)

    private var locationProviderMap: [String: LocationProvider] = [:]

    func storeLocationProvider(_ key: String, _ value: LocationProvider) {
        locationProviderMap[key] = value
    }

    func locationProviderSize() -> Int {
        return locationProviderMap.count
    }

    func locationProviderMapKeys() -> [String] {
        return Array(locationProviderMap.keys)
    }

    func getLocationProvider(_ key: String) -> LocationProvider? {
        return locationProviderMap[key]
    }

    func removeLocationProvider(_ key: String) {
        locationProviderMap.removeValue(forKey: key)
    }
}

class LocationProvider: NSObject, CLLocationManagerDelegate {
    var mmid: String
    var locationManager: CLLocationManager
    //        _ accuracy: Double,
    //        _ latitude: Double,
    //        _ longitude: Double,
    //        _ altitude: Double?,
    //        _ altitudeAccuracy: Double?,
    //        _ heading: Double?,
    //        _ speed: Double?
    var clLocationCallback: @convention(c) (
        UnsafePointer<CChar>, Double, Double, Double, UnsafePointer<Double>?, UnsafePointer<Double>?, UnsafePointer<Double>?, UnsafePointer<Double>?
    ) -> Void
    var authorizationStatusCallback: (UnsafePointer<CChar>, Int) -> Void

    init(
        _ mmid: String,
        _ locationManager: CLLocationManager,

        _ clLocationCallback: @escaping @convention(c) (
            UnsafePointer<CChar>, Double, Double, Double, UnsafePointer<Double>?, UnsafePointer<Double>?, UnsafePointer<Double>?, UnsafePointer<Double>?
        ) -> Void,
        _ authorizationStatusCallback: @escaping @convention(c) (UnsafePointer<CChar>, Int) -> Void
    ) {
        self.mmid = mmid
        self.locationManager = locationManager
        self.clLocationCallback = clLocationCallback
        self.authorizationStatusCallback = authorizationStatusCallback
    }

    func requestAlwaysAuthorization() {
        locationManager.requestAlwaysAuthorization()
    }

    func requestWhenInUseAuthorization() {
        locationManager.requestWhenInUseAuthorization()
    }

    func requestLocation() {
        locationManager.requestLocation()
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        print("QAQ locationManager=\(locations.last)")
        if let clLocation = locations.last {
            clLocationCallback(
                self.mmid,
                max(clLocation.horizontalAccuracy, clLocation.verticalAccuracy),
                clLocation.coordinate.latitude,
                clLocation.coordinate.longitude,

                withUnsafePointer(to: clLocation.altitude) { UnsafePointer($0) },
                nil,
                clLocation.course < 0 ? nil : withUnsafePointer(to: clLocation.course) { UnsafePointer($0) },
                clLocation.speed < 0 ? nil : withUnsafePointer(to: clLocation.speed) { UnsafePointer($0) }
            )
        }
        LocationProviderManager.semaphore.signal()
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        authorizationStatusCallback(self.mmid, Int(manager.authorizationStatus.rawValue))
    }
    
    func currentLocationAuthorizationStatus() -> Int {
        return Int(self.locationManager.authorizationStatus.rawValue)
    }

    func startUpdatingLocation() {
        locationManager.startUpdatingLocation()
        print("QAQ location=\(locationManager.location)")
    }

    func stopUpdatingLocation() {
        locationManager.stopUpdatingLocation()
    }
}

@_cdecl("locationprovider_create")
func locationProviderCreate(
    mmid: SRString,
    precise: Bool,
    distance: Double,

    clLocationCallback: @escaping @convention(c) (
        UnsafePointer<CChar>, Double, Double, Double, UnsafePointer<Double>?, UnsafePointer<Double>?, UnsafePointer<Double>?, UnsafePointer<Double>?
    ) -> Void,
    authorizationStatusCallback: @escaping @convention(c) (UnsafePointer<CChar>, Int) -> Void
) {
    print("QAQ newLocationProvider start mmid:\(mmid) precise:\(precise) distance:\(distance)")
    let clLocationManager = CLLocationManager()

    if precise {
        clLocationManager.desiredAccuracy = kCLLocationAccuracyBest
    } else {
        clLocationManager.desiredAccuracy = kCLLocationAccuracyKilometer
    }

    clLocationManager.distanceFilter = distance
    print("auth => \(clLocationManager.authorizationStatus)")
    clLocationManager.requestAlwaysAuthorization()
    let locationProvider = LocationProvider(mmid.toString(), clLocationManager, clLocationCallback, authorizationStatusCallback)
    clLocationManager.delegate = locationProvider

    LocationProviderManager.shared.storeLocationProvider(
        mmid.toString(), locationProvider
    )
    print("QAQ newLocationProvider finish")
}

@_cdecl("microModule_requestAlwaysAuthorization")
func requestAlwaysAuthorization(mmid: SRString) {
    if let provider = LocationProviderManager.shared.getLocationProvider(mmid.toString()) {
        provider.requestAlwaysAuthorization()
    }
}

@_cdecl("microModule_requestWhenInUseAuthorization")
func requestWhenInUseAuthorization(mmid: SRString) {
    if let provider = LocationProviderManager.shared.getLocationProvider(mmid.toString()) {
        provider.requestWhenInUseAuthorization()
    }
}

@_cdecl("microModule_requestLocation")
func requestLocation(mmid: SRString) {
    if let provider = LocationProviderManager.shared.getLocationProvider(mmid.toString()) {
        provider.requestLocation()
    }
}

@_cdecl("microModule_currentLocationAuthorizationStatus")
func currentLocationAuthorizationStatus(mmid: SRString) -> Int {
    if let provider = LocationProviderManager.shared.getLocationProvider(mmid.toString()) {
        print("QAQ currentLocationAuthorizationStatus mmid=\(mmid) status=\(provider.currentLocationAuthorizationStatus())")
        return provider.currentLocationAuthorizationStatus()
    } else {
        print("QAQ notDetermined")
        return Int(CLAuthorizationStatus.notDetermined.rawValue)
    }
}

@_cdecl("microModule_startUpdatingLocation")
func startUpdatingLocation(mmid: SRString) {
    if let provider = LocationProviderManager.shared.getLocationProvider(mmid.toString()) {
        print("QAQ startUpdatingLocation start")
        provider.startUpdatingLocation()
        print("QAQ startUpdatingLocation end")
    } else {
        print("QAQ startUpdatingLocation failed")
    }
}

@_cdecl("microModule_stopUpdatingLocation")
func stopUpdatingLocation(mmid: SRString) {
    if let provider = LocationProviderManager.shared.getLocationProvider(mmid.toString()) {
        LocationProviderManager.semaphore.wait()
        print("QAQ stopUpdatingLocation start")
        provider.stopUpdatingLocation()
        LocationProviderManager.shared.removeLocationProvider(mmid.toString())
        print("QAQ stopUpdatingLocation stop")
    }
}
