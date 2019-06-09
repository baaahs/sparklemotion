#include "led-renderer.h"


#include "led-renderer_private.h"

LEDRenderer::LEDRenderer(TimeBase& timeBase) :
    m_pixels(PIXEL_COUNT, BRN01D_LED_OUT),
    m_buffer(PIXEL_COUNT, 1, nullptr),
    m_timeBase(timeBase)
{
    // Do something
    m_pixels.Begin();
    m_pixels.SetBrightness(32);
}

void static task_ledren(void* pvParameters) {
    ((LEDRenderer*)pvParameters)->_task();
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

    tcResult  = xTaskCreate(task_ledren, "ledren", TASK_LEDREN_STACK_SIZE,
                            this, TASK_LEDREN_PRIORITY, &tHandle);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create led render task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "LED task started");
    }

}

void static task_local_render(void* pvParameters) {
    ((LEDRenderer*)pvParameters)->_localRenderTask();
}

void
LEDRenderer::startLocalRenderTask() {
    TaskHandle_t tHandle = nullptr;
    BaseType_t tcResult;

    ESP_LOGI(TAG, "Starting local render task...");

    tcResult  = xTaskCreate(task_local_render, "ledren-local", TASK_LEDREN_STACK_SIZE,
                            this, TASK_LEDREN_PRIORITY, &tHandle);

    if (tcResult != pdPASS) {
        ESP_LOGE(TAG, "Failed to create local render task = %d", tcResult);
    } else {
        ESP_LOGI(TAG, "LED local render task started");
    }

}

void
LEDRenderer::_task() {
//    TickType_t xLastWakeTime;
//    const TickType_t xFrequency = xPortGetTickRateHz() / FPS;
//    //const TickType_t xFrequency = pdMS_TO_TICKS(500);
//
//    // Initialization
//
//    xLastWakeTime = xTaskGetTickCount();

    // bool isRed = false;

    // Make not so bright for now...

    while(true) {
        TickType_t toDelay = m_timeBase.ticksToNextFrame();
        if (!toDelay) {
            // We presumably _JUST_ did this frame, so delay by 1 tick
            // and try again
            // ESP_LOGE(TAG, "Delaying Show only 1 to advance to next frame");
            vTaskDelay(1);
            continue;
        }
        // ESP_LOGE(TAG, "Delaying Show for %d", toDelay);
        vTaskDelay(toDelay);

//        vTaskDelayUntil( &xLastWakeTime, xFrequency );

        // ESP_LOGI(TAG, "Getting pixel access to show");
        if (xSemaphoreTake(m_hPixelsAccess, portMAX_DELAY) != pdTRUE) {
            // Aack!
            continue;
        }

//        RgbColor color = isRed ? RgbColor(255, 0, 0) : RgbColor(0, 0, 255);
//        m_pixels.ClearTo(color);

        // Show waits for any pending DMA to finish, copies the values from the
        // pixel buffer into the DMA buffer, and then starts a new DMA
        // ESP_LOGI(TAG, "Show!");
        m_pixels.Show();
//
//        isRed = !isRed;

        xSemaphoreGive(m_hPixelsAccess);
    }

    // Just in case we ever exit, we're supposed to do this
    ESP_LOGE(TAG, "Aaaackk!!! LED Render main task exited");
    vTaskDelete(nullptr);

}

void
LEDRenderer::render() {

    // Render into all the pixels
    if (m_shader) {
        // The NeoBuffer concept of a renderer allows a buffer to be filtered
        // through the shader function to end up in a different destination
        // buffer. Thus we could be a whole tree out of these to match our
        // shader concept, but it means each shader would have it's own
        // buffer that is the full size of the output - something we can avoid
        // by using our own functional approach to shaders where we loop
        // through a tree structure to calculate each output value

        // Rather than forcing the shader to detect the begining by indexPixel == 0
        m_shader->Begin();

        m_buffer.Render(m_buffer, *m_shader);
    } else {
        ESP_LOGE(TAG, "Nothing to render!!!!");
    }


    // Don't want to block a rendering task in case the blitter task has gone upside down
    // ESP_LOGI(TAG, "Getting pixel access to blt");
    if (xSemaphoreTake(m_hPixelsAccess, pdMS_TO_TICKS(500)) != pdTRUE) {
        ESP_LOGE(TAG, "Failed to get pixel access semaphore in render.");
        return;
    }

    // ESP_LOGI(TAG, "Doing Blt");
    m_buffer.Blt(m_pixels, 0);

    xSemaphoreGive(m_hPixelsAccess);
}


void
LEDRenderer::_localRenderTask() {
    while(true) {
        // Render a frame
        render();

        // Delay until 1/4 frame duration into the next frame
        TickType_t toDelay = m_timeBase.ticksToNextFrame() +
            m_timeBase.toTicks(m_timeBase.getFrameDuration() / 4);
        // ESP_LOGW(TAG, "Rendered a frame. toDelay=%d", toDelay);
        vTaskDelay(toDelay);
    }

    // Just in case we ever exit, we're supposed to do this
    vTaskDelete(nullptr);
}