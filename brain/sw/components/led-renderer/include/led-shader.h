//
// Created by Tom Seago on 2019-06-18.
//

#pragma once

#include "time-base.h"

/**
 * An LEDShaderContext pointer is passed to the current LEDShader at the beginning of
 * the shading cycle. In particular it contains timing information about the frame that
 * is currently being rendered. Shaders should not themselves be inspecting the system
 * time, but need to use the information passed in via the shader context so that the
 * underlying infrastructure can do things like predictively calculate future frames
 * if it so desires.
 */
struct LEDShaderContext {
    uint16_t numPixels;
    float progress;

    // This braintime thing is broken. Use the tvNow field if you
    // want anything to run very long
    braintime_t now;

    timeval tvNow;
    uint32_t msNow;
};

/**
 * An instance of LEDShader is what drives the output to the pixel buffer.
 * At the start of frame rendering Begin() will be called. This will be
 * followed by several calls to Apply(...) during which the LEDShader is
 * responsible for generating the output color for a given pixel. After
 * that End() is called to complete the rendering cycle for a single frame.
 *
 * This algorithm could potentially be optimized to avoid the "function call
 * per pixel" cost, but let's wait and see if that makes sense or not.
 * Conceptually this more granular approach might let us even run across
 * both cores though, so let's keep this as long as we can.
 */
class LEDShader {
public:
    virtual void beginShade(LEDShaderContext* pCtx) = 0;
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) = 0;
    virtual void endShade() = 0;

    bool m_enabled = true;
};