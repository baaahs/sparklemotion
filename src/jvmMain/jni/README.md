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