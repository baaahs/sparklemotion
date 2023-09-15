@file:JsModule("@mui/base/Portal")

package external.mui

import mui.base.PortalProps

// TODO: kill when https://github.com/JetBrains/kotlin-wrappers/pull/2116 is addressed.

/**
 * Portals provide a first-class way to render children into a DOM node
 * that exists outside the DOM hierarchy of the parent component.
 *
 * Demos:
 *
 * - [Portal](https://mui.com/base-ui/react-portal/)
 *
 * API:
 *
 * - [Portal API](https://mui.com/base-ui/react-portal/components-api/#portal)
 */
@JsName("Portal")
external val Portal: react.FC<PortalProps>
