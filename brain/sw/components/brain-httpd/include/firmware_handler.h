//
// Created by Tom Seago on 2019-08-07.
//

#pragma once

#include "brain_common.h"

#include "esp_vfs.h"
#include "esp_http_server.h"

class HttpServer;

class FirmwareHandler {
public:
    esp_err_t registerHandlers(HttpServer& server);

    esp_err_t _putHandler(httpd_req_t *req);

private:
    /* Base path of file storage */
    char m_basePath[ESP_VFS_PATH_MAX + 1];
};

