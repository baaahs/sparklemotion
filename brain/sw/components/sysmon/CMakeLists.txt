set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "sysmon.cpp"
        )


set(COMPONENT_REQUIRES brain-common app_update esp_timer)

register_component()
