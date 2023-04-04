//
// Created by Tom Seago on 2/26/21.
//
// Reference:
//   Datasheet => https://cdn-shop.adafruit.com/datasheets/DHT22.pdf
//   Adafruit lib => https://github.com/adafruit/DHT-sensor-library/blob/master/DHT.cpp

#include "DHTSensor.h"
#include "brain_common.h"

static const char* TAG = TAG_PROBE;

void glue_isr(void *pArg) {
    ((DHTSensor*)pArg)->_isr();
}

void glue_handleEdge(void *pArg, uint32_t pArg2) {
    ((DHTSensor*)pArg)->_edgeNotify(pArg2);
}

void glue_edgeTimeout(TimerHandle_t xTimer) {
    void* pArg = pvTimerGetTimerID(xTimer);
    ((DHTSensor*)pArg)->_edgeTimeout();
}

DHTSensor::DHTSensor(gpio_num_t gpioNum, DHTSensorListener& listener) :
    m_gpioNum(gpioNum),
    m_listener(listener)
{

}

void
DHTSensor::start() {
    // This will cause an error to be printed the second time it is called but we don't
    // really care and it's easier than adding a guard bit. We should really check
    // it's error codes though...
    gpio_install_isr_service(0);

    gpio_config_t config;
    config.pin_bit_mask = (uint64_t)1 << m_gpioNum;
    config.mode = GPIO_MODE_INPUT_OUTPUT;
    config.pull_up_en = GPIO_PULLUP_ENABLE;
    config.pull_down_en = GPIO_PULLDOWN_DISABLE;
    config.intr_type = GPIO_INTR_ANYEDGE;

    if (ESP_OK != gpio_config(&config)) {
        ESP_LOGE(TAG, "Failed to configure gpio for DHTSensor on %d", m_gpioNum);
        return;
    }

    gpio_isr_handler_add(m_gpioNum, glue_isr, this);

    // Need a timer also
    m_hEdgeTimeout = xTimerCreate("DHTSensor",
                                     2, //pdMS_TO_TICKS(1000),
                                     pdTRUE, // res reload
                                     this,    // A timer ID equal to us
                                     glue_edgeTimeout);
    if (!m_hEdgeTimeout) {
        ESP_LOGE(TAG, "DHTSensor Failed to create timeout timer");
    } else {
        //xTimerStart(m_hEdgeTimeout, pdMS_TO_TICKS(1000));
    }

}

void
DHTSensor::maybeRead() {
    // This is sort of like OneWire but totally not. Only a single
    // device can be on the bus because there is no device addressing.

    // Init signal is at least 500us
    // The post reset signal is 20-40us
    // A presence signal (pull 0) is sent by sensor for 80us
    // An 80us gap of pull up is sent.
    // Data transmission begins

    if (m_ReadPending) {
        ESP_LOGI(TAG, "Read already pending, not starting a new one");
        // TODO make sure the timer is really pending
        return;
    }

    // Just set a timeout
    ESP_LOGI(TAG, "Starting timeout for ClearLine");
    m_Expect = ClearLine;
    m_ReadPending = true;

    _sendReadStart();
}

void
DHTSensor::_isr() {
    // Only need to do a task call if quiet. If these are different than
    // there is (presumably) an existing loop running so don't stack up a
    // bunch of task calls.
    bool needsTaskCall = m_EdgeReadIx == m_NextEdgeIx;

    // Read the signal level and note the time
    uint32_t edgeIx = m_NextEdgeIx++;
    DHTEdge *pEdge = &(m_Edges[edgeIx]);
    if (m_NextEdgeIx >= EDGE_BUFFER_LEN) {
        m_NextEdgeIx = 0;
    }

    pEdge->m_At = esp_timer_get_time();
    pEdge->m_Level = (bool)gpio_get_level(m_gpioNum);
    pEdge->m_Sequence = m_NextSequence++;

    if (needsTaskCall) {
        // Queue up a call to our regular task function that does the actual processing
        // rather than doing processing here in the ISR
        BaseType_t xHigherPriorityTaskWoken;
//        xTimerPendFunctionCallFromISR(glue_callListener, this, edgeIx, &xHigherPriorityTaskWoken);
        xTaskNotifyFromISR(m_TaskToNotify, m_NextSequence, eSetValueWithOverwrite, &xHigherPriorityTaskWoken);
        // Might need the currently interrupted task to yield, which we force here.
        if (xHigherPriorityTaskWoken) {
            portYIELD_FROM_ISR();
        }
    }
}

