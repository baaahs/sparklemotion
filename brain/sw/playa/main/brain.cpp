//
// Created by Tom Seago on 2019-06-02.
//

#include <esp_ota_ops.h>
#include "brain.h"
#include "brain_common.h"

#include "esp_log.h"

#include "freertos/FreeRTOS.h"
#include "freertos/timers.h"
#include "esp_https_ota.h"

static const uint16_t BRAIN_PORT = 8003;

static const char* TAG = TAG_BRAIN;

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

        case static_cast<int>(Msg::Type::PING):
            msgPing(pMsg);
            break;

        case static_cast<int>(Msg::Type::USE_FIRMWARE):
            msgUseFirmware(pMsg);
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

    bool havePongData = pMsg->readBoolean();
    uint8_t *data = nullptr;
    size_t dataLen = 0;
    if (havePongData) {
        dataLen = pMsg->copyBytes(&data);
        ESP_LOGD(TAG, "Read %d bytes for pong", dataLen);
    }

    m_shadeTree.handleMessage(pMsg);

    // Always force a render immediately after receipt of a panel shade message.
    // While this _does_ tie the network processing to the rendering process, this
    // is likely what we want??? Time based rendering still should be looked at, but
    // this approach should get us the highest interactive responsiveness.
    //
    // We also turn off local rendering.
    // TODO: Implement a thing where if we haven't heard from Pinky in some time we turn local rendering back on
    m_ledRenderer.enableLocalRenderLoop(false);
    m_ledRenderer.render();

    if (havePongData) {
        sendPong(pMsg->dest, data, dataLen);
    }
    if (data) {
        free(data);
    }
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
Brain::msgPing(Msg* pMsg) {
    if (!pMsg) return;

    ESP_LOGD(TAG, "MSG: Ping");

    bool isPong = pMsg->readBoolean();
    if (!isPong) {
        uint8_t* data;
        size_t dataLen = pMsg->copyBytes(&data);
        sendPong(pMsg->dest, data, dataLen);
        free(data);
    }
}

esp_err_t ota_event(esp_http_client_event_t *evt)
{
    switch (evt->event_id) {
        case HTTP_EVENT_ERROR:
            ESP_LOGD(TAG, "HTTP_EVENT_ERROR");
            break;
        case HTTP_EVENT_ON_CONNECTED:
            ESP_LOGD(TAG, "HTTP_EVENT_ON_CONNECTED");
            break;
        case HTTP_EVENT_HEADER_SENT:
            ESP_LOGD(TAG, "HTTP_EVENT_HEADER_SENT");
            break;
        case HTTP_EVENT_ON_HEADER:
            ESP_LOGD(TAG, "HTTP_EVENT_ON_HEADER, key=%s, value=%s", evt->header_key, evt->header_value);
            break;
        case HTTP_EVENT_ON_DATA:
            ESP_LOGD(TAG, "HTTP_EVENT_ON_DATA, len=%d", evt->data_len);
            break;
        case HTTP_EVENT_ON_FINISH:
            ESP_LOGD(TAG, "HTTP_EVENT_ON_FINISH");
            break;
        case HTTP_EVENT_DISCONNECTED:
            ESP_LOGD(TAG, "HTTP_EVENT_DISCONNECTED");
            break;
    }
    return ESP_OK;
}

#define MAX_URL_SIZE 512

void
Brain::msgUseFirmware(Msg *pMsg){
    if (!pMsg) return;

    ESP_LOGD(TAG, "MSG: Use Firmware");

    char* szBuf = (char*)malloc(MAX_URL_SIZE);
    if (!szBuf) {
        ESP_LOGE(TAG, "Unable to allocate a scratch buffer for the url");
        return;
    }
    // Start out nice just in case
    szBuf[0] = 0;

    auto read = pMsg->readString(szBuf, MAX_URL_SIZE-1);
    if (!read) {
        ESP_LOGE(TAG, "Firmware URL was empty");
        free(szBuf);
        return;
    }
    // Gotta keep 'er terminated
    szBuf[read] = 0;

    ESP_LOGE(TAG, "Was told to use a new firmware %s", szBuf);

    esp_http_client_config_t clientCfg = { 0 };
    clientCfg.url = szBuf;
    clientCfg.event_handler = ota_event;

    ESP_LOGE(TAG, "Starting the ota download...");

    esp_https_ota_handle_t hOta;
    esp_https_ota_config_t otaConfig;
    otaConfig.http_config = &clientCfg;

    auto err = esp_https_ota_begin(&otaConfig, &hOta);
    if (err != ESP_OK || hOta == nullptr) {
        ESP_LOGE(TAG, "Failed to start OTA: err=%d", err);
        return;
    }

    while(1) {
        err = esp_https_ota_perform(hOta);
        if (err == ESP_OK) break;

        if (err != ESP_ERR_HTTPS_OTA_IN_PROGRESS) {
            ESP_LOGE(TAG, "Error during OTA %d", err);
            break;
        } else {
            ESP_LOGE(TAG, "OTA chunk");
        }
    }

    auto finishErr = esp_https_ota_finish(hOta);
    if (finishErr != ESP_OK) {
        ESP_LOGE(TAG, "Error at OTA finish %d", finishErr);
    } else {
        ESP_LOGE(TAG, "Hey! That was a good OTA. We'll restart now");
        brain_restart(10);
    }

    free(szBuf);
}


void
Brain::sendPong(const IpPort &port, const uint8_t *data, size_t dataLen) {
    Msg *pong = new PingMsg(data, dataLen, true);
    pong->dest = port;

    pong->injectFragmentingHeader();
    m_msgSlinger.sendMsg(pong);
    pong->release();
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
    auto desc = esp_ota_get_app_description();

    Msg *pHello = new BrainHelloMsg(m_brainId, nullptr, desc->version, desc->idf_ver);
    pHello->dest = port;

    pHello->injectFragmentingHeader(); // Because hatefulness
    m_msgSlinger.sendMsg(pHello);

    pHello->release();
}

void
Brain::start() {
    gSysMon.start(DefaultBrainTasks.sysmon);
    m_brainUI.start(DefaultBrainTasks.ui);

    // TODO: Check with the UI to see if the user is holding down a button so that we enter config mode instead of regular boot

    startSecondStageBoot();
}

void Brain::startSecondStageBoot() {
    GlobalConfig.load();

    m_netTransport.start(DefaultBrainTasks.net);
//    m_netTransport.reconfigure();

    m_msgSlinger.registerHandler(this);
    m_msgSlinger.start(BRAIN_PORT, DefaultBrainTasks.netInput, DefaultBrainTasks.netOutput);

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

    m_ledRenderer.setShader(&m_shadeTree);
//    LEDShaderFiller* filler = new LEDShaderFiller();
//    m_ledRenderer.setShader(filler);

    // Start talking to the pixels
    m_ledRenderer.start(DefaultBrainTasks.show, DefaultBrainTasks.render);

    // Some initial debugging stuff
    ESP_LOGE(TAG, "------- Brain Start ---------");
    ESP_LOGE(TAG, "id = %s", m_brainId);
    ESP_LOGE(TAG, "xPortGetTickRateHz = %d", xPortGetTickRateHz());
    ESP_LOGE(TAG, "pdMS_TO_TICKS(1000) = %d", pdMS_TO_TICKS(1000));
    ESP_LOGE(TAG, "getFPS() = %d", m_timeBase.getFPS());
    ESP_LOGE(TAG, "getFrameDuration() = %d", m_timeBase.getFrameDuration());

    // Do this last!
    m_httpServer.start();
}



