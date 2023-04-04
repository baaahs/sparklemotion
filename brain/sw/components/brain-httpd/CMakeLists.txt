set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "http_server.cpp"
        "spiffs_handler.cpp"
        "firmware_handler.cpp"
        "config_handler.cpp"
        "time_handler.cpp"
        "words_handler.cpp"
        "color_handler.cpp"
        )

set(COMPONENT_REQUIRES brain-common vfs spiffs esp_http_server app_update)

register_component()
