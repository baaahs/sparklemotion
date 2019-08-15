//
// Created by Tom Seago on 2019-06-20.
//

#pragma once

#include <driver/ledc.h>

class BrainLed {
public:
    BrainLed(ledc_channel_t channel, gpio_num_t gpio, ledc_mode_t speed = LEDC_HIGH_SPEED_MODE);

    void start();
    void setValue(uint8_t val);
    uint8_t getValue() { return m_val; }

    uint8_t incrementBy(uint8_t val);
private:
    ledc_channel_config_t m_config;
    uint8_t m_val;

    static bool initDone;
    static void checkInit();
};


