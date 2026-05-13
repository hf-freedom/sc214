<template>
  <div class="register">
    <h2 style="margin-bottom: 20px;">📝 患者登记</h2>
    
    <el-card shadow="hover" style="max-width: 600px; margin: 0 auto;">
      <el-form :model="patientForm" label-width="100px" @submit.native.prevent>
        <el-form-item label="姓名">
          <el-input v-model="patientForm.name" placeholder="请输入患者姓名"></el-input>
        </el-form-item>
        
        <el-form-item label="年龄">
          <el-input-number v-model="patientForm.age" :min="0" :max="150" placeholder="请输入年龄"></el-input-number>
        </el-form-item>
        
        <el-form-item label="性别">
          <el-radio-group v-model="patientForm.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="症状描述">
          <el-input
            type="textarea"
            v-model="patientForm.symptom"
            :rows="4"
            placeholder="请描述患者症状，如：胸痛、发热、咳嗽等">
          </el-input>
        </el-form-item>
        
        <el-form-item label="紧急程度">
          <el-select v-model="patientForm.emergencyLevel" placeholder="请选择紧急程度">
            <el-option label="危急" value="危急"></el-option>
            <el-option label="紧急" value="紧急"></el-option>
            <el-option label="普通" value="普通"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="registerPatient" :loading="loading">
            确认登记
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Register',
  data() {
    return {
      patientForm: {
        name: '',
        age: 30,
        gender: '男',
        symptom: '',
        emergencyLevel: '普通'
      },
      loading: false
    }
  },
  methods: {
    async registerPatient() {
      if (!this.patientForm.name || !this.patientForm.symptom) {
        this.$message.warning('请填写姓名和症状描述')
        return
      }
      
      this.loading = true
      try {
        const response = await axios.post('http://localhost:8007/api/triage/register', this.patientForm)
        const patient = response.data
        this.$message.success(`登记成功！患者 ${patient.name} 已分配到 ${this.getDepartmentName(patient.departmentId)}，优先级: ${patient.priority}`)
        this.resetForm()
      } catch (error) {
        this.$message.error('登记失败，请重试')
        console.error('登记失败:', error)
      } finally {
        this.loading = false
      }
    },
    resetForm() {
      this.patientForm = {
        name: '',
        age: 30,
        gender: '男',
        symptom: '',
        emergencyLevel: '普通'
      }
    },
    getDepartmentName(deptId) {
      const deptMap = {
        'DEPT001': '急诊科',
        'DEPT002': '内科',
        'DEPT003': '外科',
        'DEPT004': '儿科'
      }
      return deptMap[deptId] || deptId
    }
  }
}
</script>

<style scoped>
.register {
  padding: 20px;
}
</style>
