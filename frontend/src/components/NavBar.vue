<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell, Key, User2Icon } from '@lucide/vue'
import { clearAuthSession, getAuthSession } from '@/services/auth'
import { useTenantStore } from '@/stores/tenant'

const router = useRouter()
const session = getAuthSession()
const userLabel = computed(() => session?.email ?? 'Signed in user')
const tenantStore = useTenantStore()
const storeName = computed(() => tenantStore.tenantName)

async function logout() {
  clearAuthSession()
  await router.push('/login')
}

onMounted(() => {
  tenantStore.fetchTenant()
})
</script>

<template>
  <nav class="navbar">
    <div class="brand-and-tenant">
      <div class="logo-wrapper">
        <Key class="logo-icon" />
      </div>
      <h2>Regula</h2>
      <div class="spacer-line" aria-hidden="true" />
      <h3 class="storename">{{ storeName }}</h3>
    </div>

    <div class="user-info">
      <div class="icon-wrapper">
        <component :is="Bell" :size="20" aria-hidden="true" class="notification-bell" />
      </div>
      <div class="icon-wrapper" @click="logout">
        <component :is="User2Icon" :size="20" aria-hidden="true" class="profile-picture" />
      </div>
      <p class="user-label">{{ userLabel }}</p>
    </div>
  </nav>
</template>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 54px;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--stroke);
  background-color: var(--bg);
  z-index: 1000;
}

.brand-and-tenant {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 15px;
}

.user-label {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary);
}

.user-info button {
  min-height: 30px;
  padding: 0 12px;
  border-radius: 8px;
}

.logo-icon {
  width: 20px;
  height: 20px;
  color: var(--bg-secondary);
  transform: rotate(-45deg);
}

.logo-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--neutral);
  width: 32px;
  height: 32px;
  border-radius: 5px;
}

.brand-name {
  font-size: 20px;
  font-weight: 700;
}

.spacer-line {
  width: 1px;
  height: 30px;
  background-color: var(--stroke);
}

.storename {
  font-size: 14px;
  color: var(--text-secondary);
}

.icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 30px;
  width: 30px;
  border-radius: 50%;
}

.icon-wrapper:hover {
  background-color: var(--bg-hover);
}
</style>
