//
// Created by Tom Seago on 12/30/19.
//

#include "iso-shader-list.h"

void
IsoShaderList::beginShade(LEDShaderContext* pCtx) {
    LEDShader* pCur = current();
    if (!pCur) return;

    // Maybe advance?
    if (m_maxShaderTime) {
        if (!m_nextNextAt) {
            m_nextNextAt = pCtx->msNow + m_maxShaderTime;
        } else if (pCtx->msNow > m_nextNextAt) {
            pCur = next();
            while (pCtx->msNow > m_nextNextAt) {
                m_nextNextAt += m_maxShaderTime;
            }
        }
    }

    pCur->beginShade(pCtx);
}

void
IsoShaderList::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    LEDShader* pCur = current();
    if (!pCur) return;

    pCur->Apply(indexPixel, color, currentColor);
}

void
IsoShaderList::endShade() {
    LEDShader* pCur = current();
    if (!pCur) return;

    pCur->endShade();
}