//
// Created by Tom Seago on 2019-08-07.
//

#include "firmware_handler.h"
#include "http_server.h"

#include <string.h>
#include <sys/param.h>
#include <esp_ota_ops.h>

/* Max length a file path can have on storage */
//#define FILE_PATH_MAX (ESP_VFS_PATH_MAX + CONFIG_SPIFFS_OBJ_NAME_LEN)

#define SCRATCH_BUFSIZE 8192

#define TAG TAG_HTTPD

static esp_err_t glue_putHandler(httpd_req_t *req) {
    if (!req) return ESP_ERR_INVALID_ARG;

    return ((FirmwareHandler*)(req->user_ctx))->_putHandler(req);
}

esp_err_t FirmwareHandler::registerHandlers(HttpServer &server) {
    // Also do the equivalent initialization stuff - maybe want to come up with
    // a scheme for doing this better elsewhere
    //strcpy(this->basePath, "/spiffs/www");

    httpd_uri_t firmwareHandler = {
            .uri = "/firmware",
            .method = HTTP_POST,
            .handler = glue_putHandler,
            .user_ctx = this,
    };

    esp_err_t err;
    err = httpd_register_uri_handler(server.m_hServer, &firmwareHandler);
    if (ESP_OK != err) {
        ESP_LOGE(TAG, "Failed to register PUT uri handler %d", err);
        return err;
    }

    return ESP_OK;
}

// #define IS_FILE_EXT(filename, ext) \ //
//    (strcasecmp(&filename[strlen(filename) - sizeof(ext) + 1], ext) == 0)

/* Set HTTP response content type according to file extension */
//static esp_err_t set_content_type_from_file(httpd_req_t *req, const char *filename)
//{
//    if (IS_FILE_EXT(filename, ".pdf")) {
//        return httpd_resp_set_type(req, "application/pdf");
//    } else if (IS_FILE_EXT(filename, ".html")) {
//        return httpd_resp_set_type(req, "text/html");
//    } else if (IS_FILE_EXT(filename, ".jpeg")) {
//        return httpd_resp_set_type(req, "image/jpeg");
//    } else if (IS_FILE_EXT(filename, ".ico")) {
//        return httpd_resp_set_type(req, "image/x-icon");
//    }
//    /* This is a limited set only */
//    /* For any other type always set as plain text */
//    return httpd_resp_set_type(req, "text/plain");
//}
//
///* Copies the full path into destination buffer and returns
// * pointer to path (skipping the preceding base path) */
//static const char* get_path_from_uri(char *dest, const char *base_path, const char *uri, size_t destsize)
//{
//    const size_t base_pathlen = strlen(base_path);
//    size_t pathlen = strlen(uri);
//
//    const char *quest = strchr(uri, '?');
//    if (quest) {
//        pathlen = MIN(pathlen, quest - uri);
//    }
//    const char *hash = strchr(uri, '#');
//    if (hash) {
//        pathlen = MIN(pathlen, hash - uri);
//    }
//
//    if (base_pathlen + pathlen + 1 > destsize) {
//        /* Full path string won't fit into destination buffer */
//        return NULL;
//    }
//
//    /* Construct full path (base + path) */
//    strcpy(dest, base_path);
//    strlcpy(dest + base_pathlen, uri, pathlen + 1);
//
//    /* Return pointer to path, skipping the base */
//    return dest + base_pathlen;
//}

static void logFree(void *ctx) {
    ESP_LOGI(TAG, "Freeing a ctx");
    free(ctx);
}

esp_err_t FirmwareHandler::_putHandler(httpd_req_t *req) {
    ESP_LOGE(TAG, "==== OTA via HTTP PUT Started ====");

    // Could check filesize, but don't really care
    // req->content_len

    const esp_partition_t* updatePartition = esp_ota_get_next_update_partition(nullptr);
    if (!updatePartition) {
        ESP_LOGE(TAG, "Unable to get a next ota update partition");
        return ESP_FAIL;
    }

    esp_ota_handle_t otaHandle;
    if (ESP_ERROR_CHECK_WITHOUT_ABORT(esp_ota_begin(updatePartition, OTA_SIZE_UNKNOWN, &otaHandle)) != ESP_OK) {
        return ESP_FAIL;
    }
    ESP_LOGE(TAG, "ota_begin() call succeeded.");

    ESP_LOGI(TAG, "      type = %d",   updatePartition->type);
    ESP_LOGI(TAG, "   subtype = %d",   updatePartition->subtype);
    ESP_LOGI(TAG, "   address = 0x%lx", updatePartition->address);
    ESP_LOGI(TAG, "      size = %ld",   updatePartition->size);
    ESP_LOGI(TAG, "     label = %s",   updatePartition->label);

    if (!req->sess_ctx) {
        ESP_LOGI(TAG, "Creating a new session context");
        req->sess_ctx = malloc(SCRATCH_BUFSIZE);
        if (!req->sess_ctx) {
            ESP_LOGE(TAG, "Failed to create session chunk buffer");
            return ESP_FAIL;
        }
        req->free_ctx = logFree;
    }

    int received;
    int remaining = req->content_len;
    char* buf = (char*)req->sess_ctx;

    while (remaining > 0) {
        ESP_LOGI(TAG, "Remaining OTA size : %d", remaining);
        /* Receive the file part by part into a buffer */
        if ((received = httpd_req_recv(req, buf, MIN(remaining, SCRATCH_BUFSIZE))) <= 0) {
            if (received == HTTPD_SOCK_ERR_TIMEOUT) {
                /* Retry if timeout occurred */
                continue;
            }

            /* In case of unrecoverable error,
             * close and delete the unfinished file*/
            esp_ota_end(otaHandle);

            ESP_LOGE(TAG, "Firmware reception failed!");
            /* Respond with 500 Internal Server Error */
            httpd_resp_send_err(req, HTTPD_500_INTERNAL_SERVER_ERROR, "Failed to receive firmware");
            return ESP_FAIL;
        }

        /* Write buffer content to file on storage */
        if (esp_ota_write(otaHandle, buf, received) != ESP_OK) {
            esp_ota_end(otaHandle);
            ESP_LOGE(TAG, "Firmware write failed!");
            /* Respond with 500 Internal Server Error */
            httpd_resp_send_err(req, HTTPD_500_INTERNAL_SERVER_ERROR, "Failed to write file to storage");
            return ESP_FAIL;
        }

        /* Keep track of remaining size of
         * the file left to be uploaded */
        remaining -= received;
    }

    auto endResult = esp_ota_end(otaHandle);
    if (endResult != ESP_OK) {
        ESP_LOGE(TAG, "OTA Failed with %d", endResult);

        /* Respond with an empty chunk to signal HTTP response completion */
        httpd_resp_send_chunk(req, NULL, 0);
        return ESP_OK;
    }

    ESP_LOGE(TAG, "==== Valid OTA received ====");
    auto setResult = esp_ota_set_boot_partition(updatePartition);
    if (setResult != ESP_OK) {
        ESP_LOGE(TAG, "Setting the OTA boot partition failed: %d", endResult);

        /* Respond with an empty chunk to signal HTTP response completion */
        httpd_resp_send_chunk(req, NULL, 0);
        return ESP_OK;
    }

    ESP_LOGE(TAG, "Boot partition set, Triggering restart");
    brain_restart(500);

    httpd_resp_send_chunk(req, NULL, 0);
    return ESP_OK;
}
