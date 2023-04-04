#include "rainbow-shader.h"

#include "esp_log.h"
#include "math.h"

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

// This is a "Red" palette that is useful for making sure you don't have
// an Grb vs. Rgb color swap issue occurring
//const RgbColor Palette_RYB[] = {
//        RgbColor(255, 0, 0),
//        RgbColor(255, 0, 0),
//        RgbColor(255, 0, 0),
//        RgbColor(255, 0, 0),
//        RgbColor(255, 0, 0),
//        RgbColor(128, 0, 25),
//        RgbColor(0, 0, 51),
//        RgbColor(21, 0, 102),
//        RgbColor(42, 0, 153),
//        RgbColor(85, 0, 140),
//        RgbColor(128, 0, 128),
//        RgbColor(191, 0, 64),
//};
#define RYB_LEN 12

RgbColor
colorInPalette(float progress) {
    if (progress > 1.0f) {
        float iPart;
        progress = modf(progress, &iPart);
    }

    if (progress == 1.0f ) {
        progress = 0.0f;
    }

    float pos = progress * (float)RYB_LEN;
    uint8_t lowIx = floor(pos);
    uint8_t highIx = lowIx + 1;
    if (highIx >= RYB_LEN) highIx = 0;

    float intervalDistance = pos - (float)lowIx;

    RgbColor color = RgbColor::LinearBlend(Palette_RYB[lowIx], Palette_RYB[highIx], intervalDistance);

    return color;
}

RainbowShader::RainbowShader(Surface* surface, Msg* config) : Shader(surface, config) {
    // No additional bytes of configuration
}

RainbowShader::~RainbowShader() {

}

void
RainbowShader::begin(Msg *pMsg, LEDShaderContext* pCtx) {
    m_pCtx = pCtx;
}

void
RainbowShader::apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {
//    memcpy((void*)colorOut, (void*)&m_color, 3);
    float paletteProgress = m_pCtx->progress + (pixelIndex / (float)m_pCtx->numPixels);
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
