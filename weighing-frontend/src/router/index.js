import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/LoginIndex.vue'
import Weighing from '../views/WeighingPage.vue'
import Orders from '../views/OrderPage.vue'

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/login', component: Login, meta: { noAuth: true } },
    { path: '/weighing', component: Weighing },
    { path: '/orders', component: Orders }
]

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
})

// 全局前置守卫：校验登录状态
router.beforeEach((to, from, next) => {
    // 从本地存储读取后端返回的真实 Token
    const token = localStorage.getItem('admin_token')

    // 白名单路由直接放行（登录页等无需鉴权的页面）
    if (to.meta.noAuth) {
        // 已携带 Token 访问登录页，直接跳转业务首页
        if (token) {
            next('/weighing')
            return
        }
        next()
        return
    }

    // 非白名单路由强制校验 Token
    if (!token) {
        // 未登录直接跳转登录页，记录目标路径用于登录后自动回跳
        next({
            path: '/login',
            query: { redirect: to.fullPath }
        })
        return
    }

    // 正常放行
    next()
})

export default router
