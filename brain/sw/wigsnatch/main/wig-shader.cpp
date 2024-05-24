//
// Created by Tom Seago on 5/21/24.
//

#include "wig-shader.h"

WigShader::WigShader(IsoControlState& state) :
        m_state(state)
{

}

void
WigShader::beginShade(LEDShaderContext* pCtx) {
    m_pCtx = pCtx;
    m_progress = m_state.m_masterAnimationLoop.progress(m_pCtx->now);
}

void
WigShader::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {

    // If it's the top don't do the maths right now
    if (indexPixel>=136) {
        m_legIndex = -1;
        m_sideIndex = -1;
        return;
    }

    // Just update the internal geometry values
    uint8_t sectionIx = indexPixel / (19 + 15);
    uint8_t offset = indexPixel % (19 + 15);

    if (offset < 19) {
        // A leg
        m_legIndex = sectionIx;
        m_sideIndex = -1;

        m_y = -9 + offset;
        switch (m_legIndex) {
            case 0:
                m_x = 8;
                m_z = 8;
                break;

            case 1:
                m_x = -8;
                m_z = 8;
                break;

            case 2:
                m_x = -8;
                m_z = -8;
                break;

            case 3:
                m_x = 8;
                m_z = -8;
                break;

            default:
                break;
        }
    } else {
        // A side
        offset -= 19; // Get rid of the leg portion
        m_legIndex = -1;
        m_sideIndex = sectionIx;

        m_y = 10;
        switch (m_sideIndex) {
            case 0:
                m_x = 8 - offset;
                m_z = 8;
                break;

            case 1:
                m_x = -8;
                m_z = 8 - offset;
                break;

            case 2:
                m_x = -8 + offset;
                m_z = -9;
                break;

            case 3:
                m_x = 8;
                m_z = -8 + offset;
                break;

            default:
                break;
        }
    }

    // TODO: x, y, z
    // Origin is in center of the box

}

void
WigShader::endShade() {
    m_pCtx = nullptr;
}