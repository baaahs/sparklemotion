//
// Created by Tom Seago on 2019-06-02.
//

#include "spiffs_handler.h"
#include "http_server.h"

#include <string.h>
#include <sys/param.h>
//#include <sys/unistd.h>
//#include <sys/stat.h>
#include "esp_log.h"
#include "esp_spiffs.h"

/* Max length a file path can have on storage */
#define FILE_PATH_MAX (ESP_VFS_PATH_MAX + CONFIG_SPIFFS_OBJ_NAME_LEN)

#define SCRATCH_BUFSIZE 8192


static const char *TAG = "#SpiffsHandler";

static esp_err_t sGetHandler(httpd_req_t *req) {
    if (!req) return ESP_ERR_INVALID_ARG;

    return ((SpiffsHandler*)(req->user_ctx))->getHandler(req);
}

esp_err_t SpiffsHandler::registerHandlers(HttpServer &server) {
    // Also do the equivalent initialization stuff - maybe want to come up with
    // a scheme for doing this better elsewhere
    strcpy(this->basePath, "/spiffs");


    httpd_uri_t fileDownload = {
            .uri = "/*",
            .method = HTTP_GET,
            .handler = sGetHandler,
            .user_ctx = this,
    };

    esp_err_t err;
    err = httpd_register_uri_handler(server.server, &fileDownload);
    if (ESP_OK != err) {
        ESP_LOGE(TAG, "Failed to register GET uri handler %d", err);
        return err;
    }

    return ESP_OK;
}

#define IS_FILE_EXT(filename, ext) \
    (strcasecmp(&filename[strlen(filename) - sizeof(ext) + 1], ext) == 0)

/* Set HTTP response content type according to file extension */
static esp_err_t set_content_type_from_file(httpd_req_t *req, const char *filename)
{
    if (IS_FILE_EXT(filename, ".pdf")) {
        return httpd_resp_set_type(req, "application/pdf");
    } else if (IS_FILE_EXT(filename, ".html")) {
        return httpd_resp_set_type(req, "text/html");
    } else if (IS_FILE_EXT(filename, ".jpeg")) {
        return httpd_resp_set_type(req, "image/jpeg");
    } else if (IS_FILE_EXT(filename, ".ico")) {
        return httpd_resp_set_type(req, "image/x-icon");
    }
    /* This is a limited set only */
    /* For any other type always set as plain text */
    return httpd_resp_set_type(req, "text/plain");
}

/* Copies the full path into destination buffer and returns
 * pointer to path (skipping the preceding base path) */
static const char* get_path_from_uri(char *dest, const char *base_path, const char *uri, size_t destsize)
{
    const size_t base_pathlen = strlen(base_path);
    size_t pathlen = strlen(uri);

    const char *quest = strchr(uri, '?');
    if (quest) {
        pathlen = MIN(pathlen, quest - uri);
    }
    const char *hash = strchr(uri, '#');
    if (hash) {
        pathlen = MIN(pathlen, hash - uri);
    }

    if (base_pathlen + pathlen + 1 > destsize) {
        /* Full path string won't fit into destination buffer */
        return NULL;
    }

    /* Construct full path (base + path) */
    strcpy(dest, base_path);
    strlcpy(dest + base_pathlen, uri, pathlen + 1);

    /* Return pointer to path, skipping the base */
    return dest + base_pathlen;
}

void logFree(void *ctx) {
    ESP_LOGI(TAG, "Freeing a ctx");
    free(ctx);
}

esp_err_t SpiffsHandler::getHandler(httpd_req_t *req) {
    char filepath[FILE_PATH_MAX];
    FILE *fd = NULL;
    struct stat file_stat;

    const char *filename = get_path_from_uri(filepath, this->basePath,
                                             req->uri, sizeof(filepath));
    if (!filename) {
        ESP_LOGE(TAG, "Filename is too long");
        /* Respond with 500 Internal Server Error */
        httpd_resp_send_err(req, HTTPD_500_INTERNAL_SERVER_ERROR, "Filename too long");
        return ESP_FAIL;
    }

    /* If name has trailing '/', respond with directory contents */
//    if (filename[strlen(filename) - 1] == '/') {
//        return http_resp_dir_html(req, filepath);
//    }

    if (stat(filepath, &file_stat) == -1) {
        /* If file not present on SPIFFS check if URI
         * corresponds to one of the hardcoded paths */
//        if (strcmp(filename, "/index.html") == 0) {
//            return index_html_get_handler(req);
//        } else if (strcmp(filename, "/favicon.ico") == 0) {
//            return favicon_get_handler(req);
//        }
        ESP_LOGE(TAG, "Failed to stat file : %s", filepath);
        /* Respond with 404 Not Found */
        httpd_resp_send_err(req, HTTPD_404_NOT_FOUND, "File does not exist");
        return ESP_FAIL;
    }

    fd = fopen(filepath, "r");
    if (!fd) {
        ESP_LOGE(TAG, "Failed to read existing file : %s", filepath);
        /* Respond with 500 Internal Server Error */
        httpd_resp_send_err(req, HTTPD_500_INTERNAL_SERVER_ERROR, "Failed to read existing file");
        return ESP_FAIL;
    }

    ESP_LOGI(TAG, "Sending file : %s (%ld bytes)...", filename, file_stat.st_size);
    set_content_type_from_file(req, filename);

    if (!req->sess_ctx) {
        ESP_LOGI(TAG, "Creating a new session context");
        req->sess_ctx = malloc(SCRATCH_BUFSIZE);
        if (!req->sess_ctx) {
            ESP_LOGE(TAG, "Failed to create session chunk buffer");
            return ESP_FAIL;
        }
        req->free_ctx = logFree;
    }

    /* Retrieve the pointer to scratch buffer for temporary storage */
    char *chunk = (char*)req->sess_ctx;
    size_t chunksize;
    do {
        /* Read file in chunks into the scratch buffer */
        chunksize = fread(chunk, 1, SCRATCH_BUFSIZE, fd);

        /* Send the buffer contents as HTTP response chunk */
        if (httpd_resp_send_chunk(req, chunk, chunksize) != ESP_OK) {
            fclose(fd);
            ESP_LOGE(TAG, "File sending failed!");
            /* Abort sending file */
            httpd_resp_sendstr_chunk(req, NULL);
            /* Respond with 500 Internal Server Error */
            httpd_resp_send_err(req, HTTPD_500_INTERNAL_SERVER_ERROR, "Failed to send file");
            return ESP_FAIL;
        }

        /* Keep looping till the whole file is sent */
    } while (chunksize != 0);

    /* Close file after sending complete */
    fclose(fd);
    ESP_LOGI(TAG, "File sending complete");

    /* Respond with an empty chunk to signal HTTP response completion */
    httpd_resp_send_chunk(req, NULL, 0);
    return ESP_OK;
}