#include "iso-shader-solid.h"

IsoShaderSolid::IsoShaderSolid(IsoControlState& state) :
    m_state(state)
{

}

void
IsoShaderSolid::beginShade(LEDShaderContext* pCtx) {
    m_pCtx = pCtx;
}

void
IsoShaderSolid::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    RgbColor primary = m_state.chosenColor(0);
    memcpy((void*)color, (void*)&primary, 3);
}

void
IsoShaderSolid::endShade() {
    m_pCtx = nullptr;
}