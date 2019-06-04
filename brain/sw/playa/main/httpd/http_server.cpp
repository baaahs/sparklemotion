//
// Created by Tom Seago on 2019-06-02.
//

#include "http_server.h"

#include "esp_log.h"

static const char *TAG = "#HttpServer";

esp_err_t HttpServer::start() {
    esp_err_t err;

    this->server = NULL;
    httpd_config_t config = HTTPD_DEFAULT_CONFIG();
    config.uri_match_fn = httpd_uri_match_wildcard;

    ESP_LOGI(TAG, "Starting HTTP Server");
    err = httpd_start(&this->server, &config);
    if (ESP_OK != err) {
        ESP_LOGE(TAG, "Failed to start the http server: %d", err);
        return err;
    }

    ESP_LOGI(TAG, "Registering handlers");
    err = spiffs.registerHandlers(*this);
    return err;
}