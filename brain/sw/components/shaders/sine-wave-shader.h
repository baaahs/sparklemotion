#pragma once

#include "shader.h"

class SineWaveShader : public Shader {
    LEDShaderContext* m_pCtx;
    Color m_color;
    float m_theta;
    float m_density;

public:
    SineWaveShader(Surface *surface, Msg *msg);
    ~SineWaveShader();

    void begin(Msg *pMsg, LEDShaderContext* pCtx) override;
    Color apply(uint16_t pixelIndex) override;
    void end() override;
};