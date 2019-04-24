class VizPanel {
  constructor(panel) {
    this.panel = panel;
    this.name = panel.name;
    let panelGeometry = new THREE.Geometry();
    this.geometry = panelGeometry;

    let panelVertices = [];
    panelGeometry.faces = panel.faces.faces.toArray().map(face => {
      let localVerts = [];
      face.vertexIds.toArray().forEach(vi => {
        let v = geom.vertices[vi];
        let lvi = panelVertices.indexOf(v);
        if (lvi === -1) {
          lvi = panelVertices.length;
          panelVertices.push(v);
        }
        localVerts.push(lvi)
      });

      return new THREE.Face3(...localVerts);
    });
    geom.computeVertexNormals();
    panelGeometry.vertices = panelVertices;
    let lines = panel.lines.toArray().map(line => {
      let lineGeo = new THREE.Geometry();
      lineGeo.vertices = line.points.toArray().map(pt => new THREE.Vector3(pt.x, pt.y, pt.z));
      return lineGeo;
    });

    let faceMaterial = new THREE.MeshBasicMaterial({color: 0xaa0000,});
    faceMaterial.side = THREE.FrontSide;
    faceMaterial.transparent = true;
    faceMaterial.opacity = 0.75;

    this.faceMaterial = faceMaterial;
    this.faces = new THREE.Mesh(panelGeometry, faceMaterial);
    this.lines = lines.map(line => new THREE.Line(line, lineMaterial));

    this.faces.panel = this;

    this.faces.visible = false;
    scene.add(this.faces);
    this.lines.forEach((line) => {
      scene.add(line);
    });
  }

  addPixels(pixelCount) {
    const geometry = this.geometry;
    const vertices = geometry.vertices;
    geometry.computeFaceNormals();
    const pixelsGeometry = new THREE.BufferGeometry();
    const positions = [];
    const colors = [];

    let quaternion = new THREE.Quaternion();

    const panelFaces = geometry.faces;
    let curFace = panelFaces[0];
    let originalNormal = curFace.normal.clone();
    quaternion.setFromUnitVectors(curFace.normal, new THREE.Vector3(0, 0, 1));
    let matrix = new THREE.Matrix4();
    matrix.makeRotationFromQuaternion(quaternion);
    geometry.applyMatrix(matrix);
    pixelsGeometry.applyMatrix(matrix);

    let pixelSpacing = 2; // inches
    let pos = this.randomLocation(curFace, vertices);
    const nextPos = new THREE.Vector3();
    positions.push(pos.x, pos.y, pos.z);
    colors.push(0, 0, 0);

    let tries = 1000;
    let angleRad = Math.random() * 2 * Math.PI;
    let angleRadDelta = Math.random() * 0.5 - 0.5;
    let pixelsSinceEdge = 0;
    for (let pixelI = 1; pixelI < pixelCount; pixelI++) {
      nextPos.x = pos.x + pixelSpacing * Math.sin(angleRad);
      nextPos.y = pos.y + pixelSpacing * Math.cos(angleRad);
      nextPos.z = pos.z;

      const lastEdge = {};
      if (!VizPanel.isInside(VizPanel.xy(nextPos), [
        VizPanel.xy(vertices[curFace.a]),
        VizPanel.xy(vertices[curFace.b]),
        VizPanel.xy(vertices[curFace.c])], lastEdge)) {
        let newFace = this.getOtherFace(panelFaces, curFace, lastEdge);
        if (newFace) {
          quaternion.setFromUnitVectors(new THREE.Vector3(0, 0, 1), originalNormal);
          matrix.makeRotationFromQuaternion(quaternion);
          geometry.applyMatrix(matrix);
          pixelsGeometry.applyMatrix(matrix);

          curFace = newFace;
          originalNormal = curFace.normal.clone();
          quaternion.setFromUnitVectors(curFace.normal, new THREE.Vector3(0, 0, 1));
          matrix.makeRotationFromQuaternion(quaternion);
          geometry.applyMatrix(matrix);
          pixelsGeometry.applyMatrix(matrix);
        } else {
          angleRad = Math.random() * 2 * Math.PI;
          pixelI--;
          if (tries-- < 0) break;
          pixelsSinceEdge = 0;
          continue;
        }
      }

      positions.push(nextPos.x, nextPos.y, nextPos.z);
      colors.push(0, 0, 0);

      angleRad += angleRadDelta;
      angleRadDelta *= 1 - Math.random() * 0.2 + 0.1;

      // occasional disruption just in case we're in a tight loop...
      if (pixelsSinceEdge > pixelCount / 10) {
        angleRad = Math.random() * 2 * Math.PI;
        angleRadDelta = Math.random() * 0.5 - 0.5;
        pixelsSinceEdge = 0;
      }
      pos.copy(nextPos);
      pixelsSinceEdge++;
    }

    pixelsGeometry.addAttribute('position', new THREE.Float32BufferAttribute(positions, 3));

    quaternion.setFromUnitVectors(new THREE.Vector3(0, 0, 1), originalNormal);
    matrix.makeRotationFromQuaternion(quaternion);
    geometry.applyMatrix(matrix);
    pixelsGeometry.applyMatrix(matrix);

    let colorsBuffer = new THREE.Float32BufferAttribute(colors, 3);
    colorsBuffer.dynamic = true;
    pixelsGeometry.addAttribute('color', colorsBuffer);
    const material = new THREE.PointsMaterial({size: 3, vertexColors: THREE.VertexColors});
    const points = new THREE.Points(pixelsGeometry, material);
    scene.add(points);

    this.pixelCount = pixelCount;
    this.pixelColorsBuffer = colorsBuffer;
    this.pixelsGeometry = pixelsGeometry;
  }

  randomLocation(face, vertices) {
    const v = new THREE.Vector3().copy(vertices[face.a]);
    v.addScaledVector(new THREE.Vector3().copy(vertices[face.b]).sub(v), Math.random());
    v.addScaledVector(new THREE.Vector3().copy(vertices[face.c]).sub(v), Math.random());
    return v;
  }

  static isInside(point, vs, lastEdge) {
    // ray-casting algorithm based on
    // https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html

    var x = point[0], y = point[1];

    var inside = false;
    var lastIntersection = vs.length - 1;
    for (var i = 0, j = vs.length - 1; i < vs.length; j = i++) {
      var xi = vs[i][0], yi = vs[i][1];
      var xj = vs[j][0], yj = vs[j][1];

      var intersect = ((yi > y) != (yj > y))
          && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
      if (intersect) {
        inside = !inside;
        lastIntersection = i;
      }
    }

    lastEdge.v0 = lastIntersection;
    return inside;
  }

  static xy(v) {
    return [v.x, v.y];
  }

  getOtherFace(panelFaces, curFace, lastEdge) {
    if (panelFaces.length > 1) {
      // console.log("crossed edge of ", panel.name);
      const vs = [curFace.a, curFace.b, curFace.c, curFace.a];
      const v0 = vs[lastEdge.v0];
      const v1 = vs[lastEdge.v0 + 1];

      for (let i = 0; i < panelFaces.length; i++) {
        const f = panelFaces[i];
        if (f === curFace) continue;

        const pvs = [f.a, f.b, f.c];
        if (pvs.includes(v0) && pvs.includes(v1)) {
          // found the face we just crossed over into!
          console.log("Crossed into face ", i, " of panel ", this.panel.name, f);
          return f;
        }
      }
    }
    return null;
  }

  setPanelColor(panelBgColor, pixelColors) {
    this.faces.visible = true;

    if (!renderPixels) {
      this.faceMaterial.color.r = panelBgColor.redF;
      this.faceMaterial.color.g = panelBgColor.greenF;
      this.faceMaterial.color.b = panelBgColor.blueF;
    } else {
      this.faceMaterial.color.r = .3;
      this.faceMaterial.color.g = .3;
      this.faceMaterial.color.b = .3;
    }

    if (this.pixelCount && pixelColors) {
      const count = Math.min(this.pixelCount, pixelColors.length);
      for (let i = 0; i < count; i++) {
        const pColor = pixelColors[i];
        this.pixelColorsBuffer.array[i * 3] = pColor.redF;
        this.pixelColorsBuffer.array[i * 3 + 1] = pColor.greenF;
        this.pixelColorsBuffer.array[i * 3 + 2] = pColor.blueF;
      }
      this.pixelColorsBuffer.needsUpdate = true;
    }
  }

}
