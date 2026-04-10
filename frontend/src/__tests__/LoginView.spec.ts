import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { defineComponent, nextTick } from 'vue'
import { flushPromises, mount } from '@vue/test-utils'

const mocks = vi.hoisted(() => ({
  login: vi.fn(),
  push: vi.fn(),
}))

vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: mocks.push,
  }),
}))

vi.mock('@/services/auth', () => ({
  login: mocks.login,
}))

import LoginView from '../views/LoginView.vue'

const InfoCardStub = defineComponent({
  template: '<section class="info-card-stub"><slot /></section>',
})

function mountLoginView() {
  return mount(LoginView, {
    global: {
      stubs: {
        InfoCard: InfoCardStub,
      },
    },
  })
}

function mockMatchMedia(reducedMotion = false) {
  vi.stubGlobal('matchMedia', (query: string) => ({
    matches: reducedMotion && query.includes('prefers-reduced-motion'),
    media: query,
    onchange: null,
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    addListener: vi.fn(),
    removeListener: vi.fn(),
    dispatchEvent: vi.fn(),
  }))
}

beforeEach(() => {
  mocks.login.mockReset()
  mocks.push.mockReset()
  mockMatchMedia()
})

afterEach(() => {
  vi.useRealTimers()
  vi.unstubAllGlobals()
})

describe('LoginView', () => {
  it('renders the sign-in form', () => {
    const wrapper = mountLoginView()

    expect(wrapper.get('h1').text()).toBe('Welcome back')
    expect(wrapper.get('p').text()).toContain('Sign in to continue to your workspace.')
    expect(wrapper.get('button[type="submit"]').text()).toBe('Sign in')
    expect(wrapper.get('#email').attributes('placeholder')).toBe('you@company.com')
    expect(wrapper.get('#password').attributes('placeholder')).toBe('Password')

    wrapper.unmount()
  })

  it('submits credentials and navigates on success', async () => {
    vi.useFakeTimers()
    mockMatchMedia(true)
    mocks.login.mockResolvedValue(undefined)
    mocks.push.mockResolvedValue(undefined)

    const wrapper = mountLoginView()

    await wrapper.get('#email').setValue('  admin@test.no  ')
    await wrapper.get('#password').setValue('secret123')
    await wrapper.get('input[type="checkbox"]').setValue(true)

    await wrapper.get('form').trigger('submit.prevent')
    await nextTick()

    expect(wrapper.get('button[type="submit"]').text()).toBe('Signing in...')
    expect(mocks.login).toHaveBeenCalledWith(
      {
        email: 'admin@test.no',
        password: 'secret123',
      },
      true,
    )

    await vi.advanceTimersByTimeAsync(140)
    await flushPromises()

    expect(mocks.push).toHaveBeenCalledWith({
      name: 'dashboard',
      state: {
        loginTransition: 'success',
      },
    })

    wrapper.unmount()
  })

  it('shows an error message when login fails', async () => {
    mocks.login.mockRejectedValue(new Error('Invalid credentials'))

    const wrapper = mountLoginView()

    await wrapper.get('#email').setValue('user@test.no')
    await wrapper.get('#password').setValue('wrong-password')
    await wrapper.get('form').trigger('submit.prevent')

    await flushPromises()

    expect(wrapper.text()).toContain('Invalid credentials')
    expect(wrapper.get('button[type="submit"]').text()).toBe('Sign in')
    expect(mocks.push).not.toHaveBeenCalled()

    wrapper.unmount()
  })
})
