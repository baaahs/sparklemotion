//
// Created by Tom Seago on 2019-06-18.
//

#include "include/shader.h"

#include "solid-shader.h"
#include "compositor-shader.h"
#include "sine-wave-shader.h"
#include "pixel-shader.h"

Shader *
Shader::createShaderFromDescrip(Surface *surface, Msg *config) {
    if (!config->available(1)) return nullptr;

    uint8_t last = config->readByte();

    switch(last) {
        case static_cast<int>(Shader::Type::SOLID):
            return new SolidShader(surface, config);

        case static_cast<int>(Shader::Type::PIXEL):
            return new PixelShader(surface, config);

        case static_cast<int>(Shader::Type::SINE_WAVE):
            return new SineWaveShader(surface, config);

        case static_cast<int>(Shader::Type::COMPOSITOR):
            return new CompositorShader(surface, config);

//        case static_cast<int>(Shader::Type::SPARKLE):
//            return new NoOpShader(surface, config);
//
//        case static_cast<int>(Shader::Type::SIMPLE_SPATIAL):
//            return new NoOpShader(surface, config);
//
//        case static_cast<int>(Shader::Type::HEART):
//            return new NoOpShader(surface, config);
//
//        case static_cast<int>(Shader::Type::RANDOM):
//            return new NoOpShader(surface, config);
    }

    // Failsafe
    return nullptr;
}
