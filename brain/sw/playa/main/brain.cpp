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
    m_ledRenderer(m_timeBase, m_pixelCount)
{
    uint8_t mac[6];
    esp_read_mac(mac, ESP_MAC_WIFI_STA);
    if (-1 == snprintf((char*) &m_brainId, sizeof(m_brainId), "%02X%02X%02X", mac[3], mac[4], mac[5])) {
        abort();
    }
}

void
Brain::handleMsg(Msg* pMsg)
{
    if (!pMsg) return;

    ESP_LOGD(TAG, "Brain::handleMsg with length=%d", pMsg->used());

    // The first byte of the message tells us the type
    auto nMsgType = pMsg->readByte();

    switch (nMsgType) {
        case static_cast<int>(Msg::Type::BRAIN_PANEL_SHADE):
            msgBrainPanelShade(pMsg);
            break;

        case static_cast<int>(Msg::Type::BRAIN_ID_REQUEST):
            msgBrainIdRequest(pMsg);
            break;

        case static_cast<int>(Msg::Type::BRAIN_MAPPING):
            msgBrainMapping(pMsg);
            break;

        case static_cast<int>(Msg::Type::PINKY_PONG):
            msgPinkyPong(pMsg);
            break;

        default:
            ESP_LOGW(TAG, "Unknown message type %d", nMsgType);
            break;
    }
}

void
Brain::msgBrainPanelShade(Msg* pMsg) {
    if (!pMsg) return;

    ESP_LOGD(TAG, "MSG: BrainPanelShade");

    m_shadeTree.handleMessage(pMsg);

    // Always force a render immediately after receipt of a panel shade message.
    // While this _does_ tie the network processing to the rendering process, this
    // is likely what we want??? Time based rendering still should be looked at, but
    // this approach should get us the highest interactive responsiveness.
    m_ledRenderer.render();
}

void
Brain::msgBrainIdRequest(Msg* pMsg) {
    if (!pMsg) return;

    ESP_LOGE(TAG, "MSG: BrainIdRequest");

    sendHello(pMsg->dest);
}

void
Brain::msgBrainMapping(Msg* pMsg) {
    if (!pMsg) return;

    ESP_LOGD(TAG, "MSG: BrainMapping");
}

void
Brain::msgPinkyPong(Msg* pMsg) {
    if (!pMsg) return;

    ESP_LOGD(TAG, "MSG: PinkyPong");
}


void maybe_send_hello(TimerHandle_t hTimer)
{
    ((Brain*)pvTimerGetTimerID(hTimer))->maybeSendHello();
}

void
Brain::maybeSendHello()
{
    ESP_LOGE(TAG, "Want to send a hello now...");

    sendHello(IpPort::BroadcastPinky);
}

void
Brain::sendHello(const IpPort &port) {
    Msg *pHello = new BrainHelloMsg(m_brainId, "33R");
    pHello->dest = port;

    pHello->injectFragmentingHeader(); // Because hatefulness
    m_msgSlinger.sendMsg(pHello);

    pHello->release();
}

void
Brain::start()
{
    gSysMon.start();
    m_brainUI.start();

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

    m_shadeTree.start();
    ESP_LOGE(TAG, "m_shadeTree started");

    m_timeBase.setFPS(30);
    m_ledRenderer.setBrightness(255);

    m_ledRenderer.setShader(&m_shadeTree);

    // Start talking to the pixels
    m_ledRenderer.start();

    // Some initial debugging stuff
    ESP_LOGE(TAG, "------- Brain Start ---------");
    ESP_LOGE(TAG, "id = %s", m_brainId);
    ESP_LOGE(TAG, "xPortGetTickRateHz = %d", xPortGetTickRateHz());
    ESP_LOGE(TAG, "pdMS_TO_TICKS(1000) = %d", pdMS_TO_TICKS(1000));
    ESP_LOGE(TAG, "getFPS() = %d", m_timeBase.getFPS());
    ESP_LOGE(TAG, "getFrameDuration() = %d", m_timeBase.getFrameDuration());
}



