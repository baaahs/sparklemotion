class VizPanel {
  constructor(panel) {
    this.panel = panel;
    this.name = panel.name;
    this.geometry = new THREE.Geometry();
    const panelGeometry = this.geometry;
    const panelVertices = panelGeometry.vertices;

    let triangle = new THREE.Triangle(); // for computing area...
    this.area = 0;

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

      triangle.set(...localVerts.map(x => panelVertices[x]));
      this.area += triangle.getArea();

      return new THREE.Face3(...localVerts);
    });

    this.isMultiFaced = panelGeometry.faces.length > 1;

    this.edgeNeighbors = {};
    panelGeometry.faces.forEach(face => {
      [[face.a, face.b], [face.b, face.c], [face.c, face.a]].forEach(vs => {
        vs = vs.sort().join("-");
        let neighbors = this.edgeNeighbors[vs];
        if (neighbors === undefined) {
          neighbors = [];
          this.edgeNeighbors[vs] = neighbors;
        }
        neighbors.push(face);
      });
    });

    geom.computeVertexNormals(); // todo: why is this here?

    let lines = panel.lines.toArray().map(line => {
      let lineGeo = new THREE.Geometry();
      lineGeo.vertices = line.points.toArray().map(pt => new THREE.Vector3(pt.x, pt.y, pt.z));
      return lineGeo;
    });

    this.faceMaterial = new THREE.MeshBasicMaterial({color: 0xaa0000,});
    this.faceMaterial.side = THREE.FrontSide;
    this.faceMaterial.transparent = true;
    this.faceMaterial.opacity = 0.99;

    this.mesh = new THREE.Mesh(panelGeometry, this.faceMaterial);
    this.mesh.panel = this; // so we can get back to the VizPanel from a raycaster intersection...
    this.mesh.visible = false;

    this.lines = lines.map(line => new THREE.Line(line, lineMaterial));

    scene.add(this.mesh);
    this.lines.forEach((line) => {
      scene.add(line);
    });
  }

  addPixels(pixelCount) {
    const panelGeometry = this.geometry;
    const vertices = panelGeometry.vertices;
    panelGeometry.computeFaceNormals();
    const pixelsGeometry = new THREE.Geometry();

    let quaternion = new THREE.Quaternion();

    const panelFaces = panelGeometry.faces;
    let curFace = panelFaces[0];
    let revertToNormal = curFace.normal.clone();
    let straightOnNormal = new THREE.Vector3(0, 0, 1);
    quaternion.setFromUnitVectors(curFace.normal, straightOnNormal);
    let matrix = new THREE.Matrix4();
    matrix.makeRotationFromQuaternion(quaternion);
    panelGeometry.applyMatrix(matrix);
    pixelsGeometry.applyMatrix(matrix);

    let pixelSpacing = 2; // inches
    let pos = this.randomLocation(curFace, vertices);
    const nextPos = new THREE.Vector3();

    pixelsGeometry.vertices.push(pos.clone());
    const colors = [];
    colors.push(0, 0, 0);

    let tries = 1000;
    let angleRad = Math.random() * 2 * Math.PI;
    let angleRadDelta = Math.random() * 0.5 - 0.5;
    let pixelsSinceEdge = 0;
    for (let pixelI = 1; pixelI < pixelCount; pixelI++) {
      nextPos.x = pos.x + pixelSpacing * Math.sin(angleRad);
      nextPos.y = pos.y + pixelSpacing * Math.cos(angleRad);
      nextPos.z = pos.z;

      // console.log("cur face: ", this.faceVs(curFace, panelGeometry));

      if (!this.isInsideFace(curFace, nextPos)) {
        let newFace = this.getFaceForPoint(curFace, nextPos);
        if (newFace) {
          // console.log("moving from", curFace, "to", newFace);
          // console.log("prior face vs:", this.faceVs(curFace, panelGeometry));

          quaternion.setFromUnitVectors(straightOnNormal, revertToNormal);
          matrix.makeRotationFromQuaternion(quaternion);
          panelGeometry.applyMatrix(matrix);
          pixelsGeometry.applyMatrix(matrix);
          nextPos.applyMatrix4(matrix);

          curFace = newFace;
          revertToNormal = curFace.normal.clone();
          quaternion.setFromUnitVectors(curFace.normal, straightOnNormal);
          matrix.makeRotationFromQuaternion(quaternion);
          panelGeometry.applyMatrix(matrix);
          pixelsGeometry.applyMatrix(matrix);
          // console.log("pos was", nextPos);
          nextPos.applyMatrix4(matrix);
          // console.log("pos is now", nextPos);
          // console.log("new face vs:", this.faceVs(newFace, panelGeometry));
          nextPos.z = panelGeometry.vertices[newFace.a].z;
          if (!this.isInsideFace(curFace, nextPos)) {
            // console.log(nextPos, "is not in", this.faceVs(curFace, panelGeometry));
            nextPos.copy(this.randomLocation(curFace, vertices));
          } else {
            // console.log("AWESOME", nextPos, "is in", this.faceVs(curFace, panelGeometry))
          }
        } else {
          angleRad = Math.random() * 2 * Math.PI;
          pixelI--;
          if (tries-- < 0) break;
          pixelsSinceEdge = 0;
          continue;
        }
      }

      // console.log("pixel z = ", nextPos.z);
      pixelsGeometry.vertices.push(nextPos.clone());
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

    quaternion.setFromUnitVectors(straightOnNormal, revertToNormal);
    matrix.makeRotationFromQuaternion(quaternion);
    panelGeometry.applyMatrix(matrix);
    pixelsGeometry.applyMatrix(matrix);

    const pixBufGeometry = new THREE.BufferGeometry();
    pixBufGeometry.addAttribute('position',
        new THREE.Float32BufferAttribute(pixelsGeometry.vertices.flatMap(v => [v.x, v.y, v.z]), 3));

    let colorsBuffer = new THREE.Float32BufferAttribute(colors, 3);
    colorsBuffer.dynamic = true;
    pixBufGeometry.addAttribute('color', colorsBuffer);
    const material = new THREE.PointsMaterial({size: 3, vertexColors: THREE.VertexColors});
    const points = new THREE.Points(pixBufGeometry, material);
    scene.add(points);

    this.pixelCount = pixelCount;
    this.pixelColorsBuffer = colorsBuffer;
    this.pixelsGeometry = pixBufGeometry;
  }

  faceVs(face, geometry) {
    return [face.a, face.b, face.c].map(f => geometry.vertices[f]);
  }

  faceZs(face, geometry) {
    return [face.a, face.b, face.c].map(f => geometry.vertices[f].z);
  }

  randomLocation(face, vertices) {
    const v = new THREE.Vector3().copy(vertices[face.a]);
    v.addScaledVector(new THREE.Vector3().copy(vertices[face.b]).sub(v), Math.random());
    v.addScaledVector(new THREE.Vector3().copy(vertices[face.c]).sub(v), Math.random());
    return v;
  }

  isInsideFace(curFace, v) {
    const vertices = this.geometry.vertices;

    return VizPanel.isInside(VizPanel.xy(v), [
      VizPanel.xy(vertices[curFace.a]),
      VizPanel.xy(vertices[curFace.b]),
      VizPanel.xy(vertices[curFace.c])]);
  }

  static isInside(point, vs) {
    // ray-casting algorithm based on
    // https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html

    var x = point[0], y = point[1];

    var inside = false;
    for (var i = 0, j = vs.length - 1; i < vs.length; j = i++) {
      var xi = vs[i][0], yi = vs[i][1];
      var xj = vs[j][0], yj = vs[j][1];

      var intersect = ((yi > y) != (yj > y))
          && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
      if (intersect) {
        inside = !inside;
      }
    }

    return inside;
  }

  static xy(v) {
    return [v.x, v.y];
  }

  // we've tried to add a pixel that's not inside curFace; figure out which face it corresponds to...
  getFaceForPoint(curFace, v) {
    if (this.isMultiFaced) {
      const vertices = this.geometry.vertices;

      // find the edge closest to v...
      let closestEdge = [-1, -1];
      let bestDistance = Infinity;
      [[curFace.a, curFace.b], [curFace.b, curFace.c], [curFace.c, curFace.a],].forEach(edgeVs => {
        const closestPointOnEdge = new THREE.Vector3();
        const v0 = edgeVs[0];
        const v1 = edgeVs[1];
        new THREE.Line3(vertices[v0], vertices[v1]).closestPointToPoint(v, true, closestPointOnEdge);
        let thisDistance = closestPointOnEdge.distanceTo(v);
        if (thisDistance < bestDistance) {
          closestEdge = [v0, v1];
          bestDistance = thisDistance;
        }
      });

      let edgeId = closestEdge.sort().join("-");
      // console.log("Closest edge to", v, "is", edgeId, this.edgeNeighbors[edgeId]);

      const neighbors = this.edgeNeighbors[edgeId];
      const neighbor = neighbors.filter(f => f !== curFace);
      if (neighbor.length === 0) {
        return null;
      } else if (neighbor.length > 1) {
        // console.warn("Found multiple neighbors for ", this.panel.name, " edge ", edgeId, ": ", neighbors);
      }

      // console.log("Face for ", v, "is", edgeId, neighbor[0]);
      return neighbor[0];
    }
    return null;
  }

  setPanelColor(panelBgColor, pixelColors) {
    this.mesh.visible = true;

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
