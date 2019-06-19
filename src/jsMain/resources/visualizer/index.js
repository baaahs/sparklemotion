
let container, camera, controls, scene, renderer, asm, stats, clock, totalPixelCount = 0, globalBbox, globalExtents, majorAxis, currentWavePosition;

const BLACK = new THREE.Color(0,0,0);
const WHITE = new THREE.Color(1,1,1);

let orderedPanelNames = [];
let orderedPanelOffsets = [];

let hues = [];
let randomSeed = Math.random();
const ONE_OVER_PHI = 0.618033988749895;
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

init();

function init() {
  container = document.createElement('div');
  container.style.position = 'absolute';
  container.style.top = '0px';
  container.style.left = '0px';
  container.setAttribute('id', 'container');
  document.body.appendChild(container);
  camera = new THREE.PerspectiveCamera(45, window.innerWidth / window.innerHeight, 1, 100000);
  camera.position.z = 1000;
  scene = new THREE.Scene();
  scene.add(camera);
  renderer = new THREE.WebGLRenderer({ alpha: true, autoClear: false });
  renderer.setPixelRatio(window.devicePixelRatio);
  renderer.setSize(window.innerWidth, window.innerHeight);
  container.appendChild(renderer.domElement);
  controls = new THREE.OrbitControls(camera, renderer.domElement);
  controls.enableKeys = false;
  stats = new Stats();
  stats.showPanel(0);
  document.body.appendChild(stats.dom);
  stats.domElement.style.removeProperty('left');
  stats.domElement.style.right = '0px';
  clock = new THREE.Clock();

  fetch('panelDef.json')
  .then(res => res.json())
  .then((panelDef) => {

    let geom = new THREE.Geometry();
    geom.vertices.push(...panelDef.vertices.map(v => new THREE.Vector3(v.x, v.y, v.z )));
    geom.computeBoundingSphere();
    geom.computeBoundingBox();
    globalBbox = geom.boundingBox;
    globalExtents = globalBbox.max.clone().sub(globalBbox.min);

    let maxAxis = Math.max(globalExtents.x, globalExtents.y, globalExtents.z);
    if (maxAxis === globalExtents.x) majorAxis = 'x';
    else if (majorAxis === globalExtents.y) majorAxis = 'y';
    else (majorAxis = 'z');

    currentWavePosition = globalBbox.min[majorAxis];

    controls.target = geom.boundingSphere.center;
    camera.lookAt(geom.boundingSphere.center);
    let count = 0;
    for (let p of Object.values(panelDef.panels)) {
      totalPixelCount += p.numPixels;
      orderedPanelNames.push(p.name);
      orderedPanelOffsets.push(count);
      count += p.numPixels;
    }

    for (let i = 0; i < totalPixelCount; ++i) {
      hues.push(randomHue());
    }

    return fetch('textureDef.json')
    .then(res => res.json())
    .then((textureDef) => {
      asm = new Assembly(panelDef, textureDef, 512);
      asm.generateRandomPixelsForAllPanels(BLACK);
      scene.add(asm);
      render();
    });
  });
}

const tempo = 60; // bpm
let globalHue = 0;

function render() {

  let delta = clock.getDelta();


  globalHue = (globalHue + delta / 10) % 1;
  currentWavePosition += globalExtents[majorAxis] * (delta / (60 / tempo));
  if (currentWavePosition >= globalBbox.max[majorAxis]) {
    currentWavePosition -= globalExtents[majorAxis];
  }
  for (let p of Object.values(asm.panels)) {
    for (let i = 0; i < p.numPixels; ++i) {
      let x = p.getPixelPositionInAssemblySpace(i)[majorAxis];
      if (x > currentWavePosition) x -= globalExtents[majorAxis];
      let v = (currentWavePosition - x) / globalExtents[majorAxis];
      v = Math.max(0, (1 - v));
      p.setPixelColor(i, hsvToRgb(globalHue, 0.9, v));
    }
  }


  // for (let i = 0; i < totalPixelCount; ++i) {
  //   hues[i] = (hues[i] + delta) % 1;
  // }
  // asm.setPixelColors(hues.map(v => hsvToRgb(v, 0.9, 0.9)));



  controls.update();
  stats.begin();
  renderer.clear();
  renderer.render(scene, camera);
  stats.end();
  requestAnimationFrame(render);
}

