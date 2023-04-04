//
// Created by Tom Seago on 2019-08-15.
//

#include <ota_fetcher.h>
#include <brain_common.h>

#include <string.h>
#include <sys/param.h>
#include "errno.h"
#include <sysmon.h>


//#define SCRATCH_BUFSIZE 8192
#define SCRATCH_BUFSIZE 1024

#define TAG TAG_OTA

////////////////////////////////////////////////////////////
// Glue functions that let us use C++ functions with the IDF
// C apis.

void glue_fetchTask(void *pArg) {
    ((OtaFetcher*)pArg)->_fetchTask();
}

esp_err_t glue_httpEvent(esp_http_client_event_t *evt) {
    return ((OtaFetcher*)evt->user_data)->_httpEvent(evt);
}

////////////////////////////////////////////////////////////


void OtaFetcher::fetchFromUrl(const char *szBuf, bool fakeWrites) {

    gettimeofday(&m_startTime, nullptr);

    if (!szBuf || strlen(szBuf) == 0) {
        ESP_LOGE(TAG, "No buffer supplied");
        return;
    }

    if (m_szUrl) {
        ESP_LOGE(TAG, "Looks like we have already started OTA. Not doing it again");
        return;
    }

    m_szUrl = strdup(szBuf);
    if (!m_szUrl) {
        ESP_LOGE(TAG, "Unable to duplicate szBuf. No ota");
        return;
    }

    m_fakeWrites = fakeWrites;

    TaskDef def = DefaultBrainTasks.otaFetcher;
    auto tcResult = def.createTask(glue_fetchTask, this, nullptr);
    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create otaFetcher %d", tcResult);
        return;
    }

}

void OtaFetcher::_fetchTask() {

    esp_err_t err = ESP_OK;
    esp_http_client_config_t httpCfg{};

    if (!m_szUrl) {
        ESP_LOGE(TAG, "Trying to start ota fetch but url is null");
        cleanup();
        return;
    }
    ESP_LOGE(TAG, "Starting OTA fetch from URL %s", m_szUrl);

    m_scratch = (char *) malloc(SCRATCH_BUFSIZE);
    if (!m_scratch) {
        ESP_LOGE(TAG, "OTA OOM allocating scratch");
        cleanup();
        return;
    }

    if (m_fakeWrites) {
        ESP_LOGE(TAG, "Fake writes are enabled. Not starting OTA api");
    } else {
        m_updatePartition = esp_ota_get_next_update_partition(nullptr);
        if (!m_updatePartition) {
            ESP_LOGE(TAG, "Unable to get a next ota update partition");
            cleanup();
            return;
        }

        if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_ota_begin(m_updatePartition, OTA_SIZE_UNKNOWN, &m_otaHandle)) != ESP_OK) {
            cleanup();
            return;
        }
        ESP_LOGE(TAG, "ota_begin() call succeeded.");

        ESP_LOGI(TAG, "      type = %d", m_updatePartition->type);
        ESP_LOGI(TAG, "   subtype = %d", m_updatePartition->subtype);
        ESP_LOGI(TAG, "   address = 0x%lx", m_updatePartition->address);
        ESP_LOGI(TAG, "      size = %ld", m_updatePartition->size);
        ESP_LOGI(TAG, "     label = %s", m_updatePartition->label);
    }

    // Get our http client setup
    httpCfg.url = m_szUrl;
    httpCfg.event_handler = glue_httpEvent;
    httpCfg.user_data = this;

    m_httpHandle = esp_http_client_init(&httpCfg);
    if (!m_httpHandle) {
        ESP_LOGE(TAG, "Failed to create http client");
        cleanup();
        return;
    }

    err = esp_http_client_open(m_httpHandle, 0);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "OTA failed to open client: %s", esp_err_to_name(err));
        cleanup();
        return;
    }

    int m_contentLength = esp_http_client_fetch_headers(m_httpHandle);

    // I feel like we should be looking for a 200 status code at this point...

    int total = 0;
    int readLen = 0;
    err = ESP_OK;
    ESP_LOGI(TAG, "OTA m_contentLength = %d", m_contentLength);

    while (err == ESP_OK && total < m_contentLength) {
        // Read some more...]
        int toRead = MIN(SCRATCH_BUFSIZE, m_contentLength - total);

        gSysMon.startTiming(TIMING_OTA_HTTP_READ);
        readLen = esp_http_client_read(m_httpHandle, m_scratch, toRead);
        gSysMon.endTiming(TIMING_OTA_HTTP_READ);

        ESP_LOGW(TAG, "toRead=%d  total=%d  m_contentLength=%d  readLen=%d",
                toRead, total, m_contentLength, readLen);
        if (readLen > 0) {
            // Write this to ota
            if (!m_fakeWrites) {
                gSysMon.startTiming(TIMING_OTA_WRITE);
                err = esp_ota_write(m_otaHandle, m_scratch, readLen);
                gSysMon.endTiming(TIMING_OTA_WRITE);

                if (err != ESP_OK) {
                    ESP_LOGE(TAG, "OTA Write error %s", esp_err_to_name(err));
                }
            }
            total += readLen;
        } else if (readLen == 0) {
            ESP_LOGE(TAG, "Got readLen=0, errno=%d %s", errno, strerror(errno));
            if (errno != 0) {
                err = ESP_FAIL;
            }
        } else {
            ESP_LOGE(TAG, "Seems like the server closed the connection. readLen was 0. errno=%d : %s",
                    errno, strerror(errno));
            err = ESP_FAIL;
        }
    }

    gettimeofday(&m_endTime, nullptr);

    time_t elapsed = m_endTime.tv_sec - m_startTime.tv_sec;
    ESP_LOGE(TAG, "Read complete. %d bytes in %lld seconds = %lld bytes/sec",
            total, elapsed, total / elapsed);

    esp_http_client_close(m_httpHandle);
    esp_http_client_cleanup(m_httpHandle);

    cleanup();
}

