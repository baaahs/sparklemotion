const renderPixels = true;

const pixelDensity = 0.2;

const omitPanels = [
  '60R',
  '60L', // ears
  'Face',
  'Tail',
];

let totalPixels = 0;

document.movingHeads = {};

const vizRotationEl = document.getElementById('vizRotation');

/////////////////////// Mapper ///////////////////////
let mapper = { running: false };

const REFRESH_DELAY = 50; // ms
const resizeDelay = 100;

export default class ThreeVisualizer {
  constructor(canvasContainer, sheepModel, frameListenersList) {
    this.canvasContainer = canvasContainer;
    this.frameListeners = frameListenersList;
    this.rendererListeners = [];

    this.panels = [];

    this.select = document.getElementById('panelSelect');
    this.info2 = document.getElementById('info2');

    this.select.onchange = this.onSelectChange;

    this.mouse = new THREE.Vector2();

    this.camera = new THREE.PerspectiveCamera(
        45,
        canvasContainer.offsetWidth / canvasContainer.offsetHeight,
        1,
        10000
    );
    this.camera.position.z = 1000;

    this.controls = new THREE.OrbitControls(this.camera, canvasContainer);
    this.controls.minPolarAngle = Math.PI / 2 - 0.25; // radians
    this.controls.maxPolarAngle = Math.PI / 2 + 0.25; // radians

    canvasContainer.addEventListener(
      'mousemove',
      function(event) {
        onMouseMove(event, canvasContainer);
      },
      false
    );

    this.scene = new THREE.Scene();
    this.pointMaterial = new THREE.PointsMaterial({ color: 0xffffff });
    this.lineMaterial = new THREE.LineBasicMaterial({ color: 0xaaaaaa });
    this.panelMaterial = new THREE.LineBasicMaterial({
      color: 0xaaaaaa,
      linewidth: 3,
    });
    this.scene.add(this.camera);
    this.renderer = new THREE.WebGLRenderer();
    this.renderer.setPixelRatio(window.devicePixelRatio);
    this.renderer.setSize(
      canvasContainer.offsetWidth,
      canvasContainer.offsetHeight
    );
    canvasContainer.appendChild(this.renderer.domElement);
    this.geom = new THREE.Geometry();
    this.raycaster = new THREE.Raycaster();
    this.raycaster.params.Points.threshold = 1;
    this.sphere = new THREE.Mesh(
      new THREE.SphereBufferGeometry(1, 32, 32),
      new THREE.MeshBasicMaterial({ color: 0xff0000 })
    );
    this.scene.add(this.sphere);

    // convert from SheepModel to THREE
    sheepModel.vertices.toArray().forEach((v) => {
      this.geom.vertices.push(new THREE.Vector3(v.x, v.y, v.z));
    });

    this.startRender();

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
  }

  onSelectChange = (event) => {
    let idx = parseInt(event.target.value);
    for (let i = 0; i < this.panels.length; ++i) {
      this.panels[i].faces.visible = i === idx;
      for (let line of this.panels[i].lines) {
        line.material = i === idx ? this.panelMaterial : this.lineMaterial;
      }
    }
    // surface area
    let area = 0;
    for (let face of this.panels[idx].faces.geometry.faces) {
      let a = this.panels[idx].faces.geometry.vertices[face.a];
      let b = this.panels[idx].faces.geometry.vertices[face.b];
      let c = this.panels[idx].faces.geometry.vertices[face.c];
      let u = b.clone().sub(a);
      let v = c.clone().sub(a);
      area +=
        Math.sqrt(
          (u.y * v.z - u.z * v.y) ** 2 +
            (u.z * v.x - u.x * v.z) ** 2 +
            (u.x * v.y - u.y * v.x) ** 2
        ) / 2;
    }
    this.info2.innerText = `Total surface area: ${(area / 144).toFixed(
      2
    )} sq. ft.`;
  };

  onMouseMove = (event, canvasContainer) => {
    event.preventDefault();
    this.mouse.x = (event.clientX / canvasContainer.offsetWidth) * 2 - 1;
    this.mouse.y = -(event.clientY / canvasContainer.offsetHeight) * 2 + 1;
  };

  selectPanel = (panel, isSelected) => {
    panel.faces.visible = isSelected;
    for (let line of panel.lines) {
      line.material = isSelected ? this.panelMaterial : this.lineMaterial;
    }
  };

