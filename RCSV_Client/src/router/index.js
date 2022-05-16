import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import RCSVisualizer from '@/components/RCSVisualizer'
import editor from '@/components/editor'
import test from '@/components/test'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path: '/RCSV',
      name: 'RCSVisualizer',
      component: RCSVisualizer
    },
    {
      path: '/editor',
      name: 'editor',
      component: editor
    },
    {
      path: '/test',
      name: 'test',
      component: test
    }
  ]
})
