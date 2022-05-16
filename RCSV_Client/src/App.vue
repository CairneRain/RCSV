<template>
  <div id="app">
    <div id="logo">
      <img id="logoImg" src="./assets/rcsv_logo.png">
    </div>
    <br/>
    <div id="content">
      <router-view/>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
#logo{
  height: 20%;
}
#content{
  height: 80%;
}
#logoImg{
  height: 100%;
}
#app {
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  height: 100%;
}
body{
  height: 100%;
}
html{
  height: 95%;
}
</style>
