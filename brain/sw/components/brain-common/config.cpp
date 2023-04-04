//
// Created by Tom Seago on 2019-08-07.
//

#include "brain_common.h"

#include <string.h>
#include <sys/stat.h>

#include <esp_ota_ops.h>
#include <esp_mac.h>

#define TAG TAG_COMMON

BrainConfig GlobalConfig;

BrainConfig::BrainConfig() {
    setStaSsid(DEFAULT_STA_SSID);
    setStaPass(DEFAULT_STA_PASS);
}

esp_err_t BrainConfig::load(const char* filename) {
    ESP_LOGI(TAG, "Loading config from %s", filename);

    struct stat st;
    if (stat(filename, &st) != 0) {
        ESP_LOGE(TAG, "Could not stat config file %s", filename);
        return ESP_ERR_NOT_FOUND;
    }

    char* contents = (char*)malloc(st.st_size+1);
    if (!contents) {
        ESP_LOGE(TAG, "Could not malloc %ld bytes to read config file %s", st.st_size, filename);
        return ESP_ERR_NO_MEM;
    }

    FILE* f = fopen(filename, "r");
    if (!f) {
        ESP_LOGE(TAG, "Could not open config file %s", filename);
        free(contents);
        return ESP_FAIL;
    }

    size_t num = fread(contents, st.st_size, 1, f);
    if (num != 1) {
        ESP_LOGE(TAG, "Failed to read file contents from %s: ferror=%d", filename, ferror(f));
        fclose(f);
        free(contents);
        return ESP_FAIL;
    }
    // Make sure it's nicely null terminated
    contents[st.st_size] = 0;

    // Don't need the file anymore
    fclose(f);

    // Parse the data
//    ESP_LOGD(TAG, "Parsing config content '%s'", contents);
    auto ret = parseFrom(contents, st.st_size);

    free(contents);
    return ret;
}

#define TOKEN_COUNT 200

esp_err_t BrainConfig::parseFrom(char* szBuf, size_t len) {
    if (!szBuf) {
        return ESP_FAIL;
    }

    jsmn_parser parser;
    jsmn_init(&parser);

    jsmntok_t tokens[TOKEN_COUNT];
    int r = jsmn_parse(&parser, szBuf, len, tokens, TOKEN_COUNT);
    if (r<0) {
        if (r == JSMN_ERROR_INVAL) {
            ESP_LOGE(TAG, "Invalid token in config file");
        } else if (r == JSMN_ERROR_NOMEM) {
            ESP_LOGE(TAG, "Config file is too large");
        } else if (r == JSMN_ERROR_PART) {
            ESP_LOGE(TAG, "Expected more data in config file");
        } else {
            ESP_LOGE(TAG, "Failed to parse config file. r=%d", r);
        }
        return ESP_FAIL;
    }

    // Dump the parsed contents
//    for (int i=0; i<r; i++) {
//        char e = szBuf[tokens[i].end];
//        szBuf[tokens[i].end] = 0;
//        ESP_LOGD(TAG, "Token %d, type=%d, start=%d, end=%d, content='%s'",
//                i, tokens[i].type, tokens[i].start, tokens[i].end, szBuf + tokens[i].start);
//        szBuf[tokens[i].end] = e;
//    }

    int start = tokens[0].type == JSMN_OBJECT ? 1 : 0;
    bool wantKey = true;
    for (int i=start; i<r; i++) {
        // Instead of a big hierarchy we're keeping things really simple
        // and just doing key value pairs right now. Maybe we'll do more
        // structure in the future though.

        if (wantKey) {
            if (tokens[i].type != JSMN_STRING) {
                // Don't know how to handle non-string keys so bail
                ESP_LOGE(TAG, "While parsing config, was expecting a string key name but found something else");
                ESP_LOGE(TAG, "Token %d, type=%d, start=%d, end=%d, content='%s'",
                         i, tokens[i].type, tokens[i].start, tokens[i].end, szBuf + tokens[i].start);
                return ESP_FAIL;
            }
            // Make this easy on ourselves
            szBuf[tokens[i].end] = 0;

            wantKey = false;
        } else {
            handleValue(szBuf, tokens, i);
            i += tokens[i].size; // Number of child tokens
            wantKey = true;
        }
    }
    return ESP_OK;
}

esp_err_t BrainConfig::printTo(char* szBuf, size_t len) {
    if (!szBuf) {
        return ESP_ERR_NO_MEM;
    }

    if ( snprintf(szBuf, len,
        "{"
            "\"" KEY_STA_SSID "\":\"%s\","
            "\"" KEY_STA_PASS "\":\"%s\""
        "}",
        m_staSsid,
        m_staPass
    ) < 0) {
        return ESP_FAIL;
    }

    return ESP_OK;
}

