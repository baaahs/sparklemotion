#pragma once

#include "shader.h"

class RainbowShader : public Shader {
    float m_progress;
    RgbColor m_color;

public:
    RainbowShader(uint8_t** ppCursor, uint8_t* pEnd);
    ~RainbowShader();

    void begin(Msg *pMsg, float progress) override;
    void apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) override;
    void end() override;
};