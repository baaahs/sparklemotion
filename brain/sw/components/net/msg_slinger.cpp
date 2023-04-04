//
// Created by Tom Seago on 2019-06-02.
//

#include <sysmon.h>
#include "msg_slinger.h"

#include "net_priv.h"

#include "lwip/sockets.h"
#include "lwip/ip_addr.h"

void msg_slinger_input_pump(void* pParam) {
    ((MsgSlinger *) pParam)->_inputPump();
}

ESP_EVENT_DEFINE_BASE(BRAIN_NETOUT);
static const int32_t NETOUT_READY = 1;

// Glue is good
void got_ip_address(void *arg, esp_event_base_t base, int32_t id, void *data) {
    ((MsgSlinger *) arg)->_gotIp(id, data);
}

// Glue function FTW
void handle_net_out(void *arg, esp_event_base_t base, int32_t id, void *data) {
    ESP_LOGD(TAG, "handle_net_out arg=%p data=%p", arg, data);
    if (data) {
        Msg** ppMsg = (Msg**)data;
        ESP_LOGD(TAG, "  *ppMsg=%p  ", *ppMsg);

        ((MsgSlinger *) arg)->_handleNetOut(*ppMsg);
    } else {
        ESP_LOGE(TAG, "Got a data out event with invalid data parameter");
    }
}

void
MsgSlinger::start(uint16_t port, TaskDef input, TaskDef output) {
    m_port = port;

    ////////////////
    bindSocket();

    _gotIp(0, nullptr);
    // Whenever our IP changes we need to re-bind (I think?)
    ESP_ERROR_CHECK(esp_event_handler_register(IP_EVENT, IP_EVENT_ETH_GOT_IP, got_ip_address, this));
    ESP_ERROR_CHECK(esp_event_handler_register(IP_EVENT, IP_EVENT_STA_GOT_IP, got_ip_address, this));


    ////////////////
    ESP_LOGD(TAG, "Creating input pump task");
    auto result = input.createTask(msg_slinger_input_pump, this, nullptr);
    if (result != pdPASS) {
        ESP_LOGE(TAG, "Failure starting input pump task %d", result);
    }

    ////////////////
    m_argsOutputLoop.queue_size = 10;
    m_argsOutputLoop.task_name = output.name;
    m_argsOutputLoop.task_priority = output.priority;
    m_argsOutputLoop.task_stack_size = output.stack;
    // Not setting a core id at this time...

    m_hOutputLoop = nullptr;
    esp_err_t err = esp_event_loop_create(&m_argsOutputLoop, &m_hOutputLoop);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "Failed starting output event loop");
        return;
    }

    err = esp_event_handler_register_with(m_hOutputLoop, BRAIN_NETOUT, NETOUT_READY, handle_net_out, this);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "Failed to register handler");
        return;
    }
}

void
MsgSlinger::_gotIp(int32_t id, void* data) {

    ESP_LOGI(TAG, "gotIP(%ld, %p)", id, data);

    esp_netif_ip_info_t ethInfo = {};
    esp_netif_ip_info_t  staInfo = {};

    if (data) {
        // It's a valid system event
        auto event = (ip_event_got_ip_t *) data;

        if (id == IP_EVENT_ETH_GOT_IP) {
            ethInfo = event->ip_info;
        }
        if (id == IP_EVENT_STA_GOT_IP) {
            staInfo = event->ip_info;
        }
    } else {
        ESP_LOGI(TAG, "Startup - getting info for any interfaces already up");
        // TODO: Refactor this to new APIs - maybe moves to interface objects
//        if (tcpip_adapter_is_netif_up(TCPIP_ADAPTER_IF_ETH)) {
//            tcpip_adapter_get_ip_info(TCPIP_ADAPTER_IF_ETH, &ethInfo);
//        }
//
//        if (tcpip_adapter_is_netif_up(TCPIP_ADAPTER_IF_STA)) {
//            tcpip_adapter_get_ip_info(TCPIP_ADAPTER_IF_STA, &staInfo);
//        }
    }

    ESP_LOGW(TAG, "Ethernet " IPSTR "  " IPSTR "  " IPSTR,
            IP2STR(&ethInfo.ip), IP2STR(&ethInfo.netmask), IP2STR(&ethInfo.gw) );

    ESP_LOGW(TAG, "Wifi     " IPSTR "  " IPSTR "  " IPSTR,
             IP2STR(&staInfo.ip), IP2STR(&staInfo.netmask), IP2STR(&staInfo.gw) );

    if (ethInfo.netmask.addr) {
        // An updated address on the ethernet interface
        m_broadcastEth.addr = ~ethInfo.netmask.addr | ethInfo.ip.addr;
        ESP_LOGI(TAG, "Updated broadcast address for ethernet to " IPSTR, IP2STR(&m_broadcastEth));
    }

    if (staInfo.netmask.addr) {
        // An updated address on the STA interface
        m_broadcastSta.addr = ~staInfo.netmask.addr | staInfo.ip.addr;
        ESP_LOGI(TAG, "Updated broadcast address for sta to " IPSTR, IP2STR(&m_broadcastSta));
    }
}

