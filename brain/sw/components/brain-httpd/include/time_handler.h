//
// Created by Tom Seago on 12/30/19.
//

#pragma once


#include "brain_common.h"

#include "esp_vfs.h"
#include "esp_http_server.h"

class HttpServer;

class TimeHandler {
public:
    esp_err_t registerHandlers(HttpServer &server);

    esp_err_t _postCurrentTime(httpd_req_t *req);

private:
};