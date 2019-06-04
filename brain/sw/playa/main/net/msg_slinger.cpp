//
// Created by Tom Seago on 2019-06-02.
//

#include "msg_slinger.h"

#include "esp_err.h"
#include "esp_log.h"
#include "esp_event.h"

#include "lwip/err.h"
#include "lwip/sockets.h"
#include "lwip/sys.h"
#include <lwip/netdb.h>

static const char* TAG = "msg slinger";

static const int INPUT_STACK = 10240; // Because 10k seems reasonable???
static const int OUTPUT_STACK = 10240; // Because 10k seems reasonable???

// TODO: Manage priorities more centrally perhaps?
static const int INPUT_PRIO = 5;
static const int OUTPUT_PRIO = 6;

void msg_slinger_input_pump(void* pParam) {
    ((MsgSlinger*)pParam)->inputPump();
}

//void msg_slinger_output_pump(void* pParam) {
//    ((MsgSlinger*)pParam)->outputPump();
//}

ESP_EVENT_DEFINE_BASE(BRAIN_NETOUT);
static const int32_t NETOUT_READY = 1;

// Glue function FTW
void handler_output_pump(void* arg, esp_event_base_t base, int32_t id, void* data) {
    ((MsgSlinger*)arg)->handleOutputPump((Msg*)data);
}

void
MsgSlinger::start(uint16_t port)
{
    m_port = port;

//    m_hRing = xRingbufferCreateNoSplit(MAX_MSG_LEN, 4);
//    if (!m_hRing) {
//        ESP_LOGE(TAG, "Failed to create a ring buffer");
//    }
//
//    // Ready to start sending items into it!
//
//    m_hQueueSet = xQueueCreateSet(3);
//
//    if (xRingbufferAddToQueueSetRead(m_hRing, m_hQueueSet) != pdTRUE) {
//        println("Failed to add the buffer to the queue");
//    }

    BaseType_t r = xTaskCreate(msg_slinger_input_pump, "input", INPUT_STACK, (void*)this, INPUT_PRIO, NULL);
    if (r != pdPASS) {
        ESP_LOGE(TAG, "Failure starting input pump task %d", r);
    }


    esp_event_loop_args_t evConfig;
    evConfig.queue_size = 10;
    evConfig.task_name = "net output";
    evConfig.task_priority = OUTPUT_PRIO;
    evConfig.task_stack_size = OUTPUT_STACK;
    // Not setting a core id at this time...

    esp_event_loop_handle_t hLoop;
    esp_err_t err = esp_event_loop_create(&evConfig, &hLoop);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "Failed starting output event loop");
        return;
    }

    err = esp_event_handler_register_with(hLoop, BRAIN_NETOUT, NETOUT_READY, handler_output_pump, this);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "Failed to register handler");
        return;
    }
}



void
MsgSlinger::inputPump()
{
    char addr_str[128];

    struct sockaddr_in dest_addr;
    dest_addr.sin_addr.s_addr = htonl(INADDR_BROADCAST);
    dest_addr.sin_family = AF_INET;
    dest_addr.sin_port = htons(m_port);
    int addr_family = AF_INET;
    int ip_protocol = IPPROTO_IP;
    inet_ntoa_r(dest_addr.sin_addr, addr_str, sizeof(addr_str) - 1);

    int m_sock = socket(addr_family, SOCK_DGRAM, ip_protocol);
    if (m_sock < 0) {
        ESP_LOGE(TAG, "Unable to create socket: errno=%d", errno);
        return;
    }

    ESP_LOGD(TAG, "Socket created");

    int err = bind(m_sock, (struct sockaddr *)&dest_addr, sizeof(dest_addr));
    if (err < 0) {
        ESP_LOGE(TAG, "Unable to create socket: errno=%d", errno);
        return;
    }

    ESP_LOGD(TAG, "Socket bound to port %d", m_port);

    while(1) {
        ESP_LOGD(TAG, "Waiting for data in inputPump");

        Msg* pMsg = Msg::obtain();
        pMsg->prepCapacity(500);

        struct sockaddr_in source_addr;
        socklen_t socklen = sizeof(pMsg->dest.size());
//        int len = recvfrom(m_sock, m_buffer, sizeof(m_buffer) - 1, 0,
//                           (struct sockaddr *)&source_addr, &socklen);
        int len = recvfrom(m_sock, pMsg->buffer(), pMsg->capacity() - 1, 0,
                           pMsg->dest.addr(), &socklen);
        pMsg->setUsed(len);

        if (len < 0) {
            ESP_LOGE(TAG, "recvfrom failed: errno %d", errno);
            break;
        } else {
            inet_ntoa_r(pMsg->dest.addr_in()->sin_addr.s_addr, addr_str, sizeof(addr_str) - 1);

            ESP_LOGI(TAG, "Received %d bytes from %s:", len, addr_str);

            if (m_handler) {
//                m_handler->handleMsg((uint8_t *) &m_buffer, len, (struct sockaddr *) &source_addr);
                m_handler->handleMsg(pMsg);
            } else {
                ESP_LOGW(TAG, "Got input but no handler registered");
            }
        }
        pMsg->release();
    }
}

//void
//MsgSlinger::outputPump()
//{
//    ESP_LOGI(TAG, "outputPump started but implemented yet. Exiting");
//    vTaskDelete(NULL);
//}


void
MsgSlinger::handleOutputPump(Msg* pMsg) {
    ESP_LOGI(TAG, "Got an output event");
    if (!pMsg) return;

    pMsg->log();

    if (m_sock < 0) {
        ESP_LOGE(TAG, "Can not send this message because m_sock is < 0");
        pMsg->release();
        return;
    }


//
//    struct sockaddr* destAddr = to;
//    size_t destAddrLen = xToLen;
//
//    struct sockaddr_in broadcast;
//
//    if (!destAddr) {
//        broadcast.sin_addr.s_addr = htonl(INADDR_BROADCAST);
//        broadcast.sin_family = AF_INET;
//        broadcast.sin_port = htons(xToLen);
//        destAddr = (struct sockaddr*)&broadcast;
//        destAddrLen = sizeof(broadcast);
//    }

    int err = sendto(m_sock, pMsg->buffer(), pMsg->used(), 0, pMsg->dest.addr(), pMsg->dest.size());
    if (err < 0) {
        ESP_LOGE(TAG, "Error occurred during sending: errno %d", errno);

        pMsg->release();
        return;
    }

    ESP_LOGI(TAG, "Message sent ok");
    pMsg->release();
}