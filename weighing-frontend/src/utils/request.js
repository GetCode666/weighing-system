import axios from 'axios'
import router from '../router'

// 创建自定义 Axios 实例，配置基础地址和超时时间
const service = axios.create({
    baseURL: process.env.VUE_APP_BASE_API,
    timeout: 10000
})

// 请求拦截器：自动在请求头携带 Token
service.interceptors.request.use(
    config => {
        const token = localStorage.getItem('admin_token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    error => {
        console.error('请求错误', error)
        return Promise.reject(error)
    }
)

// 响应拦截器：统一处理后端返回的 401 未授权等异常
service.interceptors.response.use(
    response => {
        return response.data
    },
    error => {
        // 后端返回 401 代表 Token 过期/无效，自动清除状态跳转登录页
        if (error.response?.status === 401) {
            localStorage.removeItem('admin_token')
            localStorage.removeItem('role')
            router.push({ path: '/login' })
        }
        return Promise.reject(error)
    }
)

export default service
