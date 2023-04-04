//
// Created by Tom Seago on 2/26/21.
//


#include "screen_driver_ssd1306.h"
#include "brain_common.h"
#include <string.h>

static const char* TAG = TAG_SCREEN;

ScreenDriverSSD1306::ScreenDriverSSD1306(gpio_num_t pinSCL, gpio_num_t pinSDA) :
        ScreenDriver(128, 64),
        m_pinSCL(pinSCL), m_pinSDA(pinSDA)
{
}

ScreenDriverSSD1306::~ScreenDriverSSD1306() {
}

void
ScreenDriverSSD1306::start() {
    ESP_LOGD(TAG, "SSD1306 driver start. Configuring i2c");

    // Install first
    esp_err_t code;
    code = i2c_driver_install(PORT, I2C_MODE_MASTER, 0, 0, 0);
    if (code != ESP_OK) {
        ESP_LOGE(TAG, "Couldn't install I2C driver: %s", esp_err_to_name(code));
        return;
    }

    // Configure the I2C driver second
    i2c_config_t config;
    config.mode = I2C_MODE_MASTER;
    config.sda_io_num = m_pinSDA;
    config.sda_pullup_en = GPIO_PULLUP_ENABLE;
    config.scl_io_num = m_pinSCL;
    config.scl_pullup_en = GPIO_PULLUP_ENABLE;

    // For the clock speed we know that at least 700000 and slower speeds
    // work. According to the library we referenced 700k works with an
    // 8266 in 160 Mhz mode, but in 80 Mhz mode it's limited to around 400k.
    // The ESP32 sdk says this is limited to 1Mhz "for now". A value of 1000000 does
    // seem to work ok, but of course if the clock speed of the board is
    // lowered it might not.
    // 1024 * 1024 = Works
    // 1025 * 1024 = Works
    // 1250 * 1024 = Works <-- Stopping here for now (failing in 4.3)
    // 1500 * 1024 = Fail
    // 2048 * 1024 = ESP_FAIL on command send
    // 100000 works for clock in 4.3 but is laggy of course
    config.master.clk_speed = 1025 * 1024; // 10000;
    config.clk_flags = 0; // Be explicit just in case?? - yes this is important
    code = i2c_param_config(PORT, &config);
    if (code != ESP_OK) {
        ESP_LOGE(TAG, "Couldn't config I2C: %s", esp_err_to_name(code));
        ESP_LOGE(TAG, "*****************************************************");
        return;
    }
    ESP_LOGI(TAG, "++++++++++++++++++++++++++++++++++++++++");

//    ESP_LOGD(TAG, "SSD1306 driver start. Calling reset() and pattern()");
//    reset();
//     pattern();
    ESP_LOGD(TAG, "SSD1306 driver started.");
}


void
ScreenDriverSSD1306::handleReset() {
    ESP_LOGD(TAG, "handleReset() start");

    // OMG was this reset sequence difficult to come by. It required
    // guessing at what the datasheet meant, reading other supposedly
    // working code, and a huge amount of trial and error.  The sequence
    // and values here work with the example board in front of me. I
    // definitely don't understand the details of how all the settings interact,
    // but again, this works so like, moving on.

    // For future people - if you go changing the order or the value for any
    // reason I _strongly_ recommend both moving slowly and testing a lot. In my
    // explorations I had many empty screens for no reason. You have been
    // warned.

    cmdDisplayOn(false);
    cmdClockDivideRatioOscFreq(0, 8); // Defaults
    cmdMuxRatio(0x3F); // Equal to height - 1
    cmdDisplayOffset(0);
    cmdDisplayStartLine(0);
    cmdChargeBump(true);
    cmdMemAddrMode(horizontal);

    // Select the left to right column mapping. The value of true works for
    // my example hardware, but other cheap integrators may have done it differently.
    // This is exactly the config that needs to be visible externally.
    cmdSegmentRemap(true);

    // Changes top to bottom mapping between hardware output pins and the way
    // the controller is reasoning. Should also be externalized.
    cmdComOutScanDirection(true); // remap no, means inc

    // These also effect translation from memory to output.
    cmdComPinsHwConfig(true,true); // This was 0x12

    // This is slightly arbitrary and is reasonably that it should be adjustable
    // in some generic way.
    cmdContrast(0xCF); // derp, ok...

    // OLED specific things I don't understand but which therefore may vary
    // by manufacturer
    cmdPrechargePeriod(1, 0xF);
    cmdVcomhDeselect(0x40); // 0x40 is default, to lower contrast use 0

    // If we don't follow ram then I really don't really know where it gets
    // things from but they don't work!
    cmdEntireDisplayOn(true);

    // A couple "Hey, really, be normal" things
    cmdInvertDisplay(false);
    cmdScrollDeactivate();

    // Light it up!
    cmdDisplayOn(true);

    // Since we just changed things, let's clear it all out so we start
    // from a blank display on each reset.
    handleClear();
    handleSend();

    ESP_LOGD(TAG, "handleReset() end");
}

