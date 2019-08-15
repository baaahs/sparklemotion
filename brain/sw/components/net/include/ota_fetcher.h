//
// Created by Tom Seago on 2019-08-15.
//

#pragma once

#include "brain_common.h"
#include <esp_ota_ops.h>
#include <esp_http_client.h>

/**
 * Uses the lower level http client to go out and fetch a new
 * image. This is basically what esp_https_ota is supposed to
 * do but we have found it to generate a lot of seemingly
 * incorrect errors. Uploaded via the httpd seems more
 * reliable, so hence doing this ourselves.
 */
class OtaFetcher {
public:
    void fetchFromUrl(const char* szBuf);

    void _fetchTask();
    esp_err_t _httpEvent(esp_http_client_event_t* evt);

private:
    char *m_szUrl;
    char *m_scratch;
    int m_contentLength;
    const esp_partition_t* m_updatePartition;


    esp_ota_handle_t m_otaHandle;
    esp_http_client_handle_t m_httpHandle;

    void cleanup();
};