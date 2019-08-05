set(COMPONENT_SRCS
        "main.cpp"
        "brain.cpp"
        "httpd/task_httpd.cpp"
        "httpd/spiffs_handler.cpp"
        "httpd/http_server.cpp"
)

set(COMPONENT_ADD_INCLUDEDIRS ".")
set(COMPONENT_REQUIRES brain-common led-renderer spiffs esp_http_server net shaders sysmon brain-ui)

register_component()

# Create a SPIFFS image from the contents of the 'spiffs_image' directory
# that fits the partition named 'storage'. FLASH_IN_PROJECT indicates that
# the generated image should be flashed when the entire project is flashed to
# the target with 'idf.py flash'.
spiffs_create_partition_image(storage ../spiffs FLASH_IN_PROJECT)
