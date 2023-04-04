#include "iso-control-state.h"

#define TAG TAG_ISO

IsoControlState::IsoControlState(uint8_t maxChoices) :
    m_maxChoices(maxChoices)
{
    m_pColorChoices = (RgbColor*)malloc(sizeof(RgbColor) * 3);
    if (!m_pColorChoices) {
        ESP_LOGE(TAG, "OOM allocating color choices array");
        m_pColorChoices = 0;
    }
}

RgbColor
IsoControlState::chosenColor(uint8_t ix) {
    if (!m_maxChoices) {
        return RgbColor(0, 0, 0);
    }

    if (ix > m_maxChoices) {
        ix = 0;
    }

    return m_pColorChoices[ix];
}

void
IsoControlState::setChosenColor(uint8_t ix, RgbColor color) {
    if (!m_maxChoices) return;

    if (ix > m_maxChoices) return;

    m_pColorChoices[ix] = color;

    // TODO: Send an event??
}