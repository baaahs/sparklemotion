//
// Created by Tom Seago on 2019-08-07.
//

#include "brain_common.h"

#define TAG TAG_COMMON

#include <esp_spiffs.h>

/* Function to initialize SPIFFS */
esp_err_t brain_init_spiffs() {
    ESP_LOGI(TAG, "Initializing SPIFFS");

    esp_vfs_spiffs_conf_t conf = {
            .base_path = "/spiffs",
            .partition_label = NULL,
            .max_files = 5,   // Maximum number of simultaneously open files
            .format_if_mount_failed = true
    };

    esp_err_t ret = esp_vfs_spiffs_register(&conf);
    if (ret != ESP_OK) {
        if (ret == ESP_FAIL) {
            ESP_LOGE(TAG, "Failed to mount or format filesystem");
        } else if (ret == ESP_ERR_NOT_FOUND) {
            ESP_LOGE(TAG, "Failed to find SPIFFS partition");
        } else {
            ESP_LOGE(TAG, "Failed to initialize SPIFFS (%s)", esp_err_to_name(ret));
        }
        return ret;
    }

    size_t total = 0, used = 0;
    ret = esp_spiffs_info(NULL, &total, &used);
    if (ret != ESP_OK) {
        ESP_LOGE(TAG, "Failed to get SPIFFS partition information (%s)", esp_err_to_name(ret));
        return ret;
    }

    ESP_LOGI(TAG, "Partition size: total: %d, used: %d", total, used);
    return ESP_OK;
}

static void restart_task(void* pParam) {
    uint32_t millis = (uint32_t)pParam;

    TickType_t xLastWakeTime = xTaskGetTickCount();
    auto delayTime = pdMS_TO_TICKS(millis);

    ESP_LOGE(TAG, "Delaying restart by %d ms", millis);
    vTaskDelayUntil(&xLastWakeTime, delayTime);

    ESP_LOGE(TAG, "Time is up! Restart!!!!");
    esp_restart();

    // Unnecessary but for completeness...
    vTaskDelete(nullptr);
}

void brain_restart(uint32_t delayMillis) {
    if (!delayMillis) {
        ESP_LOGE(TAG, "Restarting without delay");
        esp_restart();
        return; // yeah right....
    }

    // If the task creation fails just restart right away. Oh well we tried!
    auto result = xTaskCreate(restart_task, "restart", 4096, (void*)delayMillis, TaskDef::MAX_PRIORITY, nullptr);
    if (result != pdPASS) {
        ESP_LOGE(TAG, "Delayed restart task creation failed. Restarting immediately");
        esp_restart();
    }
}