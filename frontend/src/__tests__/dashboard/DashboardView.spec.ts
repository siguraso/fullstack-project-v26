import { beforeEach, describe, expect, it, vi } from 'vitest'
import { defineComponent } from 'vue'
import { flushPromises, mount } from '@vue/test-utils'

const mocks = vi.hoisted(() => ({
  getDashboardOverview: vi.fn(),
}))

vi.mock('@/services/dashboard', () => ({
  getDashboardOverview: mocks.getDashboardOverview,
}))

import DashboardView from '../../views/dashboard/DashboardView.vue'

const CriticalAlertsStub = defineComponent({
  props: {
    alerts: {
      type: Array,
      required: true,
    },
  },
  emits: ['resolved'],
  template: `
    <section data-test="alerts">
      {{ alerts.length }}
      <button data-test="alerts-resolved" @click="$emit('resolved')">Reload</button>
    </section>
  `,
})

const ChecklistCompletionCardStub = defineComponent({
  props: {
    completed: {
      type: Number,
      required: true,
    },
    total: {
      type: Number,
      required: true,
    },
  },
  template: '<section data-test="completion">{{ completed }}/{{ total }}</section>',
})

const ActiveDeviationCardStub = defineComponent({
  props: {
    active: {
      type: Number,
      required: true,
    },
    weekly: {
      type: Number,
      required: true,
    },
    closed: {
      type: Number,
      required: true,
    },
  },
  template: '<section data-test="deviation-card">{{ active }}|{{ weekly }}|{{ closed }}</section>',
})

const QuickActionsCardStub = defineComponent({
  template: '<section data-test="quick-actions">Quick actions</section>',
})

const PendingChecklistsStub = defineComponent({
  props: {
    checklists: {
      type: Array,
      required: false,
      default: undefined,
    },
  },
  emits: ['updated'],
  template: `
    <section data-test="pending-checklists">
      {{ (checklists || []).length }}
      <button data-test="pending-updated" @click="$emit('updated')">Updated</button>
    </section>
  `,
})

const TeamActivityStub = defineComponent({
  props: {
    activities: {
      type: Array,
      required: false,
      default: undefined,
    },
  },
  template: '<section data-test="team-activity">{{ (activities || []).length }}</section>',
})

function mountDashboardView() {
  return mount(DashboardView, {
    global: {
      stubs: {
        ActiveDeviationCard: ActiveDeviationCardStub,
        ChecklistCompletionCard: ChecklistCompletionCardStub,
        CriticalAlerts: CriticalAlertsStub,
        PendingChecklists: PendingChecklistsStub,
        QuickActionsCard: QuickActionsCardStub,
        TeamActivity: TeamActivityStub,
      },
    },
  })
}

beforeEach(() => {
  mocks.getDashboardOverview.mockReset()
})

describe('DashboardView', () => {
  it('loads dashboard data and passes the expected child props', async () => {
    mocks.getDashboardOverview.mockResolvedValue({
      activeDeviations: [{ id: 1 }, { id: 2 }],
      checklistsToday: {
        completedItems: 2,
        totalItems: 5,
      },
      criticalAlerts: [{ id: 'alert-1' }],
      pendingChecklists: [{ id: 7 }],
      teamActivity: [{ id: 9 }],
    })

    const wrapper = mountDashboardView()
    await flushPromises()

    expect(mocks.getDashboardOverview).toHaveBeenCalledTimes(1)
    expect(console.info).toHaveBeenCalledWith('[frontend][dashboard-view] dashboard load started', {
      source: 'mount',
    })
    expect(wrapper.get('h1').text()).toBe('Dashboard')
    expect(wrapper.get('[data-test="alerts"]').text()).toContain('1')
    expect(wrapper.get('[data-test="completion"]').text()).toBe('2/5')
    expect(wrapper.get('[data-test="deviation-card"]').text()).toBe('2|2|0')
    expect(wrapper.get('[data-test="pending-checklists"]').text()).toContain('1')
    expect(wrapper.get('[data-test="team-activity"]').text()).toBe('1')

    wrapper.unmount()
  })

  it('falls back to empty values when the initial load fails', async () => {
    mocks.getDashboardOverview.mockRejectedValue(new Error('Failed to load dashboard overview.'))

    const wrapper = mountDashboardView()
    await flushPromises()

    expect(console.error).toHaveBeenCalledWith('[frontend][dashboard-view] dashboard load failed', {
      source: 'mount',
      error: {
        name: 'Error',
        message: 'Failed to load dashboard overview.',
      },
    })
    expect(wrapper.find('[data-test="alerts"]').exists()).toBe(false)
    expect(wrapper.get('[data-test="completion"]').text()).toBe('0/0')
    expect(wrapper.get('[data-test="deviation-card"]').text()).toBe('0|0|0')
    expect(wrapper.get('[data-test="pending-checklists"]').text()).toContain('0')
    expect(wrapper.get('[data-test="team-activity"]').text()).toBe('0')

    wrapper.unmount()
  })

  it('reloads the dashboard after child update events', async () => {
    vi.useFakeTimers()
    mocks.getDashboardOverview
      .mockResolvedValueOnce({
        activeDeviations: [{ id: 1 }],
        checklistsToday: {
          completedItems: 1,
          totalItems: 3,
        },
        criticalAlerts: [{ id: 'alert-1' }],
        pendingChecklists: [{ id: 7 }],
        teamActivity: [],
      })
      .mockResolvedValueOnce({
        activeDeviations: [{ id: 1 }, { id: 2 }, { id: 3 }],
        checklistsToday: {
          completedItems: 3,
          totalItems: 3,
        },
        criticalAlerts: [],
        pendingChecklists: [],
        teamActivity: [{ id: 10 }, { id: 11 }],
      })

    const wrapper = mountDashboardView()
    await flushPromises()

    await wrapper.get('[data-test="pending-updated"]').trigger('click')

    expect(mocks.getDashboardOverview).toHaveBeenCalledTimes(1)

    await vi.advanceTimersByTimeAsync(250)
    await flushPromises()

    expect(mocks.getDashboardOverview).toHaveBeenCalledTimes(2)
    expect(wrapper.find('[data-test="alerts"]').exists()).toBe(false)
    expect(wrapper.get('[data-test="completion"]').text()).toBe('3/3')
    expect(wrapper.get('[data-test="deviation-card"]').text()).toBe('3|3|0')
    expect(wrapper.get('[data-test="team-activity"]').text()).toBe('2')

    wrapper.unmount()
    vi.useRealTimers()
  })
})
