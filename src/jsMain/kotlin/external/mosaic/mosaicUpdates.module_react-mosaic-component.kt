@file:Suppress("INTERFACE_WITH_SUPERCLASS", "EXTERNAL_DELEGATION")

package external.mosaic

external fun <T> buildSpecFromUpdate(mosaicUpdate: MosaicUpdate<T>): dynamic /* typealias MosaicUpdateSpec = dynamic */

external fun <T> updateTree(root: MosaicParent<T>, updates: Array<MosaicUpdate<T>>): dynamic /* MosaicParent<T> | T */

external fun <T> updateTree(root: T, updates: Array<MosaicUpdate<T>>): dynamic /* MosaicParent<T> | T */

external fun <T> createRemoveUpdate(root: MosaicParent<T>, path: MosaicPath): MosaicUpdate<T>

external fun <T> createRemoveUpdate(root: T, path: MosaicPath): MosaicUpdate<T>

external fun <T> createDragToUpdates(root: MosaicParent<T>, sourcePath: MosaicPath, destinationPath: MosaicPath, position: String /* 'top' | 'bottom' | 'left' | 'right' */): Array<MosaicUpdate<T>>

external fun <T> createDragToUpdates(root: T, sourcePath: MosaicPath, destinationPath: MosaicPath, position: String /* 'top' | 'bottom' | 'left' | 'right' */): Array<MosaicUpdate<T>>

external fun <T> createHideUpdate(path: MosaicPath): MosaicUpdate<T>

external fun <T> createExpandUpdate(path: MosaicPath, percentage: Number): MosaicUpdate<T>