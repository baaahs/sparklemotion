
idf_component_register(SRCS
                    "main.cpp" "wigsnatch.cpp" "menu.cpp"
                    "shader-growing.cpp"
                    "shader-legs.cpp"
                    "wig-shader.cpp"
                    INCLUDE_DIRS ".")

# This is needed or else the ESP_LOGX functions generate errors during compile
# because of printf format checking. Grr. See https://github.com/espressif/esp-idf/issues/10379
target_compile_options(${COMPONENT_LIB} PRIVATE "-Wno-format")
