import { afterEach, describe, expect, it, vi } from 'vitest'
import { defineComponent, nextTick } from 'vue'
import { mount } from '@vue/test-utils'
import TemperatureLogView from '@/views/temperature-logs/TemperatureLogView.vue'
import type { TemperatureLog } from '@/interfaces/TemperatureLog.interface'
import type { TemperatureZone } from '@/interfaces/TemperatureZone.interface'

vi.mock('@/services/temperatureLog', () => ({
  fetchTemperatureLogs: vi.fn(),
}))

vi.mock('@/services/temperatureZone', () => ({
  getTemperatureZones: vi.fn(),
  editTemperatureZone: vi.fn(),
  deleteTemperatureZone: vi.fn(),
  addTemperatureZone: vi.fn(),
}))

import { fetchTemperatureLogs } from '@/services/temperatureLog'
import {
  addTemperatureZone,
  deleteTemperatureZone,
  editTemperatureZone,
  getTemperatureZones,
} from '@/services/temperatureZone'

const CreateTemperatureLogStub = defineComponent({
  props: {
    temperatureZones: {
      type: Array,
      required: true,
    },
  },
  emits: ['created'],
  template: `
    <section data-test="create-log-stub">
      <p data-test="create-log-zones">{{ temperatureZones.length }}</p>
      <button
        data-test="emit-created-log"
        @click="$emit('created', { id: 99, temperatureZoneId: 1, temperatureCelsius: 4, timestamp: '2026-04-09T10:00:00Z' })"
      >
        emit created
      </button>
    </section>
  `,
})

const TemperatureZoneOverviewStub = defineComponent({
  props: {
    zones: {
      type: Array,
      required: true,
    },
  },
  emits: ['edit-zone', 'create-zone'],
  template: `
    <section data-test="zone-overview-stub">
      <p data-test="zone-overview-count">{{ zones.length }}</p>
      <button data-test="open-edit-zone" @click="$emit('edit-zone', zones[0])">edit zone</button>
      <button data-test="open-create-zone" @click="$emit('create-zone')">create zone</button>
    </section>
  `,
})

const TemperatureLogHistoryStub = defineComponent({
  props: {
    temperatureZones: {
      type: Array,
      required: true,
    },
    temperatureLogs: {
      type: Array,
      required: true,
    },
  },
  emits: ['view-log'],
  template: `
    <section data-test="log-history-stub">
      <p data-test="history-zones">{{ temperatureZones.length }}</p>
      <p data-test="history-logs">{{ temperatureLogs.length }}</p>
      <p data-test="history-first-log-id">{{ temperatureLogs[0]?.id ?? 'none' }}</p>
      <button data-test="open-log-details" @click="$emit('view-log', temperatureLogs[0])">view log</button>
    </section>
  `,
})

const EditTemperatureZoneStub = defineComponent({
  props: {
    zone: {
      type: Object,
      required: true,
    },
  },
  emits: ['close', 'save', 'delete'],
  template: `
    <section data-test="edit-zone-stub">
      <p data-test="editing-zone-id">{{ zone.id }}</p>
      <button
        data-test="save-zone"
        @click="$emit('save', { ...zone, name: 'Updated Zone Name' })"
      >
        save
      </button>
      <button data-test="delete-zone" @click="$emit('delete', zone.id)">delete</button>
      <button data-test="close-edit-zone" @click="$emit('close')">close</button>
    </section>
  `,
})

const CreateTemperatureZoneStub = defineComponent({
  emits: ['close', 'create'],
  template: `
    <section data-test="create-zone-stub">
      <button data-test="emit-create-zone" @click="$emit('create', { name: 'Dry Storage', lowerLimitCelsius: 1, upperLimitCelsius: 7, active: true })">
        create zone
      </button>
      <button data-test="close-create-zone" @click="$emit('close')">close create</button>
    </section>
  `,
})

