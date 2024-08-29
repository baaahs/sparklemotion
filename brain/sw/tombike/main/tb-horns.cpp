//
// Created by Tom Seago on 8/28/24.
//

#include "tb-horns.h"

TBHorns::TBHorns() {

}


void
TBHorns::beginShade(LEDShaderContext* pCtx) {
    m_pCtx = pCtx;
}

void
TBHorns::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {

    if (indexPixel >= 27 && indexPixel <= 30) {
        // Left horn
        memset((void *) color, 0, 3);
    }

    if (indexPixel == 30) {
        color[0] = 255;
        color[1] = 0;
        color[2] = 0;
    }

    if (indexPixel == 62) {
        // Right horn
        color[0] = 0;
        color[1] = 255;
        color[2] = 0;
    }

    if (indexPixel >= 63 && indexPixel <= 65) {
        // Right horn
        memset((void*)color, 0, 3);
    }
}

void
TBHorns::endShade() {
    m_pCtx = nullptr;
}