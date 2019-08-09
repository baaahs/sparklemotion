set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "brain-ui.cpp"
        "brain-led.cpp"
        "brain-button.cpp"
        "brain-ui-events.cpp"
        )


set(COMPONENT_REQUIRES brain-common)

register_component()