void
DHTSensor::_edgeNotify(uint32_t edgeIx) {
//    ESP_LOGW(TAG,"_callListener start edgeIx=%d", edgeIx);
    while(m_EdgeReadIx != m_NextEdgeIx) {
        _handleEdge(m_EdgeReadIx);

        m_EdgeReadIx++;
        if (m_EdgeReadIx >= EDGE_BUFFER_LEN) {
            m_EdgeReadIx = 0;
        }
    }
//    ESP_LOGW(TAG,"_callListener end edgeIx=%d", edgeIx);
}

void
DHTSensor::_handleEdge(uint32_t edgeIx) {
    DHTEdge *pEdge = &(m_Edges[edgeIx]);

//    ESP_LOGI(TAG, "_handleEdge(%d) pEdge->m_Sequence=%d, m_LastSequence=%d, state=%d",
//             edgeIx, pEdge->m_Sequence, m_LastSequence, m_Expect);
    // ring buffer overflow
    if (pEdge->m_Sequence - m_LastSequence > 1) {
        ESP_LOGE(TAG, "Ring buffer overflow lastSeq=%d thisSeq=%d delta=%d edgeIx=%d", m_LastSequence, pEdge->m_Sequence,
                 pEdge->m_Sequence - m_LastSequence, edgeIx);
        m_LastSequence++;

        // TODO: This probably entirely aborts the reading, so maybe a state thing?
        return;
    }

    // special case startup
    if (pEdge->m_Sequence == m_LastSequence) {
        // All is well, but it's almost certainly 0 right?? I guess we could check
        // but whatever.

        // Specifically do NOT increment m_LastSequence yet
        return;
    }

    // Okay we should be able to lookup the old record reasonably now
    uint8_t lastIx = edgeIx - 1;
    if (edgeIx == 0) {
        lastIx = EDGE_BUFFER_LEN - 1;
    }
    DHTEdge *pLast = &(m_Edges[lastIx]);

    // Let's make sure this really is an edge that we expect and we didn't miss
    // and ISR call somehow

    // Special case the first one though so it will pass the "did we miss something?"
    // checking
    if (m_LastSequence == 0) {
        pLast->m_Level = !pEdge->m_Level;
    }

    if (pLast->m_Level == pEdge->m_Level) {
        ESP_LOGE(TAG, "DHTSensor: level didn't change for this edge int???");

        // TODO: Error condition?

        m_LastSequence++;
        return;
    }

    // Okay it really did change, so what is the pulse width?
    int64_t pulseWidth = pEdge->m_At - pLast->m_At;
    bool pulseLevel = !pEdge->m_Level;

    switch (m_Expect) {
        case ClearLine:
            ESP_LOGD(TAG, "\\%s %lld  ClearLine", pulseLevel?"HIGH":"LOW ", pulseWidth);
            // Just wait for any data to clear

            if (pulseLevel) {
                // It went low, but we need it to go high
                _setTimeout(0);
            } else {
                // Line is now high, so set a timeout which will move us into
                // the InitStart state. If the line goes low again before this
                // timeout, then it will be cleared by the other claus there.
                _setTimeout(500);
            }
            break;

        case InitStart:
            // The beginning of the output section
            ESP_LOGD(TAG, "\\%s %lld  InitStart", pulseLevel?"HIGH":"LOW ", pulseWidth);
            if (pulseLevel) {
                ESP_LOGE(TAG, "Got HIGH while waiting for InitStart pulse");
            } else {
                m_Expect = InitEnd;
                _setTimeout(1000);
            }
            break;

        case InitEnd:
            // Output has ended and then we wait for a bit until the
            // sensor responds
            ESP_LOGD(TAG, "\\%s %lld  InitEnd", pulseLevel?"HIGH":"LOW ", pulseWidth);
            if (pulseLevel) {
                m_Expect = PresenceLow;
                _setTimeout(150);
            } else {
                // We should also register a timer here I think...
                ESP_LOGE(TAG, "Got HIGH while waiting for InitEnd");
            }
            break;

        case PresenceLow:
            ESP_LOGD(TAG, "\\%s %lld  PresenceLow", pulseLevel?"HIGH":"LOW ", pulseWidth);
            if (pulseLevel) {
                // This is the begining of presence low pulse. just ignore
                ESP_LOGE(TAG, "Got HIGH while waiting for PresenceLow");
            } else {
                // End of the first half of presence, Should be 80us
                m_Expect = PresenceHigh;
            }
            break;

        case PresenceHigh:
            ESP_LOGD(TAG, "\\%s %lld  PresenceHigh", pulseLevel?"HIGH":"LOW ", pulseWidth);
            if (pulseLevel) {
                // Should be about 80us. From here we are ready for data
                m_Expect = BitMarker;
                _setTimeout(150);
            } else {
                ESP_LOGE(TAG, "Got LOW while waiting for PresenceHigh");
            }
            break;

        case BitMarker:
            ESP_LOGD(TAG, "\\%s %lld  BitMarker", pulseLevel?"HIGH":"LOW ", pulseWidth);
            _setTimeout(150);
            // The bit marker is a low pulse of 50us
            if (pulseLevel) {
                ESP_LOGE(TAG, "Got HIGH while waiting for BitMarker");
            } else {
                if (pulseWidth < 40 || pulseWidth > 70) {
                    ESP_LOGE(TAG, "Out of range pulseWidth of %lld for BitMarker", pulseWidth);
                }
                m_Expect = BitValue;
            }
            break;

        case BitValue:
            ESP_LOGD(TAG, "\\%s %lld  BitValue", pulseLevel?"HIGH":"LOW ", pulseWidth);
            _setTimeout(150);
            // The value pulse is a high pulse whose length indicates the bit value
            // A 0 is 26-28us, A 1 is 70us
            if (pulseLevel) {
                _addBit(pulseWidth > 40);

                m_Expect = BitMarker;
            } else {
                ESP_LOGE(TAG, "Got low pulse when expecting a BitValue pulse");
            }
            break;
    }

    m_LastSequence++;
}

