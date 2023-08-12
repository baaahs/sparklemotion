//
// Created by Tom Seago on 2/26/21.
//

#pragma once

#include "screen_driver.h"
#include <driver/i2c.h>

/**
 * ScreenDriver implementation for an OLED display driven by a SSD1306
 * chip. Something like these : https://www.amazon.com/dp/B08R8X2WWL
 *
 * These displays are very cheap (< $3 in lots of 10 at that link above
 * in Sept 2021), but there is little documentation and presumably variable
 * configuration of the connection between the OLED display and the driver
 * chip. The checked in implementation works with what I have in front of
 * me, but the need to change some of the remapping of rows or columns
 * is very plausible - although not particularly easy to do right now
 * as a user of this class. I guess that is a
 *
 * TODO: Make it possible to change remapping after or during start()
 *
 *
 *
 */
class ScreenDriverSSD1306 : public ScreenDriver {
public:
    ScreenDriverSSD1306(gpio_num_t pinSCL, gpio_num_t pinSDA);
    virtual ~ScreenDriverSSD1306();

    virtual void start();

protected:
    void handleReset() override;
    void handleClear() override;
    void handlePattern(enum ScreenDriverCommand::Pattern pattern) override;
    void handleSend() override;
    void handlePixmap(ScreenDriverCommand::PixmapData& data) override;

private:
    /**
     * This buffer is laid out in the order we are going to be sending things
     * to the display so that it can be copied directly without juggling
     * data on every display blit.
     *
     * That means each byte is 8 rows of pixels and each character line is
     * 128 pixels wide. This is called a "page" and there are 8 pages.
     *
     * Given bytes a, b, c, d with a0 being the LSB of a and a7 being the MSB of a
     * the pixels are laid out as follows:
     *
     *      x axis --->
     *
     *   y   a0 b0 c0 d0 .....
     *   |   a1 b1 c1 d1 .....
     *   |   a2 b2 c2 d2 .....
     *   V   a3 b3 c3 d3 .....
     *       a4 b4 c4 d4 .....
     *       a5 b5 c5 d5 .....
     *       a6 b6 c6 d6 .....
     *       a7 b7 c7 d7 .....
     *
     * That patterns follows for a single "page" which is 128 bytes long. If the
     * first page is p0, the next p1, p2, p3, etc. where each page contains bytes
     * a, b, c, d, and so on from above the pages themselves (each of
     * which is 8 rows of pixels high) are then laid out as expected:
     *
     *      x axis --->
     *
     *   y   p0: a b c d .....
     *   |   p1: a b c d .....
     *   |   p2: a b c d .....
     *   V   p3: a b c d .....
     *       p4: a b c d .....
     *       p5: a b c d .....
     *       p6: a b c d .....
     *       p7: a b c d .....
     *
     * Eight pages with eight rows in each page adds up to 64 rows of pixels. The
     * pages are stored sequentially in the buffer.
     *
     * Since the driver IC itself doesn't have double buffered memory it doesn't really
     * make much sense to try and double buffer on the MCU side so we don't do that. We
     * have a display buffer that can be draw into and then this buffer will be "blitted"
     * to the device at appropriate times.
     */
    uint8_t m_buffer[8][128];


    static const i2c_port_t PORT = (i2c_port_t)1;

    /**
     * It's unclear from the datasheet if communication via the I2C interface can
     * read from the driver or not. The way I read it seems to indicate that it can
     * but it's probably not super interesting to do so this value is more for completeness
     * than anything.
     */
    static const uint8_t ADDR_READ  = 0b01111001;

    /**
     * The full address for writing to the I2C device. There are alternate addresses
     * where bit1 is true which would allow multiple displays on a single serial
     * line which might be somewhat interesting in some application or another.
     */
    static const uint8_t ADDR_WRITE = 0b01111000;

    // The alternate version of the addresses
//    static const uint8_t ADDR_READ  = 0b01111011;
//    static const uint8_t ADDR_WRITE = 0b01111010;


    gpio_num_t m_pinSCL;
    gpio_num_t m_pinSDA;

    /**
     * From reading other people's code it seems that "commands" are sent in
     * a single I2C transaction per byte. This seems slow and bad and lame, but
     * at the moment it appears to be required. Perhaps someday with some experimentation
     * it can be figured out if the commands and their data can be strung together
     * in a single serial transaction which would reduce overhead on the serial
     * line dramatically.
     *
     * But for now, we have this. Once everything is better understood this should
     * probably revert back to being an inline function if plausible.
     *
     * @param val
     */
    void sendCmd(uint8_t val);



