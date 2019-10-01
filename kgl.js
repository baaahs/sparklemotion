(function (root, factory) {
  if (typeof define === 'function' && define.amd)
    define(['exports', 'kotlin'], factory);
  else if (typeof exports === 'object')
    factory(module.exports, require('kotlin'));
  else {
    if (typeof kotlin === 'undefined') {
      throw new Error("Error loading module 'kgl'. Its dependency 'kotlin' was not found. Please, check whether 'kotlin' is loaded prior to 'kgl'.");
    }
    root.kgl = factory(typeof kgl === 'undefined' ? {} : kgl, kotlin);
  }
}(this, function (_, Kotlin) {
  'use strict';
  var Kind_INTERFACE = Kotlin.Kind.INTERFACE;
  var Kind_CLASS = Kotlin.Kind.CLASS;
  var toTypedArray = Kotlin.kotlin.collections.toTypedArray_rjqryz$;
  var toTypedArray_0 = Kotlin.kotlin.collections.toTypedArray_964n91$;
  var ensureNotNull = Kotlin.ensureNotNull;
  var throwCCE = Kotlin.throwCCE;
  var Exception_init = Kotlin.kotlin.Exception_init;
  var Unit = Kotlin.kotlin.Unit;
  var Array_0 = Array;
  FloatBuffer.prototype = Object.create(Buffer.prototype);
  FloatBuffer.prototype.constructor = FloatBuffer;
  ByteBuffer.prototype = Object.create(Buffer.prototype);
  ByteBuffer.prototype.constructor = ByteBuffer;
  function allocate(sizeInBytes) {
    return FloatBuffer_init_1(sizeInBytes / 4 | 0);
  }
  var GL_ACTIVE_TEXTURE;
  var GL_DEPTH_BUFFER_BIT;
  var GL_STENCIL_BUFFER_BIT;
  var GL_COLOR_BUFFER_BIT;
  var GL_FALSE;
  var GL_TRUE;
  var GL_POINTS;
  var GL_LINES;
  var GL_LINE_LOOP;
  var GL_LINE_STRIP;
  var GL_TRIANGLES;
  var GL_TRIANGLE_STRIP;
  var GL_TRIANGLE_FAN;
  var GL_ZERO;
  var GL_ONE;
  var GL_SRC_COLOR;
  var GL_ONE_MINUS_SRC_COLOR;
  var GL_SRC_ALPHA;
  var GL_ONE_MINUS_SRC_ALPHA;
  var GL_DST_ALPHA;
  var GL_ONE_MINUS_DST_ALPHA;
  var GL_DST_COLOR;
  var GL_ONE_MINUS_DST_COLOR;
  var GL_SRC_ALPHA_SATURATE;
  var GL_FUNC_ADD;
  var GL_BLEND_EQUATION;
  var GL_BLEND_EQUATION_RGB;
  var GL_BLEND_EQUATION_ALPHA;
  var GL_FUNC_SUBTRACT;
  var GL_FUNC_REVERSE_SUBTRACT;
  var GL_BLEND_DST_RGB;
  var GL_BLEND_SRC_RGB;
  var GL_BLEND_DST_ALPHA;
  var GL_BLEND_SRC_ALPHA;
  var GL_CONSTANT_COLOR;
  var GL_ONE_MINUS_CONSTANT_COLOR;
  var GL_CONSTANT_ALPHA;
  var GL_ONE_MINUS_CONSTANT_ALPHA;
  var GL_BLEND_COLOR;
  var GL_ARRAY_BUFFER;
  var GL_ELEMENT_ARRAY_BUFFER;
  var GL_ARRAY_BUFFER_BINDING;
  var GL_ELEMENT_ARRAY_BUFFER_BINDING;
  var GL_STREAM_DRAW;
  var GL_STATIC_DRAW;
  var GL_DYNAMIC_DRAW;
  var GL_BUFFER_SIZE;
  var GL_BUFFER_USAGE;
  var GL_CURRENT_VERTEX_ATTRIB;
  var GL_FRONT;
  var GL_BACK;
  var GL_FRONT_AND_BACK;
  var GL_TEXTURE_2D;
  var GL_CULL_FACE;
  var GL_BLEND;
  var GL_DITHER;
  var GL_STENCIL_TEST;
  var GL_DEPTH_TEST;
  var GL_SCISSOR_TEST;
  var GL_POLYGON_OFFSET_FILL;
  var GL_SAMPLE_ALPHA_TO_COVERAGE;
  var GL_SAMPLE_COVERAGE;
  var GL_NO_ERROR;
  var GL_INVALID_ENUM;
  var GL_INVALID_VALUE;
  var GL_INVALID_OPERATION;
  var GL_OUT_OF_MEMORY;
  var GL_INVALID_FRAMEBUFFER_OPERATION;
  var GL_CW;
  var GL_CCW;
  var GL_LINE_WIDTH;
  var GL_ALIASED_POINT_SIZE_RANGE;
  var GL_ALIASED_LINE_WIDTH_RANGE;
  var GL_CULL_FACE_MODE;
  var GL_FRONT_FACE;
  var GL_DEPTH_RANGE;
  var GL_DEPTH_WRITEMASK;
  var GL_DEPTH_CLEAR_VALUE;
  var GL_DEPTH_FUNC;
  var GL_STENCIL_CLEAR_VALUE;
  var GL_STENCIL_FUNC;
  var GL_STENCIL_FAIL;
  var GL_STENCIL_PASS_DEPTH_FAIL;
  var GL_STENCIL_PASS_DEPTH_PASS;
  var GL_STENCIL_REF;
  var GL_STENCIL_VALUE_MASK;
  var GL_STENCIL_WRITEMASK;
  var GL_STENCIL_BACK_FUNC;
  var GL_STENCIL_BACK_FAIL;
  var GL_STENCIL_BACK_PASS_DEPTH_FAIL;
  var GL_STENCIL_BACK_PASS_DEPTH_PASS;
  var GL_STENCIL_BACK_REF;
  var GL_STENCIL_BACK_VALUE_MASK;
  var GL_STENCIL_BACK_WRITEMASK;
  var GL_VIEWPORT;
  var GL_SCISSOR_BOX;
  var GL_COLOR_CLEAR_VALUE;
  var GL_COLOR_WRITEMASK;
  var GL_UNPACK_ALIGNMENT;
  var GL_PACK_ALIGNMENT;
  var GL_MAX_TEXTURE_SIZE;
  var GL_MAX_VIEWPORT_DIMS;
  var GL_SUBPIXEL_BITS;
  var GL_RED_BITS;
  var GL_GREEN_BITS;
  var GL_BLUE_BITS;
  var GL_ALPHA_BITS;
  var GL_DEPTH_BITS;
  var GL_STENCIL_BITS;
  var GL_POLYGON_OFFSET_UNITS;
  var GL_POLYGON_OFFSET_FACTOR;
  var GL_TEXTURE_BINDING_2D;
  var GL_SAMPLE_BUFFERS;
  var GL_SAMPLES;
  var GL_SAMPLE_COVERAGE_VALUE;
  var GL_SAMPLE_COVERAGE_INVERT;
  var GL_NUM_COMPRESSED_TEXTURE_FORMATS;
  var GL_COMPRESSED_TEXTURE_FORMATS;
  var GL_DONT_CARE;
  var GL_FASTEST;
  var GL_NICEST;
  var GL_GENERATE_MIPMAP_HINT;
  var GL_BYTE;
  var GL_UNSIGNED_BYTE;
  var GL_SHORT;
  var GL_UNSIGNED_SHORT;
  var GL_INT;
  var GL_UNSIGNED_INT;
  var GL_FLOAT;
  var GL_FIXED;
  var GL_STENCIL_INDEX;
  var GL_DEPTH_COMPONENT;
  var GL_RED;
  var GL_GREEN;
  var GL_BLUE;
  var GL_ALPHA;
  var GL_RGB;
  var GL_RGBA;
  var GL_LUMINANCE;
  var GL_LUMINANCE_ALPHA;
  var GL_UNSIGNED_SHORT_4_4_4_4;
  var GL_UNSIGNED_SHORT_5_5_5_1;
  var GL_UNSIGNED_SHORT_5_6_5;
  var GL_FRAGMENT_SHADER;
  var GL_VERTEX_SHADER;
  var GL_MAX_VERTEX_ATTRIBS;
  var GL_MAX_VERTEX_UNIFORM_VECTORS;
  var GL_MAX_VARYING_VECTORS;
  var GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
  var GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS;
  var GL_MAX_TEXTURE_IMAGE_UNITS;
  var GL_MAX_FRAGMENT_UNIFORM_VECTORS;
  var GL_SHADER_TYPE;
  var GL_DELETE_STATUS;
  var GL_LINK_STATUS;
  var GL_VALIDATE_STATUS;
  var GL_ATTACHED_SHADERS;
  var GL_ACTIVE_UNIFORMS;
  var GL_ACTIVE_UNIFORM_MAX_LENGTH;
  var GL_ACTIVE_ATTRIBUTES;
  var GL_ACTIVE_ATTRIBUTE_MAX_LENGTH;
  var GL_SHADING_LANGUAGE_VERSION;
  var GL_CURRENT_PROGRAM;
  var GL_NEVER;
  var GL_LESS;
  var GL_EQUAL;
  var GL_LEQUAL;
  var GL_GREATER;
  var GL_NOTEQUAL;
  var GL_GEQUAL;
  var GL_ALWAYS;
  var GL_KEEP;
  var GL_REPLACE;
  var GL_INCR;
  var GL_DECR;
  var GL_INVERT;
  var GL_INCR_WRAP;
  var GL_DECR_WRAP;
  var GL_VENDOR;
  var GL_RENDERER;
  var GL_VERSION;
  var GL_EXTENSIONS;
  var GL_NEAREST;
  var GL_LINEAR;
  var GL_NEAREST_MIPMAP_NEAREST;
  var GL_LINEAR_MIPMAP_NEAREST;
  var GL_NEAREST_MIPMAP_LINEAR;
  var GL_LINEAR_MIPMAP_LINEAR;
  var GL_TEXTURE_MAG_FILTER;
  var GL_TEXTURE_MIN_FILTER;
  var GL_TEXTURE_WRAP_S;
  var GL_TEXTURE_WRAP_T;
  var GL_TEXTURE;
  var GL_TEXTURE_CUBE_MAP;
  var GL_TEXTURE_BINDING_CUBE_MAP;
  var GL_TEXTURE_CUBE_MAP_POSITIVE_X;
  var GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
  var GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
  var GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
  var GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
  var GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
  var GL_MAX_CUBE_MAP_TEXTURE_SIZE;
  var GL_TEXTURE0;
  var GL_TEXTURE1;
  var GL_TEXTURE2;
  var GL_TEXTURE3;
  var GL_TEXTURE4;
  var GL_TEXTURE5;
  var GL_TEXTURE6;
  var GL_TEXTURE7;
  var GL_TEXTURE8;
  var GL_TEXTURE9;
  var GL_TEXTURE10;
  var GL_TEXTURE11;
  var GL_TEXTURE12;
  var GL_TEXTURE13;
  var GL_TEXTURE14;
  var GL_TEXTURE15;
  var GL_TEXTURE16;
  var GL_TEXTURE17;
  var GL_TEXTURE18;
  var GL_TEXTURE19;
  var GL_TEXTURE20;
  var GL_TEXTURE21;
  var GL_TEXTURE22;
  var GL_TEXTURE23;
  var GL_TEXTURE24;
  var GL_TEXTURE25;
  var GL_TEXTURE26;
  var GL_TEXTURE27;
  var GL_TEXTURE28;
  var GL_TEXTURE29;
  var GL_TEXTURE30;
  var GL_TEXTURE31;
  var GL_REPEAT;
  var GL_CLAMP_TO_EDGE;
  var GL_MIRRORED_REPEAT;
  var GL_FLOAT_VEC2;
  var GL_FLOAT_VEC3;
  var GL_FLOAT_VEC4;
  var GL_INT_VEC2;
  var GL_INT_VEC3;
  var GL_INT_VEC4;
  var GL_BOOL;
  var GL_BOOL_VEC2;
  var GL_BOOL_VEC3;
  var GL_BOOL_VEC4;
  var GL_FLOAT_MAT2;
  var GL_FLOAT_MAT3;
  var GL_FLOAT_MAT4;
  var GL_SAMPLER_2D;
  var GL_SAMPLER_CUBE;
  var GL_VERTEX_ATTRIB_ARRAY_ENABLED;
  var GL_VERTEX_ATTRIB_ARRAY_SIZE;
  var GL_VERTEX_ATTRIB_ARRAY_STRIDE;
  var GL_VERTEX_ATTRIB_ARRAY_TYPE;
  var GL_VERTEX_ATTRIB_ARRAY_NORMALIZED;
  var GL_VERTEX_ATTRIB_ARRAY_POINTER;
  var GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING;
  var GL_IMPLEMENTATION_COLOR_READ_TYPE;
  var GL_IMPLEMENTATION_COLOR_READ_FORMAT;
  var GL_COMPILE_STATUS;
  var GL_INFO_LOG_LENGTH;
  var GL_SHADER_SOURCE_LENGTH;
  var GL_SHADER_COMPILER;
  var GL_SHADER_BINARY_FORMATS;
  var GL_NUM_SHADER_BINARY_FORMATS;
  var GL_LOW_FLOAT;
  var GL_MEDIUM_FLOAT;
  var GL_HIGH_FLOAT;
  var GL_LOW_INT;
  var GL_MEDIUM_INT;
  var GL_HIGH_INT;
  var GL_FRAMEBUFFER;
  var GL_RENDERBUFFER;
  var GL_RGBA4;
  var GL_RGB5_A1;
  var GL_RGB565;
  var GL_DEPTH_COMPONENT16;
  var GL_FRAMEBUFFER_COMPLETE;
  var GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
  var GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
  var GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
  var GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
  var GL_FRAMEBUFFER_UNSUPPORTED;
  var GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE;
  var GL_FRAMEBUFFER_UNDEFINED;
  var GL_COLOR_ATTACHMENT0;
  var GL_COLOR_ATTACHMENT1;
  var GL_COLOR_ATTACHMENT2;
  var GL_COLOR_ATTACHMENT3;
  var GL_COLOR_ATTACHMENT4;
  var GL_COLOR_ATTACHMENT5;
  var GL_COLOR_ATTACHMENT6;
  var GL_COLOR_ATTACHMENT7;
  var GL_COLOR_ATTACHMENT8;
  var GL_COLOR_ATTACHMENT9;
  var GL_COLOR_ATTACHMENT10;
  var GL_COLOR_ATTACHMENT11;
  var GL_COLOR_ATTACHMENT12;
  var GL_COLOR_ATTACHMENT13;
  var GL_COLOR_ATTACHMENT14;
  var GL_COLOR_ATTACHMENT15;
  var GL_COLOR_ATTACHMENT16;
  var GL_COLOR_ATTACHMENT17;
  var GL_COLOR_ATTACHMENT18;
  var GL_COLOR_ATTACHMENT19;
  var GL_COLOR_ATTACHMENT20;
  var GL_COLOR_ATTACHMENT21;
  var GL_COLOR_ATTACHMENT22;
  var GL_COLOR_ATTACHMENT23;
  var GL_COLOR_ATTACHMENT24;
  var GL_COLOR_ATTACHMENT25;
  var GL_COLOR_ATTACHMENT26;
  var GL_COLOR_ATTACHMENT27;
  var GL_COLOR_ATTACHMENT28;
  var GL_COLOR_ATTACHMENT29;
  var GL_COLOR_ATTACHMENT30;
  var GL_COLOR_ATTACHMENT31;
  var GL_DEPTH_ATTACHMENT;
  var GL_STENCIL_ATTACHMENT;
  var GL_DEPTH_STENCIL_ATTACHMENT;
  var GL_R8;
  var GL_R16;
  var GL_RG8;
  var GL_RG16;
  var GL_R16F;
  var GL_R32F;
  var GL_RG16F;
  var GL_RG32F;
  var GL_R8I;
  var GL_R8UI;
  var GL_R16I;
  var GL_R16UI;
  var GL_R32I;
  var GL_R32UI;
  var GL_RG8I;
  var GL_RG8UI;
  var GL_RG16I;
  var GL_RG16UI;
  var GL_RG32I;
  var GL_RG32UI;
  var GL_RG;
  var GL_COMPRESSED_RED;
  var GL_COMPRESSED_RG;
  function Kgl() {
  }
  Kgl.prototype.bufferData_8en9n9$ = function (target, sourceData, size, usage, offset, callback$default) {
    if (offset === void 0)
      offset = 0;
    callback$default ? callback$default(target, sourceData, size, usage, offset) : this.bufferData_8en9n9$$default(target, sourceData, size, usage, offset);
  };
  Kgl.prototype.uniform_rvcsvw$ = function (location, f) {
    this.uniform1f_rvcsvw$(location, f);
  };
  Kgl.prototype.uniform_zcqyrj$ = function (location, x, y) {
    this.uniform2f_zcqyrj$(location, x, y);
  };
  Kgl.prototype.uniform_ig0gt8$ = function (location, x, y, z) {
    this.uniform3f_ig0gt8$(location, x, y, z);
  };
  Kgl.prototype.uniform_k644h$ = function (location, x, y, z, w) {
    this.uniform4f_k644h$(location, x, y, z, w);
  };
  Kgl.prototype.uniform_wn2dyp$ = function (location, i) {
    this.uniform1i_wn2dyp$(location, i);
  };
  Kgl.prototype.uniform_47d3mp$ = function (location, x, y) {
    this.uniform2i_47d3mp$(location, x, y);
  };
  Kgl.prototype.uniform_ab551r$ = function (location, x, y, z) {
    this.uniform3i_ab551r$(location, x, y, z);
  };
  Kgl.prototype.uniform_tiwvvj$ = function (location, x, y, z, w) {
    this.uniform4i_tiwvvj$(location, x, y, z, w);
  };
  Kgl.prototype.texImage2D_e7c6np$ = function (target, level, internalFormat, width, height, border, format, type, buffer, offset, callback$default) {
    if (offset === void 0)
      offset = 0;
    callback$default ? callback$default(target, level, internalFormat, width, height, border, format, type, buffer, offset) : this.texImage2D_e7c6np$$default(target, level, internalFormat, width, height, border, format, type, buffer, offset);
  };
  Kgl.prototype.readPixels_idctqj$ = function (x, y, width, height, format, type, buffer, offset, callback$default) {
    if (offset === void 0)
      offset = 0;
    callback$default ? callback$default(x, y, width, height, format, type, buffer, offset) : this.readPixels_idctqj$$default(x, y, width, height, format, type, buffer, offset);
  };
  Kgl.$metadata$ = {
    kind: Kind_INTERFACE,
    simpleName: 'Kgl',
    interfaces: []
  };
  function Buffer(buffer) {
    this.buffer_eccfgl$_0 = buffer;
    this.position = 0;
  }
  Buffer.prototype.withGlBuffer_5tvim5$ = function (offset, fn) {
    return fn(this.buffer_eccfgl$_0.subarray(offset));
  };
  Buffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'Buffer',
    interfaces: []
  };
  function FloatBuffer(buffer) {
    Buffer.call(this, buffer);
    this.floatBuffer_0 = buffer;
  }
  FloatBuffer.prototype.put_mx4ult$ = function (f) {
    this.floatBuffer_0[this.position] = f;
    this.position = this.position + 1 | 0;
  };
  FloatBuffer.prototype.put_q3cr5i$ = function (floatArray) {
    this.put_kgymra$(floatArray, 0, floatArray.length);
  };
  FloatBuffer.prototype.put_kgymra$ = function (floatArray, offset, length) {
    this.floatBuffer_0.set(floatArray.subarray(offset, length), this.position);
    this.position = this.position + length | 0;
  };
  FloatBuffer.prototype.set_24o109$ = function (pos, f) {
    this.floatBuffer_0[pos] = f;
  };
  FloatBuffer.prototype.get = function () {
    return this.floatBuffer_0[this.position];
  };
  FloatBuffer.prototype.get_q3cr5i$ = function (floatArray) {
    this.get_kgymra$(floatArray, 0, floatArray.length);
  };
  FloatBuffer.prototype.get_kgymra$ = function (floatArray, offset, length) {
    var dest = floatArray;
    dest.subarray(offset, length).set(this.floatBuffer_0, this.position);
  };
  FloatBuffer.prototype.get_za3lpa$ = function (pos) {
    return this.floatBuffer_0[pos];
  };
  FloatBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'FloatBuffer',
    interfaces: [Buffer]
  };
  function FloatBuffer_init(buffer, $this) {
    $this = $this || Object.create(FloatBuffer.prototype);
    FloatBuffer.call($this, new Float32Array(buffer));
    return $this;
  }
  function FloatBuffer_init_0(buffer, $this) {
    $this = $this || Object.create(FloatBuffer.prototype);
    FloatBuffer.call($this, new Float32Array(toTypedArray(buffer)));
    return $this;
  }
  function FloatBuffer_init_1(size, $this) {
    $this = $this || Object.create(FloatBuffer.prototype);
    FloatBuffer_init_0(new Float32Array(size), $this);
    return $this;
  }
  function ByteBuffer(buffer) {
    Buffer.call(this, buffer);
    this.byteBuffer_0 = buffer;
  }
  ByteBuffer.prototype.put_s8j3t7$ = function (b) {
    this.byteBuffer_0[this.position] = b;
    this.position = this.position + 1 | 0;
  };
  ByteBuffer.prototype.put_fqrh44$ = function (byteArray) {
    this.put_mj6st8$(byteArray, 0, byteArray.length);
  };
  ByteBuffer.prototype.put_mj6st8$ = function (byteArray, offset, length) {
    this.byteBuffer_0.set(byteArray.subarray(offset, length), this.position);
    this.position = this.position + length | 0;
  };
  ByteBuffer.prototype.set_6t1wet$ = function (pos, b) {
    this.byteBuffer_0[pos] = b;
  };
  ByteBuffer.prototype.get = function () {
    return this.byteBuffer_0[this.position];
  };
  ByteBuffer.prototype.get_fqrh44$ = function (byteArray) {
    this.get_mj6st8$(byteArray, 0, byteArray.length);
  };
  ByteBuffer.prototype.get_mj6st8$ = function (byteArray, offset, length) {
    var dest = byteArray;
    dest.subarray(offset, length).set(this.byteBuffer_0, this.position);
  };
  ByteBuffer.prototype.get_za3lpa$ = function (pos) {
    return this.byteBuffer_0[pos];
  };
  ByteBuffer.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'ByteBuffer',
    interfaces: [Buffer]
  };
  function ByteBuffer_init(buffer, $this) {
    $this = $this || Object.create(ByteBuffer.prototype);
    ByteBuffer.call($this, new Uint8Array(buffer));
    return $this;
  }
  function ByteBuffer_init_0(buffer, $this) {
    $this = $this || Object.create(ByteBuffer.prototype);
    ByteBuffer.call($this, new Uint8Array(toTypedArray_0(buffer)));
    return $this;
  }
  function ByteBuffer_init_1(size, $this) {
    $this = $this || Object.create(ByteBuffer.prototype);
    ByteBuffer_init_0(new Int8Array(size), $this);
    return $this;
  }
  function KglJs(gl) {
    this.gl_0 = gl;
  }
  KglJs.prototype.createShader_za3lpa$ = function (type) {
    return this.gl_0.createShader(type);
  };
  KglJs.prototype.shaderSource_hwpqgh$ = function (shaderId, source) {
    this.gl_0.shaderSource(shaderId, source);
  };
  KglJs.prototype.compileShader_za3rmp$ = function (shaderId) {
    this.gl_0.compileShader(shaderId);
  };
  KglJs.prototype.deleteShader_za3rmp$ = function (shaderId) {
    this.gl_0.deleteShader(shaderId);
  };
  KglJs.prototype.getShaderParameter_wn2dyp$ = function (shader, pname) {
    var tmp$;
    var value = ensureNotNull(this.gl_0.getShaderParameter(shader, pname));
    if (typeof value === 'boolean') {
      return value ? 1 : 0;
    }
    return typeof (tmp$ = value) === 'number' ? tmp$ : throwCCE();
  };
  KglJs.prototype.getProgramInfoLog_za3rmp$ = function (program) {
    return this.gl_0.getProgramInfoLog(program);
  };
  KglJs.prototype.getShaderInfoLog_za3rmp$ = function (shaderId) {
    return this.gl_0.getShaderInfoLog(shaderId);
  };
  KglJs.prototype.getProgramParameter_wn2dyp$ = function (program, pname) {
    var tmp$;
    var value = ensureNotNull(this.gl_0.getProgramParameter(program, pname));
    if (typeof value === 'boolean') {
      return value ? 1 : 0;
    }
    return typeof (tmp$ = value) === 'number' ? tmp$ : throwCCE();
  };
  KglJs.prototype.createProgram = function () {
    return this.gl_0.createProgram();
  };
  KglJs.prototype.attachShader_wn2jw4$ = function (programId, shaderId) {
    this.gl_0.attachShader(programId, shaderId);
  };
  KglJs.prototype.linkProgram_za3rmp$ = function (programId) {
    this.gl_0.linkProgram(programId);
  };
  KglJs.prototype.useProgram_za3rmp$ = function (programId) {
    this.gl_0.useProgram(programId);
  };
  KglJs.prototype.getUniformLocation_hwpqgh$ = function (programId, name) {
    return this.gl_0.getUniformLocation(programId, name);
  };
  KglJs.prototype.getAttribLocation_hwpqgh$ = function (programId, name) {
    return this.gl_0.getAttribLocation(programId, name);
  };
  KglJs.prototype.bindAttribLocation_piykpr$ = function (programId, index, name) {
    this.gl_0.bindAttribLocation(programId, index, name);
  };
  KglJs.prototype.enableVertexAttribArray_za3lpa$ = function (location) {
    this.gl_0.enableVertexAttribArray(location);
  };
  KglJs.prototype.disableVertexAttribArray_za3lpa$ = function (location) {
    this.gl_0.disableVertexAttribArray(location);
  };
  KglJs.prototype.enable_za3lpa$ = function (cap) {
    this.gl_0.enable(cap);
  };
  KglJs.prototype.disable_za3lpa$ = function (cap) {
    this.gl_0.disable(cap);
  };
  KglJs.prototype.createBuffer = function () {
    var tmp$;
    tmp$ = this.gl_0.createBuffer();
    if (tmp$ == null) {
      throw Exception_init();
    }
    return tmp$;
  };
  KglJs.prototype.createBuffers_za3lpa$ = function (count) {
    var array = Array_0(count);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      var tmp$_0;
      tmp$_0 = this.gl_0.createBuffer();
      if (tmp$_0 == null) {
        throw Exception_init();
      }
      array[i] = tmp$_0;
    }
    return array;
  };
  KglJs.prototype.bindBuffer_6t2rgq$ = function (target, bufferId) {
    this.gl_0.bindBuffer(target, bufferId);
  };
  function KglJs$bufferData$lambda(this$KglJs, closure$target, closure$usage) {
    return function (glBuffer) {
      this$KglJs.gl_0.bufferData(closure$target, glBuffer, closure$usage);
      return Unit;
    };
  }
  KglJs.prototype.bufferData_8en9n9$$default = function (target, sourceData, size, usage, offset) {
    sourceData.withGlBuffer_5tvim5$(offset, KglJs$bufferData$lambda(this, target, usage));
  };
  KglJs.prototype.deleteBuffer_za3rmp$ = function (buffer) {
    this.gl_0.deleteBuffer(buffer);
  };
  KglJs.prototype.vertexAttribPointer_owihk5$ = function (location, size, type, normalized, stride, offset) {
    this.gl_0.vertexAttribPointer(location, size, type, normalized, stride, offset);
  };
  KglJs.prototype.uniform1f_rvcsvw$ = function (location, f) {
    this.gl_0.uniform1f(location, f);
  };
  KglJs.prototype.uniform1i_wn2dyp$ = function (location, i) {
    this.gl_0.uniform1i(location, i);
  };
  KglJs.prototype.uniform2f_zcqyrj$ = function (location, x, y) {
    this.gl_0.uniform2f(location, x, y);
  };
  KglJs.prototype.uniform2i_47d3mp$ = function (location, x, y) {
    this.gl_0.uniform2i(location, x, y);
  };
  KglJs.prototype.uniform3f_ig0gt8$ = function (location, x, y, z) {
    this.gl_0.uniform3f(location, x, y, z);
  };
  KglJs.prototype.uniform3fv_8iqxz7$ = function (location, value) {
    this.gl_0.uniform3fv(location, value);
  };
  KglJs.prototype.uniform3i_ab551r$ = function (location, x, y, z) {
    this.gl_0.uniform3i(location, x, y, z);
  };
  KglJs.prototype.uniform4f_k644h$ = function (location, x, y, z, w) {
    this.gl_0.uniform4f(location, x, y, z, w);
  };
  KglJs.prototype.uniform4i_tiwvvj$ = function (location, x, y, z, w) {
    this.gl_0.uniform4i(location, x, y, z, w);
  };
  KglJs.prototype.uniformMatrix3fv_fzejoa$ = function (location, transpose, value) {
    this.gl_0.uniformMatrix3fv(location, transpose, value);
  };
  KglJs.prototype.uniformMatrix4fv_fzejoa$ = function (location, transpose, value) {
    this.gl_0.uniformMatrix4fv(location, transpose, value);
  };
  KglJs.prototype.viewport_tjonv8$ = function (x, y, width, height) {
    this.gl_0.viewport(x, y, width, height);
  };
  KglJs.prototype.clear_za3lpa$ = function (mask) {
    this.gl_0.clear(mask);
  };
  KglJs.prototype.clearColor_7b5o5w$ = function (r, g, b, a) {
    this.gl_0.clearColor(r, g, b, a);
  };
  KglJs.prototype.blendFunc_vux9f0$ = function (sFactor, dFactor) {
    this.gl_0.blendFunc(sFactor, dFactor);
  };
  KglJs.prototype.cullFace_za3lpa$ = function (mode) {
    this.gl_0.cullFace(mode);
  };
  KglJs.prototype.createTexture = function () {
    var tmp$;
    tmp$ = this.gl_0.createTexture();
    if (tmp$ == null) {
      throw Exception_init();
    }
    return tmp$;
  };
  KglJs.prototype.createTextures_za3lpa$ = function (n) {
    var array = Array_0(n);
    var tmp$;
    tmp$ = array.length - 1 | 0;
    for (var i = 0; i <= tmp$; i++) {
      var tmp$_0;
      tmp$_0 = this.gl_0.createTexture();
      if (tmp$_0 == null) {
        throw Exception_init();
      }
      array[i] = tmp$_0;
    }
    return array;
  };
  KglJs.prototype.deleteTexture_za3rmp$ = function (texture) {
    this.gl_0.deleteTexture(texture);
  };
  KglJs.prototype.texImage2D_ot366o$ = function (target, level, internalFormat, border, resource) {
    this.gl_0.texImage2D(target, level, internalFormat, 6408, 5121, resource.image);
  };
  function KglJs$texImage2D$lambda(this$KglJs, closure$target, closure$level, closure$internalFormat, closure$width, closure$height, closure$border, closure$format, closure$type) {
    return function (glBuffer) {
      this$KglJs.gl_0.texImage2D(closure$target, closure$level, closure$internalFormat, closure$width, closure$height, closure$border, closure$format, closure$type, glBuffer);
      return Unit;
    };
  }
  KglJs.prototype.texImage2D_e7c6np$$default = function (target, level, internalFormat, width, height, border, format, type, buffer, offset) {
    buffer.withGlBuffer_5tvim5$(offset, KglJs$texImage2D$lambda(this, target, level, internalFormat, width, height, border, format, type));
  };
  KglJs.prototype.activeTexture_za3lpa$ = function (texture) {
    this.gl_0.activeTexture(texture);
  };
  KglJs.prototype.bindTexture_6t2rgq$ = function (target, texture) {
    this.gl_0.bindTexture(target, texture);
  };
  KglJs.prototype.generateMipmap_za3lpa$ = function (target) {
    this.gl_0.generateMipmap(target);
  };
  KglJs.prototype.texParameteri_qt1dr2$ = function (target, pname, value) {
    this.gl_0.texParameteri(target, pname, value);
  };
  KglJs.prototype.createVertexArray = function () {
    var tmp$, tmp$_0;
    tmp$_0 = (Kotlin.isType(tmp$ = this.gl_0, WebGL2RenderingContext) ? tmp$ : throwCCE()).createVertexArray();
    if (tmp$_0 == null) {
      throw Exception_init();
    }
    return tmp$_0;
  };
  KglJs.prototype.bindVertexArray_s8jyv4$ = function (vertexArrayObject) {
    var tmp$;
    (Kotlin.isType(tmp$ = this.gl_0, WebGL2RenderingContext) ? tmp$ : throwCCE()).bindVertexArray(vertexArrayObject);
  };
  KglJs.prototype.deleteVertexArray_za3rmp$ = function (vertexArrayObject) {
    var tmp$;
    (Kotlin.isType(tmp$ = this.gl_0, WebGL2RenderingContext) ? tmp$ : throwCCE()).deleteVertexArray(vertexArrayObject);
  };
  KglJs.prototype.drawArrays_qt1dr2$ = function (mode, first, count) {
    this.gl_0.drawArrays(mode, first, count);
  };
  KglJs.prototype.getError = function () {
    return this.gl_0.getError();
  };
  KglJs.prototype.finish = function () {
    this.gl_0.finish();
  };
  KglJs.prototype.bindFramebuffer_6t2rgq$ = function (target, framebuffer) {
    this.gl_0.bindFramebuffer(target, framebuffer);
  };
  KglJs.prototype.createFramebuffer = function () {
    var tmp$;
    tmp$ = this.gl_0.createFramebuffer();
    if (tmp$ == null) {
      throw Exception_init();
    }
    return tmp$;
  };
  KglJs.prototype.deleteFramebuffer_za3rmp$ = function (framebuffer) {
    this.gl_0.deleteFramebuffer(framebuffer);
  };
  KglJs.prototype.checkFramebufferStatus_za3lpa$ = function (target) {
    return this.gl_0.checkFramebufferStatus(target);
  };
  KglJs.prototype.framebufferTexture2D_b9tebp$ = function (target, attachment, textarget, texture, level) {
    this.gl_0.framebufferTexture2D(target, attachment, textarget, texture, level);
  };
  KglJs.prototype.isFramebuffer_za3rmp$ = function (framebuffer) {
    return this.gl_0.isFramebuffer(framebuffer);
  };
  KglJs.prototype.bindRenderbuffer_6t2rgq$ = function (target, renderbuffer) {
    this.gl_0.bindRenderbuffer(target, renderbuffer);
  };
  KglJs.prototype.createRenderbuffer = function () {
    var tmp$;
    tmp$ = this.gl_0.createRenderbuffer();
    if (tmp$ == null) {
      throw Exception_init();
    }
    return tmp$;
  };
  KglJs.prototype.deleteRenderbuffer_za3rmp$ = function (renderbuffer) {
    this.gl_0.deleteRenderbuffer(renderbuffer);
  };
  KglJs.prototype.framebufferRenderbuffer_tjotsn$ = function (target, attachment, renderbuffertarget, renderbuffer) {
    this.gl_0.framebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
  };
  KglJs.prototype.isRenderbuffer_za3rmp$ = function (renderbuffer) {
    return this.gl_0.isRenderbuffer(renderbuffer);
  };
  KglJs.prototype.renderbufferStorage_tjonv8$ = function (target, internalformat, width, height) {
    this.gl_0.renderbufferStorage(target, internalformat, width, height);
  };
  function KglJs$readPixels$lambda(this$KglJs, closure$x, closure$y, closure$width, closure$height, closure$format, closure$type) {
    return function (glBuffer) {
      this$KglJs.gl_0.readPixels(closure$x, closure$y, closure$width, closure$height, closure$format, closure$type, glBuffer);
      return Unit;
    };
  }
  KglJs.prototype.readPixels_idctqj$$default = function (x, y, width, height, format, type, buffer, offset) {
    buffer.withGlBuffer_5tvim5$(offset, KglJs$readPixels$lambda(this, x, y, width, height, format, type));
  };
  KglJs.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'KglJs',
    interfaces: [Kgl]
  };
  function TextureResource(image) {
    this.image = image;
  }
  TextureResource.prototype.disposeInner = function () {
  };
  TextureResource.$metadata$ = {
    kind: Kind_CLASS,
    simpleName: 'TextureResource',
    interfaces: []
  };
  function dispose($receiver) {
    $receiver.disposeInner();
  }
  var package$com = _.com || (_.com = {});
  var package$danielgergely = package$com.danielgergely || (package$com.danielgergely = {});
  var package$kgl = package$danielgergely.kgl || (package$danielgergely.kgl = {});
  package$kgl.allocate_za3lpa$ = allocate;
  Object.defineProperty(package$kgl, 'GL_ACTIVE_TEXTURE', {
    get: function () {
      return GL_ACTIVE_TEXTURE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DEPTH_BUFFER_BIT', {
    get: function () {
      return GL_DEPTH_BUFFER_BIT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_BUFFER_BIT', {
    get: function () {
      return GL_STENCIL_BUFFER_BIT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_BUFFER_BIT', {
    get: function () {
      return GL_COLOR_BUFFER_BIT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FALSE', {
    get: function () {
      return GL_FALSE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TRUE', {
    get: function () {
      return GL_TRUE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_POINTS', {
    get: function () {
      return GL_POINTS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LINES', {
    get: function () {
      return GL_LINES;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LINE_LOOP', {
    get: function () {
      return GL_LINE_LOOP;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LINE_STRIP', {
    get: function () {
      return GL_LINE_STRIP;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TRIANGLES', {
    get: function () {
      return GL_TRIANGLES;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TRIANGLE_STRIP', {
    get: function () {
      return GL_TRIANGLE_STRIP;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TRIANGLE_FAN', {
    get: function () {
      return GL_TRIANGLE_FAN;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ZERO', {
    get: function () {
      return GL_ZERO;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ONE', {
    get: function () {
      return GL_ONE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SRC_COLOR', {
    get: function () {
      return GL_SRC_COLOR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ONE_MINUS_SRC_COLOR', {
    get: function () {
      return GL_ONE_MINUS_SRC_COLOR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SRC_ALPHA', {
    get: function () {
      return GL_SRC_ALPHA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ONE_MINUS_SRC_ALPHA', {
    get: function () {
      return GL_ONE_MINUS_SRC_ALPHA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DST_ALPHA', {
    get: function () {
      return GL_DST_ALPHA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ONE_MINUS_DST_ALPHA', {
    get: function () {
      return GL_ONE_MINUS_DST_ALPHA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DST_COLOR', {
    get: function () {
      return GL_DST_COLOR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ONE_MINUS_DST_COLOR', {
    get: function () {
      return GL_ONE_MINUS_DST_COLOR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SRC_ALPHA_SATURATE', {
    get: function () {
      return GL_SRC_ALPHA_SATURATE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FUNC_ADD', {
    get: function () {
      return GL_FUNC_ADD;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BLEND_EQUATION', {
    get: function () {
      return GL_BLEND_EQUATION;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BLEND_EQUATION_RGB', {
    get: function () {
      return GL_BLEND_EQUATION_RGB;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BLEND_EQUATION_ALPHA', {
    get: function () {
      return GL_BLEND_EQUATION_ALPHA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FUNC_SUBTRACT', {
    get: function () {
      return GL_FUNC_SUBTRACT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FUNC_REVERSE_SUBTRACT', {
    get: function () {
      return GL_FUNC_REVERSE_SUBTRACT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BLEND_DST_RGB', {
    get: function () {
      return GL_BLEND_DST_RGB;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BLEND_SRC_RGB', {
    get: function () {
      return GL_BLEND_SRC_RGB;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BLEND_DST_ALPHA', {
    get: function () {
      return GL_BLEND_DST_ALPHA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BLEND_SRC_ALPHA', {
    get: function () {
      return GL_BLEND_SRC_ALPHA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_CONSTANT_COLOR', {
    get: function () {
      return GL_CONSTANT_COLOR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ONE_MINUS_CONSTANT_COLOR', {
    get: function () {
      return GL_ONE_MINUS_CONSTANT_COLOR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_CONSTANT_ALPHA', {
    get: function () {
      return GL_CONSTANT_ALPHA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ONE_MINUS_CONSTANT_ALPHA', {
    get: function () {
      return GL_ONE_MINUS_CONSTANT_ALPHA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BLEND_COLOR', {
    get: function () {
      return GL_BLEND_COLOR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ARRAY_BUFFER', {
    get: function () {
      return GL_ARRAY_BUFFER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ELEMENT_ARRAY_BUFFER', {
    get: function () {
      return GL_ELEMENT_ARRAY_BUFFER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ARRAY_BUFFER_BINDING', {
    get: function () {
      return GL_ARRAY_BUFFER_BINDING;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ELEMENT_ARRAY_BUFFER_BINDING', {
    get: function () {
      return GL_ELEMENT_ARRAY_BUFFER_BINDING;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STREAM_DRAW', {
    get: function () {
      return GL_STREAM_DRAW;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STATIC_DRAW', {
    get: function () {
      return GL_STATIC_DRAW;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DYNAMIC_DRAW', {
    get: function () {
      return GL_DYNAMIC_DRAW;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BUFFER_SIZE', {
    get: function () {
      return GL_BUFFER_SIZE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BUFFER_USAGE', {
    get: function () {
      return GL_BUFFER_USAGE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_CURRENT_VERTEX_ATTRIB', {
    get: function () {
      return GL_CURRENT_VERTEX_ATTRIB;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRONT', {
    get: function () {
      return GL_FRONT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BACK', {
    get: function () {
      return GL_BACK;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRONT_AND_BACK', {
    get: function () {
      return GL_FRONT_AND_BACK;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_2D', {
    get: function () {
      return GL_TEXTURE_2D;
    }
  });
  Object.defineProperty(package$kgl, 'GL_CULL_FACE', {
    get: function () {
      return GL_CULL_FACE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BLEND', {
    get: function () {
      return GL_BLEND;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DITHER', {
    get: function () {
      return GL_DITHER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_TEST', {
    get: function () {
      return GL_STENCIL_TEST;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DEPTH_TEST', {
    get: function () {
      return GL_DEPTH_TEST;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SCISSOR_TEST', {
    get: function () {
      return GL_SCISSOR_TEST;
    }
  });
  Object.defineProperty(package$kgl, 'GL_POLYGON_OFFSET_FILL', {
    get: function () {
      return GL_POLYGON_OFFSET_FILL;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SAMPLE_ALPHA_TO_COVERAGE', {
    get: function () {
      return GL_SAMPLE_ALPHA_TO_COVERAGE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SAMPLE_COVERAGE', {
    get: function () {
      return GL_SAMPLE_COVERAGE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_NO_ERROR', {
    get: function () {
      return GL_NO_ERROR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INVALID_ENUM', {
    get: function () {
      return GL_INVALID_ENUM;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INVALID_VALUE', {
    get: function () {
      return GL_INVALID_VALUE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INVALID_OPERATION', {
    get: function () {
      return GL_INVALID_OPERATION;
    }
  });
  Object.defineProperty(package$kgl, 'GL_OUT_OF_MEMORY', {
    get: function () {
      return GL_OUT_OF_MEMORY;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INVALID_FRAMEBUFFER_OPERATION', {
    get: function () {
      return GL_INVALID_FRAMEBUFFER_OPERATION;
    }
  });
  Object.defineProperty(package$kgl, 'GL_CW', {
    get: function () {
      return GL_CW;
    }
  });
  Object.defineProperty(package$kgl, 'GL_CCW', {
    get: function () {
      return GL_CCW;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LINE_WIDTH', {
    get: function () {
      return GL_LINE_WIDTH;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ALIASED_POINT_SIZE_RANGE', {
    get: function () {
      return GL_ALIASED_POINT_SIZE_RANGE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ALIASED_LINE_WIDTH_RANGE', {
    get: function () {
      return GL_ALIASED_LINE_WIDTH_RANGE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_CULL_FACE_MODE', {
    get: function () {
      return GL_CULL_FACE_MODE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRONT_FACE', {
    get: function () {
      return GL_FRONT_FACE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DEPTH_RANGE', {
    get: function () {
      return GL_DEPTH_RANGE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DEPTH_WRITEMASK', {
    get: function () {
      return GL_DEPTH_WRITEMASK;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DEPTH_CLEAR_VALUE', {
    get: function () {
      return GL_DEPTH_CLEAR_VALUE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DEPTH_FUNC', {
    get: function () {
      return GL_DEPTH_FUNC;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_CLEAR_VALUE', {
    get: function () {
      return GL_STENCIL_CLEAR_VALUE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_FUNC', {
    get: function () {
      return GL_STENCIL_FUNC;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_FAIL', {
    get: function () {
      return GL_STENCIL_FAIL;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_PASS_DEPTH_FAIL', {
    get: function () {
      return GL_STENCIL_PASS_DEPTH_FAIL;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_PASS_DEPTH_PASS', {
    get: function () {
      return GL_STENCIL_PASS_DEPTH_PASS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_REF', {
    get: function () {
      return GL_STENCIL_REF;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_VALUE_MASK', {
    get: function () {
      return GL_STENCIL_VALUE_MASK;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_WRITEMASK', {
    get: function () {
      return GL_STENCIL_WRITEMASK;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_BACK_FUNC', {
    get: function () {
      return GL_STENCIL_BACK_FUNC;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_BACK_FAIL', {
    get: function () {
      return GL_STENCIL_BACK_FAIL;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_BACK_PASS_DEPTH_FAIL', {
    get: function () {
      return GL_STENCIL_BACK_PASS_DEPTH_FAIL;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_BACK_PASS_DEPTH_PASS', {
    get: function () {
      return GL_STENCIL_BACK_PASS_DEPTH_PASS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_BACK_REF', {
    get: function () {
      return GL_STENCIL_BACK_REF;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_BACK_VALUE_MASK', {
    get: function () {
      return GL_STENCIL_BACK_VALUE_MASK;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_BACK_WRITEMASK', {
    get: function () {
      return GL_STENCIL_BACK_WRITEMASK;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VIEWPORT', {
    get: function () {
      return GL_VIEWPORT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SCISSOR_BOX', {
    get: function () {
      return GL_SCISSOR_BOX;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_CLEAR_VALUE', {
    get: function () {
      return GL_COLOR_CLEAR_VALUE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_WRITEMASK', {
    get: function () {
      return GL_COLOR_WRITEMASK;
    }
  });
  Object.defineProperty(package$kgl, 'GL_UNPACK_ALIGNMENT', {
    get: function () {
      return GL_UNPACK_ALIGNMENT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_PACK_ALIGNMENT', {
    get: function () {
      return GL_PACK_ALIGNMENT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MAX_TEXTURE_SIZE', {
    get: function () {
      return GL_MAX_TEXTURE_SIZE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MAX_VIEWPORT_DIMS', {
    get: function () {
      return GL_MAX_VIEWPORT_DIMS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SUBPIXEL_BITS', {
    get: function () {
      return GL_SUBPIXEL_BITS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RED_BITS', {
    get: function () {
      return GL_RED_BITS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_GREEN_BITS', {
    get: function () {
      return GL_GREEN_BITS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BLUE_BITS', {
    get: function () {
      return GL_BLUE_BITS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ALPHA_BITS', {
    get: function () {
      return GL_ALPHA_BITS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DEPTH_BITS', {
    get: function () {
      return GL_DEPTH_BITS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_BITS', {
    get: function () {
      return GL_STENCIL_BITS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_POLYGON_OFFSET_UNITS', {
    get: function () {
      return GL_POLYGON_OFFSET_UNITS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_POLYGON_OFFSET_FACTOR', {
    get: function () {
      return GL_POLYGON_OFFSET_FACTOR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_BINDING_2D', {
    get: function () {
      return GL_TEXTURE_BINDING_2D;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SAMPLE_BUFFERS', {
    get: function () {
      return GL_SAMPLE_BUFFERS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SAMPLES', {
    get: function () {
      return GL_SAMPLES;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SAMPLE_COVERAGE_VALUE', {
    get: function () {
      return GL_SAMPLE_COVERAGE_VALUE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SAMPLE_COVERAGE_INVERT', {
    get: function () {
      return GL_SAMPLE_COVERAGE_INVERT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_NUM_COMPRESSED_TEXTURE_FORMATS', {
    get: function () {
      return GL_NUM_COMPRESSED_TEXTURE_FORMATS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COMPRESSED_TEXTURE_FORMATS', {
    get: function () {
      return GL_COMPRESSED_TEXTURE_FORMATS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DONT_CARE', {
    get: function () {
      return GL_DONT_CARE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FASTEST', {
    get: function () {
      return GL_FASTEST;
    }
  });
  Object.defineProperty(package$kgl, 'GL_NICEST', {
    get: function () {
      return GL_NICEST;
    }
  });
  Object.defineProperty(package$kgl, 'GL_GENERATE_MIPMAP_HINT', {
    get: function () {
      return GL_GENERATE_MIPMAP_HINT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BYTE', {
    get: function () {
      return GL_BYTE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_UNSIGNED_BYTE', {
    get: function () {
      return GL_UNSIGNED_BYTE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SHORT', {
    get: function () {
      return GL_SHORT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_UNSIGNED_SHORT', {
    get: function () {
      return GL_UNSIGNED_SHORT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INT', {
    get: function () {
      return GL_INT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_UNSIGNED_INT', {
    get: function () {
      return GL_UNSIGNED_INT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FLOAT', {
    get: function () {
      return GL_FLOAT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FIXED', {
    get: function () {
      return GL_FIXED;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_INDEX', {
    get: function () {
      return GL_STENCIL_INDEX;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DEPTH_COMPONENT', {
    get: function () {
      return GL_DEPTH_COMPONENT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RED', {
    get: function () {
      return GL_RED;
    }
  });
  Object.defineProperty(package$kgl, 'GL_GREEN', {
    get: function () {
      return GL_GREEN;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BLUE', {
    get: function () {
      return GL_BLUE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ALPHA', {
    get: function () {
      return GL_ALPHA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RGB', {
    get: function () {
      return GL_RGB;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RGBA', {
    get: function () {
      return GL_RGBA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LUMINANCE', {
    get: function () {
      return GL_LUMINANCE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LUMINANCE_ALPHA', {
    get: function () {
      return GL_LUMINANCE_ALPHA;
    }
  });
  Object.defineProperty(package$kgl, 'GL_UNSIGNED_SHORT_4_4_4_4', {
    get: function () {
      return GL_UNSIGNED_SHORT_4_4_4_4;
    }
  });
  Object.defineProperty(package$kgl, 'GL_UNSIGNED_SHORT_5_5_5_1', {
    get: function () {
      return GL_UNSIGNED_SHORT_5_5_5_1;
    }
  });
  Object.defineProperty(package$kgl, 'GL_UNSIGNED_SHORT_5_6_5', {
    get: function () {
      return GL_UNSIGNED_SHORT_5_6_5;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRAGMENT_SHADER', {
    get: function () {
      return GL_FRAGMENT_SHADER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VERTEX_SHADER', {
    get: function () {
      return GL_VERTEX_SHADER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MAX_VERTEX_ATTRIBS', {
    get: function () {
      return GL_MAX_VERTEX_ATTRIBS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MAX_VERTEX_UNIFORM_VECTORS', {
    get: function () {
      return GL_MAX_VERTEX_UNIFORM_VECTORS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MAX_VARYING_VECTORS', {
    get: function () {
      return GL_MAX_VARYING_VECTORS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS', {
    get: function () {
      return GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS', {
    get: function () {
      return GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MAX_TEXTURE_IMAGE_UNITS', {
    get: function () {
      return GL_MAX_TEXTURE_IMAGE_UNITS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MAX_FRAGMENT_UNIFORM_VECTORS', {
    get: function () {
      return GL_MAX_FRAGMENT_UNIFORM_VECTORS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SHADER_TYPE', {
    get: function () {
      return GL_SHADER_TYPE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DELETE_STATUS', {
    get: function () {
      return GL_DELETE_STATUS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LINK_STATUS', {
    get: function () {
      return GL_LINK_STATUS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VALIDATE_STATUS', {
    get: function () {
      return GL_VALIDATE_STATUS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ATTACHED_SHADERS', {
    get: function () {
      return GL_ATTACHED_SHADERS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ACTIVE_UNIFORMS', {
    get: function () {
      return GL_ACTIVE_UNIFORMS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ACTIVE_UNIFORM_MAX_LENGTH', {
    get: function () {
      return GL_ACTIVE_UNIFORM_MAX_LENGTH;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ACTIVE_ATTRIBUTES', {
    get: function () {
      return GL_ACTIVE_ATTRIBUTES;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ACTIVE_ATTRIBUTE_MAX_LENGTH', {
    get: function () {
      return GL_ACTIVE_ATTRIBUTE_MAX_LENGTH;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SHADING_LANGUAGE_VERSION', {
    get: function () {
      return GL_SHADING_LANGUAGE_VERSION;
    }
  });
  Object.defineProperty(package$kgl, 'GL_CURRENT_PROGRAM', {
    get: function () {
      return GL_CURRENT_PROGRAM;
    }
  });
  Object.defineProperty(package$kgl, 'GL_NEVER', {
    get: function () {
      return GL_NEVER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LESS', {
    get: function () {
      return GL_LESS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_EQUAL', {
    get: function () {
      return GL_EQUAL;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LEQUAL', {
    get: function () {
      return GL_LEQUAL;
    }
  });
  Object.defineProperty(package$kgl, 'GL_GREATER', {
    get: function () {
      return GL_GREATER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_NOTEQUAL', {
    get: function () {
      return GL_NOTEQUAL;
    }
  });
  Object.defineProperty(package$kgl, 'GL_GEQUAL', {
    get: function () {
      return GL_GEQUAL;
    }
  });
  Object.defineProperty(package$kgl, 'GL_ALWAYS', {
    get: function () {
      return GL_ALWAYS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_KEEP', {
    get: function () {
      return GL_KEEP;
    }
  });
  Object.defineProperty(package$kgl, 'GL_REPLACE', {
    get: function () {
      return GL_REPLACE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INCR', {
    get: function () {
      return GL_INCR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DECR', {
    get: function () {
      return GL_DECR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INVERT', {
    get: function () {
      return GL_INVERT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INCR_WRAP', {
    get: function () {
      return GL_INCR_WRAP;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DECR_WRAP', {
    get: function () {
      return GL_DECR_WRAP;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VENDOR', {
    get: function () {
      return GL_VENDOR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RENDERER', {
    get: function () {
      return GL_RENDERER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VERSION', {
    get: function () {
      return GL_VERSION;
    }
  });
  Object.defineProperty(package$kgl, 'GL_EXTENSIONS', {
    get: function () {
      return GL_EXTENSIONS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_NEAREST', {
    get: function () {
      return GL_NEAREST;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LINEAR', {
    get: function () {
      return GL_LINEAR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_NEAREST_MIPMAP_NEAREST', {
    get: function () {
      return GL_NEAREST_MIPMAP_NEAREST;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LINEAR_MIPMAP_NEAREST', {
    get: function () {
      return GL_LINEAR_MIPMAP_NEAREST;
    }
  });
  Object.defineProperty(package$kgl, 'GL_NEAREST_MIPMAP_LINEAR', {
    get: function () {
      return GL_NEAREST_MIPMAP_LINEAR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LINEAR_MIPMAP_LINEAR', {
    get: function () {
      return GL_LINEAR_MIPMAP_LINEAR;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_MAG_FILTER', {
    get: function () {
      return GL_TEXTURE_MAG_FILTER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_MIN_FILTER', {
    get: function () {
      return GL_TEXTURE_MIN_FILTER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_WRAP_S', {
    get: function () {
      return GL_TEXTURE_WRAP_S;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_WRAP_T', {
    get: function () {
      return GL_TEXTURE_WRAP_T;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE', {
    get: function () {
      return GL_TEXTURE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_CUBE_MAP', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_BINDING_CUBE_MAP', {
    get: function () {
      return GL_TEXTURE_BINDING_CUBE_MAP;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_CUBE_MAP_POSITIVE_X', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_POSITIVE_X;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_CUBE_MAP_NEGATIVE_X', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_CUBE_MAP_POSITIVE_Y', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_CUBE_MAP_NEGATIVE_Y', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_CUBE_MAP_POSITIVE_Z', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_POSITIVE_Z;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE_CUBE_MAP_NEGATIVE_Z', {
    get: function () {
      return GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MAX_CUBE_MAP_TEXTURE_SIZE', {
    get: function () {
      return GL_MAX_CUBE_MAP_TEXTURE_SIZE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE0', {
    get: function () {
      return GL_TEXTURE0;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE1', {
    get: function () {
      return GL_TEXTURE1;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE2', {
    get: function () {
      return GL_TEXTURE2;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE3', {
    get: function () {
      return GL_TEXTURE3;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE4', {
    get: function () {
      return GL_TEXTURE4;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE5', {
    get: function () {
      return GL_TEXTURE5;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE6', {
    get: function () {
      return GL_TEXTURE6;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE7', {
    get: function () {
      return GL_TEXTURE7;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE8', {
    get: function () {
      return GL_TEXTURE8;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE9', {
    get: function () {
      return GL_TEXTURE9;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE10', {
    get: function () {
      return GL_TEXTURE10;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE11', {
    get: function () {
      return GL_TEXTURE11;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE12', {
    get: function () {
      return GL_TEXTURE12;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE13', {
    get: function () {
      return GL_TEXTURE13;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE14', {
    get: function () {
      return GL_TEXTURE14;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE15', {
    get: function () {
      return GL_TEXTURE15;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE16', {
    get: function () {
      return GL_TEXTURE16;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE17', {
    get: function () {
      return GL_TEXTURE17;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE18', {
    get: function () {
      return GL_TEXTURE18;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE19', {
    get: function () {
      return GL_TEXTURE19;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE20', {
    get: function () {
      return GL_TEXTURE20;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE21', {
    get: function () {
      return GL_TEXTURE21;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE22', {
    get: function () {
      return GL_TEXTURE22;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE23', {
    get: function () {
      return GL_TEXTURE23;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE24', {
    get: function () {
      return GL_TEXTURE24;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE25', {
    get: function () {
      return GL_TEXTURE25;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE26', {
    get: function () {
      return GL_TEXTURE26;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE27', {
    get: function () {
      return GL_TEXTURE27;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE28', {
    get: function () {
      return GL_TEXTURE28;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE29', {
    get: function () {
      return GL_TEXTURE29;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE30', {
    get: function () {
      return GL_TEXTURE30;
    }
  });
  Object.defineProperty(package$kgl, 'GL_TEXTURE31', {
    get: function () {
      return GL_TEXTURE31;
    }
  });
  Object.defineProperty(package$kgl, 'GL_REPEAT', {
    get: function () {
      return GL_REPEAT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_CLAMP_TO_EDGE', {
    get: function () {
      return GL_CLAMP_TO_EDGE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MIRRORED_REPEAT', {
    get: function () {
      return GL_MIRRORED_REPEAT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FLOAT_VEC2', {
    get: function () {
      return GL_FLOAT_VEC2;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FLOAT_VEC3', {
    get: function () {
      return GL_FLOAT_VEC3;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FLOAT_VEC4', {
    get: function () {
      return GL_FLOAT_VEC4;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INT_VEC2', {
    get: function () {
      return GL_INT_VEC2;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INT_VEC3', {
    get: function () {
      return GL_INT_VEC3;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INT_VEC4', {
    get: function () {
      return GL_INT_VEC4;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BOOL', {
    get: function () {
      return GL_BOOL;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BOOL_VEC2', {
    get: function () {
      return GL_BOOL_VEC2;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BOOL_VEC3', {
    get: function () {
      return GL_BOOL_VEC3;
    }
  });
  Object.defineProperty(package$kgl, 'GL_BOOL_VEC4', {
    get: function () {
      return GL_BOOL_VEC4;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FLOAT_MAT2', {
    get: function () {
      return GL_FLOAT_MAT2;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FLOAT_MAT3', {
    get: function () {
      return GL_FLOAT_MAT3;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FLOAT_MAT4', {
    get: function () {
      return GL_FLOAT_MAT4;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SAMPLER_2D', {
    get: function () {
      return GL_SAMPLER_2D;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SAMPLER_CUBE', {
    get: function () {
      return GL_SAMPLER_CUBE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VERTEX_ATTRIB_ARRAY_ENABLED', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_ENABLED;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VERTEX_ATTRIB_ARRAY_SIZE', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_SIZE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VERTEX_ATTRIB_ARRAY_STRIDE', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_STRIDE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VERTEX_ATTRIB_ARRAY_TYPE', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_TYPE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VERTEX_ATTRIB_ARRAY_NORMALIZED', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_NORMALIZED;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VERTEX_ATTRIB_ARRAY_POINTER', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_POINTER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING', {
    get: function () {
      return GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING;
    }
  });
  Object.defineProperty(package$kgl, 'GL_IMPLEMENTATION_COLOR_READ_TYPE', {
    get: function () {
      return GL_IMPLEMENTATION_COLOR_READ_TYPE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_IMPLEMENTATION_COLOR_READ_FORMAT', {
    get: function () {
      return GL_IMPLEMENTATION_COLOR_READ_FORMAT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COMPILE_STATUS', {
    get: function () {
      return GL_COMPILE_STATUS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_INFO_LOG_LENGTH', {
    get: function () {
      return GL_INFO_LOG_LENGTH;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SHADER_SOURCE_LENGTH', {
    get: function () {
      return GL_SHADER_SOURCE_LENGTH;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SHADER_COMPILER', {
    get: function () {
      return GL_SHADER_COMPILER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_SHADER_BINARY_FORMATS', {
    get: function () {
      return GL_SHADER_BINARY_FORMATS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_NUM_SHADER_BINARY_FORMATS', {
    get: function () {
      return GL_NUM_SHADER_BINARY_FORMATS;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LOW_FLOAT', {
    get: function () {
      return GL_LOW_FLOAT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MEDIUM_FLOAT', {
    get: function () {
      return GL_MEDIUM_FLOAT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_HIGH_FLOAT', {
    get: function () {
      return GL_HIGH_FLOAT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_LOW_INT', {
    get: function () {
      return GL_LOW_INT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_MEDIUM_INT', {
    get: function () {
      return GL_MEDIUM_INT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_HIGH_INT', {
    get: function () {
      return GL_HIGH_INT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRAMEBUFFER', {
    get: function () {
      return GL_FRAMEBUFFER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RENDERBUFFER', {
    get: function () {
      return GL_RENDERBUFFER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RGBA4', {
    get: function () {
      return GL_RGBA4;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RGB5_A1', {
    get: function () {
      return GL_RGB5_A1;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RGB565', {
    get: function () {
      return GL_RGB565;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DEPTH_COMPONENT16', {
    get: function () {
      return GL_DEPTH_COMPONENT16;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRAMEBUFFER_COMPLETE', {
    get: function () {
      return GL_FRAMEBUFFER_COMPLETE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT', {
    get: function () {
      return GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT', {
    get: function () {
      return GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER', {
    get: function () {
      return GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER', {
    get: function () {
      return GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRAMEBUFFER_UNSUPPORTED', {
    get: function () {
      return GL_FRAMEBUFFER_UNSUPPORTED;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE', {
    get: function () {
      return GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE;
    }
  });
  Object.defineProperty(package$kgl, 'GL_FRAMEBUFFER_UNDEFINED', {
    get: function () {
      return GL_FRAMEBUFFER_UNDEFINED;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT0', {
    get: function () {
      return GL_COLOR_ATTACHMENT0;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT1', {
    get: function () {
      return GL_COLOR_ATTACHMENT1;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT2', {
    get: function () {
      return GL_COLOR_ATTACHMENT2;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT3', {
    get: function () {
      return GL_COLOR_ATTACHMENT3;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT4', {
    get: function () {
      return GL_COLOR_ATTACHMENT4;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT5', {
    get: function () {
      return GL_COLOR_ATTACHMENT5;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT6', {
    get: function () {
      return GL_COLOR_ATTACHMENT6;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT7', {
    get: function () {
      return GL_COLOR_ATTACHMENT7;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT8', {
    get: function () {
      return GL_COLOR_ATTACHMENT8;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT9', {
    get: function () {
      return GL_COLOR_ATTACHMENT9;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT10', {
    get: function () {
      return GL_COLOR_ATTACHMENT10;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT11', {
    get: function () {
      return GL_COLOR_ATTACHMENT11;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT12', {
    get: function () {
      return GL_COLOR_ATTACHMENT12;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT13', {
    get: function () {
      return GL_COLOR_ATTACHMENT13;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT14', {
    get: function () {
      return GL_COLOR_ATTACHMENT14;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT15', {
    get: function () {
      return GL_COLOR_ATTACHMENT15;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT16', {
    get: function () {
      return GL_COLOR_ATTACHMENT16;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT17', {
    get: function () {
      return GL_COLOR_ATTACHMENT17;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT18', {
    get: function () {
      return GL_COLOR_ATTACHMENT18;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT19', {
    get: function () {
      return GL_COLOR_ATTACHMENT19;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT20', {
    get: function () {
      return GL_COLOR_ATTACHMENT20;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT21', {
    get: function () {
      return GL_COLOR_ATTACHMENT21;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT22', {
    get: function () {
      return GL_COLOR_ATTACHMENT22;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT23', {
    get: function () {
      return GL_COLOR_ATTACHMENT23;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT24', {
    get: function () {
      return GL_COLOR_ATTACHMENT24;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT25', {
    get: function () {
      return GL_COLOR_ATTACHMENT25;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT26', {
    get: function () {
      return GL_COLOR_ATTACHMENT26;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT27', {
    get: function () {
      return GL_COLOR_ATTACHMENT27;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT28', {
    get: function () {
      return GL_COLOR_ATTACHMENT28;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT29', {
    get: function () {
      return GL_COLOR_ATTACHMENT29;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT30', {
    get: function () {
      return GL_COLOR_ATTACHMENT30;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COLOR_ATTACHMENT31', {
    get: function () {
      return GL_COLOR_ATTACHMENT31;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DEPTH_ATTACHMENT', {
    get: function () {
      return GL_DEPTH_ATTACHMENT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_STENCIL_ATTACHMENT', {
    get: function () {
      return GL_STENCIL_ATTACHMENT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_DEPTH_STENCIL_ATTACHMENT', {
    get: function () {
      return GL_DEPTH_STENCIL_ATTACHMENT;
    }
  });
  Object.defineProperty(package$kgl, 'GL_R8', {
    get: function () {
      return GL_R8;
    }
  });
  Object.defineProperty(package$kgl, 'GL_R16', {
    get: function () {
      return GL_R16;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RG8', {
    get: function () {
      return GL_RG8;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RG16', {
    get: function () {
      return GL_RG16;
    }
  });
  Object.defineProperty(package$kgl, 'GL_R16F', {
    get: function () {
      return GL_R16F;
    }
  });
  Object.defineProperty(package$kgl, 'GL_R32F', {
    get: function () {
      return GL_R32F;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RG16F', {
    get: function () {
      return GL_RG16F;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RG32F', {
    get: function () {
      return GL_RG32F;
    }
  });
  Object.defineProperty(package$kgl, 'GL_R8I', {
    get: function () {
      return GL_R8I;
    }
  });
  Object.defineProperty(package$kgl, 'GL_R8UI', {
    get: function () {
      return GL_R8UI;
    }
  });
  Object.defineProperty(package$kgl, 'GL_R16I', {
    get: function () {
      return GL_R16I;
    }
  });
  Object.defineProperty(package$kgl, 'GL_R16UI', {
    get: function () {
      return GL_R16UI;
    }
  });
  Object.defineProperty(package$kgl, 'GL_R32I', {
    get: function () {
      return GL_R32I;
    }
  });
  Object.defineProperty(package$kgl, 'GL_R32UI', {
    get: function () {
      return GL_R32UI;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RG8I', {
    get: function () {
      return GL_RG8I;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RG8UI', {
    get: function () {
      return GL_RG8UI;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RG16I', {
    get: function () {
      return GL_RG16I;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RG16UI', {
    get: function () {
      return GL_RG16UI;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RG32I', {
    get: function () {
      return GL_RG32I;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RG32UI', {
    get: function () {
      return GL_RG32UI;
    }
  });
  Object.defineProperty(package$kgl, 'GL_RG', {
    get: function () {
      return GL_RG;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COMPRESSED_RED', {
    get: function () {
      return GL_COMPRESSED_RED;
    }
  });
  Object.defineProperty(package$kgl, 'GL_COMPRESSED_RG', {
    get: function () {
      return GL_COMPRESSED_RG;
    }
  });
  package$kgl.Kgl = Kgl;
  package$kgl.Buffer = Buffer;
  package$kgl.FloatBuffer_init_o5v4nz$ = FloatBuffer_init;
  package$kgl.FloatBuffer_init_q3cr5i$ = FloatBuffer_init_0;
  package$kgl.FloatBuffer_init_za3lpa$ = FloatBuffer_init_1;
  package$kgl.FloatBuffer = FloatBuffer;
  package$kgl.ByteBuffer_init_eh0p09$ = ByteBuffer_init;
  package$kgl.ByteBuffer_init_fqrh44$ = ByteBuffer_init_0;
  package$kgl.ByteBuffer_init_za3lpa$ = ByteBuffer_init_1;
  package$kgl.ByteBuffer = ByteBuffer;
  package$kgl.KglJs = KglJs;
  package$kgl.TextureResource = TextureResource;
  package$kgl.dispose_cd8zb3$ = dispose;
  KglJs.prototype.uniform_rvcsvw$ = Kgl.prototype.uniform_rvcsvw$;
  KglJs.prototype.uniform_zcqyrj$ = Kgl.prototype.uniform_zcqyrj$;
  KglJs.prototype.uniform_ig0gt8$ = Kgl.prototype.uniform_ig0gt8$;
  KglJs.prototype.uniform_k644h$ = Kgl.prototype.uniform_k644h$;
  KglJs.prototype.uniform_wn2dyp$ = Kgl.prototype.uniform_wn2dyp$;
  KglJs.prototype.uniform_47d3mp$ = Kgl.prototype.uniform_47d3mp$;
  KglJs.prototype.uniform_ab551r$ = Kgl.prototype.uniform_ab551r$;
  KglJs.prototype.uniform_tiwvvj$ = Kgl.prototype.uniform_tiwvvj$;
  KglJs.prototype.bufferData_8en9n9$ = Kgl.prototype.bufferData_8en9n9$;
  KglJs.prototype.texImage2D_e7c6np$ = Kgl.prototype.texImage2D_e7c6np$;
  KglJs.prototype.readPixels_idctqj$ = Kgl.prototype.readPixels_idctqj$;
  GL_ACTIVE_TEXTURE = 34016;
  GL_DEPTH_BUFFER_BIT = 256;
  GL_STENCIL_BUFFER_BIT = 1024;
  GL_COLOR_BUFFER_BIT = 16384;
  GL_FALSE = 0;
  GL_TRUE = 1;
  GL_POINTS = 0;
  GL_LINES = 1;
  GL_LINE_LOOP = 2;
  GL_LINE_STRIP = 3;
  GL_TRIANGLES = 4;
  GL_TRIANGLE_STRIP = 5;
  GL_TRIANGLE_FAN = 6;
  GL_ZERO = 0;
  GL_ONE = 1;
  GL_SRC_COLOR = 768;
  GL_ONE_MINUS_SRC_COLOR = 769;
  GL_SRC_ALPHA = 770;
  GL_ONE_MINUS_SRC_ALPHA = 771;
  GL_DST_ALPHA = 772;
  GL_ONE_MINUS_DST_ALPHA = 773;
  GL_DST_COLOR = 774;
  GL_ONE_MINUS_DST_COLOR = 775;
  GL_SRC_ALPHA_SATURATE = 776;
  GL_FUNC_ADD = 32774;
  GL_BLEND_EQUATION = 32777;
  GL_BLEND_EQUATION_RGB = 32777;
  GL_BLEND_EQUATION_ALPHA = 34877;
  GL_FUNC_SUBTRACT = 32778;
  GL_FUNC_REVERSE_SUBTRACT = 32779;
  GL_BLEND_DST_RGB = 32968;
  GL_BLEND_SRC_RGB = 32969;
  GL_BLEND_DST_ALPHA = 32970;
  GL_BLEND_SRC_ALPHA = 32971;
  GL_CONSTANT_COLOR = 32769;
  GL_ONE_MINUS_CONSTANT_COLOR = 32770;
  GL_CONSTANT_ALPHA = 32771;
  GL_ONE_MINUS_CONSTANT_ALPHA = 32772;
  GL_BLEND_COLOR = 32773;
  GL_ARRAY_BUFFER = 34962;
  GL_ELEMENT_ARRAY_BUFFER = 34963;
  GL_ARRAY_BUFFER_BINDING = 34964;
  GL_ELEMENT_ARRAY_BUFFER_BINDING = 34965;
  GL_STREAM_DRAW = 35040;
  GL_STATIC_DRAW = 35044;
  GL_DYNAMIC_DRAW = 35048;
  GL_BUFFER_SIZE = 34660;
  GL_BUFFER_USAGE = 34661;
  GL_CURRENT_VERTEX_ATTRIB = 34342;
  GL_FRONT = 1028;
  GL_BACK = 1029;
  GL_FRONT_AND_BACK = 1032;
  GL_TEXTURE_2D = 3553;
  GL_CULL_FACE = 2884;
  GL_BLEND = 3042;
  GL_DITHER = 3024;
  GL_STENCIL_TEST = 2960;
  GL_DEPTH_TEST = 2929;
  GL_SCISSOR_TEST = 3089;
  GL_POLYGON_OFFSET_FILL = 32823;
  GL_SAMPLE_ALPHA_TO_COVERAGE = 32926;
  GL_SAMPLE_COVERAGE = 32928;
  GL_NO_ERROR = 0;
  GL_INVALID_ENUM = 1280;
  GL_INVALID_VALUE = 1281;
  GL_INVALID_OPERATION = 1282;
  GL_OUT_OF_MEMORY = 1285;
  GL_INVALID_FRAMEBUFFER_OPERATION = 1286;
  GL_CW = 2304;
  GL_CCW = 2305;
  GL_LINE_WIDTH = 2849;
  GL_ALIASED_POINT_SIZE_RANGE = 33901;
  GL_ALIASED_LINE_WIDTH_RANGE = 33902;
  GL_CULL_FACE_MODE = 2885;
  GL_FRONT_FACE = 2886;
  GL_DEPTH_RANGE = 2928;
  GL_DEPTH_WRITEMASK = 2930;
  GL_DEPTH_CLEAR_VALUE = 2931;
  GL_DEPTH_FUNC = 2932;
  GL_STENCIL_CLEAR_VALUE = 2961;
  GL_STENCIL_FUNC = 2962;
  GL_STENCIL_FAIL = 2964;
  GL_STENCIL_PASS_DEPTH_FAIL = 2965;
  GL_STENCIL_PASS_DEPTH_PASS = 2966;
  GL_STENCIL_REF = 2967;
  GL_STENCIL_VALUE_MASK = 2963;
  GL_STENCIL_WRITEMASK = 2968;
  GL_STENCIL_BACK_FUNC = 34816;
  GL_STENCIL_BACK_FAIL = 34817;
  GL_STENCIL_BACK_PASS_DEPTH_FAIL = 34818;
  GL_STENCIL_BACK_PASS_DEPTH_PASS = 34819;
  GL_STENCIL_BACK_REF = 36003;
  GL_STENCIL_BACK_VALUE_MASK = 36004;
  GL_STENCIL_BACK_WRITEMASK = 36005;
  GL_VIEWPORT = 2978;
  GL_SCISSOR_BOX = 3088;
  GL_COLOR_CLEAR_VALUE = 3106;
  GL_COLOR_WRITEMASK = 3107;
  GL_UNPACK_ALIGNMENT = 3317;
  GL_PACK_ALIGNMENT = 3333;
  GL_MAX_TEXTURE_SIZE = 3379;
  GL_MAX_VIEWPORT_DIMS = 3386;
  GL_SUBPIXEL_BITS = 3408;
  GL_RED_BITS = 3410;
  GL_GREEN_BITS = 3411;
  GL_BLUE_BITS = 3412;
  GL_ALPHA_BITS = 3413;
  GL_DEPTH_BITS = 3414;
  GL_STENCIL_BITS = 3415;
  GL_POLYGON_OFFSET_UNITS = 10752;
  GL_POLYGON_OFFSET_FACTOR = 32824;
  GL_TEXTURE_BINDING_2D = 32873;
  GL_SAMPLE_BUFFERS = 32936;
  GL_SAMPLES = 32937;
  GL_SAMPLE_COVERAGE_VALUE = 32938;
  GL_SAMPLE_COVERAGE_INVERT = 32939;
  GL_NUM_COMPRESSED_TEXTURE_FORMATS = 34466;
  GL_COMPRESSED_TEXTURE_FORMATS = 34467;
  GL_DONT_CARE = 4352;
  GL_FASTEST = 4353;
  GL_NICEST = 4354;
  GL_GENERATE_MIPMAP_HINT = 33170;
  GL_BYTE = 5120;
  GL_UNSIGNED_BYTE = 5121;
  GL_SHORT = 5122;
  GL_UNSIGNED_SHORT = 5123;
  GL_INT = 5124;
  GL_UNSIGNED_INT = 5125;
  GL_FLOAT = 5126;
  GL_FIXED = 5132;
  GL_STENCIL_INDEX = 6401;
  GL_DEPTH_COMPONENT = 6402;
  GL_RED = 6403;
  GL_GREEN = 6404;
  GL_BLUE = 6405;
  GL_ALPHA = 6406;
  GL_RGB = 6407;
  GL_RGBA = 6408;
  GL_LUMINANCE = 6409;
  GL_LUMINANCE_ALPHA = 6410;
  GL_UNSIGNED_SHORT_4_4_4_4 = 32819;
  GL_UNSIGNED_SHORT_5_5_5_1 = 32820;
  GL_UNSIGNED_SHORT_5_6_5 = 33635;
  GL_FRAGMENT_SHADER = 35632;
  GL_VERTEX_SHADER = 35633;
  GL_MAX_VERTEX_ATTRIBS = 34921;
  GL_MAX_VERTEX_UNIFORM_VECTORS = 36347;
  GL_MAX_VARYING_VECTORS = 36348;
  GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 35661;
  GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 35660;
  GL_MAX_TEXTURE_IMAGE_UNITS = 34930;
  GL_MAX_FRAGMENT_UNIFORM_VECTORS = 36349;
  GL_SHADER_TYPE = 35663;
  GL_DELETE_STATUS = 35712;
  GL_LINK_STATUS = 35714;
  GL_VALIDATE_STATUS = 35715;
  GL_ATTACHED_SHADERS = 35717;
  GL_ACTIVE_UNIFORMS = 35718;
  GL_ACTIVE_UNIFORM_MAX_LENGTH = 35719;
  GL_ACTIVE_ATTRIBUTES = 35721;
  GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 35722;
  GL_SHADING_LANGUAGE_VERSION = 35724;
  GL_CURRENT_PROGRAM = 35725;
  GL_NEVER = 512;
  GL_LESS = 513;
  GL_EQUAL = 514;
  GL_LEQUAL = 515;
  GL_GREATER = 516;
  GL_NOTEQUAL = 517;
  GL_GEQUAL = 518;
  GL_ALWAYS = 519;
  GL_KEEP = 7680;
  GL_REPLACE = 7681;
  GL_INCR = 7682;
  GL_DECR = 7683;
  GL_INVERT = 5386;
  GL_INCR_WRAP = 34055;
  GL_DECR_WRAP = 34056;
  GL_VENDOR = 7936;
  GL_RENDERER = 7937;
  GL_VERSION = 7938;
  GL_EXTENSIONS = 7939;
  GL_NEAREST = 9728;
  GL_LINEAR = 9729;
  GL_NEAREST_MIPMAP_NEAREST = 9984;
  GL_LINEAR_MIPMAP_NEAREST = 9985;
  GL_NEAREST_MIPMAP_LINEAR = 9986;
  GL_LINEAR_MIPMAP_LINEAR = 9987;
  GL_TEXTURE_MAG_FILTER = 10240;
  GL_TEXTURE_MIN_FILTER = 10241;
  GL_TEXTURE_WRAP_S = 10242;
  GL_TEXTURE_WRAP_T = 10243;
  GL_TEXTURE = 5890;
  GL_TEXTURE_CUBE_MAP = 34067;
  GL_TEXTURE_BINDING_CUBE_MAP = 34068;
  GL_TEXTURE_CUBE_MAP_POSITIVE_X = 34069;
  GL_TEXTURE_CUBE_MAP_NEGATIVE_X = 34070;
  GL_TEXTURE_CUBE_MAP_POSITIVE_Y = 34071;
  GL_TEXTURE_CUBE_MAP_NEGATIVE_Y = 34072;
  GL_TEXTURE_CUBE_MAP_POSITIVE_Z = 34073;
  GL_TEXTURE_CUBE_MAP_NEGATIVE_Z = 34074;
  GL_MAX_CUBE_MAP_TEXTURE_SIZE = 34076;
  GL_TEXTURE0 = 33984;
  GL_TEXTURE1 = 33985;
  GL_TEXTURE2 = 33986;
  GL_TEXTURE3 = 33987;
  GL_TEXTURE4 = 33988;
  GL_TEXTURE5 = 33989;
  GL_TEXTURE6 = 33990;
  GL_TEXTURE7 = 33991;
  GL_TEXTURE8 = 33992;
  GL_TEXTURE9 = 33993;
  GL_TEXTURE10 = 33994;
  GL_TEXTURE11 = 33995;
  GL_TEXTURE12 = 33996;
  GL_TEXTURE13 = 33997;
  GL_TEXTURE14 = 33998;
  GL_TEXTURE15 = 33999;
  GL_TEXTURE16 = 34000;
  GL_TEXTURE17 = 34001;
  GL_TEXTURE18 = 34002;
  GL_TEXTURE19 = 34003;
  GL_TEXTURE20 = 34004;
  GL_TEXTURE21 = 34005;
  GL_TEXTURE22 = 34006;
  GL_TEXTURE23 = 34007;
  GL_TEXTURE24 = 34008;
  GL_TEXTURE25 = 34009;
  GL_TEXTURE26 = 34010;
  GL_TEXTURE27 = 34011;
  GL_TEXTURE28 = 34012;
  GL_TEXTURE29 = 34013;
  GL_TEXTURE30 = 34014;
  GL_TEXTURE31 = 34015;
  GL_REPEAT = 10497;
  GL_CLAMP_TO_EDGE = 33071;
  GL_MIRRORED_REPEAT = 33648;
  GL_FLOAT_VEC2 = 35664;
  GL_FLOAT_VEC3 = 35665;
  GL_FLOAT_VEC4 = 35666;
  GL_INT_VEC2 = 35667;
  GL_INT_VEC3 = 35668;
  GL_INT_VEC4 = 35669;
  GL_BOOL = 35670;
  GL_BOOL_VEC2 = 35671;
  GL_BOOL_VEC3 = 35672;
  GL_BOOL_VEC4 = 35673;
  GL_FLOAT_MAT2 = 35674;
  GL_FLOAT_MAT3 = 35675;
  GL_FLOAT_MAT4 = 35676;
  GL_SAMPLER_2D = 35678;
  GL_SAMPLER_CUBE = 35680;
  GL_VERTEX_ATTRIB_ARRAY_ENABLED = 34338;
  GL_VERTEX_ATTRIB_ARRAY_SIZE = 34339;
  GL_VERTEX_ATTRIB_ARRAY_STRIDE = 34340;
  GL_VERTEX_ATTRIB_ARRAY_TYPE = 34341;
  GL_VERTEX_ATTRIB_ARRAY_NORMALIZED = 34922;
  GL_VERTEX_ATTRIB_ARRAY_POINTER = 34373;
  GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 34975;
  GL_IMPLEMENTATION_COLOR_READ_TYPE = 35738;
  GL_IMPLEMENTATION_COLOR_READ_FORMAT = 35739;
  GL_COMPILE_STATUS = 35713;
  GL_INFO_LOG_LENGTH = 35716;
  GL_SHADER_SOURCE_LENGTH = 35720;
  GL_SHADER_COMPILER = 36346;
  GL_SHADER_BINARY_FORMATS = 36344;
  GL_NUM_SHADER_BINARY_FORMATS = 36345;
  GL_LOW_FLOAT = 36336;
  GL_MEDIUM_FLOAT = 36337;
  GL_HIGH_FLOAT = 36338;
  GL_LOW_INT = 36339;
  GL_MEDIUM_INT = 36340;
  GL_HIGH_INT = 36341;
  GL_FRAMEBUFFER = 36160;
  GL_RENDERBUFFER = 36161;
  GL_RGBA4 = 32854;
  GL_RGB5_A1 = 32855;
  GL_RGB565 = 36194;
  GL_DEPTH_COMPONENT16 = 33189;
  GL_FRAMEBUFFER_COMPLETE = 36053;
  GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 36054;
  GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
  GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
  GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 36060;
  GL_FRAMEBUFFER_UNSUPPORTED = 36061;
  GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE = 36182;
  GL_FRAMEBUFFER_UNDEFINED = 33305;
  GL_COLOR_ATTACHMENT0 = 36064;
  GL_COLOR_ATTACHMENT1 = 36065;
  GL_COLOR_ATTACHMENT2 = 36066;
  GL_COLOR_ATTACHMENT3 = 36067;
  GL_COLOR_ATTACHMENT4 = 36068;
  GL_COLOR_ATTACHMENT5 = 36069;
  GL_COLOR_ATTACHMENT6 = 36070;
  GL_COLOR_ATTACHMENT7 = 36071;
  GL_COLOR_ATTACHMENT8 = 36072;
  GL_COLOR_ATTACHMENT9 = 36073;
  GL_COLOR_ATTACHMENT10 = 36074;
  GL_COLOR_ATTACHMENT11 = 36075;
  GL_COLOR_ATTACHMENT12 = 36076;
  GL_COLOR_ATTACHMENT13 = 36077;
  GL_COLOR_ATTACHMENT14 = 36078;
  GL_COLOR_ATTACHMENT15 = 36079;
  GL_COLOR_ATTACHMENT16 = 36080;
  GL_COLOR_ATTACHMENT17 = 36081;
  GL_COLOR_ATTACHMENT18 = 36082;
  GL_COLOR_ATTACHMENT19 = 36083;
  GL_COLOR_ATTACHMENT20 = 36084;
  GL_COLOR_ATTACHMENT21 = 36085;
  GL_COLOR_ATTACHMENT22 = 36086;
  GL_COLOR_ATTACHMENT23 = 36087;
  GL_COLOR_ATTACHMENT24 = 36088;
  GL_COLOR_ATTACHMENT25 = 36089;
  GL_COLOR_ATTACHMENT26 = 36090;
  GL_COLOR_ATTACHMENT27 = 36091;
  GL_COLOR_ATTACHMENT28 = 36092;
  GL_COLOR_ATTACHMENT29 = 36093;
  GL_COLOR_ATTACHMENT30 = 36094;
  GL_COLOR_ATTACHMENT31 = 36095;
  GL_DEPTH_ATTACHMENT = 36096;
  GL_STENCIL_ATTACHMENT = 36128;
  GL_DEPTH_STENCIL_ATTACHMENT = 33306;
  GL_R8 = 33321;
  GL_R16 = 33322;
  GL_RG8 = 33323;
  GL_RG16 = 33324;
  GL_R16F = 33325;
  GL_R32F = 33326;
  GL_RG16F = 33327;
  GL_RG32F = 33328;
  GL_R8I = 33329;
  GL_R8UI = 33330;
  GL_R16I = 33331;
  GL_R16UI = 33332;
  GL_R32I = 33333;
  GL_R32UI = 33334;
  GL_RG8I = 33335;
  GL_RG8UI = 33336;
  GL_RG16I = 33337;
  GL_RG16UI = 33338;
  GL_RG32I = 33339;
  GL_RG32UI = 33340;
  GL_RG = 33319;
  GL_COMPRESSED_RED = 33317;
  GL_COMPRESSED_RG = 33318;
  Kotlin.defineModule('kgl', _);
  return _;
}));

//# sourceMappingURL=kgl.js.map
