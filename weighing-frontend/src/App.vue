<template>
    <div id="app">
        <!-- 顶部导航：登录后显示 -->
        <nav v-if="isLoggedIn" class="navbar">
            <div class="navbar-left">
                <span class="brand">称重系统</span>
                <router-link to="/weighing">过磅操作</router-link>
                <router-link to="/orders">订单管理</router-link>
            </div>
            <div class="navbar-right">
                <span class="role">角色: {{ role }}</span>
                <button class="logout-btn" @click="logout">退出登录</button>
            </div>
        </nav>

        <router-view />
    </div>
</template>

<script>
export default {
    name: 'App',
    computed: {
        isLoggedIn() {
            return !!localStorage.getItem('admin_token')
        },
        role() {
            return localStorage.getItem('role') || ''
        }
    },
    methods: {
        logout() {
            localStorage.removeItem('admin_token')
            localStorage.removeItem('role')
            this.$router.push('/login')
        }
    }
}
</script>

<style>
* {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
}

body {
    font-family: 'Microsoft YaHei', Avenir, Helvetica, Arial, sans-serif;
    background-color: #f0f2f5;
    color: #333;
}

.navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #001529;
    color: #fff;
    padding: 0 24px;
    height: 56px;
}

.navbar-left {
    display: flex;
    align-items: center;
    gap: 24px;
}

.navbar .brand {
    font-size: 18px;
    font-weight: bold;
    margin-right: 16px;
}

.navbar a {
    color: #a6adb4;
    text-decoration: none;
    line-height: 56px;
    display: inline-block;
}

.navbar a.router-link-exact-active {
    color: #fff;
    border-bottom: 2px solid #1890ff;
}

.navbar-right {
    display: flex;
    align-items: center;
    gap: 16px;
}

.navbar .role {
    color: #a6adb4;
    font-size: 14px;
}

.logout-btn {
    background: transparent;
    border: 1px solid #a6adb4;
    color: #a6adb4;
    border-radius: 4px;
    padding: 4px 12px;
    cursor: pointer;
    font-size: 14px;
}

.logout-btn:hover {
    border-color: #fff;
    color: #fff;
}
</style>
