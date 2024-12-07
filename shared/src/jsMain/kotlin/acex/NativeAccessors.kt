@file:Suppress("NOTHING_TO_INLINE", "USELESS_CAST")

package acex

import ReactAce.Ace.IAceOptions
import ReactAce.Ace.IEditorProps

inline operator fun IEditorProps.get(index: String): Any? =
    asDynamic()[index] as? Any

inline operator fun IEditorProps.set(index: String, value: Any) {
    asDynamic()[index] = value
}

inline operator fun IAceOptions.get(index: String): Any? =
    asDynamic()[index] as? Any

inline operator fun IAceOptions.set(index: String, value: Any) {
    asDynamic()[index] = value
}

inline operator fun `T$4`.get(key: String): String? =
    asDynamic()[key] as? String
inline operator fun `T$4`.set(key: String, value: String) {
    asDynamic()[key] = value
}

inline operator fun `T$9`.get(id: Number): MarkerLike? =
    asDynamic()[id] as? MarkerLike
inline operator fun `T$9`.set(id: Number, value: MarkerLike) {
    asDynamic()[id] = value
}

inline operator fun `T$12`.get(s: String): Function<*>? =
    asDynamic()[s] as? Function<*>
inline operator fun `T$12`.set(s: String, value: Function<*>) {
    asDynamic()[s] = value
}