//
// Created by Tom Seago on 2/26/21.
//

#pragma once

#include <driver/gpio.h>
#include <freertos/FreeRTOS.h>
#include <freertos/portable.h>
#include <freertos/timers.h>

class DHTSensor;

class DHTSensorListener {
public:
    virtual void dhtSensorReadData(DHTSensor& sensor) = 0;
};

struct DHTEdge {
    bool m_Level;
    uint64_t m_At;
    uint32_t m_Sequence; // used to detect an overlap in the ring buffer
};

#define EDGE_BUFFER_LEN 200
#define RAW_BUFFER_LEN 10

/**
 * This implements an I/O Interface for Dallas Semiconductonr DHT22 sensor.
 * Originally other implementations were attempted porting from Arduino
 * world before frustration set in to just do it properly with an ISR
 * etc.
 *
 * This installs an ISR in start() but then when it has interesting
 * work to do the task which was set via setTaskHandle is notified
 * and is expected to call _callListener (which is a little bit of a
 * misnoymer) as necessary with an
 * edgeIx in order to continue the parsing of the data that has been read.
 *
 * If
 */
class DHTSensor {
public:
    DHTSensor(gpio_num_t gpioNum, DHTSensorListener& listener);

    gpio_num_t gpioNum() { return m_gpioNum; }

    void start();

    /**
     * Sets the task which will be notified when the ISR has detected
     * an edge in the protocol. This is probably the task in probe.cpp.
     *
     * That task will then call _edgeNotify followed by _edgeTimeout. This
     * is a little twisted because of how we got to this point and could
     * certainly be optimized to be a little more sane.
     *
     * @param taskToNotify
     */
    void setTaskHandle(TaskHandle_t taskToNotify) {
        m_TaskToNotify = taskToNotify;
    }

    void maybeRead();

    uint64_t getReadingAt() { return m_readingAt; }
    float getRelativeHumidity() { return m_relativeHumidity; }
    float getTemperature(bool asF = false) {
        if (!asF) {
            return m_tempC;
        } else {
            return (m_tempC * 1.8f) + 32;
        }
    }

    void _isr();
    void _edgeNotify(uint32_t edgeIx);
    void _edgeTimeout();

private:
    float m_relativeHumidity = 0;
    float m_tempC = 0;
    uint64_t m_readingAt;

    enum Expecting {
        ClearLine,
        InitStart,
        InitEnd,
        PresenceLow,
        PresenceHigh,
        BitMarker,
        BitValue,
    };

    TaskHandle_t m_TaskToNotify;

    TimerHandle_t m_hEdgeTimeout;
    uint64_t m_NextTimeoutTarget;

    gpio_num_t m_gpioNum;
    DHTSensorListener& m_listener;

    DHTEdge m_Edges[EDGE_BUFFER_LEN];
    uint16_t m_NextEdgeIx;
    uint16_t m_EdgeReadIx;
    uint32_t m_NextSequence;

    uint32_t m_LastSequence;

    Expecting m_Expect = Expecting::ClearLine;

    // For the data we read
    uint8_t m_raw[RAW_BUFFER_LEN]; // It's really only 5 bytes, but just in case
    uint8_t m_CurrentByte;
    uint8_t m_CurrentBitmask;

    void _handleTimeout();
    void _handleEdge(uint32_t edgeIx);

    void _resetReadPos();
    void _addBit(bool bit);

    void _setTimeout(uint32_t micros);

    bool m_ReadPending;
    void _sendReadStart();

    void _readEnded();
};