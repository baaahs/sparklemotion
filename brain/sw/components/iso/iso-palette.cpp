//
// Created by Tom Seago on 12/30/19.
//

#include "iso-palette.h"
#include <math.h>

IsoPalette::IsoPalette(uint8_t len, const RgbColor *values) :
    m_len(len), m_values(values)
{

}

RgbColor
IsoPalette::colorInPalette(float progress) {
    if (progress > 1.0f) {
        float iPart;
        progress = modf(progress, &iPart);
    }

    if (progress == 1.0f ) {
        progress = 0.0f;
    }

    float pos = progress * (float)m_len;
    uint8_t lowIx = floor(pos);
    uint8_t highIx = lowIx + 1;
    if (highIx >= m_len) highIx = 0;

    float intervalDistance = pos - (float)lowIx;

    RgbColor color = RgbColor::LinearBlend(m_values[lowIx], m_values[highIx], intervalDistance);

    return color;
}

const RgbColor Palette_RYB[] = {
        RgbColor(255, 0, 0),
        RgbColor(255, 64, 0),
        RgbColor(255, 128, 0),
        RgbColor(255, 191, 0),
        RgbColor(255, 255, 0),
        RgbColor(128, 212, 25),
        RgbColor(0, 168, 51),
        RgbColor(21, 132, 102),
        RgbColor(42, 95, 153),
        RgbColor(85, 48, 140),
        RgbColor(128, 0, 128),
        RgbColor(191, 0, 64),
};

IsoPalette gPaletteRYBRainbow(12, Palette_RYB);
