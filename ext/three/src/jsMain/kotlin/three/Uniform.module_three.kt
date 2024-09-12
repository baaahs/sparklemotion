@file:JsModule("three")
@file:JsNonModule
package three

open external class Uniform<T>(value: T) {
    open var value: T
    open fun clone(): Uniform<T>
}