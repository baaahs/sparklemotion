#pragma once

#include "shader.h"

class SineWaveShader : public Shader {
    RgbColor m_color;
    float m_theta;
    float m_density;

public:
    SineWaveShader(Surface *surface, Msg *msg);
    ~SineWaveShader();

    void begin(Msg* pMsg) override;
    void apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) override;
    void end() override;
};