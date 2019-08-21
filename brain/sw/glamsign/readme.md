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

