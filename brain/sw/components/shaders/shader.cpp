//
// Created by Tom Seago on 2019-06-18.
//

#include "include/shader.h"

#include "solid-shader.h"
#include "compositor-shader.h"
#include "sine-wave-shader.h"
#include "pixel-shader.h"

/**
 * Creates a shader based on the given configuration bytes.
 *
 * On return, `config`'s position will be moved past this shader's configuration bytes.
 *
 * If the requested shader is of an unknown type, or it can't be allocated, `nullptr`
 * will be returned and subsequent shaders in the tree will probably be invalid.
 *
 * @param surface The surface the shader will be shading.
 * @param config A buffer containing the specification for the shader.
 * @return The shader, or `nullptr` if the shader couldn't be created.
 */
Shader *
Shader::createShaderFromDescrip(Surface *surface, Msg *config) {
    if (!config->available(1)) return nullptr;

    uint8_t shaderType = config->readByte();

    switch(shaderType) {
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
