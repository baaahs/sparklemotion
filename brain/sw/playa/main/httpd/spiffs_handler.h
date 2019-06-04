//
// Created by Tom Seago on 2019-06-02.
//

#ifndef PLAYA_SPIFFS_HANDLER_H
#define PLAYA_SPIFFS_HANDLER_H

#include "esp_err.h"
#include "esp_vfs.h"
#include "esp_http_server.h"

class HttpServer;

class SpiffsHandler {
    /* Base path of file storage */
    char basePath[ESP_VFS_PATH_MAX + 1];

public:
    esp_err_t registerHandlers(HttpServer& server);

    esp_err_t getHandler(httpd_req_t *req);
};


#endif //PLAYA_SPIFFS_HANDLER_H
