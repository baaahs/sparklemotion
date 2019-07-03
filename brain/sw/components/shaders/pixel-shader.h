#pragma once

#include "shader.h"
#include "../../../../../../../esp/xtensa-esp32-elf/xtensa-esp32-elf/include/c++/8.2.0/cstdint"

class PixelShader : public Shader {
    enum class Encoding : uint8_t {
        DIRECT_ARGB = 0,
        DIRECT_RGB,
        INDEXED_2,
        INDEXED_4,
        INDEXED_16
    };

private:
    Encoding m_encoding;
    Color *m_palette;
    size_t m_dataBufSize;
    uint8_t *m_dataBuf;

    /** Shader construction failed, we'll render nothing. */
    bool m_disabled = false;

    size_t bufferSizeFor(PixelShader::Encoding encoding, uint16_t pixelCount);

    size_t paletteCountFor(PixelShader::Encoding encoding);

    uint8_t paletteIndex(uint16_t pixelIndex, uint8_t pixelsPerByte, uint8_t bitsPerPixel, uint8_t mask);

public:
    PixelShader(Surface *surface, Msg *pMsg);

    ~PixelShader();

    void begin(Msg *pMsg) override;

    void apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) override;

    void end() override;
};
