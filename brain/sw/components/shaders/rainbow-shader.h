#pragma once

#include "shader.h"

class RainbowShader : public Shader {
    LEDShaderContext* m_pCtx;

public:
    RainbowShader(Surface* pSurface, Msg* config);
    ~RainbowShader();

    void begin(Msg *pMsg, LEDShaderContext* pCtx) override;
    void apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) override;
    void end() override;

    bool m_pastel;
};