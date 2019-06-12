((global) => {
  let randomColor = (() => {

    let randomSeed = Math.random();
    const ONE_OVER_PHI = 0.618033988749895;

    return function() {
      return hsvToRgb(randomHue(), 1, 1);
    };

    function hsvToRgb(h, s, v) {
      if (h < 0 || h > 1) throw new Error('h out of range');
      if (s < 0 || s > 1) throw new Error('s out of range');
      if (v < 0 || v > 1) throw new Error('v out of range');
      h *= 6;
      let c = v * s;
      let x = c * (1 - Math.abs(h % 2 - 1));
      if (h <= 1) {
        return new THREE.Color(c, x, 0);
      } else if (h <= 2) {
        return new THREE.Color(x, c, 0);
      } else if (h <= 3) {
        return new THREE.Color(0, c, x);
      } else if (h <= 4) {
        return new THREE.Color(0, x, c);
      } else if (h <= 5) {
        return new THREE.Color(x, 0, c);
      } else {
        return new THREE.Color(c, 0 ,x);
      }
    }

    function randomHue() {
      randomSeed = (randomSeed + ONE_OVER_PHI) % 1;
      return randomSeed;
    }

  })();

  class TyvekMaterial extends THREE.ShaderMaterial {

    constructor() {
      super({
        uniforms: {
          tPixels: { value: null },
        },
        vertexShader: `
      varying vec2 vUv;
      
      void main() {
        vUv = (uv + 1.0) / 2.0; // rescale from [-1,1] to [0,1]
        vec4 mvPosition = modelViewMatrix * vec4(position, 1.0);
        gl_Position = projectionMatrix * mvPosition;
      } 
      `,
        fragmentShader: `
      varying vec2 vUv;
      
      uniform sampler2D tPixels;
      
      void main() {
        vec4 emission = texture2D(tPixels, vUv);
        gl_FragColor = vec4(emission.rgb, 1.0);
        //gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
      }
      `
      });
    }

    get pixels() {
      return this.uniforms.tPixels.value;
    }

    set pixels(tex) {
      if (tex && !(tex instanceof THREE.Texture)) throw new Error('incorrect type, must be a Texture');
      this.uniforms.tPixels.value = tex;
    }

  }

  class PixelsMaterial extends THREE.ShaderMaterial {

    constructor(pointScale, pointSize = 512, coneAngle = 130 * Math.PI / 180) {
      super({
        uniforms: {},
        defines: {
          POINT_SCALE: pointScale,
          POINT_SIZE: pointSize,
          CONE_ANGLE: coneAngle,

        },
        blending: THREE.AdditiveBlending,
        depthTest: true,
        depthWrite: false,
        transparent: true,
        vertexColors: THREE.VertexColors,
        vertexShader: `
      varying float fPixelDepth;
      varying vec3 vPixelColor;
      varying vec3 vPixelNormal;
      
      void main() {
        fPixelDepth = -position.z;
        vPixelColor = color;
        vPixelNormal = normal;
      
        vec4 mvPosition = modelViewMatrix * vec4(position, 1.0);
        gl_PointSize = float(POINT_SIZE);
        gl_Position = projectionMatrix * mvPosition;  
      }
      `,
        fragmentShader: `
      varying float fPixelDepth;
      varying vec3 vPixelColor;
      varying vec3 vPixelNormal;

      float intensity(float fDepth, vec3 vOrientation, float fAngle, vec2 vFragmentUv) {
        vec3 vLed = vec3(0.0, 0.0, -fDepth);
        vec2 uv = (vFragmentUv * 2.0 - 1.0) * float(POINT_SCALE);
        vec3 v3Uv = vec3(uv, 0.0);
        float fAxialDist = dot(v3Uv - vLed, vOrientation);
        float fRadius = fAxialDist * tan(fAngle / 2.0);
        float fOrthoDist = length((v3Uv - vLed) - fAxialDist * vOrientation);
        float fDist = length(v3Uv - vLed);
        return exp(-8.0 * (fOrthoDist / fRadius) * (fOrthoDist / fRadius)) / (1.0 + (fDist * fDist)); 
      }
      
      void main() {
        float fIntensity = intensity(fPixelDepth, vPixelNormal, float(CONE_ANGLE), gl_PointCoord);
        if (int(fIntensity * 255.0) < 1) discard;
        gl_FragColor = vec4(vPixelColor, fIntensity);
      }
      `
      });
    }

  }

  class Panel extends THREE.Mesh {

    constructor(triangles, numPixels, defaultRenderTargetSize = 512, renderTarget = null, matrix = null) {
      super(new THREE.Geometry(), new TyvekMaterial());
      triangles = Array.isArray(triangles) ? triangles : [triangles];
      if (triangles.some(t => !(t instanceof THREE.Triangle))) throw new Error('Panel(): first argument (triangles) must be a list of `THREE.Triangle`s');
      if (isNaN(numPixels) || numPixels < 0) {
        console.warn('Panel(): second argument (numPixels) must be a number >= 0; using 0 instead');
        numPixels = 0;
      }
      this.triangles = triangles;
      this.numPixels = numPixels;
      if (renderTarget && !(renderTarget instanceof THREE.WebGLRenderTarget)) {
        console.warn('Panel(): third argument (renderTarget) is not a THREE.WebGLRenderTarget; ignoring');
        renderTarget = null;
        matrix = null;
      }
      if (matrix && !(matrix instanceof THREE.Matrix4)) {
        console.warn('Panel(): fourth argument (matrix) is not a THREE.Matrix4; ignoring');
        renderTarget = null;
        matrix = null;
      }
      if (renderTarget && !matrix) {
        console.warn('Panel(): no matrix supplied; ignoring renderTarget');
        renderTarget = null;
        matrix = null;
      }
      this.panelMatrix = matrix || new THREE.Matrix4();
      let builtGeom = buildPanelGeometry(this.triangles, this.panelMatrix);
      this.geometry = builtGeom.geom;
      this.panelScale = builtGeom.panelScale;
      this.panelCam = builtGeom.cam;
      this.renderTarget = renderTarget || new THREE.WebGLRenderTarget(defaultRenderTargetSize, defaultRenderTargetSize, {
        depthBuffer: false,
        stencilBuffer: false,
        format: THREE.RGBAFormat
      });
      this.renderTarget.depthScale = this.renderTarget.depthScale || 1;
      this.pointSize = this.renderTarget.width / 4;
      this.pointScale = this.pointSize / this.renderTarget.width;
      this.pixels = new THREE.Points(buildPixelGeometry(this.numPixels), new PixelsMaterial(this.pointScale, this.pointSize));
      this.pixels.applyMatrix(this.panelMatrix);
      if (!this.renderTarget.camera) {
        this.renderTarget.camera = new THREE.OrthographicCamera(-1, 1, 1, -1, 0, 2);
        this.renderTarget.camera.position.set(0, 0, 1);
        this.renderTarget.camera.lookAt(new THREE.Vector3());
        this.renderTarget.camera.updateMatrixWorld();
        this.renderTarget.camera.updateProjectionMatrix();
      }
      this.material.pixels = this.renderTarget.texture;
      this.renderTarget.dirty = true;
      this.renderTarget.scene = this.renderTarget.scene || new THREE.Scene();
      this.renderTarget.scene.add(this.pixels);
    }

    onBeforeRender(renderer, scene, camera, geometry, material, group) {
      super.onBeforeRender(renderer, scene, camera, geometry, material, group);
      if (this.renderTarget.dirty) {
        renderer.setRenderTarget(this.renderTarget);
        renderer.render(this.renderTarget.scene, this.renderTarget.camera, this.renderTarget);
        renderer.setRenderTarget(null);
        this.renderTarget.dirty = false;
        if (this.renderTarget.img) {
          textureToImage(renderer, this.renderTarget, this.renderTarget.img);
        }
      }
    }

    setPixelPositionsInAssemblySpace(values) {
      values = Array.isArray(values) ? values : [values];
      if (values.some(p => !(p instanceof THREE.Vector3))) throw new Error('setPixelPositionsInAssemblySpace() requires a list of Vector3s');
      return this.setPixelPositionsInPanelSpace(values.map(p => {
        let pp = p.clone().project(this.panelCam);
        return new THREE.Vector2(pp.x, pp.y);
      }));
    }

    setPixelPositionInAssemblySpace(idx, value) {
      if (typeof idx !== 'number' || !(value instanceof THREE.Vector3)) throw new Error('setPixelPositionInAssemblySpace() requires an index and a Vector3');
      if (idx < 0 || idx >= this.numPixels) throw new Error('index out of range');
      let pp = value.clone().project(this.panelCam);
      return this.setPixelPositionInPanelSpace(idx, new THREE.Vector2(pp.x, pp.y));
    }

    setPixelPositionsInPanelSpace(values) {
      values = Array.isArray(values) ? values : [values];
      if (values.some(p => !(p instanceof THREE.Vector2))) throw new Error('setPixelPositionsInPanelSpace() requires a list of Vector2s');
      let positions = this.pixels.geometry.getAttribute('position');
      for (let i = 0; i < Math.min(this.numPixels, values.length); ++i) {
        let j = i % values.length;
        positions.setXY(i, values[j].x, values[j].y);
      }
      positions.needsUpdate = true;
      this.renderTarget.dirty = true;
    }

    setPixelPositionInPanelSpace(idx, value) {
      if (typeof idx !== 'number' || !(value instanceof THREE.Vector2)) throw new Error('setPixelPositionInPanelSpace() requires an index and a Vector2');
      if (idx < 0 || idx >= this.numPixels) throw new Error('index out of range');
      let positions = this.pixels.geometry.getAttribute('position');
      positions.setXY(idx, value.x, value.y);
      positions.needsUpdate = true;
      this.renderTarget.dirty = true;
    }

    setPixelDepths(values) {
      values = Array.isArray(values) ? values : [values];
      if (values.some(p => typeof p !== 'number' || isNaN(p))) throw new Error('setPixelDepths() requires a list of numbers');
      let positions = this.pixels.geometry.getAttribute('position');
      for (let i = 0; i < Math.min(this.numPixels, values.length); ++i) {
        let j = i % values.length;
        positions.setZ(i, -values[j] / this.renderTarget.depthScale);
      }
      positions.needsUpdate = true;
      this.renderTarget.dirty = true;
    }

    setPixelDepth(idx, value) {
      if (typeof idx !== 'number' || typeof value !== 'number' || isNaN(value)) throw new Error('setPixelDepth() requires an index and a number');
      if (idx < 0 || idx >= this.numPixels) throw new Error('index out of range');
      if (value < 0) throw new Error('depth out of range');
      let positions = this.pixels.geometry.getAttribute('position');
      positions.setZ(idx, -value / this.renderTarget.depthScale);
      positions.needsUpdate = true;
      this.renderTarget.dirty = true;
    }

    setPixelOrientations(values) {
      values = Array.isArray(values) ? values : [values];
      if (values.some(p => !(p instanceof THREE.Vector3))) throw new Error('setPixelOrientations() requires a list of Vector3s');
      let normals = this.pixels.geometry.getAttribute('normal');
      for (let i = 0; i < Math.min(this.numPixels, values.length); ++i) {
        let j = i % values.length;
        normals.setXYZ(i, values[j].x, values[j].y, values[j].z);
      }
      normals.needsUpdate = true;
      this.renderTarget.dirty = true;
    }

    setPixelOrientation(idx, value) {
      if (typeof idx !== 'number' || !(value instanceof THREE.Vector3)) throw new Error('setPixelOrientation() requires an index and a Vector3');
      if (idx < 0 || idx >= this.numPixels) throw new Error('index out of range');
      let normals = this.pixels.geometry.getAttribute('normal');
      normals.setXYZ(idx, value.x, value.y, value.z);
      normals.needsUpdate = true;
      this.renderTarget.dirty = true;
    }

    setPixelColors(values) {
      values = Array.isArray(values) ? values : [values];
      if (values.some(p => !(p instanceof THREE.Color))) throw new Error('setPixelColors() requires a list of Colors');
      let colors = this.pixels.geometry.getAttribute('color');
      for (let i = 0; i < Math.min(this.numPixels, values.length); ++i) {
        let j = i % values.length;
        colors.setXYZ(i, values[j].r, values[j].g, values[j].b);
      }
      colors.needsUpdate = true;
      this.renderTarget.dirty = true;
    }

    setPixelColor(idx, value) {
      if (typeof idx !== 'number' || !(value instanceof THREE.Color)) throw new Error('setPixelColor() requires an index and a Color');
      if (idx < 0 || idx >= this.numPixels) throw new Error('index out of range');
      let colors = this.pixels.geometry.getAttribute('color');
      colors.setXYZ(idx, value.r, value.g, value.b);
      colors.needsUpdate = true;
      this.renderTarget.dirty = true;
    }

    getPixelColor(idx) {
      if (typeof idx !== 'number') throw new Error('getPixelColor() requires an index');
      if (idx < 0 || idx >= this.numPixels) throw new Error('index out of range');
      let colors = this.pixels.geometry.getAttribute('color');
      return new THREE.Color(colors.getX(idx), colors.getY(idx), colors.getZ(idx));
    }

    getPerimeter() {
      // returns polygon hull as list of vertices in panel space in winding order

      let verts = new Set();
      for (let t of this.triangles) {
        verts.add(t.a);
        verts.add(t.b);
        verts.add(t.c);
      }
      verts = [...verts];
      let edges = {};
      for (let t of this.triangles) {
        let ai = verts.indexOf(t.a);
        let bi = verts.indexOf(t.b);
        let ci = verts.indexOf(t.c);
        let e0 = [ai, bi].sort().join(',');
        let e1 = [bi, ci].sort().join(',');
        let e2 = [ci, ai].sort().join(',');
        edges[e0] = (edges[e0] || 0) + 1;
        edges[e1] = (edges[e1] || 0) + 1;
        edges[e2] = (edges[e2] || 0) + 1;
      }
      for (let [k, v] of Object.entries(edges)) {
        if (v > 1) delete edges[k];
      }
      edges = Object.keys(edges).map(k => /^(\d+),(\d+)/.exec(k).slice(1).map(v => parseInt(v)));
      let [start, next] = edges.shift();
      let path = [start, next];
      while (edges.length) {
        let len = edges.length;
        for (let i = 0; i < edges.length; ++i) {
          let edge = edges[i];
          if (edge[0] === next) {
            path.push(edge[1]);
            next = edge[1];
            edges.splice(i, 1);
            break;
          } else if (edge[1] === next) {
            path.push(edge[0]);
            next = edge[0];
            edges.splice(i, 1);
            break;
          }
        }
        if (edges.length === len) {
          throw new Error('open perimeter!');
        }
      }
      if (start !== next) {
        throw new Error('unclosed perimeter!');
      }
      path = path.slice(0, -1).map(i => verts[i].clone());
      let flatPath = path.map(v => v.clone().project(this.panelCam).clamp(new THREE.Vector3(-1, -1, 0), new THREE.Vector3(1, 1, 0)));
      let sum = new THREE.Vector3(0, 0, 0);
      for (let i = 0; i < flatPath.length - 1; ++i) {
        sum.add(flatPath[i].clone().cross(flatPath[i+1]));
      }
      let area = new THREE.Vector3(0, 0, 1).dot(sum) / 2;
      if (area < 0) {
        flatPath.reverse();
      }
      return flatPath;
    }

  }

  class Assembly extends THREE.Object3D {

    constructor(panelDef, textureDef = {}, defaultTextureSize = 512) {
      super();
      const vertices = panelDef.vertices.map(v => new THREE.Vector3(v.x, v.y, v.z));
      this.textures = {};
      this.images = [];
      this.panels = {};
      this.orderedPanels = [];
      this.pixelOffsets = [];
      this.totalPixels = 0;
      for (let p of Object.values(panelDef.panels)) {
        let triangles = p.triangles.map(t => new THREE.Triangle(vertices[t.a], vertices[t.b], vertices[t.c]));
        if (p.numPixels > 0) {
          let texture, matrix;
          if (textureDef.panels[p.name]) {
            let sheetId = textureDef.panels[p.name].sheet;
            let sheet = textureDef.sheets[sheetId.toString()];
            // textureDef matrices are in row-major order
            matrix = new THREE.Matrix4().set(...textureDef.panels[p.name].matrix);
            texture = this.textures[sheetId.toString()] = this.textures[sheetId.toString()] || new THREE.WebGLRenderTarget(defaultTextureSize, defaultTextureSize, {
              depthBuffer: false,
              stencilBuffer: false,
              format: THREE.RGBAFormat
            });
            texture.depthScale = texture.depthScale || sheet.binSize;
          }
          this.panels[p.name] = new Panel(triangles, p.numPixels, defaultTextureSize, texture, matrix);
          this.orderedPanels.push(this.panels[p.name]);
          this.pixelOffsets.push(this.totalPixels);
          this.totalPixels += p.numPixels;
          this.panels[p.name].name = p.name;
          this.add(this.panels[p.name]);
        }
      }
    }

    setPixelColor(idx, color) {
      idx = idx % this.totalPixels;
      let i = 0;
      while (idx >= this.pixelOffsets[i+1]) i++;
      this.orderedPanels[i].setPixelColor(idx - this.pixelOffsets[i], color);
    }

    setPixelColors(values) {
      if (values.length !== this.totalPixels) throw new Error('wrong number of values');
      let i = 0;
      for (let p of this.orderedPanels) {
        p.setPixelColors(values.slice(i, i + p.numPixels));
        i += p.numPixels;
      }
    }

    getPanel(name) {
      return this.panels[name];
    }

    generateRandomPixelsForAllPanels(colorOverride, depthOverride, orientationOverride) {
      for (let panel of Object.values(this.panels)) {
        if (panel.numPixels < 1) continue;
        let abortThreshold = panel.numPixels * 1000;
        let abortCount = 0;
        let mappedVerts = panel.getPerimeter();
        let [pixels, depths, colors, normals] = [[], [], [], []];
        while (pixels.length < panel.numPixels && abortCount < abortThreshold) {
          let p = new THREE.Vector2(Math.random() * 2 - 1 , Math.random() * 2 - 1);
          if (pointInPoly(p, mappedVerts)) {
            pixels.push(p);
            colors.push(colorOverride || randomColor());
            depths.push(depthOverride || Math.random() + 1);
            normals.push(orientationOverride || randomNormal());
          }
          abortCount++;
        }
        if (abortCount >= abortThreshold && pixels.length < panel.numPixels) throw new Error(`could not generate enough pixels (got only ${pixels.length} of ${panel.numPixels} in ${abortThreshold} tries)`);
        panel.setPixelPositionsInPanelSpace(pixels);
        panel.setPixelDepths(depths);
        panel.setPixelColors(colors);
        panel.setPixelOrientations(normals);
      }
    }

  }

  global.Assembly = Assembly;

  function getBoundingBox(triangles) {
    let bbox = {
      min: new THREE.Vector3(Infinity, Infinity, Infinity),
      max: new THREE.Vector3(-Infinity, -Infinity, -Infinity)
    };
    for (let t of triangles) {
      for (let v of [t.a, t.b, t.c]) {
        bbox.min.min(v);
        bbox.max.max(v);
      }
    }
    return bbox;
  }

  function getAverageNormal(triangles) {
    let sum = new THREE.Vector3(0, 0, 0);
    let tar = new THREE.Vector3();
    for (let t of triangles) {
      t.getNormal(tar);
      sum.add(tar.normalize().multiplyScalar(t.getArea())); // weight average normal by area
    }
    return sum.divideScalar(triangles.length).normalize();
  }

  function getCentroid(triangles) {
    let bbox = getBoundingBox(triangles);
    return bbox.min.clone().add(bbox.max.clone().sub(bbox.min).divideScalar(2));
  }

  function getFlattened(triangles, centroid, normal) {
    let cam = new THREE.OrthographicCamera(-1,1,1,-1,-1,1);
    cam.position.copy(centroid.clone().add(normal));
    cam.lookAt(centroid);
    cam.updateMatrixWorld();
    return triangles.map(t => {
      let p = t.clone();
      for (let v of [p.a, p.b, p.c]) {
        v.project(cam);
        v.z = 0;
      }
      return p;
    });
  }

  function extractScale(matrix) {
    let e = matrix.elements;
    return new THREE.Vector3(new THREE.Vector3(e[0], e[1], e[2]).length(), new THREE.Vector3(e[4], e[5], e[6]).length(), new THREE.Vector3(e[8], e[9], e[10]).length());
  }

  function buildPanelGeometry(triangles, matrix) {
    let geom = new THREE.Geometry();
    let centroid = getCentroid(triangles);
    let normal = getAverageNormal(triangles);
    let flattenedA = getFlattened(triangles, centroid, normal);
    let bbox = getBoundingBox(flattenedA);
    let extents = bbox.max.clone().sub(bbox.min);
    let extent = Math.max(extents.x, extents.y);

    let panelScale = extent / 2;

    let verts = new Set();
    for (let t of triangles) {
      for (let v of [t.a, t.b, t.c]) {
        verts.add(v);
      }
    }
    verts = [...verts]; // set order
    geom.vertices.push(...verts);

    let cam = new THREE.OrthographicCamera(-extent/2, extent/2, extent/2, -extent/2, 0, 2 + (bbox.max.z - bbox.min.z));
    cam.position.copy(centroid.clone().add(normal));
    cam.lookAt(centroid);
    cam.updateMatrixWorld();
    let uvs = geom.faceVertexUvs[0] = [], faces = geom.faces;
    let min = new THREE.Vector3(-1, -1, 0);
    let max = new THREE.Vector3(1, 1, 0);
    for (let t of triangles) {
      let a = t.a.clone().project(cam).clamp(min, max).applyMatrix4(matrix);
      let b = t.b.clone().project(cam).clamp(min, max).applyMatrix4(matrix);
      let c = t.c.clone().project(cam).clamp(min, max).applyMatrix4(matrix);
      uvs.push([new THREE.Vector2(a.x, a.y), new THREE.Vector2(b.x, b.y), new THREE.Vector2(c.x, c.y)]);
      faces.push(new THREE.Face3(verts.indexOf(t.a), verts.indexOf(t.b), verts.indexOf(t.c)));
    }
    geom.computeFaceNormals();
    geom = new THREE.BufferGeometry().fromGeometry(geom);
    geom.uvsNeedUpdate = true;
    return { geom, panelScale, cam };
  }

  function buildPixelGeometry(numPixels) {
    let geom = new THREE.BufferGeometry();
    let positions =  new THREE.BufferAttribute(new Float32Array(numPixels * 3), 3);
    let normals = new THREE.BufferAttribute(new Float32Array(numPixels * 3), 3, true);
    let colors = new THREE.BufferAttribute(new Float32Array(numPixels * 3), 3);
    for (let i = 0; i < numPixels; ++i) {
      positions.setXYZ(i, 0, 0, 0);
      normals.setXYZ(i, 0, 0, 1);
      colors.setXYZ(i, 0, 0, 0);
    }
    geom.addAttribute('position', positions);
    geom.addAttribute('normal', normals);
    geom.addAttribute('color', colors);
    return geom;
  }

  function isLeft(p0, p1, p2) {
    return ((p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y));
  }

  function pointInPoly(point, verts) {
    let wn = 0;
    for (let i = 0; i < verts.length; ++i) {
      let j = (i + 1) % verts.length;
      if (verts[i].y <= point.y) {
        if (verts[j].y > point.y) {
          if (isLeft(verts[i], verts[j], point) > 0) {
            ++wn;
          }
        }
      } else {
        if (verts[j].y <= point.y) {
          if (isLeft(verts[i], verts[j], point) < 0) {
            --wn;
          }
        }
      }
    }
    return wn !== 0;
  }

  function randomNormal() {
    let phi = Math.random() * (Math.PI / 6);
    let theta = Math.random() * Math.PI * 2;
    return new THREE.Vector3(Math.sin(phi) * Math.cos(theta), Math.sin(phi) * Math.sin(theta), Math.cos(phi));
  }

  function textureToImage(renderer, renderTarget, img) {
    let ctx = renderer.getContext();
    let webglTexture = renderer.properties.get(renderTarget.texture).__webglTexture;
    let width = renderTarget.width;
    let height = renderTarget.height;

    let framebuffer = ctx.createFramebuffer();
    let data = new Uint8Array(width * height * 4);
    let canvas = document.createElement('canvas');
    let context = canvas.getContext('2d');
    let imageData = context.createImageData(width, height);

    ctx.bindFramebuffer(ctx.FRAMEBUFFER, framebuffer);
    ctx.framebufferTexture2D(ctx.FRAMEBUFFER, ctx.COLOR_ATTACHMENT0, ctx.TEXTURE_2D, webglTexture, 0);
    ctx.readPixels(0, 0, width, height, ctx.RGBA, ctx.UNSIGNED_BYTE, data);
    ctx.deleteFramebuffer(framebuffer);
    canvas.width = width;
    canvas.height = height;
    imageData.data.set(data);
    context.putImageData(imageData, 0, 0);
    img.src = canvas.toDataURL();

    return img;
  }
})(window);



