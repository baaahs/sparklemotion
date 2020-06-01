@file:JsModule("js/app/hooks/useResizeListener.js")
@file:JsNonModule

package baaahs.jsx

import react.RMutableRef

@JsName("useResizeListener")
external fun useResizeListener(element: RMutableRef<*>, onResized: () -> Unit)