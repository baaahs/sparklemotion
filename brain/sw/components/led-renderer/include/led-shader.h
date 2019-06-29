//
// Created by Tom Seago on 2019-06-18.
//

#pragma once

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
    virtual void beginShade(float progress) = 0;
    virtual void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) = 0;
    virtual void endShade() = 0;
};