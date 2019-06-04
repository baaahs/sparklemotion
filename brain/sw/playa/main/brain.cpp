//
// Created by Tom Seago on 2019-06-02.
//

#include "brain.h"

#include "esp_log.h"

#include "freertos/FreeRTOS.h"
#include "freertos/timers.h"

static const uint16_t BRAIN_PORT = 8003;

static const char* TAG = "brain";

Brain::Brain()
{
}

void
Brain::handleMsg(Msg* pMsg)
{
    ESP_LOGI(TAG, "Got message with length=%d", pMsg->used());
}

void maybe_send_hello(TimerHandle_t hTimer)
{
    ((Brain*)pvTimerGetTimerID(hTimer))->maybeSendHello();
}

void
Brain::maybeSendHello()
{
    ESP_LOGE(TAG, "Want to send a hello now...");
}

void
Brain::start()
{
    m_msgSlinger.registerHandler(this);
    m_msgSlinger.start(BRAIN_PORT);

    TimerHandle_t hTimer = xTimerCreate("say hello", pdMS_TO_TICKS(5000), pdTRUE, this, maybe_send_hello);
    if (!hTimer) {
        ESP_LOGE(TAG, "Failed to create hello timer");
        return;
    }

    if (xTimerStart(hTimer, 0) != pdPASS) {
        ESP_LOGE(TAG, "failed to start timer");
        return;
    }
}



