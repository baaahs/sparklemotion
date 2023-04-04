//
// Created by Tom Seago on 12/30/19.
//

#pragma once


#include "brain_common.h"

#include "esp_vfs.h"
#include "esp_http_server.h"

class HttpServer;

class WordsHandlerListener {
public:
    virtual void gotWords(const char* buf, size_t len) = 0;
};

class WordsHandler {
public:
    esp_err_t registerHandlers(HttpServer &server);

    esp_err_t _postWords(httpd_req_t *req);

    void setWordsHandlerListener(WordsHandlerListener* listener) {
        m_pListener = listener;
    }

private:
    WordsHandlerListener* m_pListener = nullptr;
};