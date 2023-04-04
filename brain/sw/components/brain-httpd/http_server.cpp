//
// Created by Tom Seago on 2019-06-02.
//

#include "http_server.h"

#define TAG TAG_HTTPD

void HttpServer::start() {
    m_hServer = nullptr;
    httpd_config_t config = HTTPD_DEFAULT_CONFIG();
    config.uri_match_fn = httpd_uri_match_wildcard;
    config.task_priority = DefaultBrainTasks.httpd.priority;
    config.core_id = DefaultBrainTasks.httpd.coreId;
    config.stack_size = DefaultBrainTasks.httpd.stack;

    ESP_LOGI(TAG, "Starting HTTP Server");
    if (ESP_ERROR_CHECK_WITHOUT_ABORT(httpd_start(&m_hServer, &config)) != ESP_OK) return;

    ESP_LOGI(TAG, "Registering handlers");
    ESP_ERROR_CHECK_WITHOUT_ABORT(m_firmware.registerHandlers(*this));
    ESP_ERROR_CHECK_WITHOUT_ABORT(m_config.registerHandlers(*this));

    ESP_ERROR_CHECK_WITHOUT_ABORT(m_time.registerHandlers(*this));
    ESP_ERROR_CHECK_WITHOUT_ABORT(m_words.registerHandlers(*this));
    ESP_ERROR_CHECK_WITHOUT_ABORT(m_color.registerHandlers(*this));

    // Because SPIFFS registers /* it has to be last or the other
    // registrations will fail.
    ESP_ERROR_CHECK_WITHOUT_ABORT(m_spiffs.registerHandlers(*this));
}

void
HttpServer::setWordsHandlerListener(WordsHandlerListener* listener) {
    m_words.setWordsHandlerListener(listener);
}

void
HttpServer::setColorHandlerDelegate(ColorHandlerDelegate* delegate) {
    m_color.setColorHandlerDelegate(delegate);
}
