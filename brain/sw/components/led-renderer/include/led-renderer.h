
#define ARDUINO_ARCH_ESP32
#include "NeoPixelBrightnessBus.h"

class LEDRenderer {
public:
    LEDRenderer();

    void start();
    NeoPixelBrightnessBus<NeoGrbFeature, NeoEsp32I2s0Ws2812xMethod> m_pixels;

    void _task();
};
