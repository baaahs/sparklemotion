package three.js

open external class Uniform<T>(value: T) {
    open var value: T
    open fun clone(): Uniform<T>
}

typealias Uniform__0 = Uniform<Any>