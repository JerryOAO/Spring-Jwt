import { createApp } from 'vue'
import { createPinia } from 'pinia'
//引入自定义字体
import '@/assets/font/font.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
