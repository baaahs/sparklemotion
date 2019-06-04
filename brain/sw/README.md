Firmware Directories
====================

`playa` is the first and main firmware intended for use at the 2019 burn.

Others are likely to appear overtime.

Build Environment Setup
=======================

The basic thing is to follow the [Get Started](https://docs.espressif.com/projects/esp-idf/en/latest/get-started/#step-1-set-up-the-toolchain) section of the Espressif ESP-IDF.

The highlights are 
* We're using the latest version on master until we find a reason not to. The bleeding edge is where it's at. Basically this is v4. We're also using the CMake build system which is officially in Preview at the moment, but again, why not?
* Clone the IDF into a suitable directory. Their examples use `~/esp/esp-idf` so unless you are particular about your machine that's probably a reasonable thing to do.
* Note that they use git submodules so you either need to use `--recursive` when you clone the repo or you need to do a `git submodule update --init` after you have cloned it.
* The `IDF_PATH` environment variable needs to be set to the root of the idf.
* You also want to add `IDF_PATH/tools` to your system `PATH`
* The build tools are written in python. Check `${IDF_PATH}/requirements.txt` for details
* I think I also installed the `ninja` build tool per the Get Started directions. That might be a thing also.

Assuming all that is done you should be able to cd into one of the firmware directories and run `idf.py build`.

However, right now I don't understand why the spiffs storage.bin file doesn't build. To build this manually in the playa directory, from the `playa\build` directory you can run `ninja spiffs_storage_bin` or possibly `make spiffs_storage_bin` depending on which build system you are using.

If the build works use `idf.py flash` to flash a board connected via usb and `idf.py monitor` to connect to the serial console.

