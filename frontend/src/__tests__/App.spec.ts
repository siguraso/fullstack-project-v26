import { describe, it, expect } from 'vitest'

import { mount } from '@vue/test-utils'
import LoginView from '../views/LoginView.vue'

describe('App', () => {
  it('renders the login page', () => {
    const wrapper = mount(LoginView, {
      global: {
        stubs: {
          RouterLink: true,
        },
      },
    })

    expect(wrapper.text()).toContain('Welcome back')
    expect(wrapper.text()).toContain('Sign in to continue to your workspace.')
  })
})
