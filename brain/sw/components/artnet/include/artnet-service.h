
#include <brain_common.h>

#include "esp_event.h"

#include "artmsg.h"

class ArtnetService {
public:
    /**
     * Start the tasks needed to handle artnet messages
     * @param input
     * @param output
     */
    void start(TaskDef input, TaskDef output);

    /**
     * Will cause all tasks to exit. They can't be restarted.
     */
    void stop() { m_timeToDie = true; }

    /**
     * Send an artnet message
     *
     * @param pMsg
     * @return
     */
    esp_err_t sendMsg(ArtMsg* pMsg);

    // These are private things that have to be public so the
    // C glue code can access them.
    void _inputPump();
    void _handleNetOut(ArtMsg* pMsg);

private:
    bool m_timeToDie = false;

    // Default port by spec. As a var because I guess someone
    // might want to change it.
    uint16_t m_port = 0x1936;
    int m_sock = -1;

    esp_event_loop_args_t m_argsOutputLoop;
    esp_event_loop_handle_t m_hOutputLoop;

    void bindSocket();

    void handleNetIn(Msg *pMsg);
};