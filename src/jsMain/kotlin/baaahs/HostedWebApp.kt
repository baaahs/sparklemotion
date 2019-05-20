package baaahs

import org.w3c.dom.HTMLElement

interface HostedWebApp {
    fun onRender(container: HTMLElement);
    fun onResize(container: HTMLElement);
    fun onClose();
}