void BrainConfig::handleValue(char *szBuf, jsmntok_t *pTokens, int ix) {
    // The key token is always ix-1
    jsmntok_t* pKey = pTokens + (ix-1);
    jsmntok_t* pVal = pTokens + ix;

//    ESP_LOGD(TAG, "Key %d, type=%d, start=%d, end=%d, content='%s'",
//             ix-1, pKey->type, pKey->start, pKey->end, szBuf + pKey->start);
//    ESP_LOGD(TAG, "Value %d, type=%d, start=%d, end=%d, content='%s'",
//             ix, pVal->type, pVal->start, pVal->end, szBuf + pVal->start);

    if (strncmp(szBuf + pKey->start, KEY_STA_SSID, sizeof(KEY_STA_SSID)) == 0) {
        if (pVal->type != JSMN_STRING) {
            ESP_LOGE(TAG, "Value of key %s must be a string not type=%d", szBuf + pKey->start, pVal->type);
        } else {
            szBuf[pVal->end] = 0;
            setStaSsid(szBuf + pVal->start);
        }
    }
    else if (strncmp(szBuf + pKey->start, KEY_STA_PASS, sizeof(KEY_STA_PASS)) == 0) {
        if (pVal->type != JSMN_STRING) {
            ESP_LOGE(TAG, "Value of key %s must be a string not type=%d", szBuf + pKey->start, pVal->type);
        } else {
            szBuf[pVal->end] = 0;
            setStaPass(szBuf + pVal->start);
        }
    }


}

#define SAVE_BUF_SIZE 1024

esp_err_t BrainConfig::save(const char* filename) {
    ESP_LOGI(TAG, "Saving config to %s", filename);

    // Get the config into a buffer
    char* szBuf = (char*)malloc(SAVE_BUF_SIZE);
    if (!szBuf) {
        return ESP_ERR_NO_MEM;
    }

    esp_err_t result = ESP_OK;

    result = printTo(szBuf, SAVE_BUF_SIZE);
    if (result != ESP_OK) {
        ESP_LOGE(TAG, "Printing the current config failed %d", result);
        free(szBuf);
        return result;
    }

    ESP_LOGD(TAG, "Saving config data: %s", szBuf);

    FILE* f = fopen(filename, "w");
    if (!f) {
        ESP_LOGE(TAG, "Could not open config file %s", filename);
        free(szBuf);
        return ESP_FAIL;
    }

    auto toWrite = strlen(szBuf);
    auto recordsWritten = fwrite(szBuf, toWrite, 1, f);

    if (recordsWritten != 1) {
        ESP_LOGE(TAG, "ERROR Writing to the config file: ferror = %d", ferror(f));
        result = ESP_FAIL;
    }

    fclose(f);

    free(szBuf);
    return result;
}

void BrainConfig::setStaSsid(const char* val) {
    if (!val) val = "";
    ESP_LOGI(TAG, "Setting sta ssid to '%s'", val);
    setString(m_staSsid, STA_SSID_MAX_LEN, val);
}

void BrainConfig::setStaPass(const char* val) {
    if (!val) val = "";
    ESP_LOGI(TAG, "Setting sta pass to '%s'", val);
    setString(m_staPass, STA_PASS_MAX_LEN, val);
}

void BrainConfig::setString(char* szBuf, size_t size, const char* val) {
    if (!szBuf) return;

    if (!val) {
        szBuf[0] = 0;
        return;
    }

    strncpy(szBuf, val, size);
}

uint8_t* BrainConfig::mac() {
    ESP_ERROR_CHECK_WITHOUT_ABORT(esp_efuse_mac_get_default(m_mac));

    return m_mac;
}

const char* BrainConfig::macStr() {
    if (!m_mac[0]) {
        // Aack, we don't have the mac yet, so fetch it
        mac();
        sprintf(m_macStr, MACSTR, MAC2STR(m_mac));
    } // else just give them what we already constructed

    return m_macStr;
}

// Some stupid thing I found at https://stackoverflow.com/questions/8317508/hash-function-for-a-string
#define A 54059 /* a prime */
#define B 76963 /* another prime */
#define C 86969 /* yet another prime */
#define FIRSTH 37 /* also prime */

uint32_t hashStr(const char* s) {
    uint32_t h = FIRSTH;

    while (*s) {
        h = (h * A) ^ (s[0] * B);
        s++;
    }
    return h; // or return h % C;
}

uint32_t BrainConfig::versionHash() {
    auto desc = esp_app_get_description();
    if (!desc) {
        ESP_LOGE(TAG, "Couldn't get a real version hash. Odd");
        return 0xFEEDFACE;
    }

    // desc->secure_version is 0 so we have to do
    // our own hash. We just want entropy!

    uint32_t ret = hashStr(desc->idf_ver);
    ret += hashStr(desc->version);
    ret += hashStr(desc->date);
    ret += hashStr(desc->time);

    return ret;
}