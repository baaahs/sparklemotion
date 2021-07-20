@file:JsModule("js/app/hooks/useResizeListener.js")
@file:JsNonModule

package baaahs.jsx

import react.RReadableRef

@JsName("useResizeListener")
external fun useResizeListener(element: RReadableRef<*>, onResized: () -> Unit)