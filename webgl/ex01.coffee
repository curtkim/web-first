gl = undefined
shaderProgram = undefined

shader_vs = '''
  attribute vec3 aVertexPosition;
  uniform mat4 uMVMatrix;
  uniform mat4 uPMatrix;
  void main(void) {
    gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);
  }
'''
shader_fs = '''
  precision mediump float;
  void main(void) {
    gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
  }
'''

initGL = (canvas) ->
  try
    gl = canvas.getContext("experimental-webgl")
    gl.viewportWidth = canvas.width
    gl.viewportHeight = canvas.height
  alert "Could not initialise WebGL, sorry :-("  unless gl

getShader = (gl, type, str) ->
  shader = gl.createShader(type)
  gl.shaderSource shader, str
  gl.compileShader shader
  unless gl.getShaderParameter(shader, gl.COMPILE_STATUS)
    alert gl.getShaderInfoLog(shader)
    return null
  shader

initShaders = ->
  fragmentShader = getShader(gl, gl.FRAGMENT_SHADER, shader_fs)
  vertexShader = getShader(gl, gl.VERTEX_SHADER, shader_vs)
  shaderProgram = gl.createProgram()
  gl.attachShader shaderProgram, vertexShader
  gl.attachShader shaderProgram, fragmentShader
  gl.linkProgram shaderProgram
  alert "Could not initialise shaders"  unless gl.getProgramParameter(shaderProgram, gl.LINK_STATUS)
  gl.useProgram shaderProgram
  shaderProgram.vertexPositionAttribute = gl.getAttribLocation(shaderProgram, "aVertexPosition")
  gl.enableVertexAttribArray shaderProgram.vertexPositionAttribute
  shaderProgram.pMatrixUniform = gl.getUniformLocation(shaderProgram, "uPMatrix")
  shaderProgram.mvMatrixUniform = gl.getUniformLocation(shaderProgram, "uMVMatrix")



initBuffers = ->
  triangleVertexPositionBuffer = gl.createBuffer()
  gl.bindBuffer gl.ARRAY_BUFFER, triangleVertexPositionBuffer
  vertices = [
    0.0, 1.0, 0.0,
    -1.0, -1.0, 0.0,
    1.0, -1.0, 0.0
  ]
  gl.bufferData gl.ARRAY_BUFFER, new Float32Array(vertices), gl.STATIC_DRAW
  triangleVertexPositionBuffer.itemSize = 3
  triangleVertexPositionBuffer.numItems = 3

  squareVertexPositionBuffer = gl.createBuffer()
  gl.bindBuffer gl.ARRAY_BUFFER, squareVertexPositionBuffer
  vertices = [
    1.0, 1.0, 0.0,
    -1.0, 1.0, 0.0,
    1.0, -1.0, 0.0,
    -1.0, -1.0, 0.0
  ]
  gl.bufferData gl.ARRAY_BUFFER, new Float32Array(vertices), gl.STATIC_DRAW
  squareVertexPositionBuffer.itemSize = 3
  squareVertexPositionBuffer.numItems = 4

  {triangleVertexPositionBuffer, squareVertexPositionBuffer}


drawObject = (type, buffer, matrix, mvMatrix, pMatrix)->
  gl.bindBuffer gl.ARRAY_BUFFER, buffer
  gl.vertexAttribPointer shaderProgram.vertexPositionAttribute, buffer.itemSize, gl.FLOAT, false, 0, 0
  gl.uniformMatrix4fv shaderProgram.pMatrixUniform, false, pMatrix
  gl.uniformMatrix4fv shaderProgram.mvMatrixUniform, false, mvMatrix
  gl.drawArrays type, 0, buffer.numItems
  mat4.translate mvMatrix, matrix

drawScene = (commands)->
  gl.viewport 0, 0, gl.viewportWidth, gl.viewportHeight
  gl.clear gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT

  mvMatrix = mat4.create()
  pMatrix = mat4.create()

  mat4.perspective 45, gl.viewportWidth / gl.viewportHeight, 0.1, 100.0, pMatrix
  mat4.identity mvMatrix
  mat4.translate mvMatrix, [-1.5, 0.0, -7.0]

  drawObject command[0], command[1], command[2], mvMatrix, pMatrix for command in commands

doit = (canvas)->
  initGL canvas
  initShaders()
  {triangleVertexPositionBuffer, squareVertexPositionBuffer} = initBuffers()
  gl.clearColor 0.0, 0.0, 0.0, 1.0
  gl.enable gl.DEPTH_TEST
  drawScene([
    [gl.TRIANGLES, triangleVertexPositionBuffer, [3.0, 0.0, 0.0]]
    [gl.TRIANGLE_STRIP, squareVertexPositionBuffer, [0.0, 0.0, 0.0]]
  ])

window.doit = doit