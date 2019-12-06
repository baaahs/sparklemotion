#pragma once

#include "shader.h"

class RainbowShader : public Shader {
    LEDShaderContext* m_pCtx;

public:
    RainbowShader(Surface* pSurface, Msg* config);
    ~RainbowShader();

    void begin(Msg *pMsg, LEDShaderContext* pCtx) override;
    Color apply(uint16_t pixelIndex) override;
    void end() override;
};