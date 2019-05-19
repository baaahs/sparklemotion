const renderPixels = true;

let controls,
  camera,
  scene,
  renderer,
  geom,
  object,
  pointMaterial,
  faceMaterial,
  lineMaterial,
  panelMaterial,
  raycaster,
  mouse,
  sphere;
let frameListeners = [];
let rendererListeners = [];

let panels = [];

let select = document.getElementById('panelSelect');
let info2 = document.getElementById('info2');

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
    area +=
      Math.sqrt(
        (u.y * v.z - u.z * v.y) ** 2 +
          (u.z * v.x - u.x * v.z) ** 2 +
          (u.x * v.y - u.y * v.x) ** 2
      ) / 2;
  }
  info2.innerText = `Total surface area: ${(area / 144).toFixed(2)} sq. ft.`;
}

function onMouseMove(event, canvasContainer) {
  event.preventDefault();
  mouse.x = (event.clientX / canvasContainer.offsetWidth) * 2 - 1;
  mouse.y = -(event.clientY / canvasContainer.offsetHeight) * 2 + 1;
}

function selectPanel(panel, isSelected) {
  panel.faces.visible = isSelected;
  for (let line of panel.lines) {
    line.material = isSelected ? panelMaterial : lineMaterial;
  }
}

function initThreeJs(canvasContainer, sheepModel, frameListenersList) {
  frameListeners = frameListenersList;

  canvasContainer.addEventListener(
    'mousemove',
    function(event) {
      onMouseMove(event, canvasContainer);
    },
    false
  );
  camera = new THREE.PerspectiveCamera(
    45,
    canvasContainer.offsetWidth / canvasContainer.offsetHeight,
    1,
    10000
  );
  camera.position.z = 1000;
  controls = new THREE.OrbitControls(camera, canvasContainer);
  controls.minPolarAngle = Math.PI / 2 - 0.25; // radians
  controls.maxPolarAngle = Math.PI / 2 + 0.25; // radians

  scene = new THREE.Scene();
  pointMaterial = new THREE.PointsMaterial({ color: 0xffffff });
  lineMaterial = new THREE.LineBasicMaterial({ color: 0xaaaaaa });
  panelMaterial = new THREE.LineBasicMaterial({
    color: 0xaaaaaa,
    linewidth: 3,
  });
  scene.add(camera);
  renderer = new THREE.WebGLRenderer();
  renderer.setPixelRatio(window.devicePixelRatio);
  renderer.setSize(canvasContainer.offsetWidth, canvasContainer.offsetHeight);
  canvasContainer.appendChild(renderer.domElement);
  geom = new THREE.Geometry();
  raycaster = new THREE.Raycaster();
  raycaster.params.Points.threshold = 1;
  sphere = new THREE.Mesh(
    new THREE.SphereBufferGeometry(1, 32, 32),
    new THREE.MeshBasicMaterial({ color: 0xff0000 })
  );
  scene.add(sphere);

  // convert from SheepModel to THREE
  sheepModel.vertices.toArray().forEach((v) => {
    geom.vertices.push(new THREE.Vector3(v.x, v.y, v.z));
  });

  startRender();
}

const pixelDensity = 0.2;

const omitPanels = [
  '60R',
  '60L', // ears
  'Face',
  'Tail',
];

let totalPixels = 0;

function addPanel(p) {
  // if (p.name !== '15R') return;
  // if (omitPanels.includes(p.name)) return;

  let vizPanel = new VizPanel(p);
  panels.push(vizPanel);

  let pixelCount = Math.floor(vizPanel.area * pixelDensity);
  // console.log("Panel " + p.name + " area is " + vizPanel.area + "; will add " + pixelCount + " pixels");

  // try to draw pixel-ish things...
  if (renderPixels) {
    vizPanel.addPixels(pixelCount);
  }

  totalPixels += pixelCount;
  document.getElementById(
    'visualizerPixelCount'
  ).innerText = totalPixels.toString();

  select.options[select.options.length] = new Option(
    p.name,
    (panels.length - 1).toString()
  );

  return vizPanel;
}

function setPanelColor(vizPanel, panelBgColor, pixelColors) {
  if (vizPanel == null) return;

  vizPanel.setPanelColor(panelBgColor, pixelColors);
}