    ////////////////////////////////////////////////////////////////////////////////////
    // Driver Commands
    //
    // This section consists of various hardware commands understood by the
    // controller IC. Instead of using a set of constants for these command
    // numbers they are listed directly since the method name tells you what
    // is going on and the datasheet often refers to them by number rather
    // than name.
    //
    // It is unclear from the datasheet if multiple commands could be written
    // in a message the way several data values can be written in a single
    // i2c transaction. However, the way it is written with multiple
    // sendCmd() calls in a row does work. This means a lot of possibly
    // unnecessary i2c transactions, but you know what? These don't happen
    // all that often. Mostly just at reset and then it's write write write
    // via the blitt'ing so yeah, an unnecessary optimization at this point
    // and probably forever.
    //
    // It should also be said that one should really be referring to the
    // datasheet directly for slightly confusing documentation about what
    // these things do.

    /**
     * Contrast ranges for 1 to 256. Higher values mean increased contrast.
     *
     * This is the command from the datasheet, but it seems that the effective
     * procedure for varying the contrast is better described in setBrightness
     *
     * @param cmd
     * @param contrast
     */
    void cmdContrast(uint8_t contrast) {
        sendCmd(0x81); // set contrast
        sendCmd(contrast);
    }

    /**
     * Ported from another library this is the full procedure that cmdBrightness
     * actually uses. Understanding of the relevance here is kind of low at the
     * moment.
     *
     * @param contrast
     * @param precharge
     * @param comdetect
     */
    void cmdContrastFull(uint8_t contrast, uint8_t precharge, uint8_t comdetect) {
        sendCmd(0xd9); // set precharge
        sendCmd(precharge);

        sendCmd(0x81); // set contrast
        sendCmd(contrast);

        sendCmd(0xDB); // set vcom detect
        sendCmd(comdetect);

        sendCmd(0xa4); // display all on resume
        sendCmd(0xa6); // normal display

        sendCmd(0xaf); // display on - not totally sure this should be here
    }

    /**
     * Uses some internal magic to directly translate from "brightness" to
     * contrast settings.
     * @param brightness value from 0 to 255 (maybe 1 to 255)
     */
    void cmdBrightness(uint8_t brightness) {
        uint8_t contrast = brightness;
        if (brightness < 128) {
            // Magic
            contrast = brightness * 1.171;
        } else {
            contrast = brightness * 1.172 - 43;
        }

        uint8_t precharge = 241;
        if (!brightness) {
            precharge = 0;
        }
        uint8_t comdetect = brightness / 4;
        cmdContrastFull(contrast, precharge, comdetect);
    }

    /**
     * This allows you to just turn all pixels on the display to the ON state instead
     * of "following what's in RAM" - which seems pointless. Anyway it's good during
     * initialization to tell the driver heh, yeah, use what's in RAM there okay
     * buddy?
     *
     * @param followRam - true to do as one would expect. false to just turn on all the
     *        pixels for god knows what reason.
     */
    void cmdEntireDisplayOn(bool followRam) {
        if (followRam) {
            sendCmd(0xa4);
        } else {
            sendCmd(0xa5);
        }
    }

    /**
     * Inverts the display in terms of color. With normal mode a 0 bit in memory
     * will be an "OFF" pixel, usually black, whereas a 1 bit will be an "ON" pixel.
     * If inverted is true the opposite will be the case. A 0 will be ON and a 1
     * will be OFF.
     *
     * This affects the entire display not just new writes to graphic memory.
     *
     * When post pixels on the display are ON I think the current needs of the display
     * go up and you may get some flicker if using the charge bump method of getting the
     * voltage up to the required level versus supplying a high VCC separately from
     * the VDD interface rail.
     *
     * @param inverted
     */
    void cmdInvertDisplay(bool inverted) {
        if (inverted) {
            sendCmd(0xa7);
        } else {
            sendCmd(0xa6);
        }
    }

    void cmdDisplayOn(bool on) {
        if (on) {
            sendCmd(0xaf);
        } else {
            sendCmd(0xae);
        }
    }

    void cmdScrollDeactivate() {
        sendCmd(0x2E);
    }

    void cmdScrollActivate() {
        sendCmd(0x2F);
    }

    // Addressing Setting commands
    
    // For page addressing mode
    void cmdPageColAddrLow(uint8_t addr) {
        sendCmd(addr & 0x0f);
    }

