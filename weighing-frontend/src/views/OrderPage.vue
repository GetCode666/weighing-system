<template>
    <div class="page-container">
        <h2>订单管理</h2>

        <!-- 创建订单表单 -->
        <div class="form-card">
            <h3>创建新订单</h3>
            <div class="form-grid">
                <div class="form-item">
                    <label>提单号 *</label>
                    <input v-model="form.orderNo" placeholder="如: ORD001" />
                </div>
                <div class="form-item">
                    <label>任务类型 *</label>
                    <select v-model="form.taskType">
                        <option value="">请选择</option>
                        <option value="RECEIVE">RECEIVE（收货）</option>
                        <option value="SHIP">SHIP（发货）</option>
                    </select>
                </div>
                <div class="form-item">
                    <label>物资名称</label>
                    <input v-model="form.materialName" placeholder="物资名称" />
                </div>
                <div class="form-item">
                    <label>目标重量（吨）</label>
                    <input v-model.number="form.targetWeight" type="number" step="0.01" placeholder="目标重量" />
                </div>
                <div class="form-item">
                    <label>发货方</label>
                    <input v-model="form.senderName" placeholder="发货方名称" />
                </div>
                <div class="form-item">
                    <label>收货方</label>
                    <input v-model="form.receiverName" placeholder="收货方名称" />
                </div>
                <div class="form-item">
                    <label>司机姓名</label>
                    <input v-model="form.driverName" placeholder="司机姓名" />
                </div>
                <div class="form-item">
                    <label>司机电话</label>
                    <input v-model="form.driverPhone" placeholder="司机电话" />
                </div>
                <div class="form-item">
                    <label>车牌号</label>
                    <input v-model="form.licensePlate" placeholder="车牌号" />
                </div>
            </div>
            <button class="submit-btn" :disabled="creating" @click="createOrder">
                {{ creating ? '创建中...' : '创建订单' }}
            </button>
        </div>

        <!-- 活跃订单列表 -->
        <div class="table-card">
            <div class="table-header">
                <h3>活跃订单列表</h3>
                <button class="refresh-btn" @click="fetchOrders">刷新</button>
            </div>
            <table class="order-table">
                <thead>
                    <tr>
                        <th>提单号</th>
                        <th>任务类型</th>
                        <th>物资名称</th>
                        <th>目标重量</th>
                        <th>发货方</th>
                        <th>收货方</th>
                        <th>司机</th>
                        <th>车牌号</th>
                        <th>状态</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-if="orders.length === 0">
                        <td colspan="9" class="empty">暂无活跃订单</td>
                    </tr>
                    <tr v-for="order in orders" :key="order.orderId">
                        <td>{{ order.orderNo }}</td>
                        <td>{{ order.taskType }}</td>
                        <td>{{ order.materialName }}</td>
                        <td>{{ order.targetWeight }}</td>
                        <td>{{ order.senderName }}</td>
                        <td>{{ order.receiverName }}</td>
                        <td>{{ order.driverName }}</td>
                        <td>{{ order.licensePlate }}</td>
                        <td><span class="status-tag">{{ order.status }}</span></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
import request from '../utils/request'

export default {
    name: 'OrderPage',
    data() {
        return {
            creating: false,
            orders: [],
            form: {
                orderNo: '',
                taskType: '',
                materialName: '',
                targetWeight: null,
                senderName: '',
                receiverName: '',
                driverName: '',
                driverPhone: '',
                licensePlate: ''
            }
        }
    },
    mounted() {
        this.fetchOrders()
    },
    methods: {
        async fetchOrders() {
            try {
                this.orders = await request.get('/api/order/active')
            } catch (err) {
                console.error('获取订单失败', err)
            }
        },
        async createOrder() {
            if (!this.form.orderNo || !this.form.taskType) {
                alert('请填写提单号和任务类型')
                return
            }
            this.creating = true
            try {
                const res = await request.post('/api/orders', {
                    orderNo: this.form.orderNo,
                    taskType: this.form.taskType,
                    materialName: this.form.materialName,
                    targetWeight: this.form.targetWeight,
                    senderName: this.form.senderName,
                    receiverName: this.form.receiverName,
                    driverName: this.form.driverName,
                    driverPhone: this.form.driverPhone,
                    licensePlate: this.form.licensePlate
                })
                if (res.success) {
                    alert('订单创建成功')
                    // 清空表单并刷新列表
                    this.form = {
                        orderNo: '',
                        taskType: '',
                        materialName: '',
                        targetWeight: null,
                        senderName: '',
                        receiverName: '',
                        driverName: '',
                        driverPhone: '',
                        licensePlate: ''
                    }
                    this.fetchOrders()
                } else {
                    alert('创建失败: ' + (res.message || '未知错误'))
                }
            } catch (err) {
                console.error(err)
                const msg = err.response?.data?.message || err.message || '网络错误'
                alert('创建订单失败: ' + msg)
            } finally {
                this.creating = false
            }
        }
    }
}
</script>

<style scoped>
.page-container {
    max-width: 1000px;
    margin: 24px auto;
    padding: 0 16px;
}

.page-container h2 {
    color: #001529;
    margin-bottom: 20px;
}

.form-card,
.table-card {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    margin-bottom: 20px;
}

.form-card h3,
.table-card h3 {
    color: #001529;
    margin-bottom: 16px;
}

.form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr;
    gap: 16px;
}

.form-item label {
    display: block;
    font-size: 14px;
    color: #555;
    margin-bottom: 6px;
}

.form-item input,
.form-item select {
    width: 100%;
    height: 38px;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
    padding: 0 12px;
    font-size: 14px;
    outline: none;
    background: #fff;
}

.form-item input:focus,
.form-item select:focus {
    border-color: #1890ff;
}

.submit-btn {
    width: 100%;
    height: 40px;
    background: #1890ff;
    color: #fff;
    border: none;
    border-radius: 4px;
    font-size: 15px;
    cursor: pointer;
    margin-top: 16px;
}

.submit-btn:hover {
    background: #40a9ff;
}

.submit-btn:disabled {
    background: #91caff;
    cursor: not-allowed;
}

.table-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.refresh-btn {
    padding: 6px 16px;
    border: 1px solid #d9d9d9;
    background: #fff;
    border-radius: 4px;
    cursor: pointer;
    font-size: 13px;
    color: #555;
}

.refresh-btn:hover {
    border-color: #1890ff;
    color: #1890ff;
}

.order-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 12px;
    font-size: 14px;
}

.order-table th,
.order-table td {
    padding: 10px 8px;
    text-align: left;
    border-bottom: 1px solid #f0f0f0;
}

.order-table th {
    background: #fafafa;
    color: #555;
    font-weight: 500;
}

.order-table tr:hover {
    background: #fafafa;
}

.empty {
    text-align: center;
    color: #999;
    padding: 24px 0;
}

.status-tag {
    display: inline-block;
    padding: 2px 10px;
    border-radius: 10px;
    background: #e6f7ff;
    color: #1890ff;
    font-size: 12px;
}
</style>
