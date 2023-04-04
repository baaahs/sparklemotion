#include "iso-shader-rainbow.h"

IsoShaderRainbow::IsoShaderRainbow(IsoControlState& state) :
        m_state(state)
{

}

void
IsoShaderRainbow::beginShade(LEDShaderContext* pCtx) {
    m_pCtx = pCtx;
    m_progress = m_state.m_masterAnimationLoop.progress(m_pCtx->now);
}

void
IsoShaderRainbow::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    float paletteProgress = m_progress + (indexPixel / (float)m_pCtx->numPixels);
    if (paletteProgress > 1.0f) {
        paletteProgress -= 1.0f;
    }

    RgbColor val = m_state.currentPalette().colorInPalette(paletteProgress);
    memcpy((void*)color, (void*)&val, 3);
}

void
IsoShaderRainbow::endShade() {
    m_pCtx = nullptr;
}