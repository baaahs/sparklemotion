# TempMon

The temperature (and humidity) monitor device. This is generally setup
for a ESP-Pico but of course should be fine on many devices. It was originally
begun from the BAAAHS Brain sparklemotion code with some other LED stuff 
thrown it. Principally what has been added (or will be) is DHT environment
sensor, a SSD1306 Screen with some sort of display manager thing, and then
eventually some actual control code.

Things that need to be done in roughly a priority order:

  * Add a display manager thing with widgets
  * Start the structure of the actual app code which will read from the temperature (listen to those events) and then update some temperature widget (and a humidity one??)
  * Add a menu widget with up/down and select as minimum events, back being optional
  * Some other fun widgets? Last log line? Other sysmon stuff?
  * Get the rotary encoder code working with the menu
  * (maybe) do buttons instead of the rotary encoder??
  * Decide about making AP and ETH code work?
  * (maybe) make a web interface that mimics the on-device interface? Like a UI and control event inputs maybe?
  * Write driver code for control relays
  * Hook the control loop stuff into the control stuff
  * Log env data to disk for a fixed time? Use rrdtool format maybe? Perhaps do something lame to begin with and then graduate
  * Do some physical design for the parts
  * Implement a character mode version of the menu??

## Local Config

Configuration parameters are now read from SPIFFS at boot from a file named `config.json`. The JSON parsing might be a little fragile so don't get to crazy.

The default values, key names, and parsing can be found in `brain_config.h` in the `brain_common` component.

Example config file

    {
      "sta_ssid": "Haus",
      "sta_pass": "SuperSecret"
    }

Parameters that can be set

   - `sta_ssid` - The SSID for the wifi station interface. Default = `sparklemotion`
   - `sta_pass` - The password for the wifi station interface. Default = `sparklemotion`
   

## TODOs

TODO: Check all code which is called in a timer. I'm seeing stack overflows in the timer service, and a quick google shows that it only has a 512 byte stack so a regular printf will overflow it.

Update on that TODO - I've changed the size of the high resolution timer task via menuconfig to be 10k, so maybe that fixes it. Knowing what is called from a timer is still a good plan though.


