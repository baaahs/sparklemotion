//
// Created by Tom Seago on 1/26/20.
//

#pragma once

#include "brain_common.h"
#include "led-shader.h"

#include "iso-control-state.h"

class IsoShaderManual : public LEDShader {
public:
    IsoShaderManual(IsoControlState& state);

    virtual void beginShade(LEDShaderContext* pCtx);
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor);
    virtual void endShade();

    uint16_t colorCount() { return m_count; }
    void setColorCount(uint16_t count);

    void getColor(int16_t index, uint8_t* value);
    void setColor(int16_t index, uint8_t* value);

private:
    IsoControlState &m_state;
    LEDShaderContext *m_pCtx;

    float m_progress;
    uint16_t m_count = 0;

    uint8_t* m_colors = nullptr;
};