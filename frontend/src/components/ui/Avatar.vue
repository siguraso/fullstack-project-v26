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

function getInitials(name: string): string {
  const names = name.split(' ')
  const initials = names.map((n) => n.charAt(0).toUpperCase())
  return initials.join('')
}
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
    {{ getInitials(props.name) }}
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
