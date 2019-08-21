/**
 * This file collects hardware dependent things, like pinouts, that
 * need to be baked into the firmware for boot time. In general it
 * would be nice if pins can be defined after boot, but some don't
 * make much sense like leds and buttons.
 *
 * The layout of this file is that the default values, along
 * with their documentation, is at the end. Each default #define is
 * protected by a #ifndef so that variants can get in there
 * first and change just a couple things but then get the rest
 * of the default values.
 */

#pragma once

#include <driver/gpio.h>

/**
 * Uncomment one of the following if you are building a variant
 * of the hardware. The Rev D before being reworked serves as our
 * base for all the definitions from this file. So for a plain
 * Rev D, the variants should all be commented out.
 *
 * Once we have Rev E boards in hand that will become the default
 * on master, and we should leave things checked in that way. At
 * that point we might make all the Rev D stuff a variant of Rev E
 * defaults but whatever. If someone gets to it, great.
 *
 * If you have a derivative project, feel free to define an
 * additional variant following this pattern. These can be
 * safely stored on the master branch without conflict.
 */
//#define BRAIN_VARIANT_REV_E
//#define BRAIN_VARIANT_REV_D_REWORKED
//#define BRAIN_VARIANT_PANEL_TESTER
#define BRAIN_VARIANT_GLAMSIGN
//#define BRAIN_VARIANT_GLAMSIGN_MINI

//#define BRAIN_DEFAULT_BRIGHTNESS   16


/********************************************************************
 * The Panel Testers are two boxes built by Tom S. that have
 * Rev D boards in them with an additional external button and an
 * I2C connected display.
 ********************************************************************/
#ifdef BRAIN_VARIANT_PANEL_TESTER
#define BRAIN_GPIO_LED_LEFT_EYE    GPIO_NUM_32
#define BRAIN_GPIO_BUTTON_LEFT     GPIO_NUM_39
#define BRAIN_GPIO_PIXEL_CH1       GPIO_NUM_12

#define BRAIN_VARIANT_REV_D
#endif


/********************************************************************
 * The Glamsign is really just a Rev E board but it uses both output
 * channels and does other things that normal brains won't do.
 ********************************************************************/
#ifdef BRAIN_VARIANT_GLAMSIGN

// Using some different pixels
#define BRAIN_NEO_COLORFEATURE     NeoRgbFeature

#define BRAIN_VARIANT_REV_E
#endif


/********************************************************************
 * The Glamsign mini is a prototype unit used to test animations for
 * the glamsign. It is built on a Pico board using Ton's 4way interface
 * board. It might serve as an example for other projects using that
 * hardware configuration.
 ********************************************************************/
#ifdef BRAIN_VARIANT_GLAMSIGN_MINI

// The Pico doesn't have the ethernet MAC so you have to disable
// it or crashie crashie
#define BRAIN_ETHERNET_ENABLED     false

#define BRAIN_GPIO_BUTTON_LEFT     GPIO_NUM_35
#define BRAIN_GPIO_BUTTON_RIGHT    GPIO_NUM_34
#define BRAIN_BUTTON_LEFT_ACTIVELOW  false
#define BRAIN_BUTTON_RIGHT_ACTIVELOW false

#define BRAIN_GPIO_LED_LEFT_EYE    GPIO_NUM_NC
#define BRAIN_GPIO_LED_RIGHT_EYE   GPIO_NUM_NC

#define BRAIN_GPIO_LED_RED         GPIO_NUM_21
#define BRAIN_GPIO_LED_GREEN       GPIO_NUM_19
#define BRAIN_GPIO_LED_BLUE        GPIO_NUM_22

#define BRAIN_GPIO_DISP_CLK        GPIO_NUM_14
#define BRAIN_GPIO_DISP_DATA       GPIO_NUM_27

#define BRAIN_GPIO_PIXEL_CH1       GPIO_NUM_25
#define BRAIN_DEFAULT_BRIGHTNESS   128

#endif

/********************************************************************
 * This is the variant for boards which Andrew did rework on. They
 * can be identified as the ones which have a couple tiny little
 * wires around the ESP32 module.
 *
 * The rework involves adding two wires which make the following
 * bonds:
 *
 *      GPIO34 <-> GPIO12
 *      GPIO35 <-> GPIO15
 *
 * The first one makes the RED led emitter drivable on channel 12.
 * The second connection allows output of the second led channel,
 * marked CH2 on the board, from GPIO15.
 ********************************************************************/
