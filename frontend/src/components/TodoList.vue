<template>
<div>
  <ul>
    <li>todo1</li>
    <TodoItem/>
  </ul>
</div>
</template>

<script>
import TodoItem from "@/components/TodoItem"
export default {
  name: "TodoList",
  components:{
    TodoItem
  }
}
</script>

<style scoped>

</style>