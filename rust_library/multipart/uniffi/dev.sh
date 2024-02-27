rm -rf ../src/androidMain

cargo ndk -t aarch64-linux-android -o ../src/androidMain/jniLibs build

# double build，for uniffi_bindgen::generate_external_bindings
cargo ndk -t aarch64-linux-android -o ../src/androidMain/jniLibs build
cp -r ./target/bindings/jvmMain/ ../src/androidMain

rm -rf ../src/commonMain
cp -r ./target/bindings/commonMain/ ../src/commonMain

cargo build --target aarch64-apple-ios

rm -rf ../src/nativeInterop
cp -r ./target/bindings/nativeInterop/ ../src/nativeInterop
cp ../multipart.def ../src/nativeInterop/cinterop/

rm -rf ../src/iosMain
cp -r ./target/bindings/nativeMain/ ../src/iosMain

echo "cargo building aarch64-apple-ios..."
cargo build --target aarch64-apple-ios
echo "cargo building aarch64-apple-ios-sim..."
cargo build --target aarch64-apple-ios-sim

rm -rf ../src/nativeInterop
cp -r ./target/bindings/nativeInterop/ ../src/nativeInterop
cp ../multipart.def ../src/nativeInterop/cinterop/

rm -rf ../src/iosMain
cp -r ./target/bindings/nativeMain/ ../src/iosMain

mkdir -p ../src/libs/iosArm64/
cp ./target/aarch64-apple-ios/debug/libmultipart.a ../src/libs/iosArm64/
mkdir -p ../src/libs/iosSimulatorArm64/
cp ./target/aarch64-apple-ios-sim/debug/libmultipart.a ../src/libs/iosSimulatorArm64/

