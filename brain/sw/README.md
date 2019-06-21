Firmware Directories
====================

`playa` is the first and main firmware intended for use at the 2019 burn.

`components` contains various components that are useful in the mix and match world of
applications supported by the firmware.

This directory structure is likely to evolve over time.

Build Environment Setup
=======================

The basic thing is to follow the [Get Started](https://docs.espressif.com/projects/esp-idf/en/latest/get-started/#step-1-set-up-the-toolchain) section of the Espressif ESP-IDF.

The highlights are 
* You're going to be installing a pre-built version of the xtensa toolchain. In the page referenced above you will need to click on a link to go to a second page with detailed instructions for your OS: Mac, Windows, or Linux. Follow the instructions.
* We're using the latest version on master until we find a reason not to. The bleeding edge is where it's at. Basically this is v4. We're also using the CMake build system which is officially in Preview at the moment, but again, why not?
* Clone the IDF into a suitable directory. Their examples use `~/esp/esp-idf` so unless you are particular about your machine that's probably a reasonable thing to do.
* Note that they use git submodules so you either need to use `--recursive` when you clone the repo or you need to do a `git submodule update --init` after you have cloned it.
* The `IDF_PATH` environment variable needs to be set to the root of the idf.
* You also want to add `IDF_PATH/tools` to your system `PATH`
* The build tools are written in python. Check `${IDF_PATH}/requirements.txt` for details
* I think I also installed the `ninja` build tool per the Get Started directions. That might be a thing also.

Assuming all that is done you should be able to cd into one of the firmware directories (right now that means the `playa` directory) and run `idf.py build`.

However, right now I don't understand why the spiffs storage.bin file doesn't build. To build this manually in the playa directory, from the `playa\build` directory you can run `ninja spiffs_storage_bin` or possibly `make spiffs_storage_bin` depending on which build system you are using. *Update: as of 2019-05-21 it seems that whatever was causing an issue where this spiffs file wasn't getting built has been corrected so this paragraph probably doesn't apply. Yay fast moving open source...*

Flashing the Board
==================

The Brain boards use the CH340T USB to UART serial chip. This means that when you connect the board to your machine via the micro USB connector, your machine will see this chip and if all goes well will present it as a serial port in your operating system. How this happens is going to be OS dependent.

Speaking about Macs: In the olden days of 2018, you needed to find a driver for this thing and install it. There seems to be evidence that as of Mojave, maybe this driver is already included and with a stock Apple system you're going to be okay. We probably need to understand this a little bit better. Also, it seems that if you had installed the driver in the Sierra days and still have it on your machine, weirdness is going to happen and you won't be able to actually flash the board.

But why would you have installed it previously? Well, the CH340T or it's predecessor has been a pretty common chip for various "maker"-ish boards out there. I installed the drivers because of previous things I did with ESP 8266 based boards like the WeMOS D1 and clone boards. So anyway, it's 2019, we have Mojave, how do we nuke the old driver?

Here is a [webpage](https://sparks.gogo.co.nz/ch340.html) with some information that might help, look at the bottom of it. You're going to `rm -rf` a Mac kernel extension and then a couple of receipts files. Yes this is dark magic Mac driver things you shouldn't ever have to know about, but most of you aren't in this situation are you and it all just worked magically. Well aren't you amazing for having a machine that hasn't been corrupted by the the dark codes of past times?

For the rest of me, now it's time for a reboot because deleting files from disk doesn't remove them from the running kernel on my Mac. After a reboot - Yay! 

On my Mac that is fairly stock at 10.14.5 Mojave (although upgraded over the years) after nuking the older driver doing a `ls /dev/tty.*` results in

    /dev/tty.Bluetooth-Incoming-Port /dev/tty.usbserial-142120

The Bluetooh thing is ignorable, and the `tty.usbserial-142120` seems to be our happy happy, the driver is magically already there, serial port that the idf.py flasher will find on it's own.

So now, presuming the build has worked (i.e. deal with any errors or warnings. We're running a clean codebase here so if you have any, then something isn't right and you really can't proceed until you figure out what's going on), you should be able to magically flash the firmware to the brain using the IDF tool. So

    idf.py flash

That should/maybe/might get the firmware down to the board. If that step works, you'll be ready to do 

    idf.py monitor

to get the serial console back from the board. This console is going to include debuggging output from both our code and the Espressif IDF code. Look at `main.cpp` in the `playa/main` directory to adjust how much debugging output you want to see on the console.

From here you hopefully have a working board in front of you. Do magic with it!
