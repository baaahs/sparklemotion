//
// Created by Tom Seago on 2019-08-07.
//

#pragma once

#include "brain_common.h"

#include "esp_vfs.h"
#include "esp_http_server.h"

class HttpServer;

class ConfigHandler {
public:
    esp_err_t registerHandlers(HttpServer& server);

    esp_err_t _getSysData(httpd_req_t *req);
    esp_err_t _putConfig(httpd_req_t *req);
    esp_err_t _delLife(httpd_req_t *req);

private:
};

