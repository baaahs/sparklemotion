#include "cs-shader.h"

#define TAG TAG_CS

CSFontCircles gCircleFont;

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
    uint8_t charIndex = indexPixel / m_pixelsPerChar;
    uint8_t pixelIndex = indexPixel % m_pixelsPerChar;

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