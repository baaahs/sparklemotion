//
// Created by Tom Seago on 2019-06-02.
//

#pragma once

#include "brain_common.h"

#include "spiffs_handler.h"
#include "firmware_handler.h"
#include "config_handler.h"

class HttpServer {
public:
    httpd_handle_t m_hServer;

    void start();

private:
    SpiffsHandler m_spiffs;
    FirmwareHandler m_firmware;
    ConfigHandler m_config;
};
