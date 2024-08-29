set(COMPONENT_SRCS
        "main.cpp"
        "wearable.cpp"
        "menu.cpp"
        "tb-horns.cpp"
)

set(COMPONENT_ADD_INCLUDEDIRS ".")
set(COMPONENT_REQUIRES brain-common led-renderer
        spiffs brain-httpd net shaders sysmon brain-ui esp_https_ota
        iso clock-sign
        )

register_component()

# Create a SPIFFS image from the contents of the 'spiffs_image' directory
# that fits the partition named 'storage'. FLASH_IN_PROJECT indicates that
# the generated image should be flashed when the entire project is flashed to
# the target with 'idf.py flash'.
spiffs_create_partition_image(storage ../spiffs FLASH_IN_PROJECT)
