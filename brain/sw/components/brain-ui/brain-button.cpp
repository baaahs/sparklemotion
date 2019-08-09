//
// Created by Tom Seago on 2019-07-10.
//

#include "brain-button.h"
#include "brain-ui-priv.h"

const uint32_t LONG_PRESS_MS = 1000;

void glue_isr(void *pArg) {
    ((BrainButton*)pArg)->_isr();
}

void glue_callListener(void *pArg, uint32_t pArg2) {
    ((BrainButton*)pArg)->_callListener();
}

void glue_longPressTimer(TimerHandle_t xTimer) {
    void* pArg = pvTimerGetTimerID(xTimer);
    ((BrainButton*)pArg)->_longPressTimer();
}

BrainButton::BrainButton(gpio_num_t gpioNum, bool activeLow, BrainButtonListener &listener) :
        m_gpioNum(gpioNum),
        m_listener(listener),
        m_activeLow(activeLow),
        m_Down(false)
{
}

void
BrainButton::start() {
    // This will cause an error to be printed the second time it is called but we don't
    // really care and it's easier than adding a guard bit.
    gpio_install_isr_service(0);

    gpio_config_t config;
    config.pin_bit_mask = (uint64_t)1 << m_gpioNum;
    config.mode = GPIO_MODE_INPUT;
    config.pull_up_en = m_activeLow ? GPIO_PULLUP_ENABLE : GPIO_PULLUP_DISABLE;
    config.pull_down_en = m_activeLow ? GPIO_PULLDOWN_DISABLE : GPIO_PULLDOWN_ENABLE;
    config.intr_type = GPIO_INTR_ANYEDGE;

    if (ESP_OK != gpio_config(&config)) {
        ESP_LOGE(TAG, "Failed to configure button %d", m_gpioNum);
        return;
    }

    // We're going to also need a timer to handle long press situations
    m_hLongPressTimer = xTimerCreate("Button",
            pdMS_TO_TICKS(LONG_PRESS_MS),
            pdFALSE, // No auto reloading
            this,    // A timer ID equal to us
            glue_longPressTimer);

    m_Down = readDown();
    m_ChangeAt = esp_timer_get_time();

    gpio_isr_handler_add(m_gpioNum, glue_isr, this);
    ESP_LOGI(TAG, "Button (%d) configured and m_Down=%s", m_gpioNum, m_Down ? "true" : "false");
}

bool
BrainButton::readDown() {
    bool down = (bool)gpio_get_level(m_gpioNum);
    if (m_activeLow) {
        down = !down;
    }
    return down;
}

void BrainButton::_isr() {
    bool nextState = readDown();
    if (nextState == m_Down) {
        return;
    }

    // State change!
    m_Down = nextState;
    m_ChangeAt = esp_timer_get_time();

    BaseType_t xHigherPriorityTaskWoken;
    xTimerPendFunctionCallFromISR(glue_callListener, this, 0, &xHigherPriorityTaskWoken);

    // Might need the currently interrupted task to yield, which we force here.
    if (xHigherPriorityTaskWoken) {
        portYIELD_FROM_ISR();
    }
}

void BrainButton::_callListener() {
    if (m_Down) {
        if (m_longPressEnabled) {
            m_longPressPending = true;
            // Start the timer. The period is set when the timer was created. The 0 here
            // just means don't wait around in case the timer queue itself is blocked
            xTimerStart(m_hLongPressTimer, 0);
        } else {
            // Dispatch immediately
            m_listener.buttonDown(*this, false);
        }
    } else {
        if (m_longPressPending) {
            // Make sure the timer doesn't fire!
            m_longPressPending = false;

            // Long press was pending, but UP happened first
            xTimerStop(m_hLongPressTimer, 0);
            m_listener.buttonDown(*this, false);
        }
        m_listener.buttonUp(*this);
    }
}

void BrainButton::_longPressTimer() {
    // Oh hey we expired!
    m_longPressPending = false;
    if (m_Down) {
        // Only send this if we are _still_ actually down
        m_listener.buttonDown(*this, true);
    }
}