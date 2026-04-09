import { beforeEach, describe, expect, it, vi } from 'vitest'
import { defineComponent, nextTick } from 'vue'
import { mount } from '@vue/test-utils'
import TemperatureZoneOverview from '@/views/temperature-logs/components/TemperatureZoneOverview.vue'

vi.mock('@/services/auth', () => ({
  getAuthSession: vi.fn(),
}))

import { getAuthSession } from '@/services/auth'

const InfoCardStub = defineComponent({
  template: `
    <section data-test="info-card-stub">
      <slot name="extra-header-content" />
      <slot />
    </section>
  `,
})

const zones = [
  { id: 1, name: 'Zone A', lowerLimitCelsius: 0, upperLimitCelsius: 4, active: true },
  { id: 2, name: 'Zone B', lowerLimitCelsius: 1, upperLimitCelsius: 5, active: true },
  { id: 3, name: 'Zone C', lowerLimitCelsius: 2, upperLimitCelsius: 6, active: true },
  { id: 4, name: 'Zone D', lowerLimitCelsius: 3, upperLimitCelsius: 7, active: true },
  { id: 5, name: 'Zone E', lowerLimitCelsius: 4, upperLimitCelsius: 8, active: true },
]

beforeEach(() => {
  vi.clearAllMocks()
})

describe('TemperatureZoneOverview', () => {
  it('shows admin actions and emits create/edit events', async () => {
    vi.mocked(getAuthSession).mockReturnValue({
      email: 'admin@test.no',
      fullName: 'Admin User',
      remember: true,
      token: 'token',
      refreshToken: 'refresh',
      role: 'ADMIN',
    })

    const wrapper = mount(TemperatureZoneOverview, {
      props: { zones },
      global: {
        stubs: {
          InfoCard: InfoCardStub,
        },
      },
    })

    await nextTick()

    expect(wrapper.find('button.add-button').exists()).toBe(true)
    expect(wrapper.findAll('button.edit-btn').length).toBe(4)

    await wrapper.get('button.add-button').trigger('click')
    await wrapper.get('button.edit-btn').trigger('click')

    expect(wrapper.emitted('create-zone')).toHaveLength(1)
    expect(wrapper.emitted('edit-zone')).toHaveLength(1)

    await wrapper.findAll('button.page-button')[1].trigger('click')
    expect(wrapper.text()).toContain('Page 2 of 2')
  })

  it('hides admin actions for non-admin role', async () => {
    vi.mocked(getAuthSession).mockReturnValue({
      email: 'staff@test.no',
      fullName: 'Staff User',
      remember: false,
      token: 'token',
      refreshToken: 'refresh',
      role: 'STAFF',
    })

    const wrapper = mount(TemperatureZoneOverview, {
      props: { zones: [zones[0]] },
      global: {
        stubs: {
          InfoCard: InfoCardStub,
        },
      },
    })

    await nextTick()

    expect(getAuthSession).toHaveBeenCalled()

    expect(wrapper.find('button.add-button').exists()).toBe(false)
    expect(wrapper.find('button.edit-btn').exists()).toBe(false)
  })
})
