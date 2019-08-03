#pragma once

#include "shader.h"

class SolidShader : public Shader {
    RgbColor m_color;

public:
    // Create a solid shader with a default color
    SolidShader(RgbColor color);

    SolidShader(Surface *surface, Msg *msg);
    ~SolidShader();

    void begin(Msg *pMsg, LEDShaderContext* pCtx) override;
    void apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) override;
    void end() override;
};