//
// Created by Tom Seago on 12/30/19.
//


#include "words_handler.h"
#include "http_server.h"

#define TAG TAG_HTTPD

static esp_err_t glue_postWords(httpd_req_t *req) {
    if (!req) return ESP_ERR_INVALID_ARG;

    return ((WordsHandler *) (req->user_ctx))->_postWords(req);
}

esp_err_t WordsHandler::registerHandlers(HttpServer &server) {
    httpd_uri_t postWords = {
            .uri = "/words",
            .method = HTTP_POST,
            .handler = glue_postWords,
            .user_ctx = this,
    };

    auto err = httpd_register_uri_handler(server.m_hServer, &postWords);
    if (ESP_OK != err) {
        ESP_LOGE(TAG, "Failed to register POST /words uri handler %d", err);
        return err;
    }

    return ESP_OK;
}

#define TMP_BUFFER_SIZE 500
esp_err_t WordsHandler::_postWords(httpd_req_t *req) {
    ESP_LOGD(TAG, "_postWords()");

    esp_err_t result = ESP_FAIL;

    // Need a buffer, this seems reasonable
    char *szBuf = (char *) malloc(TMP_BUFFER_SIZE);
    if (!szBuf) {
        ESP_LOGE(TAG, "OOM allocating receive buffer");
        return ESP_FAIL;
    }

    auto read = httpd_req_recv(req, szBuf, TMP_BUFFER_SIZE);
    if (read > 0) {
        // The thing we got isn't null terminated so have to do this
        // to log it
        {
            char *szTemp = (char *) malloc(read + 1);
            if (szTemp) {
                memcpy(szTemp, szBuf, read);
                szTemp[read] = 0;
                ESP_LOGD(TAG, "words to set = '%s'", szTemp);
                free(szTemp);
            }
        }

        if (m_pListener) {
            m_pListener->gotWords(szBuf, read);
        }

        httpd_resp_sendstr(req, "OK");
        result = ESP_OK;
    } else if (read == 0) {
        ESP_LOGW(TAG, "_postCurrentTime connection closed by peer");
    } else {
        ESP_LOGE(TAG, "_postCurrentTime error reading %d", read);
    }

    free(szBuf);

    return result;
}