void OtaFetcher::cleanup() {
    if (m_szUrl) {
        free(m_szUrl);
        m_szUrl = nullptr;
    }

    if (m_scratch) {
        free(m_scratch);
        m_scratch = nullptr;
    }

    if (m_otaHandle) {
        ESP_LOGW(TAG, "OTA Done. End it");
        auto err = esp_ota_end(m_otaHandle);
        if (err != ESP_OK) {
            ESP_LOGE(TAG, "OTA End error result = %d", err);
        } else {
            ESP_LOGE(TAG, "==== Valid OTA received ====");
            auto setResult = esp_ota_set_boot_partition(m_updatePartition);
            if (setResult != ESP_OK) {
                ESP_LOGE(TAG, "Setting the OTA boot partition failed: %d", setResult);
            } else {
                ESP_LOGE(TAG, "Boot partition updated! Awesome-sauce");
            }
        }
    }

    // Always restart here
    gSysMon.outputToLog(); // Make sure we get our log
    ESP_LOGE(TAG, "Always restart after OTA stuff");
    brain_restart(100);

    vTaskDelete(nullptr);
}

esp_err_t OtaFetcher::_httpEvent(esp_http_client_event_t* evt) {
    switch(evt->event_id) {
        case HTTP_EVENT_ERROR:
            ESP_LOGE(TAG, "OTA HTTP_EVENT_ERROR");
            break;

        case HTTP_EVENT_ON_CONNECTED:
            ESP_LOGI(TAG, "OTA HTTP_EVENT_ON_CONNECTED");
            break;
        case HTTP_EVENT_ON_HEADER:
            ESP_LOGD(TAG, "OTA HTTP_EVENT_ON_HEADER key='%s' value='%s'", evt->header_key, evt->header_value);
            break;

        case HTTP_EVENT_ON_DATA: {
//            ESP_LOGD(TAG, "HTTP_EVENT_ON_DATA data_len=%d", evt->data_len);
//            auto err = esp_ota_write(m_otaHandle, evt->data, evt->data_len);
//            if (err != ESP_OK) {
//                ESP_LOGE(TAG, "Error during ota write %d", err);
//                // TODO: Cleanup
//            }
        }
            break;

        case HTTP_EVENT_ON_FINISH:
            ESP_LOGD(TAG, "OTA HTTP_EVENT_ON_FINISH");
            break;

        case HTTP_EVENT_DISCONNECTED:
            ESP_LOGD(TAG, "OTA HTTP_EVENT_DISCONNECTED");
            break;

        default:
            ESP_LOGD(TAG, "OTA http event other");
            break;
    }

    return ESP_OK;
}
