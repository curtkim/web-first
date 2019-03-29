let currentThread = 'main';
let isAnimating = false; // start with animation stopped
let isSlow = false;
let worker;

// Animate the canvas using the worker, and stop the main thread if it is running there
function animateOnWorker() {
  init();

  // Prepare this canvas to be transferred to the worker
  const offscreenCanvas = canvas.transferControlToOffscreen();

  // Get the worker script from this page, and create an object url from it
  // so it can be loaded without being a separate file.
  const workerScript = document.getElementById('workerScript').textContent;
  const blob = new Blob([workerScript], {type: 'text/javascript'});
  const url = URL.createObjectURL(blob);
  worker = new Worker(url);

  // Pass our init message to the worker, along with the transferred canvas
  worker.postMessage({msg: 'init', canvas: offscreenCanvas}, [offscreenCanvas]);

  // We're done with the object url, get rid of it to save memory
  URL.revokeObjectURL(url);
}

// Animate the canvas using the main thread
function animateOnMain() {
  init();
  window.postMessage({msg: 'init'}, '*');
}

/*****
 *
 * Below here are the things that make the demo run better, but
 * the important stuff is above here!
 *
 *****/

function init() {
  stopAnimation();

  // We need to make a new canvas every time we switch between the main thread
  // and the worker. This is because a canvas cannot be transferred once it has
  // a rendering context.
  document.getElementById('canvas-container').innerHTML = '<canvas id="canvas"></canvas>';
  canvas = document.getElementById('canvas');
  canvas.width = canvas.clientWidth;
  canvas.height = canvas.clientHeight;

  // when running on the main thread, we can't pass the canvas in a postMessage,
  // so we just set it globally
  window.canvas = canvas;

  tickTimer();
}

function setThread(thread) {
  currentThread = thread;

  if(currentThread === 'main') {
    animateOnMain();
  } else if(currentThread === 'worker') {
    animateOnWorker();
  }

  // Getting slow to turn on when we switch requires listening
  // for the worker to be ready, which is a bit complicated for now.
  // So, we just turn off slow.
  toggleSlow(false);

  document.querySelector('#toggle-thread .target').innerText = currentThread === 'main' ? 'Worker' : 'Main';
  document.querySelector('#canvas-info .thread').innerText = currentThread;
}

function toggleThread() {
  if(typeof currentThread !== 'undefined' && currentThread === 'main') {
    setThread('worker');
  } else {
    setThread('main');
  }
}

function toggleAnimation() {
  isAnimating = !isAnimating;

  if(isAnimating) {
    setThread(currentThread);
  } else {
    stopAnimation();
  }

  document.querySelector('#toggle-animation .action').innerText = isAnimating ? 'Stop' : 'Start';
}

function stopAnimation() {
  if(worker) {
    worker.terminate();
    worker = null;
  }

  [window.canvasRafId, window.timerRafId].map(rafId => {
    if(rafId) {
      window.cancelAnimationFrame(rafId);
    }
  });
}

function toggleSlow(shouldSlow) {
  isSlow = typeof shouldSlow === 'boolean' ? shouldSlow : !isSlow;

  if(worker) {
    worker.postMessage({msg: 'slow', shouldSlow: isSlow});
  } else {
    slow(isSlow);
  }

  document.querySelector('#toggle-slow .action').innerText = isSlow ? 'Unslow' : 'Slow';
  document.querySelector('#canvas-info .slow').innerText = isSlow ? '(slow)' : '';
}

const timeEl = document.getElementById('time');
function tickTimer() {
  timeEl.innerText = Date.now();
  window.timerRafId = window.requestAnimationFrame(tickTimer);
}

if(typeof window.OffscreenCanvas !== 'function') {
  document.getElementById('unsupported-warning').style.display = '';
  document.getElementById('toggle-thread').setAttribute('disabled', 'disabled');
}