<template>
    <div class="page-container">
        <h2>过磅操作</h2>

        <!-- 过磅类型切换 -->
        <div class="type-switch">
            <button
                class="type-btn"
                :class="{ active: weighType === 'FIRST' }"
                @click="weighType = 'FIRST'"
            >
                第一次过磅（毛重）
            </button>
            <button
                class="type-btn"
                :class="{ active: weighType === 'SECOND' }"
                @click="weighType = 'SECOND'"
            >
                第二次过磅（皮重）
            </button>
        </div>

        <!-- 表单 -->
        <div class="form-card">
            <div class="form-item">
                <label>提单号 *</label>
                <input v-model="form.orderNo" placeholder="请输入提单号 (如: ORD001)" />
            </div>
            <div class="form-item">
                <label>车牌号 *</label>
                <input v-model="form.licensePlate" placeholder="车牌号 (如: 沪AC12347)" />
            </div>
            <div class="form-item">
                <label>重量（吨）*</label>
                <input v-model.number="form.weight" type="number" step="0.01" placeholder="重量" />
            </div>
            <div class="form-item">
                <label>磅号 *</label>
                <input v-model="form.scaleNo" placeholder="磅号 (如: J01)" />
            </div>
            <div class="form-item">
                <label>物资名称</label>
                <input v-model="form.materialName" placeholder="物资名称 (可选)" />
            </div>
            <div class="form-item">
                <label>扣杂（吨）</label>
                <input v-model.number="form.extraWeight" type="number" step="0.01" placeholder="扣杂 (可选，默认0)" />
            </div>

            <button class="submit-btn" :disabled="loading" @click="weigh">
                {{ loading ? '提交中...' : (weighType === 'FIRST' ? '提交毛重' : '提交皮重') }}
            </button>
        </div>

        <!-- 结果展示 -->
        <div v-if="result" class="result-card" :class="result.status">
            <h3>过磅结果</h3>
            <table class="result-table">
                <tbody>
                    <tr>
                        <td>提单号</td>
                        <td>{{ result.orderNo }}</td>
                    </tr>
                    <tr>
                        <td>车牌号</td>
                        <td>{{ result.licensePlate }}</td>
                    </tr>
                    <tr>
                        <td>状态</td>
                        <td>{{ result.status }}</td>
                    </tr>
                    <tr>
                        <td>毛重（吨）</td>
                        <td>{{ result.grossWeight }}</td>
                    </tr>
                    <tr>
                        <td>皮重（吨）</td>
                        <td>{{ result.tareWeight }}</td>
                    </tr>
                    <tr>
                        <td>净重（吨）</td>
                        <td class="net-weight">{{ result.netWeight }}</td>
                    </tr>
                    <tr v-if="result.firstScaleNo">
                        <td>一磅磅号</td>
                        <td>{{ result.firstScaleNo }}</td>
                    </tr>
                    <tr v-if="result.secondScaleNo">
                        <td>二磅磅号</td>
                        <td>{{ result.secondScaleNo }}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script>
import request from '../utils/request'

export default {
    name: 'WeighingPage',
    data() {
        return {
            weighType: 'FIRST',
            loading: false,
            form: {
                orderNo: '',
                licensePlate: '',
                weight: 0,
                scaleNo: '',
                materialName: null,
                extraWeight: 0.0
            },
            result: null
        }
    },
    methods: {
        async weigh() {
            // 简单校验
            if (!this.form.orderNo || !this.form.licensePlate || !this.form.scaleNo) {
                alert('请填写完整信息：提单号、车牌号、磅号')
                return
            }
            if (!this.form.weight || this.form.weight <= 0) {
                alert('请填写正确的重量')
                return
            }

            this.loading = true
            try {
                const payload = {
                    orderNo: this.form.orderNo,
                    licensePlate: this.form.licensePlate,
                    weight: this.form.weight,
                    scaleNo: this.form.scaleNo,
                    materialName: this.form.materialName,
                    extraWeight: this.form.extraWeight || 0.0
                }

                const url = this.weighType === 'FIRST' ? '/api/weighing' : '/api/weighing/second'
                const data = await request.post(url, payload)

                this.result = data
                alert(this.weighType === 'FIRST' ? '毛重提交成功' : '皮重提交成功，本次过磅完成')
            } catch (err) {
                console.error(err)
                const msg = err.response?.data?.message || err.message || '网络错误'
                alert('过磅失败: ' + msg)
            } finally {
                this.loading = false
            }
        }
    }
}
</script>

<style scoped>
.page-container {
    max-width: 720px;
    margin: 24px auto;
    padding: 0 16px;
}

.page-container h2 {
    color: #001529;
    margin-bottom: 20px;
}

.type-switch {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
}

.type-btn {
    flex: 1;
    padding: 10px 0;
    border: 1px solid #d9d9d9;
    background: #fff;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    color: #555;
}

.type-btn.active {
    background: #1890ff;
    border-color: #1890ff;
    color: #fff;
}

.form-card {
    background: #fff;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
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

.submit-btn {
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

.submit-btn:hover {
    background: #40a9ff;
}

.submit-btn:disabled {
    background: #91caff;
    cursor: not-allowed;
}

.result-card {
    margin-top: 20px;
    background: #fff;
    border-radius: 8px;
    padding: 20px 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    border-left: 4px solid #1890ff;
}

.result-card h3 {
    color: #001529;
    margin-bottom: 12px;
}

.result-table {
    width: 100%;
    border-collapse: collapse;
}

.result-table td {
    padding: 8px 0;
    font-size: 14px;
    border-bottom: 1px solid #f0f0f0;
}

.result-table td:first-child {
    color: #999;
    width: 120px;
}

.net-weight {
    color: #1890ff;
    font-weight: bold;
    font-size: 18px;
}
</style>
