//
// Created by Tom Seago on 12/30/19.
//

#pragma once

#include "led-renderer.h"

#define MAX_SHADER_LIST_LEN 20

class IsoShaderList : public LEDShader  {
public:
    void push(LEDShader* shader) {
        if (m_len == MAX_SHADER_LIST_LEN) return;

        m_list[m_len++] = shader;
    }

    LEDShader* current() {
        if (!m_len) return nullptr;

        return m_list[m_current];
    }

    LEDShader* next() {
        if (!m_len) return nullptr;

        m_current++;
        if (m_current >= m_len) {
            m_current = 0;
        }

        m_nextNextAt = 0;
        return m_list[m_current];
    }

    void reset() {
        m_current = 0;
    }

    void setMexShaderTime(uint32_t millis) {
        m_maxShaderTime = millis;
    }

    void beginShade(LEDShaderContext* pCtx) override;
    void Apply(uint16_t indexPixel, uint8_t *color, uint8_t *currentColor) override;
    void endShade() override;

private:
    LEDShader* m_list[MAX_SHADER_LIST_LEN];
    uint8_t m_current;
    uint8_t m_len;

    uint32_t m_nextNextAt = 0;
    uint32_t m_maxShaderTime = 5000;
};