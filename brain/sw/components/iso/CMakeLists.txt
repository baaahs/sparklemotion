set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "iso-control-state.cpp"
        "iso-palette.cpp"
        "iso-shader-solid.cpp"
        "iso-shader-rainbow.cpp"
        "iso-shader-stack.cpp"
        "iso-shader-list.cpp"
        "iso-shader-manual.cpp"
        )


set(COMPONENT_REQUIRES brain-common led-renderer)

register_component()
