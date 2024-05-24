//
// Created by Tom Seago on 5/20/24.
//
#include "shader-rotator.h"
#include <math.h>

ShaderRotator::ShaderRotator(IsoControlState& state) :
        WigShader(state)
{

}

void
ShaderRotator::beginShade(LEDShaderContext* pCtx) {
    WigShader::beginShade(pCtx);

    m_isEven = m_state.m_masterAnimationLoop.count(pCtx->now) % 2;
}

void
ShaderRotator::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    WigShader::Apply(indexPixel, color, currentColor);

//    RgbColor zoneColor = RgbColor(255,0,0);
//
//    if (m_y > 0) {
//        if (m_z > 0) {
//            zoneColor = RgbColor(0,255,0); // Green
//        } else {
//            zoneColor = RgbColor(0,0,255); // Blue
//        }
//    } else {
//        if (m_z > 0) {
//            zoneColor = RgbColor(200, 200, 0);
//        } else {
//            zoneColor = RgbColor(200, 0, 200);
//        }
//    }

    int8_t y = m_y;
    int8_t x = m_x;

    switch(m_state.orientation) {
        case 1:
            y = m_z;
            break;
        case 2:
            x = m_z;
            break;
        default:
            break;
    }


    float pixelAngle = atan2(y, x);

    float divisionAngle = m_progress * M_PI;
    bool swap = false;
    if (y < 0) {
        pixelAngle += M_PI;
        swap = true;
    }
    swap = m_isEven ? swap : !swap;

    RgbColor zoneColor = m_state.chosenColor(swap ? 1 : 0);

    if (pixelAngle > divisionAngle) {
        zoneColor = m_state.chosenColor(swap ? 0 : 1);
    }

    memcpy((void*)color, (void*)&zoneColor, 3);
}

void
ShaderRotator::endShade() {
    WigShader::endShade();
}