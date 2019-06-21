set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "brain-ui.cpp"
        "brain-led.cpp"
        )


# set(COMPONENT_REQUIRES net led-renderer)

register_component()
