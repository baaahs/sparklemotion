//
// Created by Tom Seago on 2019-08-05.
//

#pragma once

#include "freertos/FreeRTOS.h"

#include "esp_err.h"
#include "esp_log.h"

#include "brain_hardware.h"
#include "brain_tasks.h"
#include "brain_tags.h"
#include "brain_config.h"

#define DEFAULT_STA_SSID "sparklemotion"
#define DEFAULT_STA_PASS "sparklemotion"

/**
 * If using the configuration or the web server or anything else that
 * needs files from SPIFFS this should be called first. Likely you want
 * this to be called right after initializing logging in app_main()
 *
 * @return
 */
extern esp_err_t brain_init_spiffs();

/**
 * Restart the whole show, optionally after a delay. It makes sense for
 * a component which has received a restart request from the outside
 * world to want to acknowledge that request and THEN restart the system.
 * Thus this function.
 *
 * If you pass 0 this will immediately call esp_restart()
 *
 * @param delayMillis
 */
extern void brain_restart(uint32_t delayMillis);
