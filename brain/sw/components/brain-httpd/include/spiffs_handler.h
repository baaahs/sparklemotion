//
// Created by Tom Seago on 2019-06-02.
//

#pragma once

#include "brain_common.h"

#include "esp_vfs.h"
#include "esp_http_server.h"

class HttpServer;

class SpiffsHandler {
public:
    esp_err_t registerHandlers(HttpServer& server);

    esp_err_t getHandler(httpd_req_t *req);

private:
    /* Base path of file storage */
    char basePath[ESP_VFS_PATH_MAX + 1];
};

