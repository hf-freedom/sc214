<template>
  <div class="home">
    <h2 style="margin-bottom: 20px;">📊 系统概览</h2>
    
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <i class="el-icon-user" style="font-size: 40px; color: #409EFF;"></i>
            <div class="stat-info">
              <div class="stat-number">{{ totalPatients }}</div>
              <div class="stat-label">总患者数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <i class="el-icon-time" style="font-size: 40px; color: #E6A23C;"></i>
            <div class="stat-info">
              <div class="stat-number">{{ waitingPatients }}</div>
              <div class="stat-label">候诊中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <i class="el-icon-s-claim" style="font-size: 40px; color: #67C23A;"></i>
            <div class="stat-info">
              <div class="stat-number">{{ consultingPatients }}</div>
              <div class="stat-label">诊疗中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <i class="el-icon-warning" style="font-size: 40px; color: #F56C6C;"></i>
            <div class="stat-info">
              <div class="stat-number">{{ timeoutPatients }}</div>
              <div class="stat-label">超时提醒</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="card-header">
            <span>🏥 科室队列</span>
          </div>
          <el-table :data="departments" style="width: 100%">
            <el-table-column prop="name" label="科室名称" width="120"></el-table-column>
            <el-table-column prop="capacity" label="容量" width="80"></el-table-column>
            <el-table-column label="排队人数" width="100">
              <template slot-scope="scope">
                <el-tag type="info">{{ scope.row.queue.length }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.queue.length >= scope.row.capacity" type="danger">满员</el-tag>
                <el-tag v-else-if="scope.row.queue.length > 0" type="warning">排队中</el-tag>
                <el-tag v-else type="success">空闲</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="card-header">
            <span>👨‍⚕️ 医生状态</span>
          </div>
          <el-table :data="doctors" style="width: 100%">
            <el-table-column prop="name" label="医生姓名" width="120"></el-table-column>
            <el-table-column prop="departmentName" label="所属科室" width="120"></el-table-column>
            <el-table-column label="状态">
              <template slot-scope="scope">
                <el-tag :type="scope.row.status === '空闲' ? 'success' : 'warning'">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Home',
  data() {
    return {
      totalPatients: 0,
      waitingPatients: 0,
      consultingPatients: 0,
      timeoutPatients: 0,
      departments: [],
      doctors: [],
      timer: null
    }
  },
  created() {
    this.loadData()
    this.timer = setInterval(() => {
      this.loadData()
    }, 5000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    async loadData() {
      try {
        const [patientsRes, departmentsRes, doctorsRes, timeoutRes] = await Promise.all([
          axios.get('http://localhost:8007/api/triage/patients'),
          axios.get('http://localhost:8007/api/triage/departments'),
          axios.get('http://localhost:8007/api/doctor/list'),
          axios.get('http://localhost:8007/api/triage/timeout')
        ])
        
        const patients = patientsRes.data
        this.totalPatients = patients.length
        this.waitingPatients = patients.filter(p => p.status === '候诊中').length
        this.consultingPatients = patients.filter(p => p.status === '诊疗中').length
        this.timeoutPatients = timeoutRes.data.length
        
        this.departments = departmentsRes.data
        this.doctors = doctorsRes.data.map(d => ({
          ...d,
          departmentName: this.getDepartmentName(d.departmentId)
        }))
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    },
    getDepartmentName(deptId) {
      const dept = this.departments.find(d => d.id === deptId)
      return dept ? dept.name : deptId
    }
  }
}
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}
.stat-info {
  text-align: left;
}
.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
.card-header {
  font-weight: bold;
  font-size: 16px;
}
</style>
