import { afterEach, describe, expect, it, vi } from 'vitest'
import { defineComponent, nextTick } from 'vue'
import { mount } from '@vue/test-utils'

const routeState = vi.hoisted(() => ({
  route: null as { fullPath: string } | null,
}))

vi.mock('vue-router', async () => {
  const { reactive } = await import('vue')

  routeState.route = reactive({
    fullPath: '/dashboard',
  })

  return {
    useRoute: () => routeState.route,
  }
})

import MainLayout from '../views/MainLayout.vue'

const NavBarStub = defineComponent({
  props: {
    showSidebarToggle: {
      type: Boolean,
      required: true,
    },
    sidebarOpen: {
      type: Boolean,
      required: true,
    },
  },
  emits: ['toggle-sidebar'],
  template: `
    <div>
      <button data-test="toggle" @click="$emit('toggle-sidebar')">Toggle</button>
      <p data-test="nav-state">{{ showSidebarToggle }}|{{ sidebarOpen }}</p>
    </div>
  `,
})

const SidebarStub = defineComponent({
  props: {
    isMobile: {
      type: Boolean,
      required: true,
    },
    open: {
      type: Boolean,
      required: true,
    },
  },
  emits: ['close'],
  template: `
    <aside
      data-test="sidebar"
      :data-mobile="String(isMobile)"
      :data-open="String(open)"
      @click="$emit('close')"
    >
      Sidebar
    </aside>
  `,
})

function setWindowWidth(width: number) {
  Object.defineProperty(window, 'innerWidth', {
    value: width,
    configurable: true,
    writable: true,
  })
}

function mountMainLayout() {
  return mount(MainLayout, {
    global: {
      stubs: {
        NavBar: NavBarStub,
        Sidebar: SidebarStub,
        RouterView: {
          template: '<div data-test="router-view" />',
        },
      },
    },
  })
}

afterEach(() => {
  document.body.style.overflow = ''
  if (routeState.route) {
    routeState.route.fullPath = '/dashboard'
  }
})

describe('MainLayout', () => {
  it('uses desktop shell defaults when viewport is wide', async () => {
    setWindowWidth(1280)

    const wrapper = mountMainLayout()
    await nextTick()

    expect(wrapper.get('[data-test="nav-state"]').text()).toBe('false|false')
    expect(wrapper.get('[data-test="sidebar"]').attributes('data-mobile')).toBe('false')
    expect(wrapper.get('[data-test="sidebar"]').attributes('data-open')).toBe('true')

    wrapper.unmount()
  })

  it('opens and closes mobile sidebar from interactions and route changes', async () => {
    setWindowWidth(800)

    const wrapper = mountMainLayout()
    await nextTick()

    expect(wrapper.get('[data-test="nav-state"]').text()).toBe('true|false')
    expect(wrapper.get('[data-test="sidebar"]').attributes('data-mobile')).toBe('true')
    expect(wrapper.get('[data-test="sidebar"]').attributes('data-open')).toBe('false')

    await wrapper.get('[data-test="toggle"]').trigger('click')
    await nextTick()

    expect(wrapper.get('[data-test="nav-state"]').text()).toBe('true|true')
    expect(wrapper.get('[data-test="sidebar"]').attributes('data-open')).toBe('true')
    expect(document.body.style.overflow).toBe('hidden')
    expect(wrapper.find('.sidebar-backdrop').exists()).toBe(true)

    routeState.route!.fullPath = '/temperature-logs'
    await nextTick()

    expect(wrapper.get('[data-test="nav-state"]').text()).toBe('true|false')
    expect(wrapper.get('[data-test="sidebar"]').attributes('data-open')).toBe('false')
    expect(document.body.style.overflow).toBe('')
    expect(wrapper.find('.sidebar-backdrop').exists()).toBe(false)

    wrapper.unmount()
  })
})
