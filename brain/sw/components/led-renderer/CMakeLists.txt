set(COMPONENT_ADD_INCLUDEDIRS "include" "neopixel")

set(COMPONENT_SRCS "led-renderer.cpp" "RgbColor.cpp"
        )

set(COMPONENT_REQUIRES nvs_flash sysmon led_strip)

register_component()
