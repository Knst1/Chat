<template>
  <div id="app">
    <h2>HeLLO</h2>
    <hr>
    <TodoList/>
  </div>
</template>

<script>
import TodoList from "@/components/TodoList"
export default {
  name: 'App',
  components: {
    TodoList
  }
}
</script>

<style>
@import url("style/app.css");
</style>
