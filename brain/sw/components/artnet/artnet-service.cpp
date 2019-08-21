
#include <artnet-service.h>
#include <sysmon.h>

#include "lwip/sockets.h"
#include "lwip/ip_addr.h"

#define TAG TAG_ARTNET

ESP_EVENT_DEFINE_BASE(BRAIN_ARTNETOUT);
static const int32_t ARTNETOUT_READY = 1;

void glue_inputPump(void* pArg) {
    ((ArtnetService*)pArg)->_inputPump();
}

void glue_handleNetOut(void *pArg, esp_event_base_t base, int32_t id, void *data) {
    ESP_LOGD(TAG, "glue_handleNetOut arg=%p data=%p", pArg, data);
    if (data) {
        ArtMsg** ppMsg = (ArtMsg**)data;
        ESP_LOGD(TAG, "  *ppMsg=%p  ", *ppMsg);

        ((ArtnetService*)pArg)->_handleNetOut(*ppMsg);
    } else {
        ESP_LOGE(TAG, "Got a data out event with invalid data parameter");
    }
}


void ArtnetService::start(TaskDef input, TaskDef output) {
    ////////////////
    bindSocket();

    ////////////////
    ESP_LOGD(TAG, "Creating input pump task");
    auto result = input.createTask(glue_inputPump, this, nullptr);
    if (result != pdPASS) {
        ESP_LOGE(TAG, "Failure starting input pump task %d", result);
    }

    ////////////////
    m_argsOutputLoop.queue_size = 10;
    m_argsOutputLoop.task_name = output.name;
    m_argsOutputLoop.task_priority = output.priority;
    m_argsOutputLoop.task_stack_size = output.stack;
    // Not setting a core id at this time...

    m_hOutputLoop = nullptr;
    esp_err_t err = esp_event_loop_create(&m_argsOutputLoop, &m_hOutputLoop);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "Failed starting output event loop");
        return;
    }

    err = esp_event_handler_register_with(m_hOutputLoop, BRAIN_ARTNETOUT, ARTNETOUT_READY, glue_handleNetOut, this);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "Failed to register handler");
        return;
    }

}

void ArtnetService::_inputPump() {
    ESP_LOGI(TAG, "_inputPump starting");

    while(!m_timeToDie) {
        ESP_LOGD(TAG, "Waiting for data in _inputPump m_sock=%d", m_sock);

        // Get a new input buffer message and make sure it has a reasonable
        // capacity.
        ArtMsg* pMsg = ArtMsg::obtain();
        pMsg->prepCapacity(ArtMsg::FRAGMENT_MAX);

        socklen_t socklen = pMsg->dest.size();

        // Because of the IN/OUT nature of the address field, we must make
        // sure that we always reset it such that we are receiving packets
        // from all senders
        pMsg->dest.addr_in()->sin_addr.s_addr = htonl(INADDR_ANY);

        int len = recvfrom(m_sock, pMsg->buffer(), pMsg->capacity() - 1, 0,
                           pMsg->dest.addr(), &socklen);

        if (!m_timeToDie) {
            if (len < 0) {
                ESP_LOGE(TAG, "recvfrom failed: %d %s", errno, strerror(errno));
                // Delay at least a little bit so the rest of the system isn't horribly
                // gunked up on an endless error scenario. Presumably the socket
                // will get re-bound at some point because an interface comes back up.
                vTaskDelay(pdMS_TO_TICKS(100));
                break;
            } else {
                pMsg->setUsed(len);

                // inet_ntoa_r(pMsg->dest.addr_in()->sin_addr.s_addr, addr_str, sizeof(addr_str) - 1);

                //ESP_LOGI(TAG, "Received %d bytes from %s:", len, addr_str);
                ESP_LOGI(TAG, "Received %d bytes from %s:", len, pMsg->dest.toString());
                gSysMon.increment(COUNTER_UDP_RECV);

                handleNetIn(pMsg);
            }
        }
        pMsg->release();
    }

    // Delete ourself!
    vTaskDelete(nullptr);
}

void ArtnetService::handleNetIn(Msg *pMsg) {
    if (!pMsg) return;

    ESP_LOGW(TAG, "Handling an artnet message of size %d", pMsg->used());

    pMsg->release();
}

void ArtnetService::_handleNetOut(ArtMsg* pMsg) {
    if (!pMsg) return;

    if (m_timeToDie) return;

    if (m_sock < 0) {
        ESP_LOGE(TAG, "Can not send this message because m_sock(%d) is < 0", m_sock);
        pMsg->release();
        return;
    }

    ESP_LOGI(TAG, "Message len=%d to %s", pMsg->used(), pMsg->dest.toString());

    auto err = sendto(m_sock, pMsg->buffer(), pMsg->used(), 0,
                      pMsg->dest.addr(), pMsg->dest.size());
    if (err < 0) {
        ESP_LOGE(TAG, "Error occurred sending to %s : m_sock=%d %d %s",
                 pMsg->dest.toString(), m_sock, errno, strerror(errno));
    } else {
        gSysMon.increment(COUNTER_MSG_SENT);
        ESP_LOGD(TAG, "Message sent to %s", pMsg->dest.toString() );
    }

    pMsg->release();
}

void ArtnetService::bindSocket() {
    ESP_LOGW(TAG, "Binding socket. m_sock=%d", m_sock);
    if (m_sock) {
        close(m_sock);
        m_sock = 0;
    }

    ESP_LOGI(TAG, "Creating socket");
    m_sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_IP);
    if (m_sock < 0) {
        ESP_LOGE(TAG, "Unable to create socket: errno=%d", errno);
        return;
    }

    struct sockaddr_in addr = {};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(m_port);
    addr.sin_addr.s_addr = htonl(INADDR_ANY);

    ESP_LOGD(TAG, "Socket created");

    int err = bind(m_sock, (struct sockaddr *)&addr, sizeof(addr));
    if (err < 0) {
        ESP_LOGE(TAG, "Unable to create socket: errno=%d", errno);
        return;
    }

    char addr_str[128];
    inet_ntoa_r(addr.sin_addr, addr_str, sizeof(addr_str) - 1);
    ESP_LOGD(TAG, "Socket %d bound to %s:%d", m_sock, addr_str, m_port);
}


esp_err_t ArtnetService::sendMsg(ArtMsg *pMsg) {
    if (m_timeToDie) return ESP_FAIL;

    if (!pMsg) {
        ESP_LOGW(TAG, "sendMsg this=%p pMsg=%p len=%d dest=%s", this, pMsg, pMsg->used(), pMsg->dest.toString());
        return ESP_FAIL;
    }

    // We just fire an event so that the output pump will take care
    // of it later and the sending task doesn't block

    pMsg->addRef(); // Will be released on the other side of the event.

    ESP_LOGI(TAG, "sendMsg this=%p pMsg=%p len=%d dest=%s", this, pMsg, pMsg->used(), pMsg->dest.toString());

    esp_event_post_to(m_hOutputLoop, BRAIN_ARTNETOUT, ARTNETOUT_READY, &pMsg, sizeof(pMsg), pdMS_TO_TICKS(500));

    return ESP_OK;
}
