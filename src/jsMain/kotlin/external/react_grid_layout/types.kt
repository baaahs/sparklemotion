package external.react_grid_layout

import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent

typealias ItemCallback = (
    layout: Layout, oldItem: LayoutItem, newItem: LayoutItem,
    placeholder: LayoutItem, e: MouseEvent, element: HTMLElement
) -> Unit