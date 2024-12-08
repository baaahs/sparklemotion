package external.mosaic

external object Corner {
    val TOP_LEFT : Corner /* = 1 */
    val TOP_RIGHT : Corner /* = 2 */
    val BOTTOM_LEFT : Corner /* = 3 */
    val BOTTOM_RIGHT : Corner /* = 4 */
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