void
DHTSensor::_edgeTimeout() {
    uint64_t now = esp_timer_get_time();

    if (!m_NextTimeoutTarget) {
        // Don't need to do anything
        return;
    }

    if (now > m_NextTimeoutTarget) {
        m_NextTimeoutTarget = 0;
        _handleTimeout();
    }
}

void
DHTSensor::_handleTimeout() {
    switch (m_Expect) {
        case ClearLine:
            // Awesome! We can move to InitStart
            ESP_LOGD(TAG, "edgeTimeout in ClearLine so moving to InitStart");
            m_Expect = InitStart;
            if (m_ReadPending) {
                // Send the init to start the read
                _sendReadStart();
            } else {
                // else we're all good, line is clear
                ESP_LOGD(TAG, "Line cleared but no read pending");
            }
            break;

        case InitStart:
            // No timeout, we control this duration
            break;

        case InitEnd:
            ESP_LOGD(TAG, "edgeTimeout in InitEnd means no device present");
            // TODO: What to do about no device??
            m_Expect = ClearLine;
            break;

        case PresenceLow:
            ESP_LOGD(TAG, "edgeTimeout in PresenceLow");
            break;

        case PresenceHigh:
            ESP_LOGD(TAG, "edgeTimeout in PresenceHigh");
            break;

        case BitMarker:
            ESP_LOGD(TAG, "edgeTimeout in BitMarker, must be done with bits");
            _readEnded();
            break;

        case BitValue:
            ESP_LOGD(TAG, "edgeTimeout in BitValue, must also be done with bits");
            _readEnded();
            break;
    }
}

