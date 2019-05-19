export default function threeEntryPoint(containerElement) {
  const canvas = createCanvas(document, containerElement);
  // todo const sceneManager = new SceneManager(canvas);

  initThreeJs(containerElement);
  bindEventListeners();
  render();

  function createCanvas(document, containerElement) {
    const canvas = document.createElement('canvas');
    canvas.setAttribute('id', 'sheepView');
    containerElement.appendChild(canvas);

    return canvas;
  }

  function bindEventListeners() {
    window.onresize = resizeCanvas;
    resizeCanvas();
  }

  function resizeCanvas() {
    canvas.style.width = '100%';
    canvas.style.height = '100%';

    canvas.width = canvas.offsetWidth;
    canvas.height = canvas.offsetHeight;

    // todo sceneManager.onWindowResize();
    window.visualizer.doResize();
  }

  function render(time) {
    window.render();
  }
}
