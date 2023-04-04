set(COMPONENT_SRCS 
        "net_transport.cpp"
        "eth_interface.cpp"
        "wifi_sta_interface.cpp"
        "wifi_ap_interface.cpp"
        "ip_port.cpp"
        "msg.cpp"
        "msg_handler.cpp"
        "msg_slinger.cpp"
        "ota_fetcher.cpp"
        )

set(COMPONENT_ADD_INCLUDEDIRS "include")

set(COMPONENT_REQUIRES brain-common nvs_flash led-renderer app_update esp_eth esp_wifi esp_netif esp_http_client)

register_component()
