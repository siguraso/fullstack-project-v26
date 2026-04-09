import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { defineComponent, nextTick } from 'vue'
import { mount } from '@vue/test-utils'

const mocks = vi.hoisted(() => ({
  clearAuthSession: vi.fn(),
  fetchTenant: vi.fn(),
  getAuthSession: vi.fn(),
  push: vi.fn(),
  tenantName: 'Northwind Market',
}))

vi.mock('@/services/auth', () => ({
  clearAuthSession: mocks.clearAuthSession,
  getAuthSession: mocks.getAuthSession,
}))

vi.mock('@/stores/tenant', () => ({
  useTenantStore: () => ({
    tenantName: mocks.tenantName,
    fetchTenant: mocks.fetchTenant,
  }),
}))

vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: mocks.push,
  }),
}))

import NavBar from '../../components/NavBar.vue'

const AvatarStub = defineComponent({
  props: {
    name: {
      type: String,
      required: true,
    },
    size: {
      type: Number,
      required: true,
    },
  },
  template: '<div data-test="avatar">{{ name }}|{{ size }}</div>',
})

function mountNavBar(props?: Record<string, unknown>) {
  return mount(NavBar, {
    props,
    global: {
      stubs: {
        Avatar: AvatarStub,
      },
    },
  })
}

beforeEach(() => {
  mocks.clearAuthSession.mockReset()
  mocks.fetchTenant.mockReset()
  mocks.getAuthSession.mockReset()
  mocks.push.mockReset()
  mocks.tenantName = 'Northwind Market'
  mocks.getAuthSession.mockReturnValue({
    email: 'ada@example.com',
    fullName: 'Ada Lovelace',
  })
})

afterEach(() => {
  document.body.innerHTML = ''
})

describe('NavBar', () => {
  it('loads tenant details and emits the mobile sidebar toggle', async () => {
    const wrapper = mountNavBar({
      showSidebarToggle: true,
      sidebarOpen: true,
    })

    await nextTick()

    expect(mocks.fetchTenant).toHaveBeenCalledTimes(1)
    expect(wrapper.text()).toContain('Regula')
    expect(wrapper.text()).toContain('Northwind Market')
    expect(wrapper.text()).toContain('Ada Lovelace')
    expect(wrapper.get('[data-test="avatar"]').text()).toBe('Ada Lovelace|30')
    expect(wrapper.get('.menu-toggle').classes()).toContain('menu-toggle-active')

    await wrapper.get('.menu-toggle').trigger('click')

    expect(wrapper.emitted('toggle-sidebar')).toEqual([[]])

    wrapper.unmount()
  })

  it('opens the user menu and closes it on outside click', async () => {
    const wrapper = mountNavBar()

    await wrapper.get('button[aria-label="Open user menu"]').trigger('click')
    await nextTick()

    expect(wrapper.get('[role="menu"]').text()).toContain('ada@example.com')
    expect(wrapper.get('[role="menu"]').text()).toContain('Log out')

    document.body.dispatchEvent(new MouseEvent('click', { bubbles: true }))
    await nextTick()

    expect(wrapper.find('[role="menu"]').exists()).toBe(false)

    wrapper.unmount()
  })

  it('clears the session and routes to login on logout', async () => {
    mocks.push.mockResolvedValue(undefined)

    const wrapper = mountNavBar()

    await wrapper.get('button[aria-label="Open user menu"]').trigger('click')
    await wrapper.get('.user-menu-logout').trigger('click')
    await nextTick()

    expect(mocks.clearAuthSession).toHaveBeenCalledTimes(1)
    expect(mocks.push).toHaveBeenCalledWith({ name: 'login' })
    expect(wrapper.find('[role="menu"]').exists()).toBe(false)

    wrapper.unmount()
  })
})
