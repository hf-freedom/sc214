<template>
  <div class="patients">
    <h2 style="margin-bottom: 20px;">👥 患者列表</h2>
    
    <el-card shadow="hover">
      <el-table :data="patients" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="姓名" width="100"></el-table-column>
        <el-table-column prop="age" label="年龄" width="80"></el-table-column>
        <el-table-column prop="gender" label="性别" width="80"></el-table-column>
        <el-table-column prop="symptom" label="症状" show-overflow-tooltip></el-table-column>
        <el-table-column label="科室" width="100">
          <template slot-scope="scope">
            {{ getDepartmentName(scope.row.departmentId) }}
          </template>
        </el-table-column>
        <el-table-column label="优先级" width="90">
          <template slot-scope="scope">
            <el-tag :type="getPriorityType(scope.row.priority)">{{ scope.row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="openPriorityDialog(scope.row)">
              调整优先级
            </el-button>
            <el-button 
              v-if="scope.row.priorityHistory && scope.row.priorityHistory.length > 0" 
              size="mini" 
              type="success" 
              @click="openPriorityHistoryDialog(scope.row)">
              优先级记录
            </el-button>
            <el-button size="mini" type="warning" @click="openTransferDialog(scope.row)">
              转诊
            </el-button>
            <el-button 
              v-if="scope.row.transferHistory && scope.row.transferHistory.length > 0" 
              size="mini" 
              type="info" 
              @click="openTransferHistoryDialog(scope.row)">
              转诊记录
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog title="调整优先级" :visible.sync="priorityDialogVisible" width="500px">
      <el-form label-width="100px">
        <el-form-item label="当前优先级">
          <el-tag>{{ currentPatient?.priority }}</el-tag>
        </el-form-item>
        <el-form-item label="新优先级">
          <el-slider v-model="newPriority" :min="0" :max="100" show-input></el-slider>
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input type="textarea" v-model="priorityReason" :rows="3"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="priorityDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmChangePriority">确认</el-button>
      </span>
    </el-dialog>

    <el-dialog title="患者转诊" :visible.sync="transferDialogVisible" width="500px">
      <el-form label-width="100px">
        <el-form-item label="患者姓名">
          <span>{{ currentPatient?.name }}</span>
        </el-form-item>
        <el-form-item label="当前科室">
          <el-tag>{{ getDepartmentName(currentPatient?.departmentId) }}</el-tag>
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

    <el-dialog title="优先级调整历史记录" :visible.sync="priorityHistoryDialogVisible" width="600px">
      <p style="margin-bottom: 15px;"><strong>患者姓名：</strong>{{ currentPatient?.name }}</p>
      <el-table :data="currentPatient?.priorityHistory || []" style="width: 100%">
        <el-table-column label="序号" type="index" width="60"></el-table-column>
        <el-table-column label="原优先级" width="100">
          <template slot-scope="scope">
            <el-tag :type="getPriorityType(scope.row.oldPriority)">{{ scope.row.oldPriority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="新优先级" width="100">
          <template slot-scope="scope">
            <el-tag :type="getPriorityType(scope.row.newPriority)">{{ scope.row.newPriority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="调整原因" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作人" width="100"></el-table-column>
        <el-table-column label="调整时间" width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.changeTime) }}
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="priorityHistoryDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>

    <el-dialog title="转诊历史记录" :visible.sync="transferHistoryDialogVisible" width="600px">
      <p style="margin-bottom: 15px;"><strong>患者姓名：</strong>{{ currentPatient?.name }}</p>
      <el-table :data="currentPatient?.transferHistory || []" style="width: 100%">
        <el-table-column label="序号" type="index" width="60"></el-table-column>
        <el-table-column label="来源科室" width="120">
          <template slot-scope="scope">
            {{ getDepartmentName(scope.row.fromDepartmentId) }}
          </template>
        </el-table-column>
        <el-table-column label="目标科室" width="120">
          <template slot-scope="scope">
            {{ getDepartmentName(scope.row.toDepartmentId) }}
          </template>
        </el-table-column>
        <el-table-column label="转诊原因" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作人" width="100"></el-table-column>
        <el-table-column label="转诊时间" width="160">
          <template slot-scope="scope">
            {{ formatTime(scope.row.transferTime) }}
          </template>
        </el-table-column>
      </el-table>
      <span slot="footer" class="dialog-footer">
        <el-button @click="transferHistoryDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Patients',
  data() {
    return {
      patients: [],
      departments: [],
      loading: false,
      priorityDialogVisible: false,
      priorityHistoryDialogVisible: false,
      transferDialogVisible: false,
      transferHistoryDialogVisible: false,
      currentPatient: null,
      newPriority: 50,
      priorityReason: '',
      targetDepartmentId: '',
      transferReason: ''
    }
  },
  computed: {
    availableDepartments() {
      if (!this.currentPatient) return this.departments
      return this.departments.filter(d => d.id !== this.currentPatient.departmentId)
    }
  },
  created() {
    this.loadData()
    this.timer = setInterval(() => {
      this.loadData()
    }, 10000)
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
        const [patientsRes, departmentsRes] = await Promise.all([
          axios.get('http://localhost:8007/api/triage/patients'),
          axios.get('http://localhost:8007/api/triage/departments')
        ])
        this.patients = patientsRes.data
        this.departments = departmentsRes.data
      } catch (error) {
        console.error('加载数据失败:', error)
      } finally {
        this.loading = false
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
    getStatusType(status) {
      const typeMap = {
        '候诊中': 'warning',
        '诊疗中': 'primary',
        '已完成': 'success'
      }
      return typeMap[status] || 'info'
    },
    openPriorityDialog(patient) {
      this.currentPatient = patient
      this.newPriority = patient.priority
      this.priorityReason = ''
      this.priorityDialogVisible = true
    },
    openPriorityHistoryDialog(patient) {
      this.currentPatient = patient
      this.priorityHistoryDialogVisible = true
    },
    async confirmChangePriority() {
      if (!this.priorityReason) {
        this.$message.warning('请填写调整原因')
        return
      }
      try {
        await axios.put(`http://localhost:8007/api/triage/patients/${this.currentPatient.id}/priority`, {
          newPriority: this.newPriority,
          reason: this.priorityReason,
          operator: '管理员'
        })
        this.$message.success('优先级调整成功')
        this.priorityDialogVisible = false
        this.loadData()
      } catch (error) {
        this.$message.error('优先级调整失败')
        console.error('调整失败:', error)
      }
    },
    openTransferDialog(patient) {
      this.currentPatient = patient
      this.targetDepartmentId = ''
      this.transferReason = ''
      this.transferDialogVisible = true
    },
    openTransferHistoryDialog(patient) {
      this.currentPatient = patient
      this.transferHistoryDialogVisible = true
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
        await axios.post(`http://localhost:8007/api/triage/patients/${this.currentPatient.id}/transfer`, {
          toDepartmentId: this.targetDepartmentId,
          reason: this.transferReason,
          operator: '医生'
        })
        this.$message.success('转诊成功')
        this.transferDialogVisible = false
        this.loadData()
      } catch (error) {
        this.$message.error('转诊失败')
        console.error('转诊失败:', error)
      }
    },
    formatTime(timeStr) {
      if (!timeStr) return '-'
      const date = new Date(timeStr)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.patients {
  padding: 20px;
}
</style>
