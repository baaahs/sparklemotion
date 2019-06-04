//
// Created by Tom Seago on 2019-06-02.
//

#ifndef PLAYA_HTTP_SERVER_H
#define PLAYA_HTTP_SERVER_H

#include "esp_err.h"

#include "spiffs_handler.h"

class HttpServer {
public:
    httpd_handle_t server;

    esp_err_t start();

private:
    SpiffsHandler spiffs;
};


#endif //PLAYA_HTTP_SERVER_H
