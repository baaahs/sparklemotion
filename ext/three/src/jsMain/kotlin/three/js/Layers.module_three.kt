package three.js

open external class Layers {
    open var mask: Number
    open fun set(layer: Number)
    open fun enable(layer: Number)
    open fun enableAll()
    open fun toggle(layer: Number)
    open fun disable(layer: Number)
    open fun disableAll()
    open fun test(layers: Layers): Boolean
    open fun isEnabled(layer: Number): Boolean
}