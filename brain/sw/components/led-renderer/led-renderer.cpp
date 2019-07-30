#include "led-renderer.h"

#include "sysmon.h"

#include "led-renderer_private.h"
#include <freertos/FreeRTOS.h>
#include <freertos/timers.h>

LEDRenderer::LEDRenderer(TimeBase& timeBase, uint16_t pixelCount) :
    m_pixels(pixelCount, BRN01D_LED1_OUT),
    m_buffer(pixelCount, 1, nullptr),
    m_timeBase(timeBase)
{
    // Start with an empty buffer
    m_buffer.ClearTo(RgbColor(255, 255, 255));

    m_pixels.Begin();

    m_nBrightness = 255;
}

void static glue_showTask(void* pvParameters) {
    ((LEDRenderer*)pvParameters)->_showTask();
}

void static glue_renderTask(void* pvParameters) {
    ((LEDRenderer*)pvParameters)->_renderTask();
}


void
LEDRenderer::start() {
    TaskHandle_t tHandle = nullptr;
    BaseType_t tcResult;

    ESP_LOGI(TAG, "Starting ledren task...");

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
    tcResult  = xTaskCreatePinnedToCore(glue_showTask, "show", TASK_SHOW_STACK_SIZE,
                            this, TASK_SHOW_PRIORITY, &tHandle, TASK_SHOW_CORE);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create led show task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "LED show task started");
    }

    //////////////////////////////////////////
    // The render task
    tcResult  = xTaskCreatePinnedToCore(glue_renderTask, "render", TASK_RENDER_STACK_SIZE,
                                        this, TASK_RENDER_PRIORITY, &tHandle, TASK_RENDER_CORE);

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
    while(true) {
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
        m_pixels.Show();

        xSemaphoreGive(m_hPixelsAccess);
    }

    // Just in case we ever exit, we're supposed to do this
    ESP_LOGE(TAG, "Aaaackk!!! LED Render main task exited");
    vTaskDelete(nullptr);
}

void
LEDRenderer::render() {

    gSysMon.startTiming(TIMING_RENDER);

    m_context.now = m_timeBase.currentTime();

    // Render into all the pixels
    if (m_shader) {
        // The NeoBuffer concept of a renderer allows a buffer to be filtered
        // through the shader function to end up in a different destination
        // buffer. Thus we could be a whole tree out of these to match our
        // shader concept, but it means each shader would have it's own
        // buffer that is the full size of the output - something we can avoid
        // by using our own functional approach to shaders where we loop
        // through a tree structure to calculate each output value

        // Rather than forcing the shader to detect the beginning by indexPixel == 0
        const uint16_t INTERVAL_BASE = 1000;
        uint16_t intPos = m_timeBase.posInInterval(m_timeBase.currentTime()/10000, 8 * USEC_IN_SEC/10000, INTERVAL_BASE);
        m_context.progress = ((float)intPos) / (float)INTERVAL_BASE;

        m_shader->beginShade(&m_context);
        // ESP_LOGI(TAG, "time=%d, intPos = %d  progress=%f", m_timeBase.currentTime(), intPos, progress);

        m_buffer.Render(m_buffer, *m_shader);

        // We used to implement brightness by calling Render a second time on the same buffer.
        // However, that is inefficient because if you are using a non-RGB color feature you would
        // have to un-apply the feature since Rendering automatically applies it back. That's a waste
        // so we do this more efficient thing instead.
        if (m_nBrightness != 255) {
            NeoBufferContext<LED_RENDERER_COLORFEATURE> context = m_buffer;
            uint8_t* pCursor = context.Pixels;
            uint8_t* pEnd = pCursor + context.SizePixels;
            while(pCursor != pEnd) {
                uint16_t value = *pCursor;
                *(pCursor++) = (value * m_nBrightness) >> 8;
            }
        }

        m_shader->endShade();
    } else {
        ESP_LOGE(TAG, "Nothing to render!!!!");
    }

    gSysMon.endTiming(TIMING_RENDER);

    // Don't want to block a rendering task in case the blitter task has gone upside down
    // ESP_LOGI(TAG, "Getting pixel access to blt");
    if (xSemaphoreTake(m_hPixelsAccess, pdMS_TO_TICKS(500)) != pdTRUE) {
        ESP_LOGE(TAG, "Failed to get pixel access semaphore in render.");
        return;
    }

    // ESP_LOGI(TAG, "Doing Blt");
    m_buffer.Blt(m_pixels, 0);
    // logPixels();
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
    while(true) {
        // Render a frame
        if (m_localRenderEnabled) {
            render();
        }
        // Could we stop this task? Yes. However, it's easier to leave it running and only consuming
        // a tiny amount of resources.

        // Delay until 1/4 frame duration into the next frame
        TickType_t toDelay = m_timeBase.ticksToNextFrame() +
            m_timeBase.toTicks(m_timeBase.getFrameDuration() / 4);
        // ESP_LOGW(TAG, "Rendered a frame. toDelay=%d", toDelay);
        vTaskDelay(toDelay);
    }

    // Just in case we ever exit, we're supposed to do this
    vTaskDelete(nullptr);
}

void
LEDRenderer::logPixels() {
    ESP_LOG_BUFFER_HEXDUMP(TAG, m_pixels.Pixels(), 6, ESP_LOG_INFO);
    // ESP_LOG_BUFFER_HEXDUMP(TAG, m_pixels.Pixels(), m_pixels.PixelsSize(), ESP_LOG_INFO);
}