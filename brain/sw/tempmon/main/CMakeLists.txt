set(COMPONENT_SRCS
        "main.cpp"
        "tempmon.cpp"
        "probe.cpp"
# OneWire isn't working right now. Nor do we need DallasTemp code
#        "OneWire.cpp"
#        "DallasTemp.cpp"
        "DHTSensor.cpp"
)

set(COMPONENT_ADD_INCLUDEDIRS ".")
set(COMPONENT_REQUIRES brain-common
        spiffs brain-httpd net sysmon esp_https_ota screen
        )

register_component()

# Create a SPIFFS image from the contents of the 'spiffs_image' directory
# that fits the partition named 'storage'. FLASH_IN_PROJECT indicates that
# the generated image should be flashed when the entire project is flashed to
# the target with 'idf.py flash'.
spiffs_create_partition_image(storage ../spiffs FLASH_IN_PROJECT)
