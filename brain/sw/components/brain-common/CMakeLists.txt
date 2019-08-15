set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "common.cpp"
        "tasks.cpp"
        "config.cpp"
        )

set(COMPONENT_REQUIRES spiffs jsmn driver app_update)

register_component()
