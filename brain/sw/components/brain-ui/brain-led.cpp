//
// Created by Tom Seago on 2019-06-20.
//

#include "brain-ui-priv.h"
#include "brain-led.h"

bool BrainLed::initDone;

BrainLed::BrainLed(ledc_channel_t channel, int gpio, ledc_mode_t speed) {
    m_config.channel = channel;
    m_config.gpio_num = gpio;
    m_config.speed_mode = speed;
    m_config.duty = 0;
    m_config.hpoint = 0;
    m_config.timer_sel = speed == LEDC_HIGH_SPEED_MODE ? LEDC_TIMER_0 : LEDC_TIMER_1;
}

void
BrainLed::start() {
    BrainLed::checkInit();

    ledc_channel_config(&m_config);
}

void
BrainLed::setValue(uint8_t val) {
    m_val = val;
    ledc_set_duty(m_config.speed_mode, m_config.channel, 256 - val);
    ledc_update_duty(m_config.speed_mode, m_config.channel);
}

void
BrainLed::checkInit() {
    if (BrainLed::initDone) return;

    /*
     * Prepare and set configuration of timers
     * that will be used by LED Controller
     */
    ledc_timer_config_t ledc_timer;
    ledc_timer.duty_resolution = LEDC_TIMER_8_BIT;  // resolution of PWM duty
    ledc_timer.freq_hz = 5000;                      // frequency of PWM signal
    ledc_timer.speed_mode = LEDC_HIGH_SPEED_MODE;   // timer mode
    ledc_timer.timer_num = LEDC_TIMER_0;            // timer index

    // Set configuration of timer0 for high speed channels
    ledc_timer_config(&ledc_timer);

    // Prepare and set configuration of timer1 for low speed channels
    ledc_timer.speed_mode = LEDC_LOW_SPEED_MODE;
    ledc_timer.timer_num = LEDC_TIMER_1;
    ledc_timer_config(&ledc_timer);

    // Initialize fade service.
    ledc_fade_func_install(0);

    ESP_LOGD(TAG, "checkInit completed");

    BrainLed::initDone = true;
}