document.movingHeads = {};

function addMovingHead(movingHead) {
  let geometry = new THREE.ConeBufferGeometry(50, 1000);
  geometry.applyMatrix(new THREE.Matrix4().makeTranslation(0, -500, 0));
  let material = new THREE.MeshBasicMaterial({ color: 0xffff00 });
  material.transparent = true;
  material.opacity = 0.75;
  let cone = new THREE.Mesh(geometry, material);
  cone.position.set(
    movingHead.origin.x,
    movingHead.origin.y,
    movingHead.origin.z
  );
  cone.rotation.x = -Math.PI / 2;

  scene.add(cone);
  return {
    cone: cone,
    material: cone.material,
  };
}

function adjustMovingHead(movingHeadJs, color, dimmer, pan, tilt) {
  movingHeadJs.material.color.r = color.redF;
  movingHeadJs.material.color.g = color.greenF;
  movingHeadJs.material.color.b = color.blueF;

  movingHeadJs.material.visible = dimmer > 0.1;

  movingHeadJs.cone.rotation.x = -Math.PI / 2 + tilt;
  movingHeadJs.cone.rotation.z = pan;
}

const vizRotationEl = document.getElementById('vizRotation');

/////////////////////// Mapper ///////////////////////
let mapper = { running: false };

function setMapperRunning(isRunning, jsMapperDisplay) {
  mapper.isRunning = isRunning;

  panels.forEach((panel) => (panel.faceMaterial.transparent = !isRunning));

  if (mapper.isRunning) {
    vizRotationEl.checked = false;
    // rendererListeners.push(jsMapperDisplay);
  } else {
    // let i = rendererListeners.indexOf(jsMapperDisplay);
    // if (i > -1) {
    //   rendererListeners = rendererListeners.splice(i, 1);
    // }
  }
}

function startRender() {
  geom.computeBoundingSphere();
  object = new THREE.Points(geom, pointMaterial);
  scene.add(object);
  let target = geom.boundingSphere.center.clone();
  controls.target = target;
  camera.lookAt(target);

  render();
}

const REFRESH_DELAY = 50; // ms

function render() {
  setTimeout(() => {
    requestAnimationFrame(render);
  }, REFRESH_DELAY);

  if (!mapper.isRunning) {
    if (vizRotationEl.checked) {
      const rotSpeed = 0.01;
      const x = camera.position.x;
      const z = camera.position.z;
      camera.position.x = x * Math.cos(rotSpeed) + z * Math.sin(rotSpeed);
      camera.position.z =
        z * Math.cos(rotSpeed * 2) - x * Math.sin(rotSpeed * 2);
      camera.lookAt(scene.position);
    }
  }

  controls.update();

  raycaster.setFromCamera(mouse, camera);
  let intersections = raycaster.intersectObjects(scene.children);
  if (intersections.length) {
    const intersection = intersections[0];
    if (intersection && intersection.object && intersection.object.panel) {
      document.getElementById('selectionInfo').innerText =
        'Selected: ' + intersections[0].object.panel.name;
    }
  }

  renderer.render(scene, camera);

  frameListeners.toArray().forEach((f) => f.onFrameReady(scene, camera));
  rendererListeners.forEach((value) => value());
}

function drawMapperImage(image) {
  const camCtx = mapper.camCanvas.getContext('2d');
  camCtx.fillStyle = '#006600';
  camCtx.fillRect(0, 0, mapper.width / 2, mapper.height / 2);
  camCtx.putImageData(image.imageData, 0, 0);
}

// vector.applyMatrix(object.matrixWorld).project(camera) to get 2d x,y coord

const resizeDelay = 100;

const doResize = (evt) => {
  camera.aspect = canvasContainer.offsetWidth / canvasContainer.offsetHeight;
  camera.updateProjectionMatrix();
  renderer.setSize(canvasContainer.offsetWidth, canvasContainer.offsetHeight);
};

window.visualizer = {
  doResize: doResize,
};

(() => {
  let resizeTaskId = null;

  window.addEventListener('resize', (evt) => {
    if (resizeTaskId !== null) {
      clearTimeout(resizeTaskId);
    }

    resizeTaskId = setTimeout(() => {
      resizeTaskId = null;
      doResize(evt);
    }, resizeDelay);
  });
})();
