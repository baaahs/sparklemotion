//
// Created by Tom Seago on 2019-06-18.
//

#include "include/shader.h"

#include "solid-shader.h"
#include "compositor-shader.h"
#include "sine-wave-shader.h"

Shader*
Shader::createShaderFromDescrip(uint8_t** ppCursor, uint8_t* pEnd) {
    if (*ppCursor >= pEnd) return nullptr;

    uint8_t last = **ppCursor;
    *ppCursor += 1;

    switch(last) {
        case static_cast<int>(Shader::Type::SOLID):
            return new SolidShader(ppCursor, pEnd);

        case static_cast<int>(Shader::Type::COMPOSITOR):
            return new CompositorShader(ppCursor, pEnd);

        case static_cast<int>(Shader::Type::SINE_WAVE):
            return new SineWaveShader(ppCursor, pEnd);
    }

    // Failsafe
    return nullptr;
}
