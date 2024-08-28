//
// Created by Tom Seago on 12/30/19.
//

#pragma once

#include "cs-shader.h"

class CSEvilClock : public CSShader {
public:
    CSEvilClock();

    void beginShade(LEDShaderContext* pCtx) override;
};