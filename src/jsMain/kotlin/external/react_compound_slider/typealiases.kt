package external.react_compound_slider

typealias CustomMode = (curr: Array<HandleItem>, next: Array<HandleItem>, step: Number, reversed: Boolean, getValue: (x: Number) -> Number) -> Array<HandleItem>
typealias GetEventData = (e: dynamic /* SyntheticEvent__0 | Event */) -> EventData
typealias GetHandleProps = (id: String/*, props: OtherProps*/) -> Any
typealias GetRailProps = (/*OtherProps?*/) -> Any
typealias GetTrackProps = (/*props: OtherProps*/) -> Any
typealias Interpolator = (x: Number) -> Number
