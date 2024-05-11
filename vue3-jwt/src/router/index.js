import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'welcome',
      component: () => import('@/views/WelcomeView.vue'),
      children: [
        {
          path: '',
          name: 'welcome-login',
          component: () => import('@/views/welcome/LoginPage.vue')
        }
      ]
    },
    {
      path: '/home',
      name: 'home',
      component: () => import('@/views/home/index.vue'),
    }
  ]
})

router.beforeEach((to, from, next) => {
  const isUnauthorized = !localStorage.getItem('access_token') && !sessionStorage.getItem('access_token')
  console.log(isUnauthorized)
  // 如果是去登录页，并且已经登录了，就跳转到首页
  if ( to.name.startsWith('welcome-') && !isUnauthorized ){
    next('/home')
    // 如果是去首页，并且没有登录，就跳转到登录页
  } else if(to.fullPath.startsWith('/home') && isUnauthorized){
    ElMessage.warning('请先登录后访问')
    next('/')
  }else{
    next()
  }
})

export default router
