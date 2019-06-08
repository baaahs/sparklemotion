set(COMPONENT_SRCS
        "main.cpp"
        "brain.cpp"
        "net/task_net.cpp"
        "net/ethernet.cpp"
        "net/wifi.cpp"
        "net/ip_port.cpp"
        "net/msg.cpp"
        "net/msg_handler.cpp"
        "net/msg_slinger.cpp"
        "httpd/task_httpd.cpp"
        "httpd/spiffs_handler.cpp"
        "httpd/http_server.cpp"
)

set(COMPONENT_ADD_INCLUDEDIRS ".")
set(COMPONENT_REQUIRES led-renderer spiffs esp_http_server)

register_component()

# Create a SPIFFS image from the contents of the 'spiffs_image' directory
# that fits the partition named 'storage'. FLASH_IN_PROJECT indicates that
# the generated image should be flashed when the entire project is flashed to
# the target with 'idf.py flash'.
spiffs_create_partition_image(storage ../spiffs FLASH_IN_PROJECT)
