//
// Created by Tom Seago on 2019-07-10.
//

#pragma once

#include <driver/gpio.h>
#include <freertos/FreeRTOS.h>
#include <freertos/portable.h>
#include <freertos/timers.h>

class BrainButton;

class BrainButtonListener {
public:
    virtual void buttonDown(BrainButton& sw, bool longPress) = 0;
    virtual void buttonUp(BrainButton& sw) = 0;
};

class BrainButton {
public:
    BrainButton(gpio_num_t gpioNum, bool pullUp, BrainButtonListener& listener);

    gpio_num_t gpioNum() { return m_gpioNum; }
    bool readDown();

    void enableLongPress(bool enable) { m_longPressEnabled = enable; }

    void start();

    void _isr();
    void _callListener();
    void _longPressTimer();

private:
    gpio_num_t m_gpioNum;
    BrainButtonListener& m_listener;

    bool m_pullUp;
    bool m_Down;
    uint64_t m_ChangeAt;

    bool m_longPressEnabled = true;

    TimerHandle_t m_hLongPressTimer;
    bool m_longPressPending;
};