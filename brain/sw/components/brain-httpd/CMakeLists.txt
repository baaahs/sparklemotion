set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "http_server.cpp"
        "spiffs_handler.cpp"
        "firmware_handler.cpp"
        "config_handler.cpp"
        )

set(COMPONENT_REQUIRES brain-common spiffs esp_http_server app_update)

register_component()
