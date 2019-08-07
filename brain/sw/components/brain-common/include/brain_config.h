//
// Created by Tom Seago on 2019-08-07.
//

#pragma once

#include <jsmn.h>

#define STA_SSID_MAX_LEN 32
#define STA_PASS_MAX_LEN 32

#define DEFAULT_CONFIG_FILE "/spiffs/config.json"

#define KEY_STA_SSID "sta_ssid"
#define KEY_STA_PASS "sta_pass"


class BrainConfig {
public:
    BrainConfig();

    esp_err_t load(const char* filename = DEFAULT_CONFIG_FILE);
    esp_err_t save(const char* filename = DEFAULT_CONFIG_FILE);

    const char* staSsid() { return m_staSsid; }
    void setStaSsid(const char* val);

    const char* staPass() { return m_staPass; }
    void setStaPass(const char* val);

private:
    char m_staSsid[STA_SSID_MAX_LEN+1];
    char m_staPass[STA_PASS_MAX_LEN+1];

    void setString(char* szBuf, size_t size, const char* val);

    esp_err_t parseConfigFrom(char* szBuf, size_t len);
    void handleValue(char *szBuf, jsmntok_t *pTokens, int ix);
};

extern BrainConfig GlobalConfig;