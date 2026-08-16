<template>
    <div class="login-page">
        <div class="login-card">
            <h2>一卡通称重系统</h2>
            <p class="subtitle">请登录后继续操作</p>
            <div class="form-item">
                <label>用户名</label>
                <input v-model="username" placeholder="请输入用户名" @keyup.enter="login" />
            </div>
            <div class="form-item">
                <label>密码</label>
                <input v-model="password" type="password" placeholder="请输入密码" @keyup.enter="login" />
            </div>
            <button class="login-btn" :disabled="loading" @click="login">
                {{ loading ? '登录中...' : '登录' }}
            </button>
            <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        </div>
    </div>
</template>

<script>
import axios from 'axios'

export default {
    name: 'LoginIndex',
    data() {
        return {
            username: '',
            password: '',
            loading: false,
            errorMsg: ''
        }
    },
    methods: {
        async login() {
            if (!this.username || !this.password) {
                this.errorMsg = '请输入用户名和密码'
                return
            }
            this.loading = true
            this.errorMsg = ''
            try {
                const res = await axios.post('/api/auth/login', {
                    username: this.username,
                    password: this.password
                })
                // 与路由守卫/请求拦截器保持一致，使用 admin_token 键名
                localStorage.setItem('admin_token', res.data.token)
                localStorage.setItem('role', res.data.role)
                // 登录成功后跳转：优先回到来源页面，否则进入过磅页
                const redirect = this.$route.query.redirect
                this.$router.push(redirect || '/weighing')
            } catch (err) {
                this.errorMsg = err.response?.data?.message || '登录失败，请检查用户名或密码'
            } finally {
                this.loading = false
            }
        }
    }
}
</script>

<style scoped>
.login-page {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #1890ff 0%, #001529 100%);
}

.login-card {
    width: 380px;
    background: #fff;
    border-radius: 8px;
    padding: 40px 32px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.login-card h2 {
    text-align: center;
    color: #001529;
    margin-bottom: 4px;
}

.login-card .subtitle {
    text-align: center;
    color: #999;
    font-size: 13px;
    margin-bottom: 28px;
}

.form-item {
    margin-bottom: 16px;
}

.form-item label {
    display: block;
    font-size: 14px;
    color: #555;
    margin-bottom: 6px;
}

.form-item input {
    width: 100%;
    height: 38px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    padding: 0 12px;
    font-size: 14px;
    outline: none;
}

.form-item input:focus {
    border-color: #1890ff;
}

.login-btn {
    width: 100%;
    height: 40px;
    background: #1890ff;
    color: #fff;
    border: none;
    border-radius: 4px;
    font-size: 15px;
    cursor: pointer;
    margin-top: 8px;
}

.login-btn:hover {
    background: #40a9ff;
}

.login-btn:disabled {
    background: #91caff;
    cursor: not-allowed;
}

.error-msg {
    color: #ff4d4f;
    font-size: 13px;
    text-align: center;
    margin-top: 12px;
}
</style>
