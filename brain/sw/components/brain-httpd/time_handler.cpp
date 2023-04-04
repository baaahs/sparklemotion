//
// Created by Tom Seago on 12/30/19.
//

#include "time_handler.h"
#include "http_server.h"

#define TAG TAG_HTTPD

static esp_err_t glue_postCurrentTime(httpd_req_t *req) {
    if (!req) return ESP_ERR_INVALID_ARG;

    return ((TimeHandler *) (req->user_ctx))->_postCurrentTime(req);
}

esp_err_t TimeHandler::registerHandlers(HttpServer &server) {
    httpd_uri_t postCurrentTime = {
            .uri = "/currentTime",
            .method = HTTP_POST,
            .handler = glue_postCurrentTime,
            .user_ctx = this,
    };

    auto err = httpd_register_uri_handler(server.m_hServer, &postCurrentTime);
    if (ESP_OK != err) {
        ESP_LOGE(TAG, "Failed to register POST /currentTime uri handler %d", err);
        return err;
    }

    return ESP_OK;
}

#define TMP_BUFFER_SIZE 100
esp_err_t TimeHandler::_postCurrentTime(httpd_req_t *req) {
    ESP_LOGD(TAG, "_putConfig()");

    esp_err_t result = ESP_FAIL;

    // Need a buffer, this seems reasonable
    char* szBuf = (char*)malloc(TMP_BUFFER_SIZE);
    if (!szBuf) {
        ESP_LOGE(TAG, "OOM allocating receive buffer");
        return ESP_FAIL;
    }

    auto read = httpd_req_recv(req, szBuf, TMP_BUFFER_SIZE);
    if (read > 0) {
        // The thing we got isn't null terminated so have to do this
        // to log it
        {
            char* szTemp = (char*)malloc(read + 1);
            if (szTemp) {
                memcpy(szTemp, szBuf, read);
                szTemp[read] = 0;
                ESP_LOGD(TAG, "currentTime = '%s'", szTemp);
                free(szTemp);
            }
        }

        // Dig in to find our markers
        char* pDot = szBuf;
        char* pEnd = szBuf + read;
        while(pDot < pEnd && (*pDot != '.')) pDot++;
        if (pDot == pEnd) {
            httpd_resp_set_status(req, "400 No dot found");
            httpd_resp_sendstr(req, "FAIL");
        } else {
            pEnd -= 1;
            *pEnd = 0;
            *pDot = 0;
            pDot++;

            timeval tv;
            tv.tv_sec = atol(szBuf);
            tv.tv_usec = atol(pDot);

            settimeofday(&tv, nullptr);
            ESP_LOGI(TAG, "Set time to %lld.%ld", tv.tv_sec, tv.tv_usec);

            httpd_resp_sendstr(req, "OK");
        }
        result = ESP_OK;
    } else if (read == 0) {
        ESP_LOGW(TAG, "_postCurrentTime connection closed by peer");
    } else {
        ESP_LOGE(TAG, "_postCurrentTime error reading %d", read);
    }

    free(szBuf);

    return result;
}