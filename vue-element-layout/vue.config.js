module.exports = {
  configureWebpack: config => {
    if( process.env.NODE_ENV === 'production')
      return {
        output: {
          publicPath: '.'
        }
      }
    else
      return {}
  }
}