  addPanel = (p) => {
    // if (p.name !== '15R') return;
    // if (omitPanels.includes(p.name)) return;

    console.log('adding panel ', p);

    let vizPanel = new VizPanel(this.geom, this.lineMaterial, this.scene, p);
    this.panels.push(vizPanel);

    let pixelCount = Math.floor(vizPanel.area * pixelDensity);
    // console.log("Panel " + p.name + " area is " + vizPanel.area + "; will add " + pixelCount + " pixels");

    // try to draw pixel-ish things...
    if (renderPixels) {
      vizPanel.addPixels(this.scene, pixelCount);
    }

    totalPixels += pixelCount;
    document.getElementById(
      'visualizerPixelCount'
    ).innerText = totalPixels.toString();

    this.select.options[this.select.options.length] = new Option(
      p.name,
      (this.panels.length - 1).toString()
    );

    return vizPanel;
  };

  setPanelColor = (vizPanel, panelBgColor, pixelColors) => {
    if (vizPanel == null) return;

    vizPanel.setPanelColor(panelBgColor, pixelColors);
  };

  addMovingHead = (movingHead) => {
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

    this.scene.add(cone);
    return {
      cone: cone,
      material: cone.material,
    };
  };

  adjustMovingHead = (movingHeadJs, color, dimmer, pan, tilt) => {
    movingHeadJs.material.color.r = color.redF;
    movingHeadJs.material.color.g = color.greenF;
    movingHeadJs.material.color.b = color.blueF;

    movingHeadJs.material.visible = dimmer > 0.1;

    movingHeadJs.cone.rotation.x = -Math.PI / 2 + tilt;
    movingHeadJs.cone.rotation.z = pan;
  };

  setMapperRunning = (isRunning, jsMapperDisplay) => {
    mapper.isRunning = isRunning;

    this.panels.forEach(
      (panel) => (panel.faceMaterial.transparent = !isRunning)
    );

    if (mapper.isRunning) {
      vizRotationEl.checked = false;
      // rendererListeners.push(jsMapperDisplay);
    } else {
      // let i = rendererListeners.indexOf(jsMapperDisplay);
      // if (i > -1) {
      //   rendererListeners = rendererListeners.splice(i, 1);
      // }
    }
  };

  startRender = () => {
    this.geom.computeBoundingSphere();
    const object = new THREE.Points(this.geom, this.pointMaterial);
    this.scene.add(object);
    let target = this.geom.boundingSphere.center.clone();
    this.controls.target = target;
    this.camera.lookAt(target);

    this.render();
  };

  render = () => {
    setTimeout(() => {
      requestAnimationFrame(this.render);
    }, REFRESH_DELAY);

    if (!mapper.isRunning) {
      if (vizRotationEl.checked) {
        const rotSpeed = 0.01;
        const x = this.camera.position.x;
        const z = this.camera.position.z;
        this.camera.position.x =
          x * Math.cos(rotSpeed) + z * Math.sin(rotSpeed);
        this.camera.position.z =
          z * Math.cos(rotSpeed * 2) - x * Math.sin(rotSpeed * 2);
        this.camera.lookAt(this.scene.position);
      }
    }

    this.controls.update();

    this.raycaster.setFromCamera(this.mouse, this.camera);
    let intersections = this.raycaster.intersectObjects(this.scene.children);
    if (intersections.length) {
      const intersection = intersections[0];
      if (intersection && intersection.object && intersection.object.panel) {
        document.getElementById('selectionInfo').innerText =
          'Selected: ' + intersections[0].object.panel.name;
      }
    }

    this.renderer.render(this.scene, this.camera);

    this.frameListeners
      .forEach((f) => f.onFrameReady(this.scene, this.camera));
    this.rendererListeners.forEach((value) => value());
  };

  drawMapperImage = (image) => {
    const camCtx = mapper.camCanvas.getContext('2d');
    camCtx.fillStyle = '#006600';
    camCtx.fillRect(0, 0, mapper.width / 2, mapper.height / 2);
    camCtx.putImageData(image.imageData, 0, 0);
  };

  doResize = (evt) => {
    this.camera.aspect = this.canvasContainer.offsetWidth / this.canvasContainer.offsetHeight;
    this.camera.updateProjectionMatrix();
    this.renderer.setSize(this.canvasContainer.offsetWidth, this.canvasContainer.offsetHeight);
  };
}

// vector.applyMatrix(object.matrixWorld).project(camera) to get 2d x,y coord

