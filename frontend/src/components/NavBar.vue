<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Bell, Key, Menu } from '@lucide/vue'
import { clearAuthSession, getAuthSession } from '@/services/auth'
import { useTenantStore } from '@/stores/tenant'
import Avatar from './ui/Avatar.vue'

const props = withDefaults(
  defineProps<{
    showSidebarToggle?: boolean
    sidebarOpen?: boolean
  }>(),
  {
    showSidebarToggle: false,
    sidebarOpen: false,
  },
)

const emit = defineEmits<{
  'toggle-sidebar': []
}>()

const router = useRouter()
const session = getAuthSession()
const userEmail = computed(() => session?.email ?? 'Signed in user')
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
      <button
        v-if="props.showSidebarToggle"
        type="button"
        class="menu-toggle"
        :class="{ 'menu-toggle-active': props.sidebarOpen }"
        aria-label="Toggle sidebar"
        @click="emit('toggle-sidebar')"
      >
        <Menu :size="18" aria-hidden="true" />
      </button>

      <div class="logo-wrapper">
        <Key class="logo-icon" />
      </div>
      <h2 class="brand-name">Regula</h2>
      <div class="spacer-line" aria-hidden="true" />
      <h3 class="storename">{{ storeName }}</h3>
    </div>

    <div class="user-info">
      <div class="icon-wrapper">
        <component :is="Bell" :size="20" aria-hidden="true" class="notification-bell" />
      </div>
      <div>
        <Avatar :name="userEmail" :size="30" />
      </div>
      <p class="user-label">{{ userEmail }}</p>
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
  border-bottom: 1px solid var(--border);
  background-color: var(--bg-secondary);
  z-index: 1000;
}

.brand-and-tenant {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.user-info {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 15px;
  min-width: 0;
}

.user-label {
  margin: 0;
  font-size: 14px;
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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
  margin: 0;
}

.spacer-line {
  width: 1px;
  height: 30px;
  background-color: var(--border);
}

.storename {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: min(30vw, 260px);
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

.menu-toggle {
  display: inline-grid;
  place-items: center;
  min-height: 36px;
  min-width: 36px;
  padding: 0;
  border-radius: 10px;
  border: 1px solid var(--border);
  background: var(--bg-secondary);
  color: var(--text);
}

.menu-toggle-active {
  background: var(--bg);
}

@media (max-width: 960px) {
  .navbar {
    padding: 0 14px;
    gap: 12px;
  }

  .brand-and-tenant,
  .user-info {
    min-width: 0;
  }

  .brand-and-tenant {
    flex: 1;
  }

  .storename {
    max-width: min(32vw, 180px);
  }

  .user-info {
    gap: 10px;
  }

  .user-label {
    max-width: 92px;
    font-size: 13px;
  }
}

@media (max-width: 640px) {
  .navbar {
    padding: 0 10px;
  }

  .spacer-line,
  .storename,
  .user-label {
    display: none;
  }

  .brand-and-tenant {
    gap: 10px;
  }

  .user-info {
    gap: 8px;
  }
}

@media (max-width: 420px) {
  .icon-wrapper {
    display: none;
  }
}
</style>