#ifdef BRAIN_VARIANT_REV_D_REWORKED
#define BRAIN_GPIO_LED_RED         GPIO_NUM_12
#define BRAIN_GPIO_PIXEL_CH2       GPIO_NUM_15

#define BRAIN_VARIANT_REV_D
#endif

/********************************************************************
 * Rev E boards are the 2019 production boards for the sheep and
 * for bikes. There are a lot of these and this is probably the
 * hardware variant you want to be using.
 ********************************************************************/
#ifdef BRAIN_VARIANT_REV_E
#define BRAIN_GPIO_LED_RED         GPIO_NUM_13
#define BRAIN_GPIO_PIXEL_CH2       GPIO_NUM_2
#define BRAIN_GPIO_BUTTON_RIGHT    GPIO_NUM_34

#define BRAIN_VARIANT_REV_D
#endif


// End of variant definition. Defaults Below.


/********************************************************************
 * Rev D boards were the first prototypes we ordered. We made some
 * mistakes with a couple of pins and then did some rework on a
 * few of the boards, which should probably get defined as it's own
 * variant here as well.
 *
 * They also have the TI 4 channel 3.3V to 5V level converter which
 * we discovered isn't really suitable as a line driver for the
 * LEDs so their expansion header has a significantly different
 * pinout as well.
 ********************************************************************/

/**
 * The pin for the the left LED emitter. The brain boards have
 * two LEDs positioned within the BAAAHS logo. This is the pin
 * that the left one is connected to.
 */
#ifndef BRAIN_GPIO_LED_LEFT_EYE
#define BRAIN_GPIO_LED_LEFT_EYE    GPIO_NUM_16
#endif

/**
 * Pin for the right LED.
 */
#ifndef BRAIN_GPIO_LED_RIGHT_EYE
#define BRAIN_GPIO_LED_RIGHT_EYE   GPIO_NUM_4
#endif

/**
 * The red emitter of a 3 element LED packaged onto the board
 */
#ifndef BRAIN_GPIO_LED_RED
#define BRAIN_GPIO_LED_RED         GPIO_NUM_34 // Invalid output
#endif

/**
 * The green emitter of a 3 element LED packaged onto the board
 */
#ifndef BRAIN_GPIO_LED_GREEN
#define BRAIN_GPIO_LED_GREEN       GPIO_NUM_33
#endif

/**
 * The blue emitter of a 3 element LED packaged onto the board
 */
#ifndef BRAIN_GPIO_LED_BLUE
#define BRAIN_GPIO_LED_BLUE        GPIO_NUM_14
#endif

/**
 * Pin for the "left" button of the board. This is probably
 * going to be used in the UI for things like "select"
 */
#ifndef BRAIN_GPIO_BUTTON_LEFT
#define BRAIN_GPIO_BUTTON_LEFT     GPIO_NUM_0
#endif

/**
 * Ping for the "right" button of the board. This is probably
 * going to be used as the enter or confirmation button, but
 * the UI hasn't been designed yet so it's hard to say.
 */
#ifndef BRAIN_GPIO_BUTTON_RIGHT
#define BRAIN_GPIO_BUTTON_RIGHT    GPIO_NUM_2
#endif

/**
 * This is the primary pixel data output pin. On a Brain it
 * is goes through a level converter and then to a pad
 * marked as CH1. This is on the right hand side of the brain
 * board.
 */
#ifndef BRAIN_GPIO_PIXEL_CH1
#define BRAIN_GPIO_PIXEL_CH1       GPIO_NUM_32
#endif

/**
 * Brains have a second channel that they can output pixel data
 * on. This is the "input" side of the board from a power
 * perspective and is marked as CH2 on the board.
 *
 * For 2019 it doesn't look like we're going to use this
 * second channel on the production sheep boards but it's there
 * for you to use. At the moment the codebase doesn't do
 * anything with this channel, but it likely will at somepoint.
 */
#ifndef BRAIN_GPIO_PIXEL_CH2
#define BRAIN_GPIO_PIXEL_CH2       GPIO_NUM_35 // Invalid output
#endif

/**
 * In addition to defining the GPIO pin for the two interface
 * buttons we need to declare whether the are active, that is
 * "pressed", when the signal is low or high. When the hardware
 * data line has a pull up resistor on it and the button
 * connects the data line to ground this should be set to true.
 *
 * Conversely, if the data line has a pulldown resistor
 * connecting it to ground and the physical switch then connects
 * the data line to 3.3V then this should be set to false.
 *
 * Some of the ESP32 pins have default pull up or pull down
 * resistors on them for bootstrapping purposes so that's why
 * we support both modalities.
 */
#ifndef BRAIN_BUTTON_LEFT_ACTIVELOW
#define BRAIN_BUTTON_LEFT_ACTIVELOW   true
#endif

/**
 * The active low definition for the right button.
 */
#ifndef BRAIN_BUTTON_RIGHT_ACTIVELOW
#define BRAIN_BUTTON_RIGHT_ACTIVELOW  false
#endif

/**
 * While it should be safe to always enable ethernet on a
 * Brain even if it doesn't have it's Ethernet jack added to
 * it (a common scenario for bike applications), this will
 * let you disable ethernet for situations where it either
 * makes no sense or flat out doesn't exist, such as when
 * using an ESP pico board.
 */
#ifndef BRAIN_ETHERNET_ENABLED
#define BRAIN_ETHERNET_ENABLED     true
#endif

/**
 * The NeoBuffer library we use for rendering pixel data is
 * heavily C++ template based so the type of pixels that
 * we are rendering to is set statically.
 *
 * This should be either NeoGrbFeature or NeoRgbFeature
 * depending on what type of pixel you are talking to.
 *
 * For the BAAAHS Sparkle Motion strands this should be set
 * to NeoGrbFeature.
 *
 * For ws2812 or ws2812b led strips it should also be set
 * to NeoGrbFeature.
 *
 * However, for the very common "bullet" leds used in sign
 * making this should be set to NeoRgbFeature. These
 * leds use the ws2811 driver chip which is separate from
 * the led emitter and this ws2811 driver expects the
 * pixel data in a slightly different format for whatever
 * reason.
 *
 * In terms of supporting other leds from this codebase,
 * that should be pretty straightforward because the
 * underlying NeoBuffer library supports quite a lot.
 * However we're just not focusing on that right now.
 *
 * The other thing we can add at some point is RGBW support
 * but we arem't there yet.
 */
#ifndef BRAIN_NEO_COLORFEATURE
#define BRAIN_NEO_COLORFEATURE     NeoGrbFeature
#endif

/**
 * The default pixel count is how many pixels are expected
 * to be connected to this board by default. This is likely
 * to be changed by board configuration, but because the
 * number of pixels is pretty closely tied to the hardware
 * configuration that's the reason this number is is
 * defined along with the hardware details.
 *
 * Be sure to check brain_config.h for any comments about
 * how this number can be overwritten through the configuration
 * infrastructure.
 */
#ifndef BRAIN_DEFAULT_PIXEL_COUNT
#define BRAIN_DEFAULT_PIXEL_COUNT  1000
#endif

/**
 * The FPS value for the default timebase
 */
#ifndef BRAIN_DEFAULT_FPS
#define BRAIN_DEFAULT_FPS          30
#endif

/**
 * Default brightness value for the led renderer
 */
#ifndef BRAIN_DEFAULT_BRIGHTNESS
#define BRAIN_DEFAULT_BRIGHTNESS   255
#endif

/**
 * Default power on color that is used if no shaders ever
 * come online. Usually a local shader will overwrite this
 * right away, but a default is good.
 *
 * This should be specified as a NeoBuffer color because
 * it is used like so:
 *
 *      m_buffer.ClearTo(BRAIN_POWER_ON_COLOR);
 *
 */
#ifndef BRAIN_POWER_ON_COLOR
#define BRAIN_POWER_ON_COLOR       RgbColor(255, 255, 255)
#endif


/**
 * SysMon is a component which periodically logs interesting
 * information about the state of the system to the standard
 * serial output. Things like the amount of free memory etc.
 * This defines how frequently it does that.
 */
#ifndef BRAIN_SYSMON_SECONDS
#define BRAIN_SYSMON_SECONDS       10
#endif