void
MsgSlinger::bindSocket() {
    ESP_LOGW(TAG, "Binding socket. m_sock=%d", m_sock);
    if (m_sock) {
        close(m_sock);
        m_sock = 0;
    }

    ESP_LOGI(TAG, "Creating socket");
    m_sock = socket(AF_INET, SOCK_DGRAM, IPPROTO_IP);
    if (m_sock < 0) {
        ESP_LOGE(TAG, "Unable to create socket: errno=%d", errno);
        return;
    }

    struct sockaddr_in addr = {};
    addr.sin_family = AF_INET;
    addr.sin_port = htons(m_port);
//    addr.sin_addr.s_addr = htonl(INADDR_BROADCAST);
    addr.sin_addr.s_addr = htonl(INADDR_ANY);

    ESP_LOGD(TAG, "Socket created");

    int err = bind(m_sock, (struct sockaddr *)&addr, sizeof(addr));
    if (err < 0) {
        ESP_LOGE(TAG, "Unable to create socket: errno=%d", errno);
        return;
    }

    char addr_str[128];
    inet_ntoa_r(addr.sin_addr, addr_str, sizeof(addr_str) - 1);
    ESP_LOGD(TAG, "Socket %d bound to %s:%d", m_sock, addr_str, m_port);
}

void
MsgSlinger::_inputPump() {
    ESP_LOGI(TAG, "_inputPump starting");

    while(!m_timeToDie) {
        ESP_LOGD(TAG, "Waiting for data in _inputPump m_sock=%d", m_sock);

        // Get a new input buffer message and make sure it has a reasonable
        // capacity.
        Msg* pMsg = Msg::obtain();
        pMsg->prepCapacity(Msg::FRAGMENT_MAX);

        socklen_t socklen = pMsg->dest.size();

        // Because of the IN/OUT nature of the address field, we must make
        // sure that we always reset it such that we are receiving packets
        // from all senders
        pMsg->dest.addr_in()->sin_addr.s_addr = htonl(INADDR_ANY);

        int len = recvfrom(m_sock, pMsg->buffer(), pMsg->capacity() - 1, 0,
                           pMsg->dest.addr(), &socklen);

        if (!m_timeToDie) {
            if (len < 0) {
                ESP_LOGE(TAG, "recvfrom failed: %d %s", errno, strerror(errno));
                // Delay at least a little bit so the rest of the system isn't horribly
                // gunked up on an endless error scenario. Presumably the socket
                // will get re-bound at some point because an interface comes back up.
                vTaskDelay(pdMS_TO_TICKS(100));
                break;
            } else {
                pMsg->setUsed(len);

                // inet_ntoa_r(pMsg->dest.addr_in()->sin_addr.s_addr, addr_str, sizeof(addr_str) - 1);

                //ESP_LOGI(TAG, "Received %d bytes from %s:", len, addr_str);
                ESP_LOGI(TAG, "Received %d bytes from %s:", len, pMsg->dest.toString());
                gSysMon.increment(COUNTER_UDP_RECV);

                handleNetIn(pMsg);
            }
        }
        pMsg->release();
    }

    // Delete ourself!
    vTaskDelete(nullptr);
}

