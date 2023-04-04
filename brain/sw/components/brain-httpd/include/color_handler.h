//
// Created by Tom Seago on 1/26/20.
//


#pragma once


#include "brain_common.h"

#include "esp_vfs.h"
#include "esp_http_server.h"

class HttpServer;

class ColorHandlerDelegate {
public:
    virtual uint16_t getColorCount() = 0;
    virtual void getColor(int16_t index, uint8_t* rgb) = 0;
    virtual void setColor(int16_t index, uint8_t* rgb) = 0;
};

class ColorHandler {
public:
    esp_err_t registerHandlers(HttpServer &server);

    esp_err_t _postColor(httpd_req_t *req);

    void setColorHandlerDelegate(ColorHandlerDelegate* delegate) {
        m_pDelegate = delegate;
    }

private:
    ColorHandlerDelegate* m_pDelegate = nullptr;
};