void
ScreenDriverSSD1306::handleClear() {
    memset(m_buffer, 0, sizeof(m_buffer));
}

void
ScreenDriverSSD1306::handlePattern(enum ScreenDriverCommand::Pattern pattern) {
    ESP_LOGI(TAG, "handlePattern %d", pattern);
    switch (pattern) {
        case ScreenDriverCommand::Diagonals:
            // One Page at a time, top to bottom
            for (uint8_t y = 0; y<8; y++) {
                // Two 8x8 cells at a time with a pyramid in each cell
                for (uint8_t cell=0; cell < 16; cell++) {

                    // Cell width of 8
                    for (uint8_t x = 0; x < 8; x++) {
                        uint8_t colData = 0x11 << x;

//                if (y > 0 || cell > 7) {
//                    colData = 0;
//                }
                        m_buffer[y][(cell * 8) + x] = colData;
                    }
                }
            }
            break;

        case ScreenDriverCommand::Checkerboard:
            for (uint8_t y = 0; y<8; y++) {
                for (uint8_t x = 0; x < 128; x++) {
                    uint8_t val = (((x / 8) + (y % 2)) % 2) ? 0xFF : 0x00;
                    //uint8_t val = 0x0f;
                    m_buffer[y][x] = val;
                }
            }
            break;

        case ScreenDriverCommand::CheckerboardAlt:
            for (uint8_t y = 0; y<8; y++) {
                for (uint8_t x = 0; x < 128; x++) {
                    uint8_t val = (((x / 8) + (y % 2)) % 2) ? 0x00 : 0xFF;
//                    uint8_t val = 0xf0;
                    m_buffer[y][x] = val;
                }
            }
            break;

        case ScreenDriverCommand::Corners:
            for (uint8_t y = 0; y<8; y++) {
                for (uint8_t x = 0; x < 128; x++) {
                    uint8_t val = 0;

                    // Top page
                    if (y==0) {
                        if (x==0) {
                            // 8 pixels down left side
                            val = 0xff;
                        } else if (x<20) {
                            // Line along top
                            val = 0x01;
                        } else if (x==m_width-1) {
                            // Line along right side
                            val = 0x0f;
                        } else if (x>m_width-11) {
                            // Line along top right
                            val = 0x01;
                        }
                    }

                    // Bottom page
                    if (y==7) {
                        if (x==0) {
                            // 8 pixels on left and right of bottom
                            val = 0x8f;
                        } else if (x<20) {
                            val = 0x80;
                        } else if (x==m_width-1) {
                            val = 0xf0;
                        } else if (x>m_width-11) {
                            val = 0x80;
                        }
                    }

                    m_buffer[y][x] = val;
                }
            }
            break;
    }
}


