#include "iso-shader-solid.h"

IsoShaderSolid::IsoShaderSolid(IsoControlState& state, uint8_t selection) :
    m_state(state),
    m_selection(selection)
{

}

void
IsoShaderSolid::beginShade(LEDShaderContext* pCtx) {
    m_pCtx = pCtx;
}

void
IsoShaderSolid::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    RgbColor primary = m_state.chosenColor(m_selection);
    memcpy((void*)color, (void*)&primary, 3);
}

void
IsoShaderSolid::endShade() {
    m_pCtx = nullptr;
}