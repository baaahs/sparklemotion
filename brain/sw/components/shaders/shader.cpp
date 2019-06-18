//
// Created by Tom Seago on 2019-06-18.
//

#include "include/shader.h"

#include "solid-shader.h"
#include "compositor-shader.h"

Shader*
Shader::createShaderFromDescrip(uint8_t* pCursor, uint8_t* pEnd) {
    if (pCursor >= pEnd) return nullptr;

    switch(*pCursor) {
        case static_cast<int>(Shader::Type::SOLID):
            return new SolidShader(pCursor+1, pEnd);

        case static_cast<int>(Shader::Type::COMPOSITOR):
            return new CompositorShader(pCursor+1, pEnd);
    }

    // Failsafe
    return nullptr;
}
