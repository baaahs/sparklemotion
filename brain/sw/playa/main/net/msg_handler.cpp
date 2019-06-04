//
// Created by Tom Seago on 2019-06-02.
//

#include "msg_handler.h"

#include "esp_log.h"
#include "esp_err.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

#include "lwip/err.h"
#include "lwip/sockets.h"
#include "lwip/sys.h"
#include <lwip/netdb.h>

static const char* TAG = "msg handler";

/*
MsgHandler::MsgHandler(uint16_t port) :
    m_port(port)
{

}

void
MsgHandler::receiveMessages()
{
    ESP_LOGD(TAG, "receiveMessages() beginning");

    if (ESP_OK != doBind()) {
        return;
    }

    startSending();
}

esp_err_t
MsgHandler::doBind()
{
    char addr_str[128];

    struct sockaddr_in dest_addr;
    dest_addr.sin_addr.s_addr = htonl(INADDR_BROADCAST);
    dest_addr.sin_family = AF_INET;
    dest_addr.sin_port = htons(m_port);
    int addr_family = AF_INET;
    int ip_protocol = IPPROTO_IP;
    inet_ntoa_r(dest_addr.sin_addr, addr_str, sizeof(addr_str) - 1);

    m_sock = socket(addr_family, SOCK_DGRAM, ip_protocol);
    if (m_sock < 0) {
        ESP_LOGE(TAG, "Unable to create socket: errno=%d", errno);
        return ESP_FAIL;
    }

    ESP_LOGD(TAG, "Socket created");

    int err = bind(m_sock, (struct sockaddr *)&dest_addr, sizeof(dest_addr));
    if (err < 0) {
        ESP_LOGE(TAG, "Unable to create socket: errno=%d", errno);
        return ESP_FAIL;
    }

    ESP_LOGD(TAG, "Socket bound to port %d", m_port);

    while(1) {
        ESP_LOGD(TAG, "Waiting for data");

        struct sockaddr_in source_addr;
        socklen_t socklen = sizeof(source_addr);
        int len = recvfrom(m_sock, m_buffer, sizeof(m_buffer) - 1, 0,
                (struct sockaddr *)&source_addr, &socklen);

        if (len < 0) {
            ESP_LOGE(TAG, "recvfrom failed: errno %d", errno);
            break;
        } else {
            inet_ntoa_r((&source_addr)->sin_addr.s_addr, addr_str, sizeof(addr_str) - 1);

            ESP_LOGI(TAG, "Received %d bytes from %s:", len, addr_str);

            handleMsg((uint8_t*)&m_buffer, len, (struct sockaddr *)&source_addr);
        }
    }

    return ESP_OK;
}

esp_err_t
MsgHandler::sendMessage(uint8_t* pMsg, size_t xLength, struct sockaddr* to, size_t xToLen)
{
    struct sockaddr* destAddr = to;
    size_t destAddrLen = xToLen;

    struct sockaddr_in broadcast;

    if (!destAddr) {
        broadcast.sin_addr.s_addr = htonl(INADDR_BROADCAST);
        broadcast.sin_family = AF_INET;
        broadcast.sin_port = htons(xToLen);
        destAddr = (struct sockaddr*)&broadcast;
        destAddrLen = sizeof(broadcast);
    }

    int err = sendto(m_sock, pMsg, xLength, 0, destAddr, destAddrLen);
    if (err < 0) {
        ESP_LOGE(TAG, "Error occurred during sending: errno %d", errno);
        return ESP_FAIL;
    }

    return ESP_OK;
}

void
MsgHandler::sendMessages()
{
    ESP_LOGD(TAG, "Sending task started");

    while(1) {

    }
}

void static task_send_messages(void* pThis) {
    ((MsgHandler*)pThis)->sendMessages();
}

void
MsgHandler::startSending()
{
    BaseType_t r = xTaskCreate(task_send_messages, "sender", 10240, this, 9,  &hSender);
    if (r != pdPASS) {
        ESP_LOGE(TAG, "Failed to create sender task: %d", r);
    } else {
        ESP_LOGD(TAG, "Created a sender task");
    }
}
 */
