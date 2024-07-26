@file:Suppress("INTERFACE_WITH_SUPERCLASS", "EXTERNAL_DELEGATION")

package external.mosaic

external enum class Corner {
    TOP_LEFT /* = 1 */,
    TOP_RIGHT /* = 2 */,
    BOTTOM_LEFT /* = 3 */,
    BOTTOM_RIGHT /* = 4 */
}

external fun <T> isParent(node: MosaicParent<T>): Boolean

external fun <T> isParent(node: T): Boolean

external fun <T> createBalancedTreeFromLeaves(leaves: Array<dynamic /* MosaicParent<T> | T */>, startDirection: String /* 'row' | 'column' */ = definedExternally): dynamic /* MosaicParent<T> | T */

external fun getOtherBranch(branch: String /* 'first' | 'second' */): String /* 'first' | 'second' */

external fun getOtherDirection(direction: String /* 'row' | 'column' */): String /* 'row' | 'column' */

external fun getPathToCorner(tree: MosaicParent<Any>, corner: Corner): MosaicPath

external fun getPathToCorner(tree: Any, corner: Corner): MosaicPath

external fun <T> getLeaves(tree: MosaicParent<T>): Array<T>

external fun <T> getLeaves(tree: T): Array<T>

external fun <T> getNodeAtPath(tree: MosaicParent<T>, path: MosaicPath): dynamic /* MosaicParent<T> | T */

external fun <T> getNodeAtPath(tree: T, path: MosaicPath): dynamic /* MosaicParent<T> | T */

external fun <T> getAndAssertNodeAtPathExists(tree: MosaicParent<T>, path: MosaicPath): dynamic /* MosaicParent<T> | T */

external fun <T> getAndAssertNodeAtPathExists(tree: T, path: MosaicPath): dynamic /* MosaicParent<T> | T */