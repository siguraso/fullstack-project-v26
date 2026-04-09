import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { nextTick, reactive } from 'vue'
import { flushPromises, mount } from '@vue/test-utils'

const mocks = vi.hoisted(() => ({
  register: vi.fn(),
  validateInviteToken: vi.fn(),
  push: vi.fn(),
  route: null as { query: Record<string, string | string[] | undefined> } | null,
}))

vi.mock('vue-router', () => ({
  useRoute: () => mocks.route,
  useRouter: () => ({
    push: mocks.push,
  }),
}))

vi.mock('@/services/auth', () => ({
  register: mocks.register,
}))

vi.mock('@/services/invitation', () => ({
  validateInviteToken: mocks.validateInviteToken,
}))

import InviteAcceptView from '../views/InviteAcceptView.vue'

function mountInviteAcceptView(token?: string | string[]) {
  mocks.route = reactive({
    query: {
      token,
    },
  }) as typeof mocks.route

  return mount(InviteAcceptView)
}

beforeEach(() => {
  mocks.register.mockReset()
  mocks.validateInviteToken.mockReset()
  mocks.push.mockReset()
  mocks.route = null
  document.title = 'Test'
})

afterEach(() => {
  vi.useRealTimers()
})

describe('InviteAcceptView', () => {
  it('shows an error when the invitation token is missing', async () => {
    const wrapper = mountInviteAcceptView(undefined)

    await flushPromises()

    expect(wrapper.text()).toContain('This invitation link is missing its token.')
    expect(wrapper.text()).toContain('Invitation unavailable')
    expect(wrapper.find('form').exists()).toBe(false)
    expect(mocks.validateInviteToken).not.toHaveBeenCalled()

    wrapper.unmount()
  })

  it('validates the invite and registers the user', async () => {
    mocks.validateInviteToken.mockResolvedValue({
      valid: true,
      email: 'user@example.com',
      organizationId: 42,
    })
    mocks.register.mockResolvedValue(undefined)
    mocks.push.mockResolvedValue(undefined)

    const wrapper = mountInviteAcceptView('invite-token-123')

    await flushPromises()

    expect(mocks.validateInviteToken).toHaveBeenCalledWith('invite-token-123')
    expect(document.title).toBe('Accept invitation · Regula')
    expect(wrapper.text()).toContain('Invitation verified')
    expect(wrapper.text()).toContain('user@example.com')
    expect(wrapper.text()).toContain('#42')

    await wrapper.get('input[placeholder="Jane"]').setValue('  Jane  ')
    await wrapper.get('input[placeholder="Doe"]').setValue('  Doe  ')
    await wrapper.get('input[placeholder="+47 99 99 99 99"]').setValue('  +47 12 34 56 78  ')
    await wrapper.get('input[placeholder="At least 8 characters"]').setValue('password123')
    await wrapper.get('input[type="checkbox"]').setValue(false)

    await wrapper.get('form').trigger('submit.prevent')
    await flushPromises()

    expect(mocks.register).toHaveBeenCalledWith(
      {
        email: 'user@example.com',
        password: 'password123',
        firstName: 'Jane',
        lastName: 'Doe',
        phone: '+47 12 34 56 78',
        inviteToken: 'invite-token-123',
      },
      false,
    )
    expect(mocks.push).toHaveBeenCalledWith('/dashboard')

    wrapper.unmount()
  })

  it('shows an error when validation fails', async () => {
    mocks.validateInviteToken.mockRejectedValue(new Error('Invalid or expired invitation link.'))

    const wrapper = mountInviteAcceptView('bad-token')

    await flushPromises()

    expect(wrapper.text()).toContain('Invalid or expired invitation link.')
    expect(wrapper.find('form').exists()).toBe(false)
    expect(mocks.register).not.toHaveBeenCalled()

    wrapper.unmount()
  })
})
