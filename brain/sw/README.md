Firmware Directories
====================

`playa` is the first and main firmware intended for use at the 2019 burn.

`components` contains various components that are useful in the mix and match world of
applications supported by the firmware.

This directory structure is likely to evolve over time.

Build Environment Setup
=======================

The basic thing is to follow the [Get Started (CMake)](https://docs.espressif.com/projects/esp-idf/en/latest/get-started-cmake/index.html) section of the Espressif ESP-IDF.

The highlights are 
* Previously you would have installed a pre-built version of the xtensa toolchain. In the page referenced above you will need to click on a link to go to a second page with detailed instructions for your OS: Mac, Windows, or Linux. Follow the instructions. I think the xtensa toolchain is now installed/compiled when you run the `install` script mentioned on the Get Started page. Haven't had a chance to test this though.
* We're using the latest version on master until we find a reason not to. The bleeding edge is where it's at. Basically this is v4. We're also using the CMake build system which is officially in Preview at the moment, but again, why not? See below for info about staying up to date.
* Clone the IDF into a suitable directory. Their examples use `~/esp/esp-idf` so unless you are particular about your machine that's probably a reasonable thing to do.
* Note that they use git submodules so you either need to use `--recursive` when you clone the repo (recommended) or you need to do a `git submodule update --init` after you have cloned it.
* The `IDF_PATH` environment variable needs to be set to the root of the idf.
* You also want to add `IDF_PATH/tools` to your system `PATH`
* The build tools are written in python. Check `${IDF_PATH}/requirements.txt` for details
* I think I also installed the `ninja` build tool per the Get Started directions. That might be a thing also.
* They recommend installing `ccache` - I have little to say about whether this is awesome or lame. I'm pretty sure I installed it.

Assuming all that is done you should be able to cd into one of the firmware directories (right now that means the `playa` directory) and run `idf.py build`.

On an earlier version of the v4 master branch the `storage.bin` file which contains the SPIFFS filesystem image which is flashed to the board wasn't building. The solution was to cd into `playa\build` and run either `ninja spiffs_storage_bin` or `make spiffs_storage_bin` depending on which build system you were using. As of 2019-05-21 it seems that whatever was causing an issue is now fixed so if you are up to date with the IDF this should just build automagically. For the moment I'm leaving these instructions here but I don't know, may delete later.

Updating the IDF
================

**NEW INSTRUCTIONS!** Run this:

    ./brain/sw/sync-idf.sh

**OLD INSTRUCTIONS** are below:

If you are updating from a version of the IDF prior to about July 1 you will have installed the xtensa toolchain manually in something like `~/esp/xtensa-esp32-elf` and will have likely added it to your path. *You need to remove this old method of installing the toolchain.* Renaming or deleting this directory should be fine. You may also want to cleanup your `.profile` or `.bashrc` script.

The IDF now includes a `install.sh` script which installs and updates the toolchain. This script also generates an `export.sh` file which should be sourced to setup appropriate paths. As a result of these changes my `.profile` now contains the following lines related to the IDF:

    ######
    # For ESP32 development
    export IDF_PATH=~/esp/esp-idf
    . ${IDF_PATH}/export.sh > /dev/null
    ######

Not that the `export.sh` file both needs to be sourced, hence the leading `.` and it generates a bunch of garbage that I don't want to see everytime I open a new shell, hence the redirection to `/dev/null`.

Since we are using a clone of master you need to do the following to stay up to date with the core IDF. (Note that this assumes Mac or Linux and the default installation location of your home directory.)

    cd ~/esp/esp-idf
    git pull
    git submodule update --init --recursive
    ./install.sh

Because of the vagaries of the universe you may periodically have to entirely remove the IDF and re-install it though. When v4 is released we'll probably stabilize on that version for some period of time, but for now it seems like we need to stay in this world.

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

OTA with Pinky
==============

Brain should've been flashed once already with `idf.py flash`.

Run:

    cd brain/sw/playa
    idf.py app
    ./deploy_firmware.sh