const TemperatureLogDetailsDialogStub = defineComponent({
  props: {
    log: {
      type: Object,
      required: true,
    },
    zones: {
      type: Array,
      required: true,
    },
  },
  emits: ['close'],
  template: `
    <section data-test="log-details-stub">
      <p data-test="details-log-id">{{ log.id }}</p>
      <p data-test="details-zone-count">{{ zones.length }}</p>
      <button data-test="close-log-details" @click="$emit('close')">close details</button>
    </section>
  `,
})

const zoneFixture: TemperatureZone[] = [
  {
    id: 1,
    name: 'Fridge',
    lowerLimitCelsius: 0,
    upperLimitCelsius: 4,
    active: true,
  },
]

const logFixture: TemperatureLog[] = [
  {
    id: 11,
    temperatureZoneId: 1,
    temperatureZoneName: 'Fridge',
    temperatureCelsius: 3,
    timestamp: '2026-04-09T12:00:00Z',
    recordedByName: 'Alice',
  },
]

function flushPromises() {
  return new Promise<void>((resolve) => {
    setTimeout(resolve, 0)
  })
}

function mountView() {
  return mount(TemperatureLogView, {
    global: {
      stubs: {
        CreateTemperatureLog: CreateTemperatureLogStub,
        TemperatureZoneOverview: TemperatureZoneOverviewStub,
        TemperatureLogHistory: TemperatureLogHistoryStub,
        EditTemperatureZone: EditTemperatureZoneStub,
        CreateTemperatureZone: CreateTemperatureZoneStub,
        TemperatureLogDetailsDialog: TemperatureLogDetailsDialogStub,
      },
    },
  })
}

afterEach(() => {
  vi.clearAllMocks()
  document.documentElement.style.overflow = ''
  document.body.style.overflow = ''
})

