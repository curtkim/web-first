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
  uniform mat4 uMVMatrix;
  uniform mat4 uPMatrix;
  void main(void) {
      gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);
  }
`;

const shader_fs = `
precision mediump float;
void main(void) {
  gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
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

    shaderProgram.pMatrixUniform = gl.getUniformLocation(shaderProgram, "uPMatrix")
    shaderProgram.mvMatrixUniform = gl.getUniformLocation(shaderProgram, "uMVMatrix")
}

function initBuffers() : any[]{
    const triangleVertexPositionBuffer = gl.createBuffer()
    gl.bindBuffer(gl.ARRAY_BUFFER, triangleVertexPositionBuffer);
    let vertices = [
    /* X Axis */
    -1.0,  0.0,  0.0,
    0.9,  0.0,  0.0,
    0.9, -0.1,  0.0,
    1.0,  0.0,  0.0,
    0.9,  0.1,  0.0,
    0.9,  0.0,  0.0,
    0.0,  0.0,  0.0,

    /* Y Axis */
    0.0, -1.0,  0.0,
    0.0,  0.9,  0.0,
   -0.1,  0.9,  0.0,
    0.0,  1.0,  0.0,
    0.1,  0.9,  0.0,
    0.0,  0.9,  0.0,
    0.0,  0.0,  0.0,

    /* Z Axis */
    0.0,  0.0, -1.0,
    0.0,  0.0,  0.9,
   -0.1, -0.1,  0.9,
    0.0,  0.0,  1.0,
    0.1,  0.1,  0.9,
    0.0,  0.0,  0.9,
    0.0,  0.0,  0.0,

    /* House Front*/
    0.6,  0.0,  0.0,
    0.6,  0.6,  0.0,
    0.0,  0.6,  0.0,
    0.3,  0.9,  0.0,
    0.6,  0.6,  0.0,
    0.6,  0.0,  0.0,

    /* House Back */
    0.6,  0.0,  0.8,
    0.6,  0.6,  0.8,
    0.0,  0.6,  0.8,
    0.3,  0.9,  0.8,
    0.6,  0.6,  0.8,

    /* Additional lines */
    0.6,  0.6,  0.0,
    0.3,  0.9,  0.0,
    0.3,  0.9,  0.8,
    0.0,  0.6,  0.8,
    0.0,  0.6,  0.0,
    0.6,  0.6,  0.0,
    0.6,  0.6,  0.8,
    0.0,  0.6,  0.8,
    0.0,  0.0,  0.8,
    0.6,  0.0,  0.8
    ]
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(vertices), gl.STATIC_DRAW);
    triangleVertexPositionBuffer.itemSize = 3
    triangleVertexPositionBuffer.numItems = vertices.length / 3

    return triangleVertexPositionBuffer
}

function drawObject(type, buffer,  mvMatrix, pMatrix) {
    //console.log(type, buffer, colorBuffer, mvMatrix, pMatrix);
    gl.bindBuffer(gl.ARRAY_BUFFER, buffer);
    gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, buffer.itemSize, gl.FLOAT, false, 0, 0);

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
    mat4.translate(mvMatrix, mvMatrix, [0, 0, -4.0]); // camera position?

    commands.forEach(command => {
        mvPushMatrix()
        mat4.translate(mvMatrix, mvMatrix, command[4]);
        mat4.rotate(mvMatrix, mvMatrix, degToRad(command[2]), command[3])
        drawObject(command[0], command[1], mvMatrix, pMatrix);
        mvPopMatrix();
    });
}


function doit(canvas) {
    initGL(canvas);
    initShaders();

    mat4.perspective(pMatrix, 45 * Math.PI / 180.0, gl.viewportWidth / gl.viewportHeight, 0.1, 100.0);

    const triangleVertexPositionBuffer = initBuffers();
    gl.clearColor(0.0, 0.0, 0.0, 1.0);
    gl.enable(gl.DEPTH_TEST);

    drawScene([
        [gl.LINE_STRIP, triangleVertexPositionBuffer, 0, [0, 1, 0], [0, 0.0, 0.0]],
    ])
}

doit( document.getElementById("canvas") )