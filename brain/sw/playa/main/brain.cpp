//
// Created by Tom Seago on 2019-06-02.
//

#include "brain.h"

#include "esp_log.h"

#include "freertos/FreeRTOS.h"
#include "freertos/timers.h"

static const uint16_t BRAIN_PORT = 8003;

static const char* TAG = "# brain";

Brain::Brain() :
    m_ledRenderer(m_timeBase)
{
}

void
Brain::handleMsg(Msg* pMsg)
{
    ESP_LOGI(TAG, "Got message with length=%d", pMsg->used());

    if (pMsg->isSingleFragmentMessage()) {
        // Do stuff
        ESP_LOGI(TAG, "Parsing...");
        pMsg->log();

        // Ignore framing data
        pMsg->rewindToPostFragmentingHeader();

        auto pParsed = pMsg->parse();
        if (pParsed) {

            pParsed->release();
        }
    } else {
        ESP_LOGW(TAG, "Ignoring multi-fragment messages");
    }
}

void maybe_send_hello(TimerHandle_t hTimer)
{
    ((Brain*)pvTimerGetTimerID(hTimer))->maybeSendHello();
}

void
Brain::maybeSendHello()
{
    ESP_LOGE(TAG, "Want to send a hello now...");

    Msg* pHello = new BrainHelloMsg("esp", "33R");
    pHello->dest = IpPort::BroadcastPinky;

    pHello->injectFragmentingHeader(); // Because hatefulness
    m_msgSlinger.sendMsg(pHello);

    pHello->release();
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

    m_timeBase.setFPS(30);
    m_ledRenderer.setBrightness(10);

    m_ledShader = new LEDShaderFiller(m_timeBase, m_ledRenderer.getNumPixels());
    m_ledRenderer.setShader(m_ledShader);

    // Start talking to the pixels
    m_ledRenderer.start();
    m_ledRenderer.startLocalRenderTask();

    // Some initial debugging stuff
    ESP_LOGE(TAG, "------- Brain Start ---------");
    ESP_LOGE(TAG, "xPortGetTickRateHz = %d", xPortGetTickRateHz());
    ESP_LOGE(TAG, "pdMS_TO_TICKS(1000) = %d", pdMS_TO_TICKS(1000));
    ESP_LOGE(TAG, "getFPS() = %d", m_timeBase.getFPS());
    ESP_LOGE(TAG, "getFrameDuration() = %d", m_timeBase.getFrameDuration());
}