void
DHTSensor::_readEnded() {

    m_Expect = ClearLine;

    if (m_CurrentByte<5) {
        ESP_LOGE(TAG, "_readEnded byt m_CurrentByte is only %d not 5 or more", m_CurrentByte);
        return;
    }

    uint8_t checksum = 0;
    for (uint8_t i = 0; i < m_CurrentByte - 1; i++) {
        checksum += m_raw[i];
        ESP_LOGD(TAG, "raw[%d] = %02x", i, m_raw[i]);
    }

    if (checksum == m_raw[m_CurrentByte-1]) {
        ESP_LOGD(TAG, "Good checksum of %d", checksum);

        m_relativeHumidity = ((int16_t)m_raw[0]) << 8 | m_raw[1];
        m_relativeHumidity *= 0.1;

        m_tempC = ((int16_t)(m_raw[2] & 0x7F)) << 8 | m_raw[3];
        m_tempC *= 0.1;
        if (m_raw[2] & 0x80) {
            m_tempC *= -1;
        }

        m_readingAt = esp_timer_get_time();
//        float tempF = (tempC * 1.8) + 32;

        //ESP_LOGW(TAG, "rh=%f, tempC=%f, tempF=%f", rh, tempC, tempF);

        m_listener.dhtSensorReadData(*this);
    } else {
        ESP_LOGD(TAG, "Bad checksum of %d instead of %d", checksum, m_raw[m_CurrentByte-1]);
    }

}

void
DHTSensor::_setTimeout(uint32_t micros) {
    if (!micros) {
        m_NextTimeoutTarget = 0;
    } else {
        m_NextTimeoutTarget = micros + esp_timer_get_time();
    }
}

void
DHTSensor::_resetReadPos() {
    m_CurrentByte = 0;
    m_CurrentBitmask = 1 << 7;
}

void
DHTSensor::_addBit(bool bit) {
    if (m_CurrentByte >= RAW_BUFFER_LEN) {
        ESP_LOGE(TAG, "DHTSensor read buffer overflow, m_CurrentByte=%d", m_CurrentByte);
        return;
    }

    if (!bit) {
        m_raw[m_CurrentByte] &= ~m_CurrentBitmask;
    } else {
        m_raw[m_CurrentByte] |= m_CurrentBitmask;
    }
    m_CurrentBitmask = m_CurrentBitmask >> 1;

    if (!m_CurrentBitmask) {
        m_CurrentByte++;
        m_CurrentBitmask = 1 << 7;
        ESP_LOGW(TAG, "Read byte #%d = %02x", m_CurrentByte-1, m_raw[m_CurrentByte-1]);
    }
}


void
DHTSensor::_sendReadStart() {
    // Just in case eh?
    _setTimeout(0);
    m_ReadPending = false;
    _resetReadPos();

    ESP_LOGD(TAG, "Sending the read start");

    esp_err_t code;
    code = gpio_set_direction(m_gpioNum, GPIO_MODE_OUTPUT);
    if (code != ESP_OK) {
        ESP_LOGE(TAG, "Failed setting to output mode : %s", esp_err_to_name(code));
        return;
    }

    m_Expect = InitStart;
    _setTimeout(1000); // This is forever

    // Drive it low
    code = gpio_set_level(m_gpioNum, 0);
    if (code != ESP_OK) {
        ESP_LOGE(TAG, "Failed setting to 0 : %s", esp_err_to_name(code));
        return;
    }

    // Don't thing we need to be super precise on this 500us so no critical
    // section here.
    ets_delay_us(500);

    code = gpio_set_direction(m_gpioNum, GPIO_MODE_INPUT);
    if (code != ESP_OK) {
        ESP_LOGE(TAG, "Failed setting to input mode : %s", esp_err_to_name(code));
        return;
    }

    ESP_LOGD(TAG, "Sent init pulse");
}