#include "rainbow-shader.h"

#include "esp_log.h"
#define TAG "#shader"

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
#define RYB_LEN 12

RgbColor
colorInPalette(float progress) {
    if (progress > 1.0f) {
        float iPart;
        progress = modf(progress, &iPart);
    }

    if (progress == 1.0f ) {
        // Serial.print("progress="); Serial.print(progress, 3);
        // Serial.print(" Resetting to 0----\n");
        progress = 0.0f;
    }

    float pos = progress * (float)RYB_LEN;
    uint8_t lowIx = floor(pos);
    uint8_t highIx = lowIx + 1;
    if (highIx >= RYB_LEN) highIx = 0;

    float intervalDistance = pos - (float)lowIx;

    // Serial.print("progress="); Serial.print(progress, 3);
    // Serial.print(" pos="); Serial.print(pos,3);
    // Serial.print(" idist="); Serial.print(intervalDistance,3);
    // Serial.printf(" lowIx=%d, highIx=%d", lowIx, highIx);

    RgbColor color = RgbColor::LinearBlend(Palette_RYB[lowIx], Palette_RYB[highIx], intervalDistance);

    // Serial.printf(" ==> (%3d, %3d, %3d)\n", color.R, color.G, color.B);
    return color;
}

RainbowShader::RainbowShader(uint8_t** ppCursor, uint8_t* pEnd) {
    // No additional bytes of configuration
    m_color = RgbColor(32, 32, 0);
}

RainbowShader::~RainbowShader() {

}

void
RainbowShader::begin(Msg *pMsg, float progress) {
    m_progress = progress;

    if (!pMsg) return;

    auto argb = pMsg->readInt();

    // ESP_LOGD(TAG, "argb = %x", argb);

    m_color.R = (argb >> 16) & 0xff;
    m_color.G = (argb >>  8) & 0xff;
    m_color.B = (argb      ) & 0xff;
}

void
RainbowShader::apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {
//    memcpy((void*)colorOut, (void*)&m_color, 3);
    float paletteProgress = m_progress + (pixelIndex / 32.0f);
    if (paletteProgress > 1.0f) {
        paletteProgress -= 1.0f;
    }
    // ESP_LOGW(TAG, "progress=%f  paletteProgress=%f", m_progress, paletteProgress);
    RgbColor color = colorInPalette(paletteProgress);

    memcpy((void*)colorOut, (void*)&color, 3);
}

void
RainbowShader::end() {

}
