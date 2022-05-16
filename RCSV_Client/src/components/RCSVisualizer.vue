<template>
  <div>
    <el-row>
      <el-button type="primary" plain v-on:click='upload'>upload</el-button>
    </el-row>
    <p>{{debugInfo}}</p>
  </div>
</template>

<script>
export default {
  name: 'RCSVisualizer',
  data () {
    return {
      msg: 'Welcome to Your Vue.js App',
      debugInfo: ''
    }
  },
  methods: {
    upload () {
      this.axios.get('/debug/run').then(res => {
        this.debugInfo = res.data
        console.log(res.data)
      })
    }
  }

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