void
MsgSlinger::handleNetIn(Msg *pMsg) {
    if (!pMsg) return;

    // This message contains a header, so let's parse it
    auto header = pMsg->readHeader();
    ESP_LOGD(TAG, "Read a msg header msgId=%d", header.id);

    ESP_LOGD(TAG, "id=( %d ).size%ldd  frameOffset=%ld frameSize=%d",
            header.id, header.msgSize, header.frameOffset, header.frameSize);

    if (header.frameOffset == 0) {
        // It is a new message
        if (header.frameSize == header.msgSize) {
            // It is a single message so we can dispatch it as is
            ESP_LOGD(TAG, "Dispatching single fragment message");
            gSysMon.increment(COUNTER_MSG_SINGLE_OK);
            dispatch(pMsg);
            ESP_LOGD(TAG, "Dispatch complete for single fragment message");
        } else {
            // It is merely the first fragment, so prepare ourselves
            // for that.

            // Allocate space for the whole message
            if (!pMsg->prepCapacity(header.msgSize + Msg::HEADER_SIZE)) {
                ESP_LOGE(TAG, "Unable to prep a fragged message of size %ld, dropping it", header.msgSize);
                gSysMon.increment(COUNTER_MSG_LOST);
                return;
            }
            ESP_LOGD(TAG, "Got first segment of a fragmented message");

            // Cool, we hold onto this message then (making sure not to leak
            // anything that was previously hanging around)
            if (m_pFraggedMsg) {
                ESP_LOGW(TAG, "Dropping old message ( %d ) and replacing with new one ( %d)",
                         m_nFraggedMsgId, header.id);
                gSysMon.increment(COUNTER_MSG_LOST);
                m_pFraggedMsg->release();
            }
            m_pFraggedMsg = pMsg;
            m_pFraggedMsg->addRef();

            m_nFraggedMsgId = header.id;
        }
    } else {
        // It is a continuing fragment of an old message
        if (!m_pFraggedMsg) {
            ESP_LOGW(TAG, "Got continuation fragment for ( %d ) but am waiting for ( %d )", header.id, m_nFraggedMsgId);
            gSysMon.increment(COUNTER_MSG_BAD_ID);
            return;
        }

        if (header.id != m_nFraggedMsgId) {
            ESP_LOGW(TAG, "Got continuation of %d but have %d, dropping both", header.id, m_nFraggedMsgId);
            gSysMon.increment(COUNTER_MSG_BAD_ID);
            m_pFraggedMsg->release();
            m_pFraggedMsg = nullptr;
            return;
        }

        // Add this fragment, which is presumably the next fragment, and
        // if it happens to be the last fragment, then we dispatch
        ESP_LOGD(TAG, "Adding a fragment to an existing message");
        if (m_pFraggedMsg->addFragment(pMsg)) {
            // Oh hey, we should dispatch it!
            ESP_LOGD(TAG, "Dispatching fragmented message %d", m_pFraggedMsg->used());
            gSysMon.increment(COUNTER_MSG_FRAG_OK);
            dispatch(m_pFraggedMsg);
            m_pFraggedMsg->release();
            m_pFraggedMsg = nullptr;
        }
    }
}

void
MsgSlinger::dispatch(Msg *pMsg) {
    if (m_handler) {
        m_handler->handleMsg(pMsg);
    } else {
        ESP_LOGW(TAG, "Got input but no handler registered");
    }
}


