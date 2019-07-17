#pragma once

#include "shader.h"

class SolidShader : public Shader {
    RgbColor m_color;

public:
    SolidShader(Surface *surface, Msg *msg);
    ~SolidShader();

    void begin(Msg* pMsg) override;
    void apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) override;
    void end() override;
};