(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'threejs-wrapper'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'threejs-wrapper'.");
    }
    root['threejs-wrapper'] = factory(typeof this['threejs-wrapper'] === 'undefined' ? {} : this['threejs-wrapper'], kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var Kind_OBJECT = Kotlin.Kind.OBJECT;
  function GUIParams(name, auto, load, parent, closed, closeOnTop, autoPlace, width) {
    if (name === void 0)
      name = undefined;
    if (auto === void 0)
      auto = undefined;
    if (load === void 0)
      load = undefined;
    if (parent === void 0)
      parent = undefined;
    if (closed === void 0)
      closed = undefined;
    if (closeOnTop === void 0)
      closeOnTop = undefined;
    if (autoPlace === void 0)
      autoPlace = undefined;
    if (width === void 0)
      width = undefined;
    this.name = name;
    this.auto = auto;
    this.load = load;
    this.parent = parent;
    this.closed = closed;
    this.closeOnTop = closeOnTop;
    this.autoPlace = autoPlace;
    this.width = width;
  }
  GUIParams.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'GUIParams',
    interfaces: []
  };
  GUIParams.prototype.component1 = function () {
    return this.name;
  };
  GUIParams.prototype.component2 = function () {
    return this.auto;
  };
  GUIParams.prototype.component3 = function () {
    return this.load;
  };
  GUIParams.prototype.component4 = function () {
    return this.parent;
  };
  GUIParams.prototype.component5 = function () {
    return this.closed;
  };
  GUIParams.prototype.component6 = function () {
    return this.closeOnTop;
  };
  GUIParams.prototype.component7 = function () {
    return this.autoPlace;
  };
  GUIParams.prototype.component8 = function () {
    return this.width;
  };
  GUIParams.prototype.copy_utfw2b$ = function (name, auto, load, parent, closed, closeOnTop, autoPlace, width) {
    return new GUIParams(name === void 0 ? this.name : name, auto === void 0 ? this.auto : auto, load === void 0 ? this.load : load, parent === void 0 ? this.parent : parent, closed === void 0 ? this.closed : closed, closeOnTop === void 0 ? this.closeOnTop : closeOnTop, autoPlace === void 0 ? this.autoPlace : autoPlace, width === void 0 ? this.width : width);
  };
  GUIParams.prototype.toString = function () {
    return 'GUIParams(name=' + Kotlin.toString(this.name) + (', auto=' + Kotlin.toString(this.auto)) + (', load=' + Kotlin.toString(this.load)) + (', parent=' + Kotlin.toString(this.parent)) + (', closed=' + Kotlin.toString(this.closed)) + (', closeOnTop=' + Kotlin.toString(this.closeOnTop)) + (', autoPlace=' + Kotlin.toString(this.autoPlace)) + (', width=' + Kotlin.toString(this.width)) + ')';
  };
  GUIParams.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.name) | 0;
    result = result * 31 + Kotlin.hashCode(this.auto) | 0;
    result = result * 31 + Kotlin.hashCode(this.load) | 0;
    result = result * 31 + Kotlin.hashCode(this.parent) | 0;
    result = result * 31 + Kotlin.hashCode(this.closed) | 0;
    result = result * 31 + Kotlin.hashCode(this.closeOnTop) | 0;
    result = result * 31 + Kotlin.hashCode(this.autoPlace) | 0;
    result = result * 31 + Kotlin.hashCode(this.width) | 0;
    return result;
  };
  GUIParams.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.name, other.name) && Kotlin.equals(this.auto, other.auto) && Kotlin.equals(this.load, other.load) && Kotlin.equals(this.parent, other.parent) && Kotlin.equals(this.closed, other.closed) && Kotlin.equals(this.closeOnTop, other.closeOnTop) && Kotlin.equals(this.autoPlace, other.autoPlace) && Kotlin.equals(this.width, other.width)))));
  };
  function WaterOptions(textureWidth, textureHeight, clipBias, alpha, time, waterNormals, sunDirection, sunColor, waterColor, eye, distortionScale, side, fog) {
    if (textureWidth === void 0)
      textureWidth = undefined;
    if (textureHeight === void 0)
      textureHeight = undefined;
    if (clipBias === void 0)
      clipBias = undefined;
    if (alpha === void 0)
      alpha = undefined;
    if (time === void 0)
      time = undefined;
    if (waterNormals === void 0)
      waterNormals = undefined;
    if (sunDirection === void 0)
      sunDirection = undefined;
    if (sunColor === void 0)
      sunColor = undefined;
    if (waterColor === void 0)
      waterColor = undefined;
    if (eye === void 0)
      eye = undefined;
    if (distortionScale === void 0)
      distortionScale = undefined;
    if (side === void 0)
      side = undefined;
    if (fog === void 0)
      fog = undefined;
    this.textureWidth = textureWidth;
    this.textureHeight = textureHeight;
    this.clipBias = clipBias;
    this.alpha = alpha;
    this.time = time;
    this.waterNormals = waterNormals;
    this.sunDirection = sunDirection;
    this.sunColor = sunColor;
    this.waterColor = waterColor;
    this.eye = eye;
    this.distortionScale = distortionScale;
    this.side = side;
    this.fog = fog;
  }
  WaterOptions.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'WaterOptions',
    interfaces: []
  };
  WaterOptions.prototype.component1 = function () {
    return this.textureWidth;
  };
  WaterOptions.prototype.component2 = function () {
    return this.textureHeight;
  };
  WaterOptions.prototype.component3 = function () {
    return this.clipBias;
  };
  WaterOptions.prototype.component4 = function () {
    return this.alpha;
  };
  WaterOptions.prototype.component5 = function () {
    return this.time;
  };
  WaterOptions.prototype.component6 = function () {
    return this.waterNormals;
  };
  WaterOptions.prototype.component7 = function () {
    return this.sunDirection;
  };
  WaterOptions.prototype.component8 = function () {
    return this.sunColor;
  };
  WaterOptions.prototype.component9 = function () {
    return this.waterColor;
  };
  WaterOptions.prototype.component10 = function () {
    return this.eye;
  };
  WaterOptions.prototype.component11 = function () {
    return this.distortionScale;
  };
  WaterOptions.prototype.component12 = function () {
    return this.side;
  };
  WaterOptions.prototype.component13 = function () {
    return this.fog;
  };
  WaterOptions.prototype.copy_1qwfw1$ = function (textureWidth, textureHeight, clipBias, alpha, time, waterNormals, sunDirection, sunColor, waterColor, eye, distortionScale, side, fog) {
    return new WaterOptions(textureWidth === void 0 ? this.textureWidth : textureWidth, textureHeight === void 0 ? this.textureHeight : textureHeight, clipBias === void 0 ? this.clipBias : clipBias, alpha === void 0 ? this.alpha : alpha, time === void 0 ? this.time : time, waterNormals === void 0 ? this.waterNormals : waterNormals, sunDirection === void 0 ? this.sunDirection : sunDirection, sunColor === void 0 ? this.sunColor : sunColor, waterColor === void 0 ? this.waterColor : waterColor, eye === void 0 ? this.eye : eye, distortionScale === void 0 ? this.distortionScale : distortionScale, side === void 0 ? this.side : side, fog === void 0 ? this.fog : fog);
  };
  WaterOptions.prototype.toString = function () {
    return 'WaterOptions(textureWidth=' + Kotlin.toString(this.textureWidth) + (', textureHeight=' + Kotlin.toString(this.textureHeight)) + (', clipBias=' + Kotlin.toString(this.clipBias)) + (', alpha=' + Kotlin.toString(this.alpha)) + (', time=' + Kotlin.toString(this.time)) + (', waterNormals=' + Kotlin.toString(this.waterNormals)) + (', sunDirection=' + Kotlin.toString(this.sunDirection)) + (', sunColor=' + Kotlin.toString(this.sunColor)) + (', waterColor=' + Kotlin.toString(this.waterColor)) + (', eye=' + Kotlin.toString(this.eye)) + (', distortionScale=' + Kotlin.toString(this.distortionScale)) + (', side=' + Kotlin.toString(this.side)) + (', fog=' + Kotlin.toString(this.fog)) + ')';
  };
  WaterOptions.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.textureWidth) | 0;
    result = result * 31 + Kotlin.hashCode(this.textureHeight) | 0;
    result = result * 31 + Kotlin.hashCode(this.clipBias) | 0;
    result = result * 31 + Kotlin.hashCode(this.alpha) | 0;
    result = result * 31 + Kotlin.hashCode(this.time) | 0;
    result = result * 31 + Kotlin.hashCode(this.waterNormals) | 0;
    result = result * 31 + Kotlin.hashCode(this.sunDirection) | 0;
    result = result * 31 + Kotlin.hashCode(this.sunColor) | 0;
    result = result * 31 + Kotlin.hashCode(this.waterColor) | 0;
    result = result * 31 + Kotlin.hashCode(this.eye) | 0;
    result = result * 31 + Kotlin.hashCode(this.distortionScale) | 0;
    result = result * 31 + Kotlin.hashCode(this.side) | 0;
    result = result * 31 + Kotlin.hashCode(this.fog) | 0;
    return result;
  };
  WaterOptions.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.textureWidth, other.textureWidth) && Kotlin.equals(this.textureHeight, other.textureHeight) && Kotlin.equals(this.clipBias, other.clipBias) && Kotlin.equals(this.alpha, other.alpha) && Kotlin.equals(this.time, other.time) && Kotlin.equals(this.waterNormals, other.waterNormals) && Kotlin.equals(this.sunDirection, other.sunDirection) && Kotlin.equals(this.sunColor, other.sunColor) && Kotlin.equals(this.waterColor, other.waterColor) && Kotlin.equals(this.eye, other.eye) && Kotlin.equals(this.distortionScale, other.distortionScale) && Kotlin.equals(this.side, other.side) && Kotlin.equals(this.fog, other.fog)))));
  };
  function ColorConstants() {
    ColorConstants_instance = this;
    this.aliceblue = 15792383;
    this.antiquewhite = 16444375;
    this.aqua = 65535;
    this.aquamarine = 8388564;
    this.azure = 15794175;
    this.beige = 16119260;
    this.bisque = 16770244;
    this.black = 0;
    this.blanchedalmond = 16772045;
    this.blue = 255;
    this.blueviolet = 9055202;
    this.brown = 10824234;
    this.burlywood = 14596231;
    this.cadetblue = 6266528;
    this.chartreuse = 8388352;
    this.chocolate = 13789470;
    this.coral = 16744272;
    this.cornflowerblue = 6591981;
    this.cornsilk = 16775388;
    this.crimson = 14423100;
    this.cyan = 65535;
    this.darkblue = 139;
    this.darkcyan = 35723;
    this.darkgoldenrod = 12092939;
    this.darkgray = 11119017;
    this.darkgreen = 25600;
    this.darkgrey = 11119017;
    this.darkkhaki = 12433259;
    this.darkmagenta = 9109643;
    this.darkolivegreen = 5597999;
    this.darkorange = 16747520;
    this.darkorchid = 10040012;
    this.darkred = 9109504;
    this.darksalmon = 15308410;
    this.darkseagreen = 9419919;
    this.darkslateblue = 4734347;
    this.darkslategray = 3100495;
    this.darkslategrey = 3100495;
    this.darkturquoise = 52945;
    this.darkviolet = 9699539;
    this.deeppink = 16716947;
    this.deepskyblue = 49151;
    this.dimgray = 6908265;
    this.dimgrey = 6908265;
    this.dodgerblue = 2003199;
    this.firebrick = 11674146;
    this.floralwhite = 16775920;
    this.forestgreen = 2263842;
    this.fuchsia = 16711935;
    this.gainsboro = 14474460;
    this.ghostwhite = 16316671;
    this.gold = 16766720;
    this.goldenrod = 14329120;
    this.gray = 8421504;
    this.green = 32768;
    this.greenyellow = 11403055;
    this.grey = 8421504;
    this.honeydew = 15794160;
    this.hotpink = 16738740;
    this.indianred = 13458524;
    this.indigo = 4915330;
    this.ivory = 16777200;
    this.khaki = 15787660;
    this.lavender = 15132410;
    this.lavenderblush = 16773365;
    this.lawngreen = 8190976;
    this.lemonchiffon = 16775885;
    this.lightblue = 11393254;
    this.lightcoral = 15761536;
    this.lightcyan = 14745599;
    this.lightgoldenrodyellow = 16448210;
    this.lightgray = 13882323;
    this.lightgreen = 9498256;
    this.lightgrey = 13882323;
    this.lightpink = 16758465;
    this.lightsalmon = 16752762;
    this.lightseagreen = 2142890;
    this.lightskyblue = 8900346;
    this.lightslategray = 7833753;
    this.lightslategrey = 7833753;
    this.lightsteelblue = 11584734;
    this.lightyellow = 16777184;
    this.lime = 65280;
    this.limegreen = 3329330;
    this.linen = 16445670;
    this.magenta = 16711935;
    this.maroon = 8388608;
    this.mediumaquamarine = 6737322;
    this.mediumblue = 205;
    this.mediumorchid = 12211667;
    this.mediumpurple = 9662683;
    this.mediumseagreen = 3978097;
    this.mediumslateblue = 8087790;
    this.mediumspringgreen = 64154;
    this.mediumturquoise = 4772300;
    this.mediumvioletred = 13047173;
    this.midnightblue = 1644912;
    this.mintcream = 16121850;
    this.mistyrose = 16770273;
    this.moccasin = 16770229;
    this.navajowhite = 16768685;
    this.navy = 128;
    this.oldlace = 16643558;
    this.olive = 8421376;
    this.olivedrab = 7048739;
    this.orange = 16753920;
    this.orangered = 16729344;
    this.orchid = 14315734;
    this.palegoldenrod = 15657130;
    this.palegreen = 10025880;
    this.paleturquoise = 11529966;
    this.palevioletred = 14381203;
    this.papayawhip = 16773077;
    this.peachpuff = 16767673;
    this.peru = 13468991;
    this.pink = 16761035;
    this.plum = 14524637;
    this.powderblue = 11591910;
    this.purple = 8388736;
    this.rebeccapurple = 6697881;
    this.red = 16711680;
    this.rosybrown = 12357519;
    this.royalblue = 4286945;
    this.saddlebrown = 9127187;
    this.salmon = 16416882;
    this.sandybrown = 16032864;
    this.seagreen = 3050327;
    this.seashell = 16774638;
    this.sienna = 10506797;
    this.silver = 12632256;
    this.skyblue = 8900331;
    this.slateblue = 6970061;
    this.slategray = 7372944;
    this.slategrey = 7372944;
    this.snow = 16775930;
    this.springgreen = 65407;
    this.steelblue = 4620980;
    this.tan = 13808780;
    this.teal = 32896;
    this.thistle = 14204888;
    this.tomato = 16737095;
    this.turquoise = 4251856;
    this.violet = 15631086;
    this.wheat = 16113331;
    this.white = 16777215;
    this.whitesmoke = 16119285;
    this.yellow = 16776960;
    this.yellowgreen = 10145074;
  }
  ColorConstants.$metadata$ = {
    kind: Kind_OBJECT,
    simpleName: 'ColorConstants',
    interfaces: []
  };
  var ColorConstants_instance = null;
  function ColorConstants_getInstance() {
    if (ColorConstants_instance === null) {
      new ColorConstants();
    }
    return ColorConstants_instance;
  }
  function unaryMinus($receiver) {
    return $receiver.clone().negate();
  }
  function plusAssign($receiver, v) {
    $receiver.add(v);
  }
  function plus($receiver, v) {
    return $receiver.clone().add(v);
  }
  function minusAssign($receiver, v) {
    $receiver.sub(v);
  }
  function minus($receiver, v) {
    return $receiver.clone().sub(v);
  }
  function times($receiver, v) {
    return $receiver.clone().multiply(v);
  }
  function timesAssign($receiver, v) {
    times($receiver, v);
  }
  function unaryMinus_0($receiver) {
    return $receiver.clone().negate();
  }
  function plusAssign_0($receiver, v) {
    $receiver.add(v);
  }
  function plus_0($receiver, v) {
    return $receiver.clone().add(v);
  }
  function minusAssign_0($receiver, v) {
    $receiver.sub(v);
  }
  function minus_0($receiver, v) {
    return $receiver.clone().sub(v);
  }
  function times_0($receiver, q) {
    return $receiver.clone().multiply(q);
  }
  function timesAssign_0($receiver, q) {
    $receiver.multiply(q);
  }
  function times_1($receiver, v) {
    return $receiver.setPosition(v.applyMatrix4($receiver));
  }
  function times_2($receiver, m) {
    return $receiver.clone().multiply(m);
  }
  function timesAssign_1($receiver, m) {
    $receiver.multiply(m);
  }
  function WebGL2RendererParams(canvas, alpha, depth, stencil, antialias, premultipliedAlpha, preserveDrawingBuffer) {
    if (canvas === void 0)
      canvas = undefined;
    if (alpha === void 0)
      alpha = undefined;
    if (depth === void 0)
      depth = undefined;
    if (stencil === void 0)
      stencil = undefined;
    if (antialias === void 0)
      antialias = undefined;
    if (premultipliedAlpha === void 0)
      premultipliedAlpha = undefined;
    if (preserveDrawingBuffer === void 0)
      preserveDrawingBuffer = undefined;
    this.canvas = canvas;
    this.alpha = alpha;
    this.depth = depth;
    this.stencil = stencil;
    this.antialias = antialias;
    this.premultipliedAlpha = premultipliedAlpha;
    this.preserveDrawingBuffer = preserveDrawingBuffer;
  }
  WebGL2RendererParams.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'WebGL2RendererParams',
    interfaces: []
  };
  WebGL2RendererParams.prototype.component1 = function () {
    return this.canvas;
  };
  WebGL2RendererParams.prototype.component2 = function () {
    return this.alpha;
  };
  WebGL2RendererParams.prototype.component3 = function () {
    return this.depth;
  };
  WebGL2RendererParams.prototype.component4 = function () {
    return this.stencil;
  };
  WebGL2RendererParams.prototype.component5 = function () {
    return this.antialias;
  };
  WebGL2RendererParams.prototype.component6 = function () {
    return this.premultipliedAlpha;
  };
  WebGL2RendererParams.prototype.component7 = function () {
    return this.preserveDrawingBuffer;
  };
  WebGL2RendererParams.prototype.copy_cflky8$ = function (canvas, alpha, depth, stencil, antialias, premultipliedAlpha, preserveDrawingBuffer) {
    return new WebGL2RendererParams(canvas === void 0 ? this.canvas : canvas, alpha === void 0 ? this.alpha : alpha, depth === void 0 ? this.depth : depth, stencil === void 0 ? this.stencil : stencil, antialias === void 0 ? this.antialias : antialias, premultipliedAlpha === void 0 ? this.premultipliedAlpha : premultipliedAlpha, preserveDrawingBuffer === void 0 ? this.preserveDrawingBuffer : preserveDrawingBuffer);
  };
  WebGL2RendererParams.prototype.toString = function () {
    return 'WebGL2RendererParams(canvas=' + Kotlin.toString(this.canvas) + (', alpha=' + Kotlin.toString(this.alpha)) + (', depth=' + Kotlin.toString(this.depth)) + (', stencil=' + Kotlin.toString(this.stencil)) + (', antialias=' + Kotlin.toString(this.antialias)) + (', premultipliedAlpha=' + Kotlin.toString(this.premultipliedAlpha)) + (', preserveDrawingBuffer=' + Kotlin.toString(this.preserveDrawingBuffer)) + ')';
  };
  WebGL2RendererParams.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.canvas) | 0;
    result = result * 31 + Kotlin.hashCode(this.alpha) | 0;
    result = result * 31 + Kotlin.hashCode(this.depth) | 0;
    result = result * 31 + Kotlin.hashCode(this.stencil) | 0;
    result = result * 31 + Kotlin.hashCode(this.antialias) | 0;
    result = result * 31 + Kotlin.hashCode(this.premultipliedAlpha) | 0;
    result = result * 31 + Kotlin.hashCode(this.preserveDrawingBuffer) | 0;
    return result;
  };
  WebGL2RendererParams.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.canvas, other.canvas) && Kotlin.equals(this.alpha, other.alpha) && Kotlin.equals(this.depth, other.depth) && Kotlin.equals(this.stencil, other.stencil) && Kotlin.equals(this.antialias, other.antialias) && Kotlin.equals(this.premultipliedAlpha, other.premultipliedAlpha) && Kotlin.equals(this.preserveDrawingBuffer, other.preserveDrawingBuffer)))));
  };
  function WebGLRenderTargetOptions(wrapS, wrapT, magFilter, minFilter, format, type, anisotropy, encoding, depthBuffer, stencilBuffer) {
    if (wrapS === void 0)
      wrapS = undefined;
    if (wrapT === void 0)
      wrapT = undefined;
    if (magFilter === void 0)
      magFilter = undefined;
    if (minFilter === void 0)
      minFilter = undefined;
    if (format === void 0)
      format = undefined;
    if (type === void 0)
      type = undefined;
    if (anisotropy === void 0)
      anisotropy = undefined;
    if (encoding === void 0)
      encoding = undefined;
    if (depthBuffer === void 0)
      depthBuffer = undefined;
    if (stencilBuffer === void 0)
      stencilBuffer = undefined;
    this.wrapS = wrapS;
    this.wrapT = wrapT;
    this.magFilter = magFilter;
    this.minFilter = minFilter;
    this.format = format;
    this.type = type;
    this.anisotropy = anisotropy;
    this.encoding = encoding;
    this.depthBuffer = depthBuffer;
    this.stencilBuffer = stencilBuffer;
  }
  WebGLRenderTargetOptions.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'WebGLRenderTargetOptions',
    interfaces: []
  };
  WebGLRenderTargetOptions.prototype.component1 = function () {
    return this.wrapS;
  };
  WebGLRenderTargetOptions.prototype.component2 = function () {
    return this.wrapT;
  };
  WebGLRenderTargetOptions.prototype.component3 = function () {
    return this.magFilter;
  };
  WebGLRenderTargetOptions.prototype.component4 = function () {
    return this.minFilter;
  };
  WebGLRenderTargetOptions.prototype.component5 = function () {
    return this.format;
  };
  WebGLRenderTargetOptions.prototype.component6 = function () {
    return this.type;
  };
  WebGLRenderTargetOptions.prototype.component7 = function () {
    return this.anisotropy;
  };
  WebGLRenderTargetOptions.prototype.component8 = function () {
    return this.encoding;
  };
  WebGLRenderTargetOptions.prototype.component9 = function () {
    return this.depthBuffer;
  };
  WebGLRenderTargetOptions.prototype.component10 = function () {
    return this.stencilBuffer;
  };
  WebGLRenderTargetOptions.prototype.copy_461kpo$ = function (wrapS, wrapT, magFilter, minFilter, format, type, anisotropy, encoding, depthBuffer, stencilBuffer) {
    return new WebGLRenderTargetOptions(wrapS === void 0 ? this.wrapS : wrapS, wrapT === void 0 ? this.wrapT : wrapT, magFilter === void 0 ? this.magFilter : magFilter, minFilter === void 0 ? this.minFilter : minFilter, format === void 0 ? this.format : format, type === void 0 ? this.type : type, anisotropy === void 0 ? this.anisotropy : anisotropy, encoding === void 0 ? this.encoding : encoding, depthBuffer === void 0 ? this.depthBuffer : depthBuffer, stencilBuffer === void 0 ? this.stencilBuffer : stencilBuffer);
  };
  WebGLRenderTargetOptions.prototype.toString = function () {
    return 'WebGLRenderTargetOptions(wrapS=' + Kotlin.toString(this.wrapS) + (', wrapT=' + Kotlin.toString(this.wrapT)) + (', magFilter=' + Kotlin.toString(this.magFilter)) + (', minFilter=' + Kotlin.toString(this.minFilter)) + (', format=' + Kotlin.toString(this.format)) + (', type=' + Kotlin.toString(this.type)) + (', anisotropy=' + Kotlin.toString(this.anisotropy)) + (', encoding=' + Kotlin.toString(this.encoding)) + (', depthBuffer=' + Kotlin.toString(this.depthBuffer)) + (', stencilBuffer=' + Kotlin.toString(this.stencilBuffer)) + ')';
  };
  WebGLRenderTargetOptions.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.wrapS) | 0;
    result = result * 31 + Kotlin.hashCode(this.wrapT) | 0;
    result = result * 31 + Kotlin.hashCode(this.magFilter) | 0;
    result = result * 31 + Kotlin.hashCode(this.minFilter) | 0;
    result = result * 31 + Kotlin.hashCode(this.format) | 0;
    result = result * 31 + Kotlin.hashCode(this.type) | 0;
    result = result * 31 + Kotlin.hashCode(this.anisotropy) | 0;
    result = result * 31 + Kotlin.hashCode(this.encoding) | 0;
    result = result * 31 + Kotlin.hashCode(this.depthBuffer) | 0;
    result = result * 31 + Kotlin.hashCode(this.stencilBuffer) | 0;
    return result;
  };
  WebGLRenderTargetOptions.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.wrapS, other.wrapS) && Kotlin.equals(this.wrapT, other.wrapT) && Kotlin.equals(this.magFilter, other.magFilter) && Kotlin.equals(this.minFilter, other.minFilter) && Kotlin.equals(this.format, other.format) && Kotlin.equals(this.type, other.type) && Kotlin.equals(this.anisotropy, other.anisotropy) && Kotlin.equals(this.encoding, other.encoding) && Kotlin.equals(this.depthBuffer, other.depthBuffer) && Kotlin.equals(this.stencilBuffer, other.stencilBuffer)))));
  };
  function WebGLRendererParams(canvas, alpha, depth, stencil, antialias, premultipliedAlpha, preserveDrawingBuffer) {
    if (canvas === void 0)
      canvas = undefined;
    if (alpha === void 0)
      alpha = undefined;
    if (depth === void 0)
      depth = undefined;
    if (stencil === void 0)
      stencil = undefined;
    if (antialias === void 0)
      antialias = undefined;
    if (premultipliedAlpha === void 0)
      premultipliedAlpha = undefined;
    if (preserveDrawingBuffer === void 0)
      preserveDrawingBuffer = undefined;
    this.canvas = canvas;
    this.alpha = alpha;
    this.depth = depth;
    this.stencil = stencil;
    this.antialias = antialias;
    this.premultipliedAlpha = premultipliedAlpha;
    this.preserveDrawingBuffer = preserveDrawingBuffer;
  }
  WebGLRendererParams.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'WebGLRendererParams',
    interfaces: []
  };
  WebGLRendererParams.prototype.component1 = function () {
    return this.canvas;
  };
  WebGLRendererParams.prototype.component2 = function () {
    return this.alpha;
  };
  WebGLRendererParams.prototype.component3 = function () {
    return this.depth;
  };
  WebGLRendererParams.prototype.component4 = function () {
    return this.stencil;
  };
  WebGLRendererParams.prototype.component5 = function () {
    return this.antialias;
  };
  WebGLRendererParams.prototype.component6 = function () {
    return this.premultipliedAlpha;
  };
  WebGLRendererParams.prototype.component7 = function () {
    return this.preserveDrawingBuffer;
  };
  WebGLRendererParams.prototype.copy_cflky8$ = function (canvas, alpha, depth, stencil, antialias, premultipliedAlpha, preserveDrawingBuffer) {
    return new WebGLRendererParams(canvas === void 0 ? this.canvas : canvas, alpha === void 0 ? this.alpha : alpha, depth === void 0 ? this.depth : depth, stencil === void 0 ? this.stencil : stencil, antialias === void 0 ? this.antialias : antialias, premultipliedAlpha === void 0 ? this.premultipliedAlpha : premultipliedAlpha, preserveDrawingBuffer === void 0 ? this.preserveDrawingBuffer : preserveDrawingBuffer);
  };
  WebGLRendererParams.prototype.toString = function () {
    return 'WebGLRendererParams(canvas=' + Kotlin.toString(this.canvas) + (', alpha=' + Kotlin.toString(this.alpha)) + (', depth=' + Kotlin.toString(this.depth)) + (', stencil=' + Kotlin.toString(this.stencil)) + (', antialias=' + Kotlin.toString(this.antialias)) + (', premultipliedAlpha=' + Kotlin.toString(this.premultipliedAlpha)) + (', preserveDrawingBuffer=' + Kotlin.toString(this.preserveDrawingBuffer)) + ')';
  };
  WebGLRendererParams.prototype.hashCode = function () {
    var result = 0;
    result = result * 31 + Kotlin.hashCode(this.canvas) | 0;
    result = result * 31 + Kotlin.hashCode(this.alpha) | 0;
    result = result * 31 + Kotlin.hashCode(this.depth) | 0;
    result = result * 31 + Kotlin.hashCode(this.stencil) | 0;
    result = result * 31 + Kotlin.hashCode(this.antialias) | 0;
    result = result * 31 + Kotlin.hashCode(this.premultipliedAlpha) | 0;
    result = result * 31 + Kotlin.hashCode(this.preserveDrawingBuffer) | 0;
    return result;
  };
  WebGLRendererParams.prototype.equals = function (other) {
    return this === other || (other !== null && (typeof other === 'object' && (Object.getPrototypeOf(this) === Object.getPrototypeOf(other) && (Kotlin.equals(this.canvas, other.canvas) && Kotlin.equals(this.alpha, other.alpha) && Kotlin.equals(this.depth, other.depth) && Kotlin.equals(this.stencil, other.stencil) && Kotlin.equals(this.antialias, other.antialias) && Kotlin.equals(this.premultipliedAlpha, other.premultipliedAlpha) && Kotlin.equals(this.preserveDrawingBuffer, other.preserveDrawingBuffer)))));
  };
  var package$info = _.info || (_.info = {});
  var package$laht = package$info.laht || (package$info.laht = {});
  var package$threekt = package$laht.threekt || (package$laht.threekt = {});
  var package$external = package$threekt.external || (package$threekt.external = {});
  var package$libs = package$external.libs || (package$external.libs = {});
  var package$datgui = package$libs.datgui || (package$libs.datgui = {});
  package$datgui.GUIParams = GUIParams;
  var package$objects = package$external.objects || (package$external.objects = {});
  package$objects.WaterOptions = WaterOptions;
  var package$math = package$threekt.math || (package$threekt.math = {});
  Object.defineProperty(package$math, 'ColorConstants', {
    get: ColorConstants_getInstance
  });
  package$math.unaryMinus_kyshpr$ = unaryMinus;
  package$math.plusAssign_gulir3$ = plusAssign;
  package$math.plus_gulir3$ = plus;
  package$math.minusAssign_gulir3$ = minusAssign;
  package$math.minus_gulir3$ = minus;
  package$math.times_gulir3$ = times;
  package$math.timesAssign_gulir3$ = timesAssign;
  package$math.unaryMinus_kyshow$ = unaryMinus_0;
  package$math.plusAssign_ge5zap$ = plusAssign_0;
  package$math.plus_ge5zap$ = plus_0;
  package$math.minusAssign_ge5zap$ = minusAssign_0;
  package$math.minus_ge5zap$ = minus_0;
  package$math.times_wuat67$ = times_0;
  package$math.timesAssign_wuat67$ = timesAssign_0;
  package$math.times_xmp7yk$ = times_1;
  package$math.times_t2mnu9$ = times_2;
  package$math.timesAssign_t2mnu9$ = timesAssign_1;
  var package$renderers = package$threekt.renderers || (package$threekt.renderers = {});
  package$renderers.WebGL2RendererParams = WebGL2RendererParams;
  package$renderers.WebGLRenderTargetOptions = WebGLRenderTargetOptions;
  package$renderers.WebGLRendererParams = WebGLRendererParams;
  Kotlin.defineModule('threejs-wrapper', _);
  return _;
}));