void
MsgSlinger::_handleNetOut(Msg *pMsg) {
    if (!pMsg) return;

    if (m_timeToDie) return;

    ESP_LOGI(TAG, "Sending this message msgId=%d", (((int) pMsg->buffer()[0]) & 0xff) * 256 + ((int) pMsg->buffer()[1] & 0xff));
    pMsg->log();

    if (m_sock < 0) {
        ESP_LOGE(TAG, "Can not send this message because m_sock(%d) is < 0", m_sock);
        pMsg->release();
        return;
    }

    ESP_LOGI(TAG, "Message len=%d to %s", pMsg->used(), pMsg->dest.toString());
//    if (pMsg->dest.addr_in()->sin_addr.s_addr == INADDR_BROADCAST) {
//        // Send it on each interface using that interface's appropriate address
//        bool sentOnEth = false;
//        if (m_broadcastEth.addr) {
//            pMsg->dest.addr_in()->sin_addr.s_addr = m_broadcastEth.addr;
//            ESP_LOGD(TAG, "Eth sending to %s", pMsg->dest.toString());
//            auto err = sendto(m_sock, pMsg->buffer(), pMsg->used(), 0,
//                    pMsg->dest.addr(), pMsg->dest.size());
//            if (err < 0) {
//                ESP_LOGE(TAG, "Eth error occurred sending to %s : m_sock=%d %d %s",
//                        pMsg->dest.toString(), m_sock, errno, strerror(errno));
//            } else {
//                ESP_LOGD(TAG, "Eth broadcast success to %s", pMsg->dest.toString());
//                sentOnEth = true;
//            }
//        }
//
//        if (m_broadcastSta.addr && (m_broadcastSta.addr != m_broadcastEth.addr || !sentOnEth)) {
//            // Sta has a different address so we need to send it there
////            ESP_LOGD(TAG, "Wifi sending to " IPSTR, IP2STR(&m_broadcastSta));
//            pMsg->dest.addr_in()->sin_addr.s_addr = m_broadcastSta.addr;
////            pMsg->dest.addr_in()->sin_addr.s_addr &= 0x0affffff;
//            ESP_LOGD(TAG, "Wifi sending to %s", pMsg->dest.toString());
//            auto err = sendto(m_sock, pMsg->buffer(), pMsg->used(), 0,
//                              pMsg->dest.addr(), pMsg->dest.size());
//            if (err < 0) {
//                ESP_LOGE(TAG, "Wifi error occurred sending to %s : m_sock=%d %d %s",
//                         pMsg->dest.toString(), m_sock, errno, strerror(errno));
//            } else {
//                ESP_LOGD(TAG, "Wifi broadcast success to %s", pMsg->dest.toString());
//            }
//        }
//    } else {
        // It's not a broadcast so we don't have to do that funny business
        // of figuring out which address to send it to.
        ESP_LOGD(TAG, "Sending to %s", pMsg->dest.toString() );
        auto err = sendto(m_sock, pMsg->buffer(), pMsg->used(), 0,
                          pMsg->dest.addr(), pMsg->dest.size());
        if (err < 0) {
            ESP_LOGE(TAG, "Error occurred sending to %s : m_sock=%d %d %s",
                     pMsg->dest.toString(), m_sock, errno, strerror(errno));
        } else {
            gSysMon.increment(COUNTER_MSG_SENT);
            ESP_LOGD(TAG, "Message sent to %s", pMsg->dest.toString() );
        }
//    }

    pMsg->release();
}

esp_err_t
MsgSlinger::sendMsg(Msg *pMsg) {
    if (m_timeToDie) return ESP_FAIL;

    if (!pMsg) {
        ESP_LOGW(TAG, "sendMsg this=%p pMsg=%p len=%d dest=%s", this, pMsg, pMsg->used(), pMsg->dest.toString());
        return ESP_FAIL;
    }

    // We just fire an event so that the output pump will take care
    // of it later and the sending task doesn't block

    pMsg->addRef(); // Will be released on the other side of the event.

    ESP_LOGI(TAG, "sendMsg this=%p pMsg=%p len=%d dest=%s", this, pMsg, pMsg->used(), pMsg->dest.toString());

    esp_event_post_to(m_hOutputLoop, BRAIN_NETOUT, NETOUT_READY, &pMsg, sizeof(pMsg), pdMS_TO_TICKS(500));

    return ESP_OK;
}