void
ScreenDriverSSD1306::handleSend() {
    ESP_LOGD(TAG, "Blitting buffer to device");
    // logBuffer();

    // These two commands ensure we address the whole display. They may
    // not generally be necessary but meh, it's safer.
    cmdColAddr(0, m_width-1);
    cmdPageAddr(0, 7);

    uint8_t *cursor = &m_buffer[0][0];

    i2c_cmd_handle_t link = i2c_cmd_link_create();
    i2c_master_start(link);
    i2c_master_write_byte(link, ADDR_WRITE, true);
    i2c_master_write_byte(link, 0x40, true); // Data bytes

    i2c_master_write(link, cursor, sizeof(m_buffer), true);
    i2c_master_stop(link);

    // 1 second to wait is rather arbitrary but whatever
    auto code = i2c_master_cmd_begin(PORT, link, pdMS_TO_TICKS(1000));
    if (code != ESP_OK) {
        ESP_LOGE(TAG, "Error blitting: %s", esp_err_to_name(code));
    } else {
        // ESP_LOGI(TAG, "Blit'ed 16 bytes");
    }

    i2c_cmd_link_delete(link);
}


void
ScreenDriverSSD1306::handlePixmap(ScreenDriverCommand::PixmapData& data) {

    if (!data.data) return;

    uint8_t outPage, outBit;

    // yCursor and xCursor move through the input data
    for (uint8_t yInCursor = 0; yInCursor < data.rect.height; yInCursor++) {
        auto yInPos = yInCursor + data.rect.top;
        if (yInPos >= m_height) {
            ESP_LOGE(TAG, "Invalid page bit from %d", yInPos);
            continue;
        }

        outPage = yInPos / 8;
        outBit = yInPos % 8;

        for (uint8_t xInCursor = 0; xInCursor < data.rect.width; xInCursor++) {
            auto col = xInCursor + data.rect.left;
            if (col >= m_width) {
                ESP_LOGE(TAG, "Invalid col from %d", col);
                continue;
            }

            uint8_t inBitPos = (data.rect.width * yInCursor) + xInCursor;
            const uint8_t *inByte = data.data + (inBitPos / 8);
            uint8_t inBitMask = 0x0080 >> (inBitPos % 8);

            bool on = inBitMask & *inByte;

            // Find the right output byte
            uint8_t outMask = (uint8_t) (1 << outBit);

//            uint8_t before = m_buffer[outPage][col];
            if (on) {
                m_buffer[outPage][col] = m_buffer[outPage][col] | outMask;
            } else {
                outMask = ~outMask;
                m_buffer[outPage][col] = m_buffer[outPage][col] & outMask;
            }
        }
    }

//    ESP_LOGI(TAG, "Wrote to rect at (%d,%d) of size (%d, %d)",
//             data.rect.left, data.rect.top, data.rect.width, data.rect.height);

//    logBuffer();

    // Clean up the data!
    if (data.needsToBeFreed) {
        free((void *) data.data);
    }
}


void
ScreenDriverSSD1306::sendCmd(uint8_t val) {
    i2c_cmd_handle_t link = i2c_cmd_link_create();
    i2c_master_start(link);

    // The address, which includes write mode
    i2c_master_write_byte(link, ADDR_WRITE, true);

    // Co bit set to 1 and D/C# set to 0
    i2c_master_write_byte(link, 0x80, true);

    i2c_master_write_byte(link, val, true);

    i2c_master_stop(link);

    auto code = i2c_master_cmd_begin(PORT, link, pdMS_TO_TICKS(1000));
    if (code != ESP_OK) {
        ESP_LOGE(TAG, "Error sending i2c command: %s", esp_err_to_name(code));
    } else {
        // ESP_LOGI(TAG, "i2c cmd: %02x", val);
    }

    i2c_cmd_link_delete(link);
}



void
ScreenDriverSSD1306::logBuffer() {

    for(uint8_t page=0; page < 8; page++) {
        for (uint8_t row=0; row < 8; row ++) {
            char bitmap[129];
            bitmap[128] = 0;
            for (uint8_t col=0; col < 128; col++) {
                bool on = m_buffer[page][col] & (1 << row);
                bitmap[col] = on ? '*' : '.';
            }
            ESP_LOGD(TAG, "%s", bitmap);
        }
    }
}