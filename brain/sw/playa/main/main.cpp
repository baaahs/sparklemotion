#include <stdio.h>
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_system.h"
#include "esp_event_loop.h"
#include "esp_err.h"
#include "esp_event.h"
#include "esp_log.h"
#include "esp_eth.h"
#include "esp32/rom/gpio.h"
#include "tcpip_adapter.h"
#include "driver/gpio.h"
#include "driver/periph_ctrl.h"

#include "net/task_net.h"

extern "C" void app_main()
{
    // Basic setup
    ESP_ERROR_CHECK(esp_event_loop_create_default());

    // Start the various tasks
    task_net_create();

    // Hang out doing nothing or exit? I think it's ok to exit...
    // while(1) {

    // }
}
