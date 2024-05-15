// The Swift Programming Language
// https://docs.swift.org/swift-book

import CoreLocation
import SwiftRs

class LocationProviderManager: NSObject {
    static var shared = LocationProviderManager()
    static func writeLog(_ message: String) {
        if let fileHandle = FileHandle(forWritingAtPath: "/Users/bfs-kingsword09/Library/Application Support/dweb-browser/data/geolocation.sys.dweb/log.txt") {
            _ = try? fileHandle.seekToEnd()
            fileHandle.write(Data("swift => \(message) \n".utf8))
        }
    }

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
        super.init()
        self.locationManager.delegate = self
    }

    func requestAlwaysAuthorization() {
        locationManager.requestAlwaysAuthorization()
    }

    func requestWhenInUseAuthorization() {
        LocationProviderManager.writeLog("QAQ requestWhenInUseAuthorization start")
        locationManager.requestWhenInUseAuthorization()
        LocationProviderManager.writeLog("QAQ requestWhenInUseAuthorization end")
    }

    func requestLocation() {
        locationManager.requestLocation()
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        LocationProviderManager.writeLog("QAQ locationManager=\(locations.last)")
        if let clLocation = locations.last {
            clLocationCallback(
                mmid,
                max(clLocation.horizontalAccuracy, clLocation.verticalAccuracy),
                clLocation.coordinate.latitude,
                clLocation.coordinate.longitude,

                withUnsafePointer(to: clLocation.altitude) { UnsafePointer($0) },
                nil,
                clLocation.course < 0 ? nil : withUnsafePointer(to: clLocation.course) { UnsafePointer($0) },
                clLocation.speed < 0 ? nil : withUnsafePointer(to: clLocation.speed) { UnsafePointer($0) }
            )
        }
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        LocationProviderManager.writeLog("current status=>\(manager.authorizationStatus.rawValue)")
        authorizationStatusCallback(mmid, Int(manager.authorizationStatus.rawValue))
    }

    func currentLocationAuthorizationStatus() -> Int {
        return Int(locationManager.authorizationStatus.rawValue)
    }

    func startUpdatingLocation() {
        locationManager.startUpdatingLocation()
    }

    func stopUpdatingLocation() {
        locationManager.stopUpdatingLocation()
    }
}

@_cdecl("locationprovider_create")
func locationProviderCreate(
    mmid: SRString,

    clLocationCallback: @escaping @convention(c) (
        UnsafePointer<CChar>, Double, Double, Double, UnsafePointer<Double>?, UnsafePointer<Double>?, UnsafePointer<Double>?, UnsafePointer<Double>?
    ) -> Void,
    authorizationStatusCallback: @escaping @convention(c) (UnsafePointer<CChar>, Int) -> Void
) {
    LocationProviderManager.writeLog("QAQ newLocationProvider start mmid:\(mmid)")
    let clLocationManager = CLLocationManager()

    let locationProvider = LocationProvider(mmid.toString(), clLocationManager, clLocationCallback, authorizationStatusCallback)

    LocationProviderManager.shared.storeLocationProvider(
        mmid.toString(), locationProvider
    )
    LocationProviderManager.writeLog("QAQ newLocationProvider finish")
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
        LocationProviderManager.writeLog("QAQ currentLocationAuthorizationStatus mmid=\(mmid) status=\(provider.currentLocationAuthorizationStatus())")
        return provider.currentLocationAuthorizationStatus()
    } else {
        LocationProviderManager.writeLog("QAQ notDetermined")
        return Int(CLAuthorizationStatus.notDetermined.rawValue)
    }
}

@_cdecl("microModule_startUpdatingLocation")
func startUpdatingLocation(mmid: SRString, precise: Bool, distance: Double) {
    if let provider = LocationProviderManager.shared.getLocationProvider(mmid.toString()) {
        LocationProviderManager.writeLog("QAQ startUpdatingLocation start")
        if precise {
            provider.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        } else {
            provider.locationManager.desiredAccuracy = kCLLocationAccuracyKilometer
        }

        provider.locationManager.distanceFilter = distance
        provider.startUpdatingLocation()
        LocationProviderManager.writeLog("QAQ startUpdatingLocation end")
    } else {
        LocationProviderManager.writeLog("QAQ startUpdatingLocation failed")
    }
}

@_cdecl("microModule_stopUpdatingLocation")
func stopUpdatingLocation(mmid: SRString) {
    if let provider = LocationProviderManager.shared.getLocationProvider(mmid.toString()) {
        LocationProviderManager.writeLog("QAQ stopUpdatingLocation start")
        provider.stopUpdatingLocation()
        LocationProviderManager.shared.removeLocationProvider(mmid.toString())
        LocationProviderManager.writeLog("QAQ stopUpdatingLocation stop")
    }
}
