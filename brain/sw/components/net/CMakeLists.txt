set(COMPONENT_SRCS 
        "task_net.cpp"
        "ethernet.cpp"
        "wifi.cpp"
        "ip_port.cpp"
        "msg.cpp"
        "msg_handler.cpp"
        "msg_slinger.cpp"
        )
set(COMPONENT_ADD_INCLUDEDIRS "include")

set(COMPONENT_REQUIRES nvs_flash led-renderer)

register_component()
