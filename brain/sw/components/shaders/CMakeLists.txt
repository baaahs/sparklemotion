set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "shade-tree.cpp"
        "shader.cpp"
        "default-shader.cpp"
        "compositor-shader.cpp"
        "pixel-shader.cpp"
        "solid-shader.cpp"
        "sine-wave-shader.cpp"
        "sparkle-shader.cpp"
        "rainbow-shader.cpp"
        )


set(COMPONENT_REQUIRES net led-renderer)

register_component()
