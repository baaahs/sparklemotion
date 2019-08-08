//
// Created by Tom Seago on 2019-08-07.
//

#include "config_handler.h"
#include "http_server.h"

#define TAG TAG_HTTPD

#define OK_FALSE_JSON "{\"ok\":false}"
#define OK_TRUE_JSON "{\"ok\":true}"

static esp_err_t glue_getSysData(httpd_req_t *req) {
    if (!req) return ESP_ERR_INVALID_ARG;

    return ((ConfigHandler *) (req->user_ctx))->_getSysData(req);
}

static esp_err_t glue_putConfig(httpd_req_t *req) {
    if (!req) return ESP_ERR_INVALID_ARG;

    return ((ConfigHandler *) (req->user_ctx))->_putConfig(req);
}

static esp_err_t glue_delLife(httpd_req_t *req) {
    if (!req) return ESP_ERR_INVALID_ARG;

    return ((ConfigHandler *) (req->user_ctx))->_delLife(req);
}

esp_err_t ConfigHandler::registerHandlers(HttpServer &server) {

    /////
    httpd_uri_t getConfig = {
            .uri = "/sysdata.json",
            .method = HTTP_GET,
            .handler = glue_getSysData,
            .user_ctx = this,
    };

    auto err = httpd_register_uri_handler(server.m_hServer, &getConfig);
    if (ESP_OK != err) {
        ESP_LOGE(TAG, "Failed to register GET /sysdata.json uri handler %d", err);
        return err;
    }

    ////
    httpd_uri_t putConfig = {
            .uri = "/config.json",
            .method = HTTP_PUT,
            .handler = glue_putConfig,
            .user_ctx = this,
    };

    err = httpd_register_uri_handler(server.m_hServer, &putConfig);
    if (ESP_OK != err) {
        ESP_LOGE(TAG, "Failed to register PUT /config.json uri handler %d", err);
        return err;
    }

    ////
    httpd_uri_t delLife = {
            .uri = "/life",
            .method = HTTP_DELETE,
            .handler = glue_delLife,
            .user_ctx = this,
    };

    err = httpd_register_uri_handler(server.m_hServer, &delLife);
    if (ESP_OK != err) {
        ESP_LOGE(TAG, "Failed to register DELETE /life uri handler %d", err);
        return err;
    }

    return ESP_OK;
}

#define TMP_BUFFER_SIZE 500
esp_err_t ConfigHandler::_getSysData(httpd_req_t *req) {
    ESP_LOGD(TAG, "_getSysData()");
    // Need a buffer, this seems reasonable
    char* szBuf = (char*)malloc(TMP_BUFFER_SIZE);
    if (!szBuf) {
        ESP_LOGE(TAG, "OOM allocating response buffer");
        return ESP_FAIL;
    }

    sprintf(szBuf, "{"
                   "\"mac\":\"%s\","
                   "\"rando\":\"Hello 3\","
                   "\"config\":",
                   GlobalConfig.macStr());

    size_t used = strlen(szBuf);
    ESP_ERROR_CHECK_WITHOUT_ABORT(GlobalConfig.printTo(szBuf + used, TMP_BUFFER_SIZE - used));
    strcat(szBuf, "}");
    ESP_LOGD(TAG, "Config string: '%s'", szBuf);

    httpd_resp_set_type(req, "application/json");
    httpd_resp_sendstr(req, szBuf);

    free(szBuf);

    return ESP_OK;
}

esp_err_t ConfigHandler::_putConfig(httpd_req_t *req) {
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
        // Have to print this before we parse it because parsing will
        // munge up the string. However, it's not null terminated, so um,
        // this is a little complex
        {
            char* szTemp = (char*)malloc(read + 1);
            if (szTemp) {
                memcpy(szTemp, szBuf, read);
                szTemp[read] = 0;
                ESP_LOGD(TAG, "setting config to %s", szTemp);
                free(szTemp);
            }
        }

        httpd_resp_set_type(req, "application/json");
        auto parseResult = GlobalConfig.parseFrom(szBuf, read);
        if (parseResult != ESP_OK) {
            ESP_LOGE(TAG, "Error parsing attempted config %d", parseResult);
            httpd_resp_set_status(req, "400 Bad JSON Data");
            httpd_resp_sendstr(req, OK_FALSE_JSON);
        } else {
            auto saveResult = GlobalConfig.save();
            if (saveResult != ESP_OK) {
                ESP_LOGE(TAG, "Error saving new config %d", saveResult);
                httpd_resp_set_status(req, "500 Error saving config");
                httpd_resp_sendstr(req, OK_FALSE_JSON);
            } else {
                httpd_resp_sendstr(req, OK_FALSE_JSON);
            }
        }
    } else if (read == 0) {
        ESP_LOGW(TAG, "_putConfig connection closed by peer");
    } else {
        ESP_LOGE(TAG, "_putConfig error reading config %d", read);
    }

    free(szBuf);

    return result;
}


esp_err_t ConfigHandler::_delLife(httpd_req_t *req) {
    ESP_LOGD(TAG, "_delLife()");

    // 500 Milliseconds seems like a long enough time to get a response
    // back to a browser saying we heard them and we're going to do it.
    brain_restart(500);

    httpd_resp_sendstr(req, OK_TRUE_JSON);

    return ESP_OK;
}