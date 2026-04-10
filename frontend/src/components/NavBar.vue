<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { Bell, Key, Menu } from '@lucide/vue'
import { clearAuthSession, getAuthSession } from '@/services/auth'
import { useTenantStore } from '@/stores/tenant'
import { useRouter } from 'vue-router'
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

const session = getAuthSession()
const fullName = computed(() => session?.fullName ?? 'Signed in user')
const email = computed(() => session?.email ?? '')
const roleLabel = computed(() => {
  if (!session?.role) return ''

  return `${session.role.slice(0, 1)}${session.role.slice(1).toLowerCase()}`
})
const tenantStore = useTenantStore()
const storeName = computed(() => tenantStore.tenantName)
const router = useRouter()
const isUserMenuOpen = ref(false)

function toggleUserMenu() {
  isUserMenuOpen.value = !isUserMenuOpen.value
}

function closeUserMenu() {
  isUserMenuOpen.value = false
}

function onDocumentClick(event: MouseEvent) {
  const target = event.target
  if (!(target instanceof HTMLElement)) return
  if (target.closest('.user-menu-wrapper')) return
  closeUserMenu()
}

async function handleLogout() {
  clearAuthSession()
  closeUserMenu()
  await router.push({ name: 'login' })
}

onMounted(() => {
  tenantStore.fetchTenant()
  document.addEventListener('click', onDocumentClick)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', onDocumentClick)
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
      <div class="user-menu-wrapper" @click.stop>
        <button
          type="button"
          class="avatar-trigger"
          aria-label="Open user menu"
          :aria-expanded="isUserMenuOpen"
          @click="toggleUserMenu"
        >
          <Avatar :name="fullName" :size="30" />
        </button>
        <div v-if="isUserMenuOpen" class="user-menu" role="menu" aria-label="User menu">
          <p class="user-menu-email">{{ email }}</p>
          <button type="button" class="user-menu-logout" @click="handleLogout">Log out</button>
        </div>
      </div>
      <div class="user-identity">
        <p class="user-label">{{ fullName }}</p>
        <p v-if="roleLabel" class="user-role">{{ roleLabel }}</p>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
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
  position: relative;
}

.user-label {
  margin: 0;
  font-size: 14px;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-identity {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-width: 0;
}

.user-role {
  margin: 0;
  font-size: 12px;
  line-height: 1.2;
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

.user-menu-wrapper {
  position: relative;
}

.avatar-trigger {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 0;
  background: transparent;
  padding: 0;
  border-radius: 50%;
  cursor: pointer;
}

.avatar-trigger:focus-visible {
  outline: 2px solid var(--neutral);
  outline-offset: 2px;
}

.user-menu {
  position: absolute;
  right: 0;
  top: calc(100% + 8px);
  min-width: 220px;
  background: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 10px;
  box-shadow: 0 10px 24px rgb(0 0 0 / 10%);
  padding: 10px;
  z-index: 1001;
}

.user-menu-email {
  margin: 0 0 10px;
  font-size: 13px;
  color: var(--text-secondary);
  word-break: break-all;
}

.user-menu-logout {
  width: 100%;
  min-height: 36px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg);
  color: var(--text);
  font-size: 14px;
  cursor: pointer;
}

.user-menu-logout:hover {
  background: var(--bg-hover);
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
  .user-label,
  .user-role {
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
