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

#include "task_net.h"
#include "httpd/task_httpd.h"

#include "brain.h"

static Brain brain;

extern "C" void app_main()
{
    // Basic setup

    // Go to a not so much debug level which can be overriden on a TAG basis
    esp_log_level_set("*", ESP_LOG_INFO);
    // Our standard for tags is start with a # and then 6 characters right aligned
    //esp_log_level_set("httpd", ESP_LOG_DEBUG);
    // esp_log_level_set("#   net", ESP_LOG_DEBUG);
    // esp_log_level_set("#   msg", ESP_LOG_DEBUG);
    esp_log_level_set("# brain", ESP_LOG_INFO);
    esp_log_level_set("#ledren", ESP_LOG_DEBUG);

    esp_log_level_set("httpd", ESP_LOG_NONE);
    esp_log_level_set("#   net", ESP_LOG_NONE);
    esp_log_level_set("#   msg", ESP_LOG_NONE);
    esp_log_level_set("#shader", ESP_LOG_NONE);
    esp_log_level_set("#shdtre", ESP_LOG_NONE);

    ESP_ERROR_CHECK(esp_event_loop_create_default());

    // Start the various tasks
    task_net_create();
    task_httpd_create();

    // Hang out doing nothing or exit? I think it's ok to exit...
    // while(1) {

    // }

    brain.start();

    // We will never exit!
    //brain.receiveMessages();
}
