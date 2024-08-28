#include "cs-shader.h"

#define TAG TAG_CS

CSFontCircles gCircleFont;
CSFont7Seg g7SegFont;

CSShader::CSShader(uint8_t pixelsPerChar, CSFont& font) :
        m_pixelsPerChar(pixelsPerChar),
        m_font(font)
{
}

CSShader::~CSShader() {
    if (m_pText) {
        delete m_pText;
    }
}


void CSShader::setText(CSText *cText) {
    if (m_pText) {
        delete m_pText;
    }

    m_pText = cText;
}

void
CSShader::beginShade(LEDShaderContext* pCtx) {
    m_pCtx = pCtx;

    if (m_pText) {
        m_pText->updateForTime(m_pCtx->msNow);
    }
}

void
CSShader::Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) {
    uint16_t effectivePixel = indexPixel;

//    static uint16_t breakLength = 2;
//    uint16_t breakAt = 2 * m_pixelsPerChar;
//    if (effectivePixel >= breakAt) {
//        if (effectivePixel < breakAt + breakLength) {
//            // It is the breaking area, probably a colon
//            color[0] = 0;
//            color[1] = 0;
//            color[2] = 255;
//            return;
//        }
//        effectivePixel -= breakLength;
//    }

    static const uint8_t numChars = 4;

    uint8_t charIndex = effectivePixel / m_pixelsPerChar;
    uint8_t pixelIndex = effectivePixel % m_pixelsPerChar;

    char c = 0;
    if (m_pText) {
        c = m_pText->charAt(charIndex);
//        ESP_LOGI(TAG, "charIndex = %d   c=%c", charIndex, c);
    }

    if (!c || !m_font.isOn(c, pixelIndex)) {
        memset((void*)color, 0, 3);
    }
}

void
CSShader::endShade() {
    m_pCtx = nullptr;
}