describe('TemperatureLogView', () => {
  it('loads zones and logs on mount and passes them to child components', async () => {
    vi.mocked(getTemperatureZones).mockResolvedValue(zoneFixture)
    vi.mocked(fetchTemperatureLogs).mockResolvedValue(logFixture)

    const wrapper = mountView()
    await flushPromises()
    await nextTick()

    expect(getTemperatureZones).toHaveBeenCalledOnce()
    expect(fetchTemperatureLogs).toHaveBeenCalledOnce()
    expect(wrapper.get('[data-test="create-log-zones"]').text()).toBe('1')
    expect(wrapper.get('[data-test="zone-overview-count"]').text()).toBe('1')
    expect(wrapper.get('[data-test="history-logs"]').text()).toBe('1')

    wrapper.unmount()
  })

  it('shows fetch errors when zone and log loading fails', async () => {
    vi.mocked(getTemperatureZones).mockRejectedValue(new Error('Zones failed'))
    vi.mocked(fetchTemperatureLogs).mockRejectedValue(new Error('Logs failed'))

    const wrapper = mountView()
    await flushPromises()
    await nextTick()

    const errorText = wrapper.text()
    expect(errorText).toContain('Zones failed')
    expect(errorText).toContain('Logs failed')

    wrapper.unmount()
  })

  it('prepends a new log when CreateTemperatureLog emits created', async () => {
    vi.mocked(getTemperatureZones).mockResolvedValue(zoneFixture)
    vi.mocked(fetchTemperatureLogs).mockResolvedValue(logFixture)

    const wrapper = mountView()
    await flushPromises()
    await nextTick()

    await wrapper.get('[data-test="emit-created-log"]').trigger('click')
    await nextTick()

    expect(wrapper.get('[data-test="history-logs"]').text()).toBe('2')
    expect(wrapper.get('[data-test="history-first-log-id"]').text()).toBe('99')

    wrapper.unmount()
  })

  it('opens and closes the log details overlay', async () => {
    vi.mocked(getTemperatureZones).mockResolvedValue(zoneFixture)
    vi.mocked(fetchTemperatureLogs).mockResolvedValue(logFixture)

    const wrapper = mountView()
    await flushPromises()
    await nextTick()

    await wrapper.get('[data-test="open-log-details"]').trigger('click')
    await nextTick()

    expect(wrapper.find('[data-test="log-details-stub"]').exists()).toBe(true)
    expect(document.body.style.overflow).toBe('hidden')

    await wrapper.get('[data-test="close-log-details"]').trigger('click')
    await nextTick()

    expect(wrapper.find('[data-test="log-details-stub"]').exists()).toBe(false)
    expect(document.body.style.overflow).toBe('')

    wrapper.unmount()
  })

  it('opens edit overlay, saves changes, and updates the in-memory zone list', async () => {
    vi.mocked(getTemperatureZones).mockResolvedValue(zoneFixture)
    vi.mocked(fetchTemperatureLogs).mockResolvedValue(logFixture)
    vi.mocked(editTemperatureZone).mockResolvedValue({
      ...zoneFixture[0],
      name: 'Updated Zone Name',
    })

    const wrapper = mountView()
    await flushPromises()
    await nextTick()

    await wrapper.get('[data-test="open-edit-zone"]').trigger('click')
    await nextTick()
    expect(wrapper.find('[data-test="edit-zone-stub"]').exists()).toBe(true)

    await wrapper.get('[data-test="save-zone"]').trigger('click')
    await flushPromises()
    await nextTick()

    expect(editTemperatureZone).toHaveBeenCalledWith(
      1,
      expect.objectContaining({ name: 'Updated Zone Name' }),
    )
    expect(wrapper.find('[data-test="edit-zone-stub"]').exists()).toBe(false)

    wrapper.unmount()
  })

  it('opens edit overlay and deletes zone from state', async () => {
    vi.mocked(getTemperatureZones).mockResolvedValue(zoneFixture)
    vi.mocked(fetchTemperatureLogs).mockResolvedValue(logFixture)
    vi.mocked(deleteTemperatureZone).mockResolvedValue()

    const wrapper = mountView()
    await flushPromises()
    await nextTick()

    await wrapper.get('[data-test="open-edit-zone"]').trigger('click')
    await nextTick()
    await wrapper.get('[data-test="delete-zone"]').trigger('click')
    await flushPromises()
    await nextTick()

    expect(deleteTemperatureZone).toHaveBeenCalledWith(1)
    expect(wrapper.get('[data-test="zone-overview-count"]').text()).toBe('0')

    wrapper.unmount()
  })

  it('creates a zone from the create overlay', async () => {
    vi.mocked(getTemperatureZones).mockResolvedValue(zoneFixture)
    vi.mocked(fetchTemperatureLogs).mockResolvedValue(logFixture)
    vi.mocked(addTemperatureZone).mockResolvedValue({
      id: 77,
      name: 'Dry Storage',
      lowerLimitCelsius: 1,
      upperLimitCelsius: 7,
      active: true,
    })

    const wrapper = mountView()
    await flushPromises()
    await nextTick()

    await wrapper.get('[data-test="open-create-zone"]').trigger('click')
    await nextTick()
    expect(wrapper.find('[data-test="create-zone-stub"]').exists()).toBe(true)

    await wrapper.get('[data-test="emit-create-zone"]').trigger('click')
    await flushPromises()
    await nextTick()

    expect(addTemperatureZone).toHaveBeenCalledWith({
      name: 'Dry Storage',
      lowerLimitCelsius: 1,
      upperLimitCelsius: 7,
      active: true,
    })
    expect(wrapper.find('[data-test="create-zone-stub"]').exists()).toBe(false)
    expect(wrapper.get('[data-test="zone-overview-count"]').text()).toBe('2')

    wrapper.unmount()
  })

  it('restores body scroll on unmount when an overlay is open', async () => {
    vi.mocked(getTemperatureZones).mockResolvedValue(zoneFixture)
    vi.mocked(fetchTemperatureLogs).mockResolvedValue(logFixture)

    const wrapper = mountView()
    await flushPromises()
    await nextTick()

    await wrapper.get('[data-test="open-create-zone"]').trigger('click')
    await nextTick()

    expect(document.body.style.overflow).toBe('hidden')

    wrapper.unmount()

    expect(document.body.style.overflow).toBe('')
    expect(document.documentElement.style.overflow).toBe('')
  })
})
