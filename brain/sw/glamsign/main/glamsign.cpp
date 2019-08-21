//
// Created by Tom Seago on 2019-06-02.
//

#include "glamsign.h"
#include "brain_common.h"

#include "esp_log.h"

static const char* TAG = TAG_BRAIN;

Glamsign::Glamsign() :
    m_ledRenderer(m_timeBase, m_pixelCount)
{
}

void
Glamsign::start() {
    gSysMon.start(DefaultBrainTasks.sysmon);
    m_brainUI.start(DefaultBrainTasks.ui);

    // TODO: Check with the UI to see if the user is holding down a button so that we enter config mode instead of regular boot

    startSecondStageBoot();
}

void Glamsign::startSecondStageBoot() {
    GlobalConfig.load();

    m_netTransport.start(DefaultBrainTasks.net);

    // Just re-use the other net task definitions for simplicity
    m_artnetService.start(DefaultBrainTasks.netInput, DefaultBrainTasks.netOutput);

    m_shadeTree.start();
    ESP_LOGE(TAG, "m_shadeTree started");

    m_ledRenderer.setShader(&m_shadeTree);

    // Start talking to the pixels
    m_ledRenderer.start(DefaultBrainTasks.show, DefaultBrainTasks.render);

    // Some initial debugging stuff
    ESP_LOGE(TAG, "------- Glamsign Start ---------");
    ESP_LOGE(TAG, "xPortGetTickRateHz = %d", xPortGetTickRateHz());
    ESP_LOGE(TAG, "pdMS_TO_TICKS(1000) = %d", pdMS_TO_TICKS(1000));
    ESP_LOGE(TAG, "getFPS() = %d", m_timeBase.getFPS());
    ESP_LOGE(TAG, "getFrameDuration() = %d", m_timeBase.getFrameDuration());

    // Do this last!
    m_httpServer.start();
}