#pragma once

#include "led-renderer.h"

class IsoPalette {
public:
    IsoPalette(uint8_t len, const RgbColor* values);

    RgbColor colorInPalette(float progress);

private:
    uint8_t m_len;
    const RgbColor *m_values;
};

extern IsoPalette gPaletteRYBRainbow;