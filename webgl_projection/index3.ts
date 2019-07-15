import { mat4 } from "gl-matrix"

let gl = undefined;
let shaderProgram = undefined;

let rTri = 0
let rSquare = 0
function degToRad(degrees) {
    return degrees * Math.PI / 180;
}

const shader_vs = `
  attribute vec3 aVertexPosition;
  attribute vec4 aVertexColor;
  uniform mat4 uMVMatrix;
  uniform mat4 uPMatrix;
  varying vec4 vColor;
  void main(void) {
      gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);
      vColor = aVertexColor;
  }
`;

const shader_fs = `
  precision mediump float;
  varying vec4 vColor;
  void main(void) {
      gl_FragColor = vColor;
  }
`;

function initGL(canvas) {
    gl = canvas.getContext("experimental-webgl");
    gl.viewportWidth = canvas.width;
    gl.viewportHeight = canvas.height;

    if( !gl )
        alert("Could not initialise WebGL, sorry");
}

function getShader(gl, type, str) {
    let shader = gl.createShader(type)
    gl.shaderSource(shader, str);
    gl.compileShader(shader);
    gl.getShaderParameter(shader, gl.COMPILE_STATUS);
    return shader;
}

function initShaders() {
    const fragmentShader = getShader(gl, gl.FRAGMENT_SHADER, shader_fs)
    const vertexShader = getShader(gl, gl.VERTEX_SHADER, shader_vs)
    shaderProgram = gl.createProgram()
    gl.attachShader(shaderProgram, vertexShader);
    gl.attachShader(shaderProgram, fragmentShader);
    gl.linkProgram(shaderProgram);
    gl.getProgramParameter(shaderProgram, gl.LINK_STATUS)
    gl.useProgram(shaderProgram)

    shaderProgram.vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "aVertexPosition")
    gl.enableVertexAttribArray(shaderProgram.vertexPositionAttribute);
    shaderProgram.vertexColorAttribute = gl.getAttribLocation(shaderProgram, "aVertexColor")
    gl.enableVertexAttribArray(shaderProgram.vertexColorAttribute);

    shaderProgram.pMatrixUniform = gl.getUniformLocation(shaderProgram, "uPMatrix")
    shaderProgram.mvMatrixUniform = gl.getUniformLocation(shaderProgram, "uMVMatrix")
}

function initBuffers() : any[]{
    const triangleVertexPositionBuffer = gl.createBuffer()
    gl.bindBuffer(gl.ARRAY_BUFFER, triangleVertexPositionBuffer);
    let vertices = [
      0.0, 1.0, 0.0,
      -1.0, -1.0, 0.0,
      1.0, -1.0, 0.0
    ]
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vertices), gl.STATIC_DRAW);
    triangleVertexPositionBuffer.itemSize = 3
    triangleVertexPositionBuffer.numItems = 3

    const triangleVertexColorBuffer = gl.createBuffer()
    gl.bindBuffer(gl.ARRAY_BUFFER, triangleVertexColorBuffer);
    let colors = [
      1.0, 0.0, 0.0, 1.0,
      0.0, 1.0, 0.0, 1.0,
      0.0, 0.0, 1.0, 1.0
    ]
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(colors), gl.STATIC_DRAW);
    triangleVertexColorBuffer.itemSize = 4
    triangleVertexColorBuffer.numItems = 3

    const squareVertexPositionBuffer = gl.createBuffer()
    gl.bindBuffer(gl.ARRAY_BUFFER, squareVertexPositionBuffer);
    vertices = [
      1.0, 1.0, 0.0,
      -1.0, 1.0, 0.0,
      1.0, -1.0, 0.0,
      -1.0, -1.0, 0.0
    ];
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vertices), gl.STATIC_DRAW);
    squareVertexPositionBuffer.itemSize = 3
    squareVertexPositionBuffer.numItems = 4

    const squareVertexColorBuffer = gl.createBuffer()
    gl.bindBuffer(gl.ARRAY_BUFFER, squareVertexColorBuffer);
    colors = []
    for(let i =0; i < 4; i++)
        colors = colors.concat([0.5, 0.5, 1.0, 1.0])
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(colors), gl.STATIC_DRAW);
    squareVertexColorBuffer.itemSize = 4
    squareVertexColorBuffer.numItems = 4

    return [triangleVertexPositionBuffer,triangleVertexColorBuffer,squareVertexPositionBuffer,squareVertexColorBuffer]
}

function drawObject(type, buffer, colorBuffer, mvMatrix, pMatrix) {
    //console.log(type, buffer, colorBuffer, mvMatrix, pMatrix);
    gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
    gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, buffer.itemSize, gl.FLOAT, false, 0, 0);
    gl.bindBuffer(gl.ARRAY_BUFFER, colorBuffer);
    gl.vertexAttribPointer(shaderProgram.vertexColorAttribute, colorBuffer.itemSize, gl.FLOAT, false, 0, 0);

    gl.uniformMatrix4fv(shaderProgram.pMatrixUniform, false, pMatrix);
    gl.uniformMatrix4fv(shaderProgram.mvMatrixUniform, false, mvMatrix);
    gl.drawArrays(type, 0, buffer.numItems);
}

// model-view
let mvMatrix = mat4.create()
let pMatrix = mat4.create()

let mvMatrixStack = []
function mvPushMatrix() {
    mvMatrixStack.push(mat4.clone(mvMatrix));
}

function mvPopMatrix() {
    if(mvMatrixStack.length == 0)
        throw "Invalid popMatrix!"
    mvMatrix = mvMatrixStack.pop()
}

function drawScene(commands) {
    gl.viewport(0, 0, gl.viewportWidth, gl.viewportHeight);
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

    mat4.identity(mvMatrix);
    mat4.translate(mvMatrix, mvMatrix, [0, 0, -5.0]); // camera position?

    commands.forEach(command => {
        mvPushMatrix()
        mat4.translate(mvMatrix, mvMatrix, command[5]);
        mat4.rotate(mvMatrix, mvMatrix, degToRad(command[3]), command[4])
        drawObject(command[0], command[1], command[2], mvMatrix, pMatrix);
        mvPopMatrix();
    });
}


let lastTime = 0
function animate() {
    const timeNow = new Date().getTime()
    if(lastTime != 0) {
        const elapsed = timeNow - lastTime
        rTri += (90 * elapsed) / 1000.0
        rSquare += (75 * elapsed) / 1000.0
    }
    lastTime = timeNow
}

function doit(canvas) {
    initGL(canvas);
    initShaders();

    mat4.perspective(pMatrix, 45 * Math.PI / 180.0, gl.viewportWidth / gl.viewportHeight, 0.1, 100.0);

    const [triangleVertexPositionBuffer,triangleVertexColorBuffer,squareVertexPositionBuffer,squareVertexColorBuffer] = initBuffers();
    gl.clearColor(0.0, 0.0, 0.0, 1.0);
    gl.enable(gl.DEPTH_TEST);

    function tick() {
        requestAnimationFrame(tick)
        drawScene([
          [gl.TRIANGLES, triangleVertexPositionBuffer, triangleVertexColorBuffer, rTri, [0, 1, 0], [-1.5, 0.0, 0.0]],
          [gl.TRIANGLE_STRIP, squareVertexPositionBuffer, squareVertexColorBuffer, rSquare, [1, 0, 0], [1.5, 0.0, 0.0]],
        ])
        animate()
    }
    tick()
}

doit( document.getElementById("canvas") )