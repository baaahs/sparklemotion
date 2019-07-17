set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "brain-ui.cpp"
        "brain-led.cpp"
        "brain-button.cpp"
        )


# set(COMPONENT_REQUIRES net led-renderer)

register_component()
