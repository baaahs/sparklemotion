let controls, camera, scene, renderer, geom, object, pointMaterial, faceMaterial, lineMaterial, panelMaterial,
    raycaster, mouse, sphere;

let panels = [];

let select = document.getElementById('panelSelect');
let info2 = document.getElementById('info2');
let sheepView = document.getElementById('sheepView');

select.onchange = onSelectChange;

mouse = new THREE.Vector2();


function onSelectChange(event) {
  let idx = parseInt(event.target.value);
  for (let i = 0; i < panels.length; ++i) {
    panels[i].faces.visible = i === idx;
    for (let line of panels[i].lines) {
      line.material = i === idx ? panelMaterial : lineMaterial;
    }

  }
  // surface area
  let area = 0;
  for (let face of panels[idx].faces.geometry.faces) {
    let a = panels[idx].faces.geometry.vertices[face.a];
    let b = panels[idx].faces.geometry.vertices[face.b];
    let c = panels[idx].faces.geometry.vertices[face.c];
    let u = b.clone().sub(a);
    let v = c.clone().sub(a);
    area += Math.sqrt((u.y * v.z - u.z * v.y) ** 2 + (u.z * v.x - u.x * v.z) ** 2 + (u.x * v.y - u.y * v.x) ** 2) / 2;
  }
  info2.innerText = `Total surface area: ${(area / 144).toFixed(2)} sq. ft.`;
}

function onMouseMove(event) {
  event.preventDefault();
  mouse.x = (event.clientX / sheepView.offsetWidth) * 2 - 1;
  mouse.y = -(event.clientY / sheepView.offsetHeight) * 2 + 1;
}

function selectPanel(panel, isSelected) {
  panel.faces.visible = isSelected;
  for (let line of panel.lines) {
    line.material = isSelected ? panelMaterial : lineMaterial;
  }
}

function setPanelColor(panel, color) {
  panel.faces.visible = true;
  panel.faceMaterial.color.r = color.red / 256.0;
  panel.faceMaterial.color.g = color.green / 256.0;
  panel.faceMaterial.color.b = color.blue / 256.0;
}

function initThreeJs(sheepModel, threeJsPanelBin) {
  sheepView.addEventListener('mousemove', onMouseMove, false);
  camera = new THREE.PerspectiveCamera(45, sheepView.offsetWidth / sheepView.offsetHeight, 1, 10000);
  camera.position.z = 1000;
  controls = new THREE.OrbitControls(camera, sheepView);
  scene = new THREE.Scene();
  pointMaterial = new THREE.PointsMaterial({color: 0xffffff});
  lineMaterial = new THREE.LineBasicMaterial({color: 0x444444});
  panelMaterial = new THREE.LineBasicMaterial({color: 0xff4444});
  scene.add(camera);
  renderer = new THREE.WebGLRenderer();
  renderer.setPixelRatio(window.devicePixelRatio);
  renderer.setSize(sheepView.offsetWidth, sheepView.offsetHeight);
  sheepView.appendChild(renderer.domElement);
  geom = new THREE.Geometry();
  raycaster = new THREE.Raycaster();
  raycaster.params.Points.threshold = 1;
  sphere = new THREE.Mesh(new THREE.SphereBufferGeometry(1, 32, 32), new THREE.MeshBasicMaterial({color: 0xff0000}));
  scene.add(sphere);

  // convert from SheepModel to THREE
  sheepModel.vertices.toArray().forEach(v => {
    geom.vertices.push(new THREE.Vector3(v.x, v.y, v.z));
  });
}

function addPanel(p) {
  let faces = new THREE.Geometry();
  let panelVertices = [];
  faces.faces = p.faces.faces.toArray().map(face => {
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
  faces.vertices = panelVertices;
  let lines = p.lines.toArray().map(line => {
    let lineGeo = new THREE.Geometry();
    lineGeo.vertices = line.points.toArray().map(pt => new THREE.Vector3(pt.x, pt.y, pt.z));
    return lineGeo;
  });

  let faceMaterial = new THREE.MeshBasicMaterial({color: 0xaa0000,});
  faceMaterial.side = THREE.DoubleSide;
  faceMaterial.transparent = true;
  faceMaterial.opacity = 0.75;

  var panel = {
    name: p.name,
    faceMaterial: faceMaterial,
    faces: new THREE.Mesh(faces, faceMaterial),
    lines: lines.map(line => new THREE.Line(line, lineMaterial))
  };

  panel.faces.visible = false;
  scene.add(panel.faces);
  panel.lines.forEach((line) => {
    scene.add(line);
  });

  panels.push(panel);

  select.options[select.options.length] = new Option(p.name, (panels.length - 1).toString());

  return panel;
}

function startRender() {
  geom.computeBoundingSphere();
  object = new THREE.Points(geom, pointMaterial);
  scene.add(object);
  controls.target = geom.boundingSphere.center;
  camera.lookAt(geom.boundingSphere.center);

  render();
}

var REFRESH_DELAY = 50; // ms

function render() {
  setTimeout(() => {
    requestAnimationFrame(render);
  }, REFRESH_DELAY);

  var rotSpeed = .01;
  var x = camera.position.x;
  var z = camera.position.z;
  camera.position.x = x * Math.cos(rotSpeed) + z * Math.sin(rotSpeed);
  camera.position.z = z * Math.cos(rotSpeed * 2) - x * Math.sin(rotSpeed * 2);
  camera.lookAt(scene.position);

  controls.update();
  raycaster.setFromCamera(mouse, camera);
  let intersections = raycaster.intersectObject(object);
  if (intersections.length) {
    let sorted = object.geometry.vertices.slice().sort((a, b) => (new THREE.Vector3()).subVectors(a, intersections[0].point).length() - (new THREE.Vector3()).subVectors(b, intersections[0].point).length());
    let nearest = sorted[0];
    sphere.position.copy(nearest);
    sphere.visible = true;
    let index = object.geometry.vertices.indexOf(nearest);
    document.getElementById('info').innerText = (index + 1).toString();
  } else {
    sphere.visible = false;
    document.getElementById('info').innerText = '';
  }
  renderer.render(scene, camera);
}