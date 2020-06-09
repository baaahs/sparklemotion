@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")

package external.mosaic

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external fun <T> buildSpecFromUpdate(mosaicUpdate: MosaicUpdate<T>): dynamic /* typealias MosaicUpdateSpec = dynamic */

external fun <T> updateTree(root: MosaicParent<T>, updates: Array<MosaicUpdate<T>>): dynamic /* MosaicParent<T> | T */

external fun <T> updateTree(root: T, updates: Array<MosaicUpdate<T>>): dynamic /* MosaicParent<T> | T */

external fun <T> createRemoveUpdate(root: MosaicParent<T>, path: MosaicPath): MosaicUpdate<T>

external fun <T> createRemoveUpdate(root: T, path: MosaicPath): MosaicUpdate<T>

external fun <T> createDragToUpdates(root: MosaicParent<T>, sourcePath: MosaicPath, destinationPath: MosaicPath, position: String /* 'top' | 'bottom' | 'left' | 'right' */): Array<MosaicUpdate<T>>

external fun <T> createDragToUpdates(root: T, sourcePath: MosaicPath, destinationPath: MosaicPath, position: String /* 'top' | 'bottom' | 'left' | 'right' */): Array<MosaicUpdate<T>>

external fun <T> createHideUpdate(path: MosaicPath): MosaicUpdate<T>

external fun <T> createExpandUpdate(path: MosaicPath, percentage: Number): MosaicUpdate<T>