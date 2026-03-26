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
    <div class="user-info">
      <p>John Doe</p>
      <button>Logout</button>
    </div>
  </div>
</template>

<style>

.sidebar {
  width: 15%;
  min-width: 260px;
  height: 100vh;
  background-color: var(--bg);
  border-right: 1px solid var(--stroke);
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
  border: none;
  font-size: 14px;
}

.menu-button-inactive {
  background-color: transparent;
  color: var(--neutral);
  /*border: 2px solid var(--stroke);*/
  border: none;
  outline: none;
  font-size: 14px;
}

.menu-button-inactive:hover {
  background-color: var(--stroke);
}

.menu-button, .menu-button-inactive {
  width: 210px;
  box-sizing: border-box;
  height: 30px;
  margin-bottom: 10px;
  border-radius: 10px;
  transition:
    background-color 220ms ease,
    color 220ms ease,
    border-color 220ms ease,
    transform 120ms ease;
}

.menu-button-inactive:active {
  transform: scale(0.98);
}

.user-info {
  margin-top: auto;
  padding: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
}

.user-info p {
  margin: 0;
}

.user-info button {
  margin-bottom: 0;
  background-color: var(--neutral);
  color: var(--bg);
  border: none;
  height: 30px;
  border-radius: 8px;
}

</style>