set(COMPONENT_ADD_INCLUDEDIRS "include" "neopixel")

set(COMPONENT_SRCS "led-renderer.cpp"
        "neopixel/internal/Esp32_i2s.c"
        "neopixel/internal/HsbColor.cpp"
        "neopixel/internal/HslColor.cpp"
        "neopixel/internal/HtmlColor.cpp"
        "neopixel/internal/HtmlColorNames.cpp"
        "neopixel/internal/HtmlColorNameStrings.cpp"
        "neopixel/internal/HtmlColorShortNames.cpp"
        "neopixel/internal/NeoGamma.cpp"
        "neopixel/internal/NeoPixelAnimator.cpp"
        "neopixel/internal/NeoPixelEsp.c"
        "neopixel/internal/RgbColor.cpp"
        "neopixel/internal/RgbwColor.cpp"
        "esp32-hal.c"
        "esp32-hal-gpio.c"
        )

set(COMPONENT_REQUIRES nvs_flash)

register_component()
