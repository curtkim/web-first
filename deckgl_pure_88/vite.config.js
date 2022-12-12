import { defineConfig } from 'vite'

export default defineConfig({
  server: {
    proxy: {
      '/map_2d_hd': {
        target: 'http://map0.daumcdn.net',
        changeOrigin: true,
      }
    }
  }
})