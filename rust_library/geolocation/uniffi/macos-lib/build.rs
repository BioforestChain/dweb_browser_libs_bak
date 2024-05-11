use swift_rs::SwiftLinker;

fn main() {
    SwiftLinker::new("13")
        .with_package("geolocation-swift", "./geolocation-swift/")
        .link();
}