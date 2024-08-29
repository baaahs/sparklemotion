#include "led-renderer.h"
#include "gamma.h"

#include "sysmon.h"

#include "led-renderer_private.h"
#include <freertos/FreeRTOS.h>
#include <freertos/timers.h>


LEDRenderer::LEDRenderer(TimeBase& timeBase, uint16_t pixelCount, int gpioNum = BRAIN_GPIO_PIXEL_CH1) :
//    m_pixels(pixelCount, BRAIN_GPIO_PIXEL_CH1),
//    m_buffer(pixelCount, 1, nullptr),
    m_gpioNum(gpioNum),
    m_pixelCount(pixelCount),
    m_timeBase(timeBase)
{
//    // Start with an empty buffer
//    m_buffer.ClearTo(BRAIN_POWER_ON_COLOR);
//
//    m_pixels.Begin();
    m_nBrightness = BRAIN_DEFAULT_BRIGHTNESS;
}

void static glue_showTask(void* pvParameters) {
    ((LEDRenderer*)pvParameters)->_showTask();
}

void static glue_renderTask(void* pvParameters) {
    ((LEDRenderer*)pvParameters)->_renderTask();
}


void
LEDRenderer::start(TaskDef show, TaskDef render) {
    TaskHandle_t tHandle = nullptr;
    BaseType_t tcResult;

    ESP_LOGI(TAG, "Starting ledren task...");

    // Moved this initialization into the task start because doing a small malloc during
    // the constructor is failing

    /* LED strip initialization with the GPIO and pixels number*/
    led_strip_config_t strip_config = {
            .strip_gpio_num = m_gpioNum, // The GPIO that connected to the LED strip's data line
            .max_leds = m_pixelCount, // The number of LEDs in the strip,
#ifdef BRAIN_USE_RGBW
            .led_pixel_format = LED_PIXEL_FORMAT_GRBW, // Pixel format of your LED strip
#else
//            .led_pixel_format = LED_PIXEL_FORMAT_GRB, // Pixel format of your LED strip
            .led_pixel_format = LED_PIXEL_FORMAT_GRB, // Pixel format of your LED strip
#endif
            .led_model = LED_MODEL_WS2812, // LED strip model
            .flags = {.invert_out = false}, // whether to invert the output signal (useful when your hardware has a level inverter)
    };

    led_strip_rmt_config_t rmt_config = {
            .clk_src = RMT_CLK_SRC_DEFAULT, // different clock source can lead to different power consumption
            .resolution_hz = 10 * 1000 * 1000, // 10MHz
            .mem_block_symbols = 0, // Use default value
            // TODO: Test this DMA feature...
            .flags = {.with_dma = false}, // whether to enable the DMA feature
    };
    ESP_ERROR_CHECK(led_strip_new_rmt_device(&strip_config, &rmt_config, &m_ledStrip));

    //m_pixels = (uint8_t *)malloc(m_pixelCount & gPixelWidth);
    m_pixels = new uint8_t[m_pixelCount * gPixelWidth];
    if (!m_pixels) {
        ESP_LOGE(TAG, "Failed to allocate %d bytes for pixel buffer!", m_pixelCount * gPixelWidth);
    }





    // Create our frame blt lock
    m_hPixelsAccess = xSemaphoreCreateBinary();
    if (!m_hPixelsAccess) {
        ESP_LOGE(TAG, "ERROR: Unable to alloocate the m_hPixelsAccess semaphore");
        return;
    }
    // Must always give to begin with.
    xSemaphoreGive(m_hPixelsAccess);

    //////////////////////////////////////////
    // The show task
    tcResult = show.createTask(glue_showTask, this, &tHandle);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create led show task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "LED show task started");
    }

    //////////////////////////////////////////
    // The render task
    tcResult = render.createTask(glue_renderTask, this, &tHandle);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create led render task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "LED render task started");
    }
}

//void
//LEDRenderer::startLocalRenderTask() {
//    TaskHandle_t tHandle = nullptr;
//    BaseType_t tcResult;
//
//    ESP_LOGI(TAG, "Starting local render task...");
//
//    tcResult  = xTaskCreate(task_local_render, "ledren-local", TASK_LEDREN_STACK_SIZE,
//                            this, TASK_LEDREN_PRIORITY, &tHandle);
//
//    if (tcResult != pdPASS) {
//        ESP_LOGE(TAG, "Failed to create local render task = %d", tcResult);
//    } else {
//        ESP_LOGI(TAG, "LED local render task started");
//    }
//
//}

/**
 * The job of the show task is to periodically blit the pixel data from the internal
 * buffer out to the output strand. The frequency of this is determined by the
 * m_timeBase component and in general should be fairly stable. The only thing that will
 * interrupt it is if it's time to blit, but rendering of the current frame is in
 * progress. In this case this task will block until the completion of the render
 * task.
 */
void
LEDRenderer::_showTask() {
    while(!m_timeToDie) {
        TickType_t toDelay = m_timeBase.ticksToNextFrame();
        if (!toDelay) {
            // We presumably _JUST_ did this frame, so delay by 1 tick
            // and try again
            // ESP_LOGE(TAG, "Delaying Show only 1 to advance to next frame");

            // TODO: Change this to a yield loop because 1 tick is 10ms which may be
            // way longer than we really want to be delaying...
            vTaskDelay(1);
            continue;
        }
        // ESP_LOGE(TAG, "Delaying Show for %d", toDelay);
        vTaskDelay(toDelay);


        // ESP_LOGI(TAG, "Getting pixel access to show");
        if (xSemaphoreTake(m_hPixelsAccess, portMAX_DELAY) != pdTRUE) {
            // Aack!
            continue;
        }

        // Show waits for any pending DMA to finish, copies the values from the
        // pixel buffer into the DMA buffer, and then starts a new DMA
        // ESP_LOGI(TAG, "Show!");
        gSysMon.endTiming(TIMING_SHOW_OUTPUTS);
        gSysMon.startTiming(TIMING_SHOW_OUTPUTS);
        ESP_ERROR_CHECK_WITHOUT_ABORT(led_strip_refresh(m_ledStrip));
        xSemaphoreGive(m_hPixelsAccess);
    }

    // Just in case we ever exit, we're supposed to do this
    ESP_LOGE(TAG, "LEDRenderer::_showTask exiting");
    vTaskDelete(nullptr);
}

void
LEDRenderer::render() {

    gSysMon.startTiming(TIMING_RENDER);

    // Not that this should change, but maybe???
    m_context.numPixels = m_pixelCount;

    // The current time, which probably shouldn't be used in favor of .progress but still, be nice
    m_context.now = m_timeBase.currentTime();

    // The m_context time stuff will wrap and do bad things. People will
    // want to use this tvNow value instead. This stuff needs to be carefully
    // examined and fixed!
    // TODO: Rework all the time stuff
    gettimeofday(&m_context.tvNow, nullptr);
    m_context.msNow = (m_context.tvNow.tv_sec * 1000) + (m_context.tvNow.tv_usec / 1000);

    // Render into all the pixels
    if (m_shader) {
        // The NeoBuffer concept of a renderer allows a buffer to be filtered
        // through the shader function to end up in a different destination
        // buffer. Thus we could be a whole tree out of these to match our
        // shader concept, but it means each shader would have it's own
        // buffer that is the full size of the output - something we can avoid
        // by using our own functional approach to shaders where we loop
        // through a tree structure to calculate each output value

//        const uint16_t INTERVAL_BASE = 1000;
//        uint16_t intPos = m_timeBase.posInInterval(m_timeBase.currentTime()/1000, 1 * USEC_IN_SEC/1000, INTERVAL_BASE);
//        m_context.progress = ((float)intPos) / (float)INTERVAL_BASE;
        // TODO: Remove this from the render context
        m_context.progress = 0.5f;

        // Rather than forcing the shader to detect the beginning by indexPixel == 0
        m_shader->beginShade(&m_context);
        // ESP_LOGI(TAG, "time=%d, intPos = %d  progress=%f", m_timeBase.currentTime(), intPos, progress);

        //m_buffer.Render(m_buffer, *m_shader);
        uint8_t* cursor = m_pixels;
        for (uint16_t ix = 0; ix < m_pixelCount; ix++) {
            m_shader->Apply(ix, cursor, cursor);

            // Apply gamma correction.
            // TODO: This should be exposed and configurable via the protocol.
            for (uint8_t bix = 0; bix < gPixelWidth; bix++) {
                cursor[bix] = Gamma::Correct22(cursor[bix], m_frameNumber, ix);
            }
            cursor += gPixelWidth;
        }

        // We used to implement brightness by calling Render a second time on the same buffer.
        // However, that is inefficient because if you are using a non-RGB color feature you would
        // have to un-apply the feature since Rendering automatically applies it back. That's a waste
        // so we do this more efficient thing instead.
//        if (m_nBrightness != 255) {
//            NeoBufferContext<BRAIN_NEO_COLORFEATURE> context = m_buffer;
//            uint8_t* pCursor = context.Pixels;
//            uint8_t* pEnd = pCursor + context.SizePixels;
//            while(pCursor != pEnd) {
//                uint16_t value = *pCursor;
//                *(pCursor++) = (value * m_nBrightness) >> 8;
//            }
//        }

        // Apply gamma correction.
//        {
//            NeoBufferContext<BRAIN_NEO_COLORFEATURE> buf = m_buffer;
//            uint8_t *pCursor = buf.Pixels;
//            uint8_t *pEnd = pCursor + buf.SizePixels;
//            uint32_t pixelIndex = 0;
//            while (pCursor != pEnd) {
//                for (int i = 0; i < BRAIN_NEO_COLORFEATURE::PixelSize; i++) {
//                    uint8_t corrected = Gamma::Correct(*pCursor, m_frameNumber, pixelIndex);
//                    *(pCursor++) = corrected;
//                }
//                pixelIndex++;
//            }
//        }

        m_shader->endShade();

        m_frameNumber++;
    } else {
        ESP_LOGE(TAG, "Nothing to render!!!!");
    }

    gSysMon.endTiming(TIMING_RENDER);

    // Safety valve in case we never allocated the pixel buffer
    if (!m_pixels) return;

    // Don't want to block a rendering task in case the blitter task has gone upside down
    // ESP_LOGI(TAG, "Getting pixel access to blt");
    if (xSemaphoreTake(m_hPixelsAccess, pdMS_TO_TICKS(500)) != pdTRUE) {
        ESP_LOGE(TAG, "Failed to get pixel access semaphore in render.");
        return;
    }

    // ESP_LOGI(TAG, "Doing Blt");
//    m_buffer.Blt(m_pixels, 0);

    uint8_t* cursor = m_pixels;
    for (uint32_t pix = 0; pix < m_pixelCount; pix++ ) {
        uint16_t r = *(cursor++);
        uint16_t g = *(cursor++);
        uint16_t b = *(cursor++);
#ifdef BRAIN_USE_RGBW
        uint16_t w = *(cursor++);
#endif
        if (m_nBrightness != 255) {
            r = (r * m_nBrightness) >> 8;
            g = (g * m_nBrightness) >> 8;
            b = (b * m_nBrightness) >> 8;
#ifdef BRAIN_USE_RGBW
            w = (w * m_nBrightness) >> 8;
#endif
        }

#ifdef BRAIN_USE_RGBW
        led_strip_set_pixel_rgbw(m_ledStrip, pix, r, g, b, w);
#else
        led_strip_set_pixel(m_ledStrip, pix, r, g, b);
#endif
    }


     logPixels();
    xSemaphoreGive(m_hPixelsAccess);
}

/**
 * The render task will periodically call the render function in order to render a new
 * frame of pixel values. In a scenario where renders are to be driven solely by the network,
 * a different approach would be used where this task wouldn't be invoked.
 *
 * As things stand at the moment, the ShadeTree component holds on to the last shader message
 * from the network until a new one is received. That "last" shader message might get
 * used to render multiple frames.
 */
void
LEDRenderer::_renderTask() {
    while(!m_timeToDie) {
        // Render a frame
        if (m_localRenderEnabled) {
            render();
        }
        // Could we stop this task? Yes. However, it's easier to leave it running and only consuming
        // a tiny amount of resources.

        // TODO: Confirm that this timing code is working as expected.
        // The idea that is trying to happen is that this runs no faster than 4x the
        // speed of the show output, but in reality it seems to run unbounded which
        // is likely wasting resources.

        // Delay until 1/4 frame duration into the next frame
        TickType_t toDelay = m_timeBase.ticksToNextFrame() +
            m_timeBase.toTicks(m_timeBase.getFrameDuration() / 4);
        // ESP_LOGW(TAG, "Rendered a frame. toDelay=%d", toDelay);
        vTaskDelay(toDelay);
    }

    // Just in case we ever exit, we're supposed to do this
    ESP_LOGE(TAG, "LEDRenderer::_renderTask exiting");
    vTaskDelete(nullptr);
}

void
LEDRenderer::logPixels() {
    ESP_LOG_BUFFER_HEXDUMP(TAG, m_pixels, 12, ESP_LOG_INFO);
    // ESP_LOG_BUFFER_HEXDUMP(TAG, m_pixels.Pixels(), m_pixels.PixelsSize(), ESP_LOG_INFO);
}