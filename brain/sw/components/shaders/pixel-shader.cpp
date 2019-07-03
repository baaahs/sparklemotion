#include "pixel-shader.h"

#include "esp_log.h"

#define TAG "#shPixl"

PixelShader::PixelShader(Surface *surface, Msg *config) : Shader(surface, config) {
    if (!config->available(1)) {
        ESP_LOGE(TAG, "invalid config");
        m_disabled = 1;
    }

    m_encoding = static_cast<Encoding>(config->readByte());
    m_dataBufSize = bufferSizeFor(m_encoding, surface->pixelCount());
    m_dataBuf = static_cast<uint8_t *>(malloc(m_dataBufSize));

    uint8_t paletteCount = paletteCountFor(m_encoding);
    size_t paletteSize = sizeof(Color) * paletteCount;
    m_palette = paletteCount > 0 ? static_cast<Color*>(malloc(paletteSize)) : nullptr;

    if (!m_dataBuf || !m_palette) {
        if (m_dataBuf) {
            ESP_LOGE(TAG, "Failed to malloc %d bytes for data buffer", m_dataBufSize);
            free(m_dataBuf);
        }
        if (m_palette) {
            ESP_LOGE(TAG, "Failed to malloc %d bytes for palette", m_dataBufSize);
            free(m_palette);
        }
        m_disabled = 1;
    }
}

PixelShader::~PixelShader() {
    if (m_dataBuf) free(m_dataBuf);
    if (m_palette) free(m_palette);
}

void
PixelShader::begin(Msg *pMsg, LEDShaderContext* pCtx) {
    pMsg->readShort(); // ignore pixel count

    uint8_t paletteCount = paletteCountFor(m_encoding);
    for (int i = 0; i < paletteCount; i++) {
        const RgbColor color = pMsg->readColor();
        m_palette[i].channel.a = 0xff;
        m_palette[i].channel.r = color.R;
        m_palette[i].channel.g = color.G;
        m_palette[i].channel.b = color.B;
    }

    size_t dataBufLen = pMsg->readInt();
    if (m_disabled) {
        // We still need to skip bytes in the buffer intended for us before bailing.
        pMsg->skip(dataBufLen);
        return;
    }

    size_t readLen = MIN(m_dataBufSize, dataBufLen);
    m_dataBufSize = pMsg->readBytes(m_dataBuf, readLen);

    // If we received data for more pixels than we have, skip those bytes.
    if (readLen < dataBufLen) {
        pMsg->skip(dataBufLen - readLen);
    }
}

uint8_t
PixelShader::paletteIndex(uint16_t pixelIndex, uint8_t pixelsPerByte, uint8_t bitsPerPixel, uint8_t mask) {
    // Offset is modulo m_dataBufSize so we wrap around and repeat any missing pixels.
    size_t bufOffset = pixelIndex / pixelsPerByte % m_dataBufSize;
    uint8_t positionInByte = pixelsPerByte - pixelIndex % pixelsPerByte - 1;
    uint8_t bitShift = positionInByte * bitsPerPixel;
    uint8_t i = m_dataBuf[bufOffset] >> bitShift & mask;
    return m_dataBuf[bufOffset] >> bitShift & mask;
}

void
PixelShader::apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {
    if (m_disabled) {
        // TODO: colorOut should have alpha channel set explicitly to zero.
        return;
    }

    uint16_t bufOffset;
    Color color;

    switch (m_encoding) {
        case Encoding::DIRECT_ARGB:
            bufOffset = pixelIndex * 4;

            // Offset is modulo m_dataBufSize so we wrap around and repeat any missing pixels.
            color.channel.a = m_dataBuf[bufOffset++ % m_dataBufSize];
            color.channel.r = m_dataBuf[bufOffset++ % m_dataBufSize];
            color.channel.g = m_dataBuf[bufOffset++ % m_dataBufSize];
            color.channel.b = m_dataBuf[bufOffset % m_dataBufSize];
            break;

        case Encoding::DIRECT_RGB:
            bufOffset = pixelIndex * 3 + 1; // skip alpha

            // Offset is modulo m_dataBufSize so we wrap around and repeat any missing pixels.
            color.channel.a = 0xff;
            color.channel.r = m_dataBuf[bufOffset++ % m_dataBufSize];
            color.channel.g = m_dataBuf[bufOffset++ % m_dataBufSize];
            color.channel.b = m_dataBuf[bufOffset % m_dataBufSize];
            break;

        case Encoding::INDEXED_2:
            color = m_palette[paletteIndex(pixelIndex, 8, 1, 0x01)];
            break;

        case Encoding::INDEXED_4:
            color = m_palette[paletteIndex(pixelIndex, 4, 2, 0x03)];
            break;

        case Encoding::INDEXED_16:
            color = m_palette[paletteIndex(pixelIndex, 2, 4, 0x0F)];
            break;
    }

    *colorOut++ = color.channel.r;
    *colorOut++ = color.channel.g;
    *colorOut = color.channel.b;
}

void
PixelShader::end() {
}

size_t PixelShader::bufferSizeFor(PixelShader::Encoding encoding, uint16_t pixelCount) {
    switch (encoding) {
        case Encoding::DIRECT_ARGB:
            return pixelCount * 4;
        case Encoding::DIRECT_RGB:
            return pixelCount * 3;
        case Encoding::INDEXED_2:
            return (pixelCount + 7) / 8;
        case Encoding::INDEXED_4:
            return (pixelCount + 3) / 4;
        case Encoding::INDEXED_16:
            return (pixelCount + 1) / 2;
        default:
            return -1;
    }
}

size_t PixelShader::paletteCountFor(PixelShader::Encoding encoding) {
    switch (encoding) {
        case Encoding::DIRECT_ARGB:
            return 0;
        case Encoding::DIRECT_RGB:
            return 0;
        case Encoding::INDEXED_2:
            return 2;
        case Encoding::INDEXED_4:
            return 4;
        case Encoding::INDEXED_16:
            return 16;
        default:
            return -1;
    }
}
