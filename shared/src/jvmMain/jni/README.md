## libftd2xx 

From https://www.ftdichip.com/Drivers/D2XX.htm

Note proviso for Mac:
>If using a device with standard FTDI vendor and product identifiers, install D2xxHelper to prevent OS X 10.11 (El Capitan) claiming the device as a serial port (locking out D2XX programs).


## libftd2xxj

Java adapter for `libftd2xx`.

From https://sourceforge.net/projects/ftd2xxj/files/ftd2xxj/2.1/ftd2xxj-native-2.1.zip/download

Built against `libftd2xx` version 1.4.4 using `CMakeLists.txt`:
```
cmake_minimum_required (VERSION 2.6)
project (ftd2xxj)
set(CMAKE_BUILD_TYPE Release)
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})
include_directories("/usr/local/include")

file(GLOB LIBRARIES "/usr/local/lib/libftd2xx.dylib")
message("LIBRARIES = ${LIBRARIES}")

set(SOURCE_FILES device.c deviceDescriptor.c eeprom.c error.c ftd2xxj.c logger.c port.c service.c)
add_library(ftd2xxj SHARED ${SOURCE_FILES})

set_target_properties(ftd2xxj PROPERTIES IMPORTED_LOCATION /usr/local/lib/libftd2xx.dylib )
target_link_libraries(ftd2xxj ${LIBRARIES})
```

## Note from Kevin BRC 2025
I also had to run the following for the jni lib to import successfully on my new macbook air
```
cd shared/src/jvmMain/jni

# 0) Optional but helpful: clear quarantine & perms
xattr -d com.apple.quarantine libftd2xx*.dylib 2>/dev/null || true
chmod 755 libftd2xx*.dylib

# 1) Make sure Java can match the name it’s looking for
ln -sf libftd2xxj.dylib libftd2xxj.jnilib

# 2) Rewrite libftd2xxj’s own install name to a portable one
install_name_tool -id @loader_path/libftd2xxj.dylib libftd2xxj.dylib

# 3) Point its dependency to the sibling file in the same folder
install_name_tool -change libftd2xx.dylib @loader_path/libftd2xx.dylib libftd2xxj.dylib

# 4) Verify
otool -L libftd2xxj.dylib
# you want to see:
#   @loader_path/libftd2xxj.dylib
#   @loader_path/libftd2xx.dylib
```
