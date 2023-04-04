set(COMPONENT_ADD_INCLUDEDIRS . "include")

set(COMPONENT_SRCS
        "screen.cpp"
        "screen_driver.cpp"
        "screen_driver_ssd1306.cpp"
        "screen_test.cpp"
        "widget_wrangler.cpp"
        )


set(COMPONENT_REQUIRES brain-common)

register_component()
