pub(crate) mod consts;
use consts::geolocationResult;

pub fn check_support_geolocation() -> i8 {
    geolocation_win::check_support_geolocation()
}

pub fn geolocation_result_content(reason: String) -> (bool, String) {
    geolocation_win::geolocation_result_content(reason)
}
