<template>
  <div id="app">
    <el-container style="height: 100vh">
      <el-header style="background: #409EFF; color: white; font-size: 24px; display: flex; align-items: center; padding: 0 20px;">
        <span>🏥 医院分诊转诊系统</span>
      </el-header>
      <el-container>
        <el-aside width="200px" style="background: #f5f5f5; padding-top: 20px;">
          <el-menu router default-active="1" style="border: none;">
            <el-menu-item index="1" route="/">
              <i class="el-icon-s-home"></i>
              <span>首页概览</span>
            </el-menu-item>
            <el-menu-item index="2" route="/register">
              <i class="el-icon-user"></i>
              <span>患者登记</span>
            </el-menu-item>
            <el-menu-item index="3" route="/patients">
              <i class="el-icon-s-custom"></i>
              <span>患者列表</span>
            </el-menu-item>
            <el-menu-item index="4" route="/doctor">
              <i class="el-icon-s-check"></i>
              <span>医生接诊</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main style="padding: 20px; background: #f0f2f5;">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.el-header {
  height: 60px !important;
  line-height: 60px;
}
</style>
