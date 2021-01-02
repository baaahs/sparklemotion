@file:JsModule("three/examples/jsm/controls/OrbitControls")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three_ext

open external class OrbitControls(theObject: Any, domElement: Any) {
    var minPolarAngle: Double
    var maxPolarAngle: Double
    var target: Any
    var enableKeys: Boolean

    fun update()
}