#pragma once

#include "brain_common.h"
#include "led-shader.h"

#include "iso-control-state.h"

#include "cs-font.h"
#include "cs-text.h"

extern CSFontCircles gCircleFont;
extern CSFont7Seg g7SegFont;

/**
 * The CSShader will use turn off pixels as defined by a CSFont.
 *
 * The idea is that other shaders have already rendered into the buffer
 * an interesting color - like say a solid color or a rainbow or whatever -
 * and then this shader comes along and renders text using negative space
 * by turning off pixels.
 *
 * The fonts need to be aligned with the physical layout of course
 * and we need to know how many pixels there are per character. All
 * characters need to be the same and need to be in sequence.
 */
class CSShader : public LEDShader {
public:
    CSShader(uint8_t pixelsPerChar = 24, CSFont& font = gCircleFont);
    ~CSShader();

    /**
     * Sets the text that this shader will display. The shader takes
     * ownership of the passed in object and will free it when something
     * comes along and replaces it.
     *
     * @param cText
     */
    void setText(CSText* cText);

    virtual void beginShade(LEDShaderContext* pCtx);
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor);
    virtual void endShade();

private:
    LEDShaderContext *m_pCtx;

    uint8_t m_pixelsPerChar;
    CSFont& m_font;

    CSText* m_pText;
};

