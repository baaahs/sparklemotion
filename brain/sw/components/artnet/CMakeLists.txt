set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "artnet-service.cpp"
        "artmsg.cpp"
        )


set(COMPONENT_REQUIRES brain-common sysmon net)

register_component()
