<script setup lang="ts">
import { ref } from 'vue';

const menuItems = [
  'Dashboard',
  'Food Compliance',
  'Alcohol Compliance',
  'Logging',
  'Dashboard',
];

const activeIndex = ref(0);

function setActive(index: number) {
  activeIndex.value = index;
}
</script>

<template>
  <div class="sidebar">
    <h2>Regula</h2>
    <h3>storename</h3>
    <ul class="menu">
      <li v-for="(item, index) in menuItems" :key="item + index">
        <button
          :class="index === activeIndex ? 'menu-button' : 'menu-button-inactive'"
          @click="setActive(index)"
        >
          {{ item }}
        </button>
      </li>
    </ul>
  </div>

</template>

<style>

.sidebar {
  width: 20%;
  min-width: 260px;
  height: 100vh;
  background-color: var(--bg);
  border-right: 2px solid var(--stroke);
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0;
  position: fixed;
  top: 0;
  left: 0;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.sidebar h2 {
  font-size: 24px;
  margin-bottom: 0px;
}

.sidebar h3 {
  font-size: 14px;
  margin-top: 10px;
  margin-bottom: 30px;
}

.menu-button {
  background-color: var(--neutral);
  color: var(--bg);
  font-size: 18px;
  border-radius: 50px;
  margin-bottom: 20px;
}

.menu-button-inactive {
  background-color: transparent;
  color: var(--neutral);
  border: 2px solid var(--stroke);
  outline: none;
  font-size: 18px;
  border-radius: 50px;
  margin-bottom: 20px;
}

.menu-button-inactive:hover {
  background-color: var(--stroke);
}

.menu-button, .menu-button-inactive {
  width: 220px;
  box-sizing: border-box;
  height: 40px;
  transition:
    background-color 220ms ease,
    color 220ms ease,
    border-color 220ms ease,
    transform 120ms ease;
}

.menu-button-inactive:active {
  transform: scale(0.98);
}

</style>