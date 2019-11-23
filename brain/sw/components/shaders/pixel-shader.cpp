#include "pixel-shader.h"

#include "esp_log.h"
#include "brain_common.h"
#include "sysmon.h"

#define TAG TAG_SHADER

PixelShader::PixelShader(Surface *surface, Msg *config) : Shader(surface, config) {
    if (!config->available(1)) {
        ESP_LOGE(TAG, "invalid config");
        m_disabled = 1;
    }

    m_encoding = static_cast<Encoding>(config->readByte());
    uint16_t pixelCount = surface->pixelCount();
    m_dataBufSize = bufferSizeFor(m_encoding, pixelCount);
    m_dataBuf = static_cast<uint8_t *>(malloc(m_dataBufSize));

    uint8_t paletteCount = paletteCountFor(m_encoding);
    size_t paletteSize = sizeof(Color) * paletteCount;
    m_palette = paletteCount > 0 ? static_cast<Color*>(malloc(paletteSize)) : nullptr;

    ESP_LOGD(TAG, "PixelShader: encoding=%d pixelCount=%d paletteCount=%d",
            static_cast<int>(m_encoding), pixelCount, paletteCount);

    if (!m_dataBuf || (paletteSize > 0 && !m_palette)) {
        if (m_dataBuf) {
            free(m_dataBuf);
            m_dataBuf = nullptr;
        } else {
            ESP_LOGE(TAG, "Failed to malloc %d bytes for data buffer", m_dataBufSize);
        }

        if (paletteSize > 0) {
            if (m_palette) {
                free(m_palette);
                m_palette = nullptr;
            } else {
                ESP_LOGE(TAG, "Failed to malloc %d bytes for palette", paletteSize);
            }
        }
        m_disabled = 1;
    }
}

PixelShader::~PixelShader() {
    if (m_dataBuf) {
        free(m_dataBuf);
        m_dataBuf = nullptr;
    }
    if (m_palette) {
        free(m_palette);
        m_palette = nullptr;
    }
}

void
PixelShader::begin(Msg *pMsg, LEDShaderContext* pCtx) {
    uint16_t pixelCount = pMsg->readShort();

    uint8_t paletteCount = paletteCountFor(m_encoding);
    for (int i = 0; i < paletteCount; i++) {
        const RgbColor color = pMsg->readColor();
        if (!m_disabled) {
            m_palette[i].channel.a = 0xff;
            m_palette[i].channel.r = color.R;
            m_palette[i].channel.g = color.G;
            m_palette[i].channel.b = color.B;
        }
    }

    size_t dataBufLen = bufferSizeFor(m_encoding, pixelCount);
    if (m_disabled) {
        // We still need to skip bytes in the buffer intended for us before bailing.
        pMsg->skip(dataBufLen);
        return;
    }

    size_t readLen = MIN(m_dataBufSize, dataBufLen);
    m_dataBufRead = pMsg->readBytes(m_dataBuf, readLen);

    ESP_LOGI(TAG, "PixelShader::begin m_dataBufSize=%d dataBufLen=%d m_dataBufRead=%d readLen=%d",
            m_dataBufSize, dataBufLen, m_dataBufRead, readLen);
    // If we received data for more pixels than we have, skip those bytes.
    if (readLen < m_dataBufRead) {
        pMsg->skip(m_dataBufRead - readLen);
    }

    m_pixelsToShade = pCtx->numPixels;
    m_pixelsShaded = 0;
}

uint8_t
PixelShader::paletteIndex(uint16_t pixelIndex, uint8_t pixelsPerByte, uint8_t bitsPerPixel, uint8_t mask) {
    // Offset is modulo m_dataBufRead so we wrap around and repeat any missing pixels.
    size_t bufOffset = pixelIndex / pixelsPerByte % m_dataBufRead;
    uint8_t positionInByte = pixelsPerByte - pixelIndex % pixelsPerByte - 1;
    uint8_t bitShift = positionInByte * bitsPerPixel;
    return m_dataBuf[bufOffset] >> bitShift & mask;
}

void
PixelShader::apply(uint16_t pixelIndex, uint8_t *colorOut, uint8_t *colorIn) {
    if (m_disabled || m_dataBufRead == 0) {
        // TODO: colorOut should have alpha channel set explicitly to zero.
        return;
    }

    uint16_t bufOffset;
    Color color = {0};
    color.channel.r = 255;
    color.channel.b = 255;

    switch (m_encoding) {
        case Encoding::DIRECT_ARGB:
            // Offset is modulo m_dataBufRead so we wrap around and repeat any missing pixels.
            bufOffset = pixelIndex * 4;
            if (bufOffset + 3 < m_dataBufRead) {
                color.channel.a = m_dataBuf[bufOffset++];
                color.channel.r = m_dataBuf[bufOffset++];
                color.channel.g = m_dataBuf[bufOffset++];
                color.channel.b = m_dataBuf[bufOffset];
            } else {
                gSysMon.increment(COUNTER_PIXEL_UNDERFLOW);
            }
            break;

        case Encoding::DIRECT_RGB:
            // Offset is modulo m_dataBufRead so we wrap around and repeat any missing pixels.
            bufOffset = pixelIndex * 3;
            if (bufOffset + 2 < m_dataBufRead) {
                color.channel.a = 0xff;
                color.channel.r = m_dataBuf[bufOffset++];
                color.channel.g = m_dataBuf[bufOffset++];
                color.channel.b = m_dataBuf[bufOffset];
            } else {
                ESP_LOGD(TAG, "Asked to shade pixel %d but my buffer only has up to %d",
                         pixelIndex,
                         (m_dataBufRead-2) / 3);
                gSysMon.increment(COUNTER_PIXEL_UNDERFLOW);
            }
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

    m_pixelsShaded++;
}

void
PixelShader::end() {
//    if (m_pixelsShaded < m_pixelsToShade) {
//        gSysMon.increment(COUNTER_PIXEL_UNDERFLOW);
//        ESP_LOGE(TAG, "Pixel underflow needed to shade %d but only shaded %d", m_pixelsToShade, m_pixelsShaded);
//    }
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
