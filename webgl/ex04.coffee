gl = undefined
shaderProgram = undefined

shader_vs = '''
  attribute vec3 aVertexPosition;
  attribute vec4 aVertexColor;
  uniform mat4 uMVMatrix;
  uniform mat4 uPMatrix;
  varying vec4 vColor;
  void main(void) {
    gl_Position = uPMatrix * uMVMatrix * vec4(aVertexPosition, 1.0);
    vColor = aVertexColor;
  }
'''
shader_fs = '''
  precision mediump float;
  varying vec4 vColor;
  void main(void) {
    gl_FragColor = vColor;
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
  shaderProgram.vertexColorAttribute = gl.getAttribLocation(shaderProgram, "aVertexColor")
  gl.enableVertexAttribArray shaderProgram.vertexColorAttribute

  shaderProgram.pMatrixUniform = gl.getUniformLocation(shaderProgram, "uPMatrix")
  shaderProgram.mvMatrixUniform = gl.getUniformLocation(shaderProgram, "uMVMatrix")



initBuffers = ->
  pyramidVertexPositionBuffer = gl.createBuffer()
  gl.bindBuffer gl.ARRAY_BUFFER, pyramidVertexPositionBuffer

  vertices = [
    # Front face
    0.0, 1.0, 0.0,
    -1.0, -1.0, 1.0,
    1.0, -1.0, 1.0,
    # Right face
    0.0, 1.0, 0.0,
    1.0, -1.0, 1.0,
    1.0, -1.0, -1.0,
    # Back face
    0.0, 1.0, 0.0,
    1.0, -1.0, -1.0,
    -1.0, -1.0, -1.0,
    # Left face
    0.0, 1.0, 0.0,
    -1.0, -1.0, -1.0,
    -1.0, -1.0, 1.0
  ]
  gl.bufferData gl.ARRAY_BUFFER, new Float32Array(vertices), gl.STATIC_DRAW
  pyramidVertexPositionBuffer.itemSize = 3
  pyramidVertexPositionBuffer.numItems = 12
  pyramidVertexColorBuffer = gl.createBuffer()
  gl.bindBuffer gl.ARRAY_BUFFER, pyramidVertexColorBuffer

  colors = [
    # Front face
    1.0, 0.0, 0.0, 1.0,
    0.0, 1.0, 0.0, 1.0,
    0.0, 0.0, 1.0, 1.0,
    # Right face
    1.0, 0.0, 0.0, 1.0,
    0.0, 0.0, 1.0, 1.0,
    0.0, 1.0, 0.0, 1.0,
    # Back face
    1.0, 0.0, 0.0, 1.0,
    0.0, 1.0, 0.0, 1.0,
    0.0, 0.0, 1.0, 1.0,
    # Left face
    1.0, 0.0, 0.0, 1.0,
    0.0, 0.0, 1.0, 1.0,
    0.0, 1.0, 0.0, 1.0
  ]
  gl.bufferData gl.ARRAY_BUFFER, new Float32Array(colors), gl.STATIC_DRAW
  pyramidVertexColorBuffer.itemSize = 4
  pyramidVertexColorBuffer.numItems = 12


  cubeVertexPositionBuffer = gl.createBuffer()
  gl.bindBuffer gl.ARRAY_BUFFER, cubeVertexPositionBuffer

  vertices = [
    #Front face
    -1.0, -1.0,  1.0,
     1.0, -1.0,  1.0,
     1.0,  1.0,  1.0,
    -1.0,  1.0,  1.0,
    #Back face
    -1.0, -1.0, -1.0,
    -1.0,  1.0, -1.0,
     1.0,  1.0, -1.0,
     1.0, -1.0, -1.0,
    #Top face
    -1.0,  1.0, -1.0,
    -1.0,  1.0,  1.0,
     1.0,  1.0,  1.0,
     1.0,  1.0, -1.0,
    #Bottom face
    -1.0, -1.0, -1.0,
     1.0, -1.0, -1.0,
     1.0, -1.0,  1.0,
    -1.0, -1.0,  1.0,
    #Right face
     1.0, -1.0, -1.0,
     1.0,  1.0, -1.0,
     1.0,  1.0,  1.0,
     1.0, -1.0,  1.0,
    #Left face
    -1.0, -1.0, -1.0,
    -1.0, -1.0,  1.0,
    -1.0,  1.0,  1.0,
    -1.0,  1.0, -1.0
  ]
  gl.bufferData gl.ARRAY_BUFFER, new Float32Array(vertices), gl.STATIC_DRAW
  cubeVertexPositionBuffer.itemSize = 3
  cubeVertexPositionBuffer.numItems = 24
  cubeVertexColorBuffer = gl.createBuffer()
  gl.bindBuffer gl.ARRAY_BUFFER, cubeVertexColorBuffer
  # Front face
  # Back face
  # Top face
  # Bottom face
  # Right face
  colors = [
    [1.0, 0.0, 0.0, 1.0]  # Front face
    [1.0, 1.0, 0.0, 1.0]  # Back face
    [0.0, 1.0, 0.0, 1.0]  # Top face
    [1.0, 0.5, 0.5, 1.0]  # Bottom face
    [1.0, 0.0, 1.0, 1.0]  # Right face
    [0.0, 0.0, 1.0, 1.0]  # Left face
  ];
  unpackedColors = []
  for i of colors
    color = colors[i]
    unpackedColors = unpackedColors.concat(color) for j in [0..4]
  gl.bufferData gl.ARRAY_BUFFER, new Float32Array(unpackedColors), gl.STATIC_DRAW
  cubeVertexColorBuffer.itemSize = 4
  cubeVertexColorBuffer.numItems = 24
  cubeVertexIndexBuffer = gl.createBuffer()
  gl.bindBuffer gl.ELEMENT_ARRAY_BUFFER, cubeVertexIndexBuffer
  cubeVertexIndices = [
    0, 1, 2,      0, 2, 3,    # Front face
    4, 5, 6,      4, 6, 7,    # Back face
    8, 9, 10,     8, 10, 11,  # Top face
    12, 13, 14,   12, 14, 15, # Bottom face
    16, 17, 18,   16, 18, 19, # Right face
    20, 21, 22,   20, 22, 23  # Left face
  ]
  gl.bufferData gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(cubeVertexIndices), gl.STATIC_DRAW
  cubeVertexIndexBuffer.itemSize = 1
  cubeVertexIndexBuffer.numItems = 36
  {triangleVertexPositionBuffer,triangleVertexColorBuffer,squareVertexPositionBuffer,squareVertexColorBuffer}


# model-view
mvMatrix = mat4.create()
pMatrix = mat4.create()

mvMatrixStack = []
mvPushMatrix = ->
  copy = mat4.create()
  mat4.set mvMatrix, copy
  mvMatrixStack.push copy
mvPopMatrix = ->
  throw "Invalid popMatrix!"  if mvMatrixStack.length is 0
  mvMatrix = mvMatrixStack.pop()


rTri = 0
rSquare = 0
degToRad = (degrees) -> degrees * Math.PI / 180


drawObject = (type, buffer, colorBuffer, mvMatrix, pMatrix)->
  gl.bindBuffer gl.ARRAY_BUFFER, buffer
  gl.vertexAttribPointer shaderProgram.vertexPositionAttribute, buffer.itemSize, gl.FLOAT, false, 0, 0
  gl.bindBuffer gl.ARRAY_BUFFER, colorBuffer
  gl.vertexAttribPointer shaderProgram.vertexColorAttribute, colorBuffer.itemSize, gl.FLOAT, false, 0, 0

  gl.uniformMatrix4fv shaderProgram.pMatrixUniform, false, pMatrix
  gl.uniformMatrix4fv shaderProgram.mvMatrixUniform, false, mvMatrix
  gl.drawArrays type, 0, buffer.numItems


drawScene = (commands)->
  gl.viewport 0, 0, gl.viewportWidth, gl.viewportHeight
  gl.clear gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT

  mat4.perspective 45, gl.viewportWidth / gl.viewportHeight, 0.1, 100.0, pMatrix
  mat4.identity mvMatrix
  mat4.translate mvMatrix, [-1.5, 0.0, -7.0]

  for command in commands
    mvPushMatrix()
    mat4.rotate(mvMatrix, degToRad(command[3]), command[4])
    drawObject command[0], command[1], command[2], mvMatrix, pMatrix
    mvPopMatrix()
    mat4.translate mvMatrix, command[5]

lastTime = 0
animate = ->
  timeNow = new Date().getTime()
  unless lastTime is 0
    elapsed = timeNow - lastTime
    rTri += (90 * elapsed) / 1000.0
    rSquare += (75 * elapsed) / 1000.0
  lastTime = timeNow


doit = (canvas)->
  initGL canvas
  initShaders()
  {triangleVertexPositionBuffer,triangleVertexColorBuffer,squareVertexPositionBuffer,squareVertexColorBuffer} = initBuffers()
  gl.clearColor 0.0, 0.0, 0.0, 1.0
  gl.enable gl.DEPTH_TEST
  tick = ->
    requestAnimFrame(tick)
    drawScene([
      [gl.TRIANGLES, triangleVertexPositionBuffer, triangleVertexColorBuffer, rTri, [0, 1, 0], [3.0, 0.0, 0.0]]
      [gl.TRIANGLE_STRIP, squareVertexPositionBuffer, squareVertexColorBuffer, rSquare, [1, 0, 0], [0.0, 0.0, 0.0]]
    ])
    animate()

  tick()


window.doit = doit