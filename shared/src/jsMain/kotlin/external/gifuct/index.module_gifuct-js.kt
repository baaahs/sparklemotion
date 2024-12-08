@file:JsModule("gifuct-js")

package external.gifuct

import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8ClampedArray

external interface ApplicationApplication {
    var blockSize: Number
    var blocks: Array<Number>
    var codes: Array<Number>
    var id: String
}

external interface Application {
    var application: ApplicationApplication
}

external interface FrameExtras {
    var userInput: Boolean
    var transparentColorGiven: Boolean
    var future: Number
    var disposal: Number
}

external interface FrameGce {
    var byteSize: Number
    var codes: Array<Number>
    var delay: Number
    var terminator: Number
    var transparentColorIndex: Number
    var extras: FrameExtras
}

external interface ImageData {
    var minCodeSize: Number
    var blocks: Array<Number>
}

external interface ImageDescriptorLct {
    var exists: Boolean
    var future: Number
    var interlaced: Boolean
    var size: Number
    var sort: Boolean
}

external interface ImageDescriptor {
    var top: Number
    var left: Number
    var width: Number
    var height: Number
    var lct: ImageDescriptorLct
}

external interface Image {
    var code: Number
    var data: FrameGce
    var descriptor: ImageDescriptor
}

external interface Frame {
    var gce: FrameGce
    var image: Image
}

external interface ParsedGifHeader {
    var signature: String
    var version: String
}

external interface ParsedGifLsdGct {
    var exists: Boolean
    var resolution: Number
    var size: Int
    var sort: Boolean
}

external interface ParsedGifLsd {
    var backgroundColorIndex: Int
    var gct: ParsedGifLsdGct
    var height: Int
    var width: Int
    var pixelAspectRatio: Double
}

external interface ParsedGif {
    var frames: Array<Frame> // Array<dynamic /* Application | Frame */>
    var gct: Array<dynamic /* JsTuple<Number, Number, Number> */>
    var header: ParsedGifHeader
    var lsd: ParsedGifLsd
}

external interface ParsedFrameDims {
    var width: Int
    var height: Int
    var top: Int
    var left: Int
}

external interface ParsedFrame {
    var dims: ParsedFrameDims
    var colorTable: Array<dynamic /* JsTuple<Number, Number, Number> */>
    var delay: Int
    var disposalType: Int
    var patch: Uint8ClampedArray
    var pixels: Array<Byte>
    var transparentIndex: Int
}

//external typealias ParsedFrameWithoutPatch = Omit<ParsedFrame, String /* "patch" */>

external fun parseGIF(arrayBuffer: ArrayBuffer): ParsedGif

external fun decompressFrames(parsedGif: ParsedGif, buildImagePatches: Boolean): Array<ParsedFrame>

external fun decompressFrame(frame: Frame, gct: Array<Any /* JsTuple<Number, Number, Number> */>, buildImagePatches: Boolean): ParsedFrame