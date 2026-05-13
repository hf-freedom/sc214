<template>
  <div class="doctor">
    <h2 style="margin-bottom: 20px;">👨‍⚕️ 医生接诊</h2>
    
    <el-card shadow="hover" style="margin-bottom: 20px;">
      <div style="margin-bottom: 15px;">
        <span style="margin-right: 10px;">选择医生：</span>
        <el-select v-model="selectedDoctorId" placeholder="请选择医生" @change="loadNextPatient">
          <el-option
            v-for="doc in doctors"
            :key="doc.id"
            :label="`${doc.name} (${getDepartmentName(doc.departmentId)})`"
            :value="doc.id">
          </el-option>
        </el-select>
        <el-tag v-if="selectedDoctor" :type="selectedDoctor.status === '空闲' ? 'success' : 'warning'" style="margin-left: 10px;">
          {{ selectedDoctor.status }}
        </el-tag>
      </div>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <div slot="header" class="card-header">
            <span>📋 下一位患者</span>
          </div>
          <div v-if="nextPatient" class="patient-info">
            <p><strong>姓名：</strong>{{ nextPatient.name }}</p>
            <p><strong>年龄：</strong>{{ nextPatient.age }}岁</p>
            <p><strong>性别：</strong>{{ nextPatient.gender }}</p>
            <p><strong>症状：</strong>{{ nextPatient.symptom }}</p>
            <p><strong>优先级：</strong>
              <el-tag :type="getPriorityType(nextPatient.priority)">{{ nextPatient.priority }}</el-tag>
            </p>
            <el-button type="primary" @click="startConsultation" :disabled="selectedDoctor?.status !== '空闲'" style="margin-top: 15px;">
              开始接诊
            </el-button>
          </div>
          <div v-else class="empty-state">
            <i class="el-icon-user" style="font-size: 48px; color: #dcdfe6;"></i>
            <p style="margin-top: 10px; color: #909399;">暂无等待患者</p>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
          <el-card shadow="hover">
            <div slot="header" class="card-header">
              <span>🏥 当前接诊</span>
            </div>
            <div v-if="currentPatient" class="patient-info">
              <p><strong>姓名：</strong>{{ currentPatient.name }}</p>
              <p><strong>年龄：</strong>{{ currentPatient.age }}岁</p>
              <p><strong>性别：</strong>{{ currentPatient.gender }}</p>
              <p><strong>症状：</strong>{{ currentPatient.symptom }}</p>
              <p><strong>当前科室：</strong>{{ getDepartmentName(currentPatient.departmentId) }}</p>
              <p><strong>接诊医生：</strong>{{ selectedDoctor?.name }}</p>
              <p><strong>开始时间：</strong>{{ formatTime(currentPatient.startTime) }}</p>
              <div style="margin-top: 15px;">
                <el-button type="success" @click="endConsultation">
                  结束接诊
                </el-button>
                <el-button type="warning" @click="openTransferDialog(currentPatient)">
                  申请转诊
                </el-button>
              </div>
            </div>
            <div v-else class="empty-state">
              <i class="el-icon-s-claim" style="font-size: 48px; color: #dcdfe6;"></i>
              <p style="margin-top: 10px; color: #909399;">暂无正在接诊的患者</p>
            </div>
          </el-card>
        </el-col>
    </el-row>

    <el-card shadow="hover" style="margin-top: 20px;">
      <div slot="header" class="card-header">
        <span>📊 科室排队列表</span>
      </div>
      <el-table :data="queuePatients" style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60"></el-table-column>
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="age" label="年龄" width="80"></el-table-column>
        <el-table-column prop="symptom" label="症状" show-overflow-tooltip></el-table-column>
        <el-table-column label="优先级" width="90">
          <template slot-scope="scope">
            <el-tag :type="getPriorityType(scope.row.priority)">{{ scope.row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="登记时间" width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.registerTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="openPriorityDialog(scope.row)">
              提升优先级
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="调整患者优先级" :visible.sync="priorityDialogVisible" width="500px">
      <el-form label-width="100px">
        <el-form-item label="患者姓名">
          <span>{{ priorityPatient?.name }}</span>
        </el-form-item>
        <el-form-item label="当前科室">
          <span>{{ getDepartmentName(priorityPatient?.departmentId) }}</span>
        </el-form-item>
        <el-form-item label="当前优先级">
          <el-tag :type="getPriorityType(priorityPatient?.priority)">{{ priorityPatient?.priority }}</el-tag>
        </el-form-item>
        <el-form-item label="新优先级">
          <el-slider v-model="newPriority" :min="0" :max="100" show-input></el-slider>
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input type="textarea" v-model="priorityReason" :rows="3" placeholder="请填写调整原因（如：患者病情加重、危急重症等）"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="priorityDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmChangePriority">确认调整</el-button>
      </span>
    </el-dialog>

    <el-dialog title="患者转诊申请" :visible.sync="transferDialogVisible" width="500px">
      <el-form label-width="100px">
        <el-form-item label="患者姓名">
          <span>{{ transferPatient?.name }}</span>
        </el-form-item>
        <el-form-item label="当前科室">
          <el-tag>{{ getDepartmentName(transferPatient?.departmentId) }}</el-tag>
        </el-form-item>
        <el-form-item label="当前医生">
          <span>{{ selectedDoctor?.name }}</span>
        </el-form-item>
        <el-form-item label="目标科室">
          <el-select v-model="targetDepartmentId" placeholder="请选择目标科室" style="width: 100%">
            <el-option
              v-for="dept in availableDepartments"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="转诊原因">
          <el-input type="textarea" v-model="transferReason" :rows="3" placeholder="请填写转诊原因"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmTransfer">确认转诊</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Doctor',
  data() {
    return {
      doctors: [],
      departments: [],
      selectedDoctorId: '',
      nextPatient: null,
      currentPatient: null,
      queuePatients: [],
      loading: false,
      timer: null,
      transferDialogVisible: false,
      transferPatient: null,
      targetDepartmentId: '',
      transferReason: '',
      priorityDialogVisible: false,
      priorityPatient: null,
      newPriority: 50,
      priorityReason: ''
    }
  },
  computed: {
    selectedDoctor() {
      return this.doctors.find(d => d.id === this.selectedDoctorId)
    },
    availableDepartments() {
      if (!this.transferPatient) return this.departments
      return this.departments.filter(d => d.id !== this.transferPatient.departmentId)
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
      this.loading = true
      try {
        const [doctorsRes, departmentsRes, patientsRes] = await Promise.all([
          axios.get('http://localhost:8007/api/doctor/list'),
          axios.get('http://localhost:8007/api/triage/departments'),
          axios.get('http://localhost:8007/api/triage/patients')
        ])
        this.doctors = doctorsRes.data
        this.departments = departmentsRes.data
        
        if (!this.selectedDoctorId && this.doctors.length > 0) {
          this.selectedDoctorId = this.doctors[0].id
        }
        
        if (this.selectedDoctorId) {
          await this.loadNextPatient()
          await this.loadQueuePatients(patientsRes.data)
        }
      } catch (error) {
        console.error('加载数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    async loadNextPatient() {
      if (!this.selectedDoctorId) return
      
      const doctor = this.doctors.find(d => d.id === this.selectedDoctorId)
      if (doctor && doctor.currentPatientId) {
        const allPatients = await axios.get('http://localhost:8007/api/triage/patients')
        this.currentPatient = allPatients.data.find(p => p.id === doctor.currentPatientId)
      } else {
        this.currentPatient = null
      }
      
      try {
        const response = await axios.get(`http://localhost:8007/api/doctor/next/${this.selectedDoctorId}`)
        this.nextPatient = response.data
      } catch (error) {
        if (error.response && error.response.status === 404) {
          this.nextPatient = null
        } else {
          console.error('获取下一位患者失败:', error)
          this.nextPatient = null
        }
      }
    },
    async loadQueuePatients(allPatients) {
      if (!this.selectedDoctor) return
      
      const deptId = this.selectedDoctor.departmentId
      const dept = this.departments.find(d => d.id === deptId)
      
      if (dept && dept.queue) {
        this.queuePatients = dept.queue
          .map(patientId => allPatients.find(p => p.id === patientId))
          .filter(p => p)
      }
    },
    getDepartmentName(deptId) {
      const dept = this.departments.find(d => d.id === deptId)
      return dept ? dept.name : deptId
    },
    getPriorityType(priority) {
      if (priority >= 80) return 'danger'
      if (priority >= 50) return 'warning'
      return 'success'
    },
    formatTime(timeStr) {
      if (!timeStr) return '-'
      const date = new Date(timeStr)
      return date.toLocaleString('zh-CN')
    },
    async startConsultation() {
      if (!this.selectedDoctorId) {
        this.$message.warning('请先选择医生')
        return
      }
      if (!this.nextPatient) {
        this.$message.warning('当前没有等待接诊的患者')
        return
      }
      if (this.selectedDoctor && this.selectedDoctor.status !== '空闲') {
        this.$message.warning('该医生正在诊疗中，请先结束当前接诊')
        return
      }
      
      try {
        await axios.post('http://localhost:8007/api/doctor/start', {
          doctorId: this.selectedDoctorId,
          patientId: this.nextPatient.id
        })
        this.$message.success('开始接诊')
        await this.loadData()
      } catch (error) {
        this.$message.error('接诊失败，请检查医生状态')
        console.error('接诊失败:', error)
      }
    },
    async endConsultation() {
      if (!this.selectedDoctorId || !this.currentPatient) return
      
      try {
        await axios.post('http://localhost:8007/api/doctor/end', {
          doctorId: this.selectedDoctorId,
          patientId: this.currentPatient.id
        })
        this.$message.success('接诊完成')
        await this.loadData()
      } catch (error) {
        this.$message.error('操作失败')
        console.error('操作失败:', error)
      }
    },
    openTransferDialog(patient) {
      this.transferPatient = patient
      this.targetDepartmentId = ''
      this.transferReason = ''
      this.transferDialogVisible = true
    },
    openPriorityDialog(patient) {
      this.priorityPatient = patient
      this.newPriority = patient.priority
      this.priorityReason = ''
      this.priorityDialogVisible = true
    },
    async confirmChangePriority() {
      if (!this.priorityReason) {
        this.$message.warning('请填写调整原因')
        return
      }
      try {
        await axios.put(`http://localhost:8007/api/triage/patients/${this.priorityPatient.id}/priority`, {
          newPriority: this.newPriority,
          reason: this.priorityReason,
          operator: this.selectedDoctor?.name || '医生'
        })
        this.$message.success('优先级调整成功，患者排队位置已更新')
        this.priorityDialogVisible = false
        await this.loadData()
      } catch (error) {
        this.$message.error('优先级调整失败')
        console.error('调整失败:', error)
      }
    },
    async confirmTransfer() {
      if (!this.targetDepartmentId) {
        this.$message.warning('请选择目标科室')
        return
      }
      if (!this.transferReason) {
        this.$message.warning('请填写转诊原因')
        return
      }
      try {
        await axios.post(`http://localhost:8007/api/triage/patients/${this.transferPatient.id}/transfer`, {
          toDepartmentId: this.targetDepartmentId,
          reason: this.transferReason,
          operator: this.selectedDoctor?.name || '医生'
        })
        this.$message.success('转诊申请成功，患者已进入目标科室队列')
        this.transferDialogVisible = false
        this.currentPatient = null
        await this.loadData()
      } catch (error) {
        this.$message.error('转诊失败')
        console.error('转诊失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.doctor {
  padding: 20px;
}
.card-header {
  font-weight: bold;
  font-size: 16px;
}
.patient-info {
  padding: 10px;
}
.patient-info p {
  margin: 10px 0;
  font-size: 14px;
}
.empty-state {
  text-align: center;
  padding: 40px 20px;
}
</style>