    // For page addressing mode
    void cmdPageColAddrHigh(uint8_t addr) {
        sendCmd(0x10 | (addr & 0x0F));
    }

    // page 0-7, for page addressing mode
    void cmdPageStart(uint8_t page) {
        sendCmd(0xB0 | (page & 0x07));
    }

    typedef uint8_t memAddrMode_t;
    static const memAddrMode_t horizontal = 0;
    static const memAddrMode_t vertical = 1;
    static const memAddrMode_t page = 2;
    void cmdMemAddrMode(memAddrMode_t mode) {
        sendCmd(0x20);
        sendCmd(mode);
    }

    void cmdColAddr(uint8_t start, uint8_t end) {
        sendCmd(0x21);
        sendCmd(start);
        sendCmd(end);
    }

    void cmdPageAddr(uint8_t start, uint8_t end) {
        sendCmd(0x22);
        sendCmd(start);
        sendCmd(end);
    }
    
    
    // Hardware Configuration
    void cmdDisplayStartLine(uint8_t line) {
        sendCmd(0x40 | line);
    }

    void cmdSegmentRemap(bool remap) {
        sendCmd(0xA0 | remap);
    }

    void cmdMuxRatio(uint8_t ratio) {
        sendCmd(0xA8);
        sendCmd(ratio);
    }

    /**
     * This inverts/remaps the scan direction from COM0 to COM63 into COM63 to COM0.
     * Effectively what this means is a top to bottom inversion of the rows of the
     * display.
     *
     * This setting interacts with the COM pins hw config values to produce 8 different
     * ways in which the oled panel can be connected to the output of the driver IC.
     *
     * @param remap
     */
    void cmdComOutScanDirection(bool remap) {
        sendCmd(remap ? 0xC8 : 0xC0);
    }

    void cmdDisplayOffset(uint8_t offset) {
        sendCmd(0xD3);
        sendCmd(offset);
    }

    /**
     * The "COM" pins can be connected from the driver IC to the actual oled screen
     * in multiple ways to allow different PCB layouts. Full screen module assemblies
     * that contain both the driver IC and the screen (the things you can typically
     * by from Amazon, Adafruit, etc) from different manufacturers may require
     * different settings for these two values.
     *
     * From the datasheet these two values interact with the COM out scan direction
     * setting to produce 8 different ways to connect the COM pins to the oled hardware.
     *
     * @param enableLeftRightRemap - the left to right remapping here refers to sides
     *        of the SSD1306 IC itself. An incorrect setting will likely cause an
     *        interlacing artifact or issue. This does NOT swap the data stream to
     *        pixel mapping from left to right on the display.
     * @param useAlternateComPinConfig - using the standard config (false) will map
     *        the first 32 COM pins to the first 32 rows on the screen and the second
     *        block of 32 to the second block of rows. The alternate config (true)
     *        will interlace the two sets of 32. Try one value here and then the other
     *        to see if you are getting interlaced rows or not.
     */
    void cmdComPinsHwConfig(bool enableLeftRightRemap, bool useAlternateComPinConfig) {
        sendCmd(0xDA);
        uint8_t val = 0x02 | (enableLeftRightRemap << 5) | (useAlternateComPinConfig << 4);
        sendCmd(val);
    }

    // Timing & Driving Scheme
    
    // Reset would be 0, 8
    void cmdClockDivideRatioOscFreq(uint8_t divideRatio = 0, uint8_t oscFreq = 8) {
        uint8_t val = (oscFreq << 4) | (divideRatio & 0x0F);

        sendCmd(0xD5);
        sendCmd(val);
    }

    // Reset would be 2 and 2
    void cmdPrechargePeriod(uint8_t phase1, uint8_t phase2) {
        uint8_t val = (phase2 << 4) | (phase1 & 0x0F);

        sendCmd(0xD9);
        sendCmd(val);
    }

    // Valid values are 0, 2, and 3. Reset is 2
    void cmdVcomhDeselect(uint8_t level) {
        sendCmd(0xDB);

        // Should val be shifted left by 4? Not sure. Use it raw for now
        sendCmd(level);
    }

    void cmdChargeBump(bool enable) {
        sendCmd(0x8D);
        sendCmd(enable ? 0x14 : 0x10);
    }

    /**
     * A utility method that will dump the entire buffer in ascii graphic form to
     * the log. This has been useful since the displays can be quite small.
     */
    void logBuffer();
};