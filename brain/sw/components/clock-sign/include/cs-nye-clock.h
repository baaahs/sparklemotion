//
// Created by Tom Seago on 12/30/19.
//

#pragma once

#include "cs-shader.h"

class CSNYEClock : public CSShader {
public:
    CSNYEClock();

    void beginShade(LEDShaderContext* pCtx) override;
};