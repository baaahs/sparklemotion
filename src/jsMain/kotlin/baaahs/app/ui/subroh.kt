package baaahs.app.ui

import kotlinext.js.Object
import kotlinx.css.CssBuilder
import kotlinx.css.px
import materialui.styles.mixins.Gutters
import materialui.styles.mixins.Mixins
import materialui.styles.mixins.ReadOnlyGutterDelegate
import kotlin.reflect.KProperty

val Mixins.gutters: Gutters by ReadOnlyGutterDelegate
val Mixins.toolbar: CssBuilder by ReadOnlyToolbarDelegate


object ReadOnlyToolbarDelegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): CssBuilder
            = buildCssBuilder(CssBuilder(), thisRef.asDynamic()[property.name])
}

private fun buildCssBuilder(cssBuilder: CssBuilder, jsObject: dynamic): CssBuilder
        = cssBuilder.apply {
    Object.keys(jsObject as Any).toList().forEach { key: String ->
        when (jsObject[key]) {
            is String -> cssBuilder.declarations[key] = jsObject[key] as String
            is Number -> cssBuilder.declarations[key] = (jsObject[key] as Number).px
            else -> key { buildCssBuilder(this, jsObject[key]) }
        }
    }
}
