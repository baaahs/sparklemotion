#pragma once

#include <inttypes.h>

typedef struct GammaData {
    uint8_t value;  // The gamma-corrected value, rounded down.
    uint8_t dither; // Bit-packed temporal dithering data; for a 1-bit we add 1 to value on that frame.
} GammaData;

class Gamma {
public:
    static uint8_t Correct22NoDither(uint8_t value);
    static uint8_t Correct22(uint8_t value, uint32_t frameNumber, uint32_t pixelIndex);
};