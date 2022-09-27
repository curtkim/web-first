import { ref, onMounted, onUnmounted } from 'vue'
import { useEventListener } from './event'

export function useMouse() {
  const x = ref(0)
  const y = ref(0)

  useEventListener(window, "mousemove", (event)=> {
    x.value = event.pageX
    y.value = event.pageY
  })
  
  return { x, y }
}