#include "net_priv.h"

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"

#include "esp_err.h"
#include "tcpip_adapter.h"

#include "ethernet.h"
#include "wifi.h"

static Ethernet ethernet;

void static task_net_main(void* pvParameters) {
    TickType_t xLastWakeTime;
    const TickType_t xFrequency = 10 * xPortGetTickRateHz();

    // Initialization
    tcpip_adapter_init();

    // Both of these probably need to be moved to something like a thread
    // or an event loop thing where rather than only initializing them at
    // booth we can re-init them later.
    // TODO: Make network init more robust.
    //ethernet_init();
    ethernet.start();

//    wifi_init();

    xLastWakeTime = xTaskGetTickCount();

    // Network input handling
    while(1) {
        // Do network stuff
        vTaskDelayUntil( &xLastWakeTime, xFrequency );
        ESP_LOGI(TAG, "Network task tick");
    }    

    // Just in case we ever exit, we're supposed to do this
    vTaskDelete(NULL);
}

void task_net_create() {

    static uint8_t vParameters = 0;
    TaskHandle_t tHandle = NULL;

    BaseType_t tcResult;

    ESP_LOGI(TAG, "Starting network task...");
    // vTaskDelay(5 * xPortGetTickRateHz());

    tcResult  = xTaskCreate(task_net_main, "net", TASK_NET_STACK_SIZE, 
        &vParameters, TASK_NET_PRIORITY, &tHandle);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create net task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "Network task started");
    }

}
