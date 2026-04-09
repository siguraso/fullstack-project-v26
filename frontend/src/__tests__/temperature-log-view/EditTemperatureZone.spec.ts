import { describe, expect, it } from 'vitest'
import { defineComponent } from 'vue'
import { mount } from '@vue/test-utils'
import EditTemperatureZone from '@/views/temperature-logs/components/EditTemperatureZone.vue'

const CardStub = defineComponent({
  template: `
    <div data-test="card-stub">
      <slot name="card-header" />
      <slot name="card-content" />
    </div>
  `,
})

const zone = {
  id: 4,
  name: 'Prep Station',
  lowerLimitCelsius: 2,
  upperLimitCelsius: 6,
  active: true,
}

describe('EditTemperatureZone', () => {
  it('emits close and delete actions', async () => {
    const wrapper = mount(EditTemperatureZone, {
      props: { zone },
      global: {
        stubs: {
          Card: CardStub,
        },
      },
    })

    await wrapper.get('button.close-button').trigger('click')
    await wrapper.get('button.delete-button').trigger('click')

    expect(wrapper.emitted('close')).toHaveLength(1)
    expect(wrapper.emitted('delete')).toEqual([[4]])
  })

  it('emits save with updated values', async () => {
    const wrapper = mount(EditTemperatureZone, {
      props: { zone },
      global: {
        stubs: {
          Card: CardStub,
        },
      },
    })

    const inputs = wrapper.findAll('input')

    await inputs[0]!.setValue('Prep Updated')
    await inputs[1]!.setValue(1)
    await inputs[2]!.setValue(7)
    await wrapper.get('button.save-btn').trigger('click')

    expect(wrapper.emitted('save')).toEqual([
      [
        {
          id: 4,
          name: 'Prep Updated',
          lowerLimitCelsius: 1,
          upperLimitCelsius: 7,
          active: true,
        },
      ],
    ])
  })
})
