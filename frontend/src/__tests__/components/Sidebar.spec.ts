import { beforeEach, describe, expect, it, vi } from 'vitest'
import { nextTick } from 'vue'
import { mount } from '@vue/test-utils'

const mocks = vi.hoisted(() => ({
  push: vi.fn(),
  resolve: vi.fn((destination: string) => ({
    fullPath: destination,
  })),
  route: null as { fullPath: string } | null,
  session: {
    email: 'admin@example.com',
    role: 'ADMIN',
  } as { email?: string; role?: string } | null,
}))

vi.mock('@/services/auth', () => ({
  clearAuthSession: vi.fn(),
  getAuthSession: () => mocks.session,
}))

vi.mock('vue-router', async () => {
  const { reactive } = await import('vue')

  mocks.route = reactive({
    fullPath: '/dashboard',
  })

  return {
    useRoute: () => mocks.route,
    useRouter: () => ({
      push: mocks.push,
      resolve: mocks.resolve,
    }),
  }
})

import Sidebar from '../../components/Sidebar.vue'

function mountSidebar(props?: Record<string, unknown>) {
  return mount(Sidebar, {
    props,
  })
}

function getButtonByText(wrapper: ReturnType<typeof mountSidebar>, text: string) {
  const button = wrapper
    .findAll('button')
    .find((entry) => entry.text().replace(/\s+/g, ' ').trim() === text)

  expect(button, `Expected button with text "${text}"`).toBeTruthy()

  return button!
}

beforeEach(() => {
  mocks.push.mockReset()
  mocks.resolve.mockClear()
  mocks.session = {
    email: 'admin@example.com',
    role: 'ADMIN',
  }

  if (mocks.route) {
    mocks.route.fullPath = '/dashboard'
  }
})

describe('Sidebar', () => {
  it('shows the admin settings entry and marks the active route', async () => {
    mocks.route!.fullPath = '/settings'

    const wrapper = mountSidebar()
    await nextTick()

    expect(wrapper.text()).toContain('Overview')
    expect(wrapper.text()).toContain('Compliance')
    expect(wrapper.text()).toContain('Settings')
    expect(getButtonByText(wrapper, 'Settings').classes()).toContain('nav-button-active')

    wrapper.unmount()
  })

  it('emits close and navigates when mobile users select a destination', async () => {
    const wrapper = mountSidebar({
      isMobile: true,
      open: true,
    })

    await nextTick()

    expect(wrapper.text()).toContain('Navigation')
    expect(wrapper.text()).toContain('admin@example.com')

    await getButtonByText(wrapper, 'Documents').trigger('click')

    expect(mocks.push).toHaveBeenCalledWith('/documents')
    expect(wrapper.emitted('close')).toEqual([[]])

    wrapper.unmount()
  })

  it('hides settings for non-admin users and toggles compliance groups', async () => {
    mocks.session = {
      email: 'staff@example.com',
      role: 'STAFF',
    }

    const wrapper = mountSidebar()
    await nextTick()

    expect(wrapper.text()).not.toContain('Settings')
    expect(wrapper.text()).toContain('Temperature Logs')

    await getButtonByText(wrapper, 'IK-food').trigger('click')
    await nextTick()

    expect(wrapper.text()).not.toContain('Temperature Logs')

    await getButtonByText(wrapper, 'IK-food').trigger('click')
    await nextTick()

    expect(wrapper.text()).toContain('Temperature Logs')

    wrapper.unmount()
  })
})
