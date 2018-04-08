# http://www.webglacademy.com/
# course 1 - 2D Colored Triangle

init = (canvas)->
  canvas.width = document.body.clientWidth
  canvas.height = document.body.clientHeight

  # get WebGL context
  try
    GL = canvas.getContext 'experimental-webgl', antialias: true
  catch e
    alert "Browser not WebGL compatible."
    return false

  # Shaders
  shader_vertex_src = """
  attribute vec2 position; // the position of the point
  void main(void) { // pre-built function
    gl_Position = vec4(position, 0., 1.); // 0. is the z, and 1 is w
  }
  """
  shader_fragment_src = """
  precision mediump float;
  void main(void) {
    gl_FragColor = vec4(0.,0.,0., 1); // black color
  }
  """
  get_shader = (src, type, typeString) ->
    shader = GL.createShader type
    GL.shaderSource shader, src
    GL.compileShader shader
    unless GL.getShaderParameter shader, GL.COMPILE_STATUS
      alert "Error in #{typeString} shader: #{GL.getShaderInfoLog shader}"
      return false
    return shader

  shader_vertex = get_shader shader_vertex_src, GL.VERTEX_SHADER, 'VERTEX'
  shader_fragment = get_shader shader_fragment_src, GL.FRAGMENT_SHADER, 'FRAGMENT'

  shader_program = GL.createProgram()
  GL.attachShader shader_program, shader_vertex
  GL.attachShader shader_program, shader_fragment
  GL.linkProgram shader_program

  _position = GL.getAttribLocation shader_program, 'position'
  GL.enableVertexAttribArray _position
  GL.useProgram shader_program
  {GL, _position}

makeBuffer = (GL)->
  # Triangle
  triangle_vertex = [
    -1, -1, # first summit -> bottom left of the viewport
    1, 0, # bottom right of the viewport
    1, 1 # top right of the viewport
  ]
  TRIANGLE_VERTEX = GL.createBuffer()
  GL.bindBuffer GL.ARRAY_BUFFER, TRIANGLE_VERTEX
  GL.bufferData GL.ARRAY_BUFFER, new Float32Array(triangle_vertex), GL.STATIC_DRAW

  triangle_faces = [
    0, 1, 2
  ]
  TRIANGLE_FACES = GL.createBuffer()
  GL.bindBuffer GL.ELEMENT_ARRAY_BUFFER, TRIANGLE_FACES
  GL.bufferData GL.ELEMENT_ARRAY_BUFFER, new Uint16Array(triangle_faces), GL.STATIC_DRAW
  {TRIANGLE_VERTEX, TRIANGLE_FACES}

doit = (e) ->
  canvas = document.getElementById 'my_canvas'
  {GL, _position} = init canvas
  {TRIANGLE_VERTEX, TRIANGLE_FACES} = makeBuffer GL

  # Draw
  GL.clearColor 0.0, 0.0, 0.0, 0.0 # set clear color to transparent
  animate = -> # draw the scene
    GL.viewport 0.0, 0.0, canvas.width, canvas.height # set drawing area on the canvas
    GL.clear GL.COLOR_BUFFER_BIT #  and clear it

    # TODO: draw stuff here
    GL.vertexAttribPointer _position, 2, GL.FLOAT, false, 4*2, 0
    GL.bindBuffer GL.ARRAY_BUFFER, TRIANGLE_VERTEX
    GL.bindBuffer GL.ELEMENT_ARRAY_BUFFER, TRIANGLE_FACES
    GL.drawElements GL.TRIANGLES, 3, GL.UNSIGNED_SHORT, 0

    GL.flush() # drawing finished; show the render
    window.requestAnimationFrame animate # redraw as soon as ready

  animate()  # kick-start animation

window.doit = doit