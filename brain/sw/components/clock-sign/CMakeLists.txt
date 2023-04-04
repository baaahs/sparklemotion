set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "cs-shader.cpp"
        "cs-font.cpp"
        "cs-text.cpp"
        "cs-nye-clock.cpp"
        )


set(COMPONENT_REQUIRES brain-common led-renderer iso)

register_component()
