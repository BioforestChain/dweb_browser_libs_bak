# macos
echo "cargo building aarch64-apple-darwin..."
cargo build --target aarch64-apple-darwin
echo "cargo building x86_64-apple-darwin..."
cargo build --target x86_64-apple-darwin

rm -rf ../src/commonMain
cp -r ./target/bindings/commonMain/ ../src/commonMain

rm -rf ../src/nativeInterop
cp -r ./target/bindings/nativeInterop/ ../src/nativeInterop
cp ../geolocation.def ../src/nativeInterop/cinterop/

mkdir -p ../src/desktopMain/resources/darwin-aarch64/
cp -r ./target/aarch64-apple-darwin/debug/libgeolocation.a ../src/desktopMain/resources/darwin-aarch64/
cp -r ./target/aarch64-apple-darwin/debug/libgeolocation.dylib ../src/desktopMain/resources/darwin-aarch64/libgeolocation.dylib
mkdir -p ../src/desktopMain/resources/darwin-x86-64/
cp -r ./target/x86_64-apple-darwin/debug/libgeolocation.a ../src/desktopMain/resources/darwin-x86-64/
cp -r ./target/x86_64-apple-darwin/debug/libgeolocation.dylib ../src/desktopMain/resources/darwin-x86-64/

# windows

# echo "cargo building x86_64-pc-windows-gnu..."
# cargo build --target x86_64-pc-windows-gnu

# mkdir -p ../src/desktopMain/resources/win32-x86-64/
# cp -r ./target/x86_64-pc-windows-gnu/debug/geolocation.dll ../src/desktopMain/resources/win32-x86-64/

# jvm
cp -r ./target/bindings/jvmMain/ ../src/desktopMain
