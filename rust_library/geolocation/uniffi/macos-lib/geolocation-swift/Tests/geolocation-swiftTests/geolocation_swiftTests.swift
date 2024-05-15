import XCTest
import SwiftRs
@testable import geolocation_swift

final class geolocation_swiftTests: XCTestCase {
    func testExample() throws {
        // XCTest Documentation
        // https://developer.apple.com/documentation/xctest

        // Defining Test Cases and Test Methods
        // https://developer.apple.com/documentation/xctest/defining_test_cases_and_test_methods
        locationProviderCreate(mmid: SRString("xxx"), clLocationCallback: {_,_,_,_,_,_,_,_ in }, authorizationStatusCallback: {_,_ in })
        
        startUpdatingLocation(mmid: SRString("xxx"), precise: true, distance: 1.0)
        
    }
}
