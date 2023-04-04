//
// Created by Tom Seago on 2019-08-07.
//

#pragma once

#define JSMN_HEADER
#include <jsmn.h>

#define STA_SSID_MAX_LEN 32
#define STA_PASS_MAX_LEN 32

#define DEFAULT_CONFIG_FILE "/spiffs/config.json"

#define KEY_STA_SSID "sta_ssid"
#define KEY_STA_PASS "sta_pass"


class BrainConfig {
public:
    BrainConfig();

    /**
     * Load the configuration from a named file or the default
     * specified by DEFAULT_CONFIG_FILE.
     *
     * @param filename
     * @return ESP_OK if it happened or the underlying error code
     */
    esp_err_t load(const char* filename = DEFAULT_CONFIG_FILE);

    /**
     * Save this configuration to a named file or the default
     * specified by DEFAULT_CONFIG_FILE if a name is given.
     *
     * @param filename
     * @return ESP_OK if it happened or the underlying error code
     */
    esp_err_t save(const char* filename = DEFAULT_CONFIG_FILE);

    /**
     * @return The SSID that the STA interface should connect to.
     */
    const char* staSsid() { return m_staSsid; }

    /**
     * Set the staSsid value for this config. The string is copied
     * into internal storage.
     *
     * @param val
     */
    void setStaSsid(const char* val);

    /**
     * @return The password that the STA interface should use
     */
    const char* staPass() { return m_staPass; }

    /**
     * Sets the staPass value for this config. The string is copied
     * into internal storage.
     *
     * @param val
     */
    void setStaPass(const char* val);

    /**
     * This is the base mac address of the system. Any place
     * that we want to identify a board uniquely should use
     * this address. The returned address comes from EFUSE
     * which is set by Espressif when they manufacture the
     * ESP32 module - so that's the thing we want.
     *
     * Because there are actually multiple network interfaces,
     * each of which needs it's own address, a brain board ends
     * up with several MACs.
     *
     * The point of having this method in the brain-common
     * component is to promote standardization and remove
     * opportunities to get confused about _which_ MAC
     * address should be used.
     *
     * @return a pointer to an array of length 6 which contains
     * the system mac address.
     */
    uint8_t* mac();

    /**
     * A convenience function to get the system's base mac
     * address as a string.
     *
     * @return a pointer to the internally held buffer which
     * contains the null terminated mac address.
     */
    const char* macStr();

    /**
     * A integer based on the system version string plus build
     * date and time strings. This is an attempt at creating
     * not a monotonically increasing but a kind of hash code
     * of this changing date. The idea is to then use this
     * to identify one firmware from another for things like
     * the UI state.
     */
    uint32_t versionHash();

    /**
     * Parse this configuration from a buffer containing valid
     * JSON configuration data. This **WILL** modify the buffer
     * passed to it by inserting convenient nulls as it reads
     * keys and values. It could be nice and not do that, but
     * it's not.
     *
     * Basically, once you've sent a buffer into this parse method
     * consider it scrambled up a bit. That means log it before
     * parsing it.
     *
     * @param szBuf
     * @param len
     * @return
     */
    esp_err_t parseFrom(char* szBuf, size_t len);

    /**
     * Print/Output this configuration to a buffer as a JSON
     * string. Useful for things such as sending to a web
     * client, but it's also probably useful for logging
     * as well.
     * @param szBuf
     * @param len
     * @return
     */
    esp_err_t printTo(char* szBuf, size_t len);

private:
    uint8_t m_mac[6];
    char m_macStr[18]; // 6 * 2 + 5 + \0

    char m_staSsid[STA_SSID_MAX_LEN+1];
    char m_staPass[STA_PASS_MAX_LEN+1];

    void setString(char* szBuf, size_t size, const char* val);

    void handleValue(char *szBuf, jsmntok_t *pTokens, int ix);
};

extern BrainConfig GlobalConfig;