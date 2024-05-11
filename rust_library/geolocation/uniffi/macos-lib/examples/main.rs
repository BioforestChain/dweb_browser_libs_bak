use geolocation_macos::{location_provider_create, start_updating_location};
use swift_rs::Int;

fn main() {
  let mmid = "xxx.test.dweb".to_string();
  location_provider_create(mmid.clone(), true, 1.0);
  start_updating_location(mmid)
}
