package three.js

import js.objects.Record
import web.events.EventTarget
import web.time.DOMHighResTimeStamp

typealias Bone__0 = Bone<Object3DEventMap>

typealias NormalBufferAttributes = Record<String, dynamic /* BufferAttribute | InterleavedBufferAttribute */>
typealias NormalOrGLBufferAttributes = Record<String, dynamic /* BufferAttribute | InterleavedBufferAttribute | GLBufferAttribute */>

typealias EventDispatcher = EventTarget

typealias Loader__1<TData> = Loader<TData, String>
typealias Loader__0 = Loader<Any, String>

typealias RenderTarget__0 = RenderTarget<Texture>

typealias Sprite__0 = Sprite<Object3DEventMap>

typealias Uniform__0 = Uniform<Any>

typealias XRHandJoints = Record<String /* "wrist" | "thumb-metacarpal" | "thumb-phalanx-proximal" | "thumb-phalanx-distal" | "thumb-tip" | "index-finger-metacarpal" | "index-finger-phalanx-proximal" | "index-finger-phalanx-intermediate" | "index-finger-phalanx-distal" | "index-finger-tip" | "middle-finger-metacarpal" | "middle-finger-phalanx-proximal" | "middle-finger-phalanx-intermediate" | "middle-finger-phalanx-distal" | "middle-finger-tip" | "ring-finger-metacarpal" | "ring-finger-phalanx-proximal" | "ring-finger-phalanx-intermediate" | "ring-finger-phalanx-distal" | "ring-finger-tip" | "pinky-finger-metacarpal" | "pinky-finger-phalanx-proximal" | "pinky-finger-phalanx-intermediate" | "pinky-finger-phalanx-distal" | "pinky-finger-tip" */, XRJointSpace>

typealias XRFrameRequestCallback = (time: DOMHighResTimeStamp, frame: XRFrame) -> Unit
typealias XRAnchorSet = Set<XRAnchor>
typealias XRPlaneSet = Set<XRPlane>
typealias XRMeshSet = Set<XRMesh>

typealias GLenum = Number
