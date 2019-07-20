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

    // Before anything else we configure logging. Logging starts with a master level set
    // for the * tag and is then overridden for subsequent tags. As a general rule,
    // the code which is checked in should have a minimal amount of debugging output
    // turned on - i.e. the values for all the tags should usually be ESP_LOG_NONE.
    //
    // Feel free to set higher amounts of debugging locally, but try not to check these
    // in without at least adding comments here explaining why the levels are set higher
    // for a specific tag.
    //
    // Yes, we probably want to have a little more centralization of tags but for now we
    // do not.
    //
    // Our standard for tags is to start with a # and then 6 characters right aligned.
    // This produces a generally pleasing output that is distinct from the IDF generated
    // log messages.
    //
    // Which brings us to the point - the IDF will generate a lot of logging messages if
    // you let it! At a gross level we control these by setting the * tag to NONE and then
    // only enabling higher level for our specific tags of interest. From time to time it
    // is interesting to see what the IDF is up to though so setting * to a higher level
    // can be nice - but again let's try not to check that in.
    //
    // It's also worth noting that there is a menuconfig setting which will remove logging
    // at a preprocessor level. For now we're letting that stuff be really general (i.e.
    // we are keeping it set to DEBUG), but there is almost certainly an optimization to
    // be had at release time by changing that build setting.

    // Set the basic level for both us and the entire IDF
    esp_log_level_set("*", ESP_LOG_WARN);

    // Enable logging for certain IDF components
//    esp_log_level_set("ledc", ESP_LOG_DEBUG);

    // Enable logging for our components
//    esp_log_level_set("#sysmon", ESP_LOG_DEBUG);
//    esp_log_level_set("# brain", ESP_LOG_DEBUG);
//    esp_log_level_set("#ledren", ESP_LOG_DEBUG);
//
//    esp_log_level_set("# httpd", ESP_LOG_DEBUG);
//    esp_log_level_set("#   net", ESP_LOG_INFO);
//    esp_log_level_set("#   msg", ESP_LOG_DEBUG);
//    esp_log_level_set("#shader", ESP_LOG_DEBUG);
//    esp_log_level_set("#shdtre", ESP_LOG_DEBUG);
//    esp_log_level_set("#    ui", ESP_LOG_DEBUG);


    // Configure the default event loop before anything else happens
    ESP_ERROR_CHECK(esp_event_loop_create_default());

    // Start the various tasks
    // TODO: These network related things should happen AFTER system configuration from SPIFFS
    task_net_create();
    task_httpd_create();

    // Start the brain task
    brain.start();
}
