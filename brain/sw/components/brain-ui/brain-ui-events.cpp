//
// Created by Tom Seago on 2019-07-30.
//

#include <esp_event.h>
#include "brain-ui-events.h"
#include "brain-ui-priv.h"

void
BrainUiEvent::post() {
    auto err = esp_event_post(BRAIN_UI_BASE, id, this, sizeof(BrainUiEvent), 2);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "Unable to post UI event %d key=%d mods=%d", id, code, modifiers);
    }
}