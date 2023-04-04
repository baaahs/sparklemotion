//
// Created by Tom Seago on 1/26/20.
//


#include "color_handler.h"
#include "http_server.h"

#define TAG TAG_HTTPD

static esp_err_t glue_postColor(httpd_req_t *req) {
    if (!req) return ESP_ERR_INVALID_ARG;

    return ((ColorHandler *) (req->user_ctx))->_postColor(req);
}

esp_err_t ColorHandler::registerHandlers(HttpServer &server) {
    httpd_uri_t postColor = {
            .uri = "/color",
            .method = HTTP_POST,
            .handler = glue_postColor,
            .user_ctx = this,
    };

    auto err = httpd_register_uri_handler(server.m_hServer, &postColor);
    if (ESP_OK != err) {
        ESP_LOGE(TAG, "Failed to register POST /color uri handler %d", err);
        return err;
    }

    return ESP_OK;
}

#define TMP_BUFFER_SIZE 500
esp_err_t ColorHandler::_postColor(httpd_req_t *req) {
    ESP_LOGD(TAG, "_postColor()");

    esp_err_t result = ESP_FAIL;

    // Need a buffer, this seems reasonable
    char *szBuf = (char *) malloc(TMP_BUFFER_SIZE);
    if (!szBuf) {
        ESP_LOGE(TAG, "OOM allocating receive buffer");
        return ESP_FAIL;
    }

    auto read = httpd_req_recv(req, szBuf, TMP_BUFFER_SIZE - 1);
    if (read > 0) {
        // Null terminate it
        szBuf[read] = 0;
        ESP_LOGD(TAG, "set color = '%s'", szBuf);

        if (m_pDelegate) {
            // Start our simple parsing algorithm
            char *szTok = strtok(szBuf, ",");
            while(szTok) {
                // Store the current value and get one more
                int32_t index = strtol(szTok, nullptr, 16);
                szTok = strtok(nullptr, "\n");
                if (szTok) {
                    int32_t value = strtol(szTok, nullptr, 16);

                    uint8_t pVal[3];
                    pVal[0] = (uint8_t)(value >> 16);
                    pVal[1] = (uint8_t)(value >>  8);
                    pVal[2] = (uint8_t)(value      );
                    ESP_LOGI(TAG, "color %ld = %d, %d, %d", index, pVal[0], pVal[1], pVal[2]);
                    m_pDelegate->setColor((int16_t)index, pVal);

                    szTok = strtok(nullptr, ",");
                }
            }
        } else {
            ESP_LOGD(TAG, "No set color delegate");
        }

        httpd_resp_sendstr(req, "OK");
        result = ESP_OK;
    } else if (read == 0) {
        ESP_LOGW(TAG, "_postColor connection closed by peer");
    } else {
        ESP_LOGE(TAG, "_postColor error reading %d", read);
    }

    free(szBuf);

    return result;
}