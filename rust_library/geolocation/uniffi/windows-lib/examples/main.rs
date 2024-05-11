use geolocation_win::{check_support_geolocation, geolocation_result_content};

fn main() {
    // let result = check_support_geolocation();

    let (success, _) = geolocation_result_content("test".to_string());
    println!("result: {}", success);
}