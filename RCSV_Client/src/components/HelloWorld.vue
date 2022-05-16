<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <h2>Rui Chen</h2>
    <!-- <el-button type="primary" round @click="$router.push('/RCSV')">Start</el-button> -->
    <el-button type="primary" round @click="$router.push('/editor')">Start</el-button>
    <!-- <el-button type="primary" round @click="$router.push('/test')">test</el-button> -->
    <el-button type="primary" round @click='toGithub'>About</el-button>
  </div>
</template>

<script>
export default {
  name: 'HelloWorld',
  data () {
    return {
      msg: 'Reversible Call Stack Visualizer'
    }
  },
  methods: {
    toGithub(){
      window.location.href = 'https://github.com/CairneRain'
    },
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
