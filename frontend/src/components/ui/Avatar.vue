<script setup lang="ts">
const props = withDefaults(
  defineProps<{
    name: string
    size?: number
  }>(),
  {
    size: 42,
  },
)
</script>

<template>
  <div
    class="avatar"
    :style="{
      width: props.size + 'px',
      height: props.size + 'px',
      fontSize: props.size / 2.5 + 'px',
    }"
  >
    {{ props.name.charAt(0).toUpperCase() }}
  </div>
</template>

<style scoped>
.avatar {
  border-radius: 100%;
  background: #eef0e7;
  color: #305431;
  display: grid;
  place-items: center;
  border: 1px solid #d4d7cc;
}
</style>
