<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getPresets } from '@/services/checklist'

interface PresetItem {
  id: number
  title: string
  description: string
  category: string
  priority: 'LOW' | 'HIGH'
  tab: string
  groupLabel: string
  sortOrder: number
}

interface PresetGroup {
  label: string
  items: PresetItem[]
}

const emit = defineEmits<{
  close: []
  add: [items: PresetItem[]]
}>()

// ── State ──────────────────────────────────────────────────────────────────────

const loading = ref(true)
const error = ref<string | null>(null)
const allPresets = ref<PresetItem[]>([])
const selected = ref<Set<number>>(new Set())
const activeTab = ref<string>('')

// ── Data loading ───────────────────────────────────────────────────────────────

onMounted(async () => {
  try {
    allPresets.value = await getPresets()
    // Default to the first tab returned by the API
    activeTab.value = tabs.value[0]?.key ?? ''
  } catch {
    error.value = 'Failed to load presets. Please try again.'
  } finally {
    loading.value = false
  }
})

// ── Derived structure ──────────────────────────────────────────────────────────

/**
 * Unique tabs in display order. The backend orders by tab alphabetically
 * (HACCP → IK_ALCOHOL → IK_FOOD), so we apply a logical order here.
 */
const TAB_ORDER = ['IK_FOOD', 'IK_ALCOHOL', 'HACCP']

const TAB_LABELS: Record<string, { label: string; accentColor: string }> = {
  IK_FOOD: { label: 'IK-Food', accentColor: '#16a34a' },
  IK_ALCOHOL: { label: 'IK-Alcohol', accentColor: '#dc2626' },
  HACCP: { label: 'HACCP', accentColor: '#2563eb' },
}

const tabs = computed(() => {
  const keys = [...new Set(allPresets.value.map((p) => p.tab))]
  keys.sort((a, b) => {
    const ai = TAB_ORDER.indexOf(a)
    const bi = TAB_ORDER.indexOf(b)
    if (ai !== -1 && bi !== -1) return ai - bi
    if (ai !== -1) return -1
    if (bi !== -1) return 1
    return a.localeCompare(b)
  })
  return keys.map((key) => ({
    key,
    label: TAB_LABELS[key]?.label ?? key.split('_').join(' '),
    accentColor: TAB_LABELS[key]?.accentColor ?? '#475569',
  }))
})

/** Items for the active tab, grouped by groupLabel in their original sort order. */
const currentGroups = computed((): PresetGroup[] => {
  const tabItems = allPresets.value.filter((p) => p.tab === activeTab.value)
  const map = new Map<string, PresetItem[]>()
  for (const item of tabItems) {
    if (!map.has(item.groupLabel)) map.set(item.groupLabel, [])
    map.get(item.groupLabel)!.push(item)
  }
  return [...map.entries()].map(([label, items]) => ({ label, items }))
})

// ── Selection ──────────────────────────────────────────────────────────────────

function toggleItem(id: number) {
  const next = new Set(selected.value)
  if (next.has(id)) {
    next.delete(id)
  } else {
    next.add(id)
  }
  selected.value = next
}

const selectedCount = computed(() => selected.value.size)

function handleAdd() {
  const items = allPresets.value.filter((p) => selected.value.has(p.id))
  emit('add', items)
}
</script>

<template>
  <div class="pip-overlay">
    <div class="pip-modal">
      <!-- Header -->
      <div class="pip-header">
        <div>
          <h2 class="pip-title">Compliance Preset Library</h2>
          <p class="pip-subtitle">
            Select items based on Mattilsynet IK requirements to quickly populate your checklist.
          </p>
        </div>
        <button class="pip-close" @click="emit('close')">✕</button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="pip-loading">Loading presets…</div>

      <!-- Error -->
      <div v-else-if="error" class="pip-error">{{ error }}</div>

      <!-- Content -->
      <template v-else>
        <!-- Tabs -->
        <div class="pip-tabs">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            class="pip-tab"
            :class="{ 'pip-tab-active': activeTab === tab.key }"
            :style="
              activeTab === tab.key
                ? { borderBottomColor: tab.accentColor, color: tab.accentColor }
                : {}
            "
            @click="activeTab = tab.key"
          >
            {{ tab.label }}
          </button>
        </div>

        <!-- Groups and items -->
        <div class="pip-content">
          <div v-for="group in currentGroups" :key="group.label" class="pip-group">
            <h3 class="pip-group-label">{{ group.label }}</h3>
            <div class="pip-items">
              <button
                v-for="item in group.items"
                :key="item.id"
                class="pip-item"
                :class="{ 'pip-item-selected': selected.has(item.id) }"
                @click="toggleItem(item.id)"
              >
                <div class="pip-item-top">
                  <span class="pip-item-name">{{ item.title }}</span>
                  <span
                    class="pip-badge"
                    :class="item.priority === 'HIGH' ? 'pip-badge-high' : 'pip-badge-low'"
                  >
                    {{ item.priority }}
                  </span>
                </div>
                <p class="pip-item-desc">{{ item.description }}</p>
                <span class="pip-indicator">{{ selected.has(item.id) ? '✓' : '+' }}</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="pip-footer">
          <span class="pip-count">
            {{ selectedCount }} item{{ selectedCount !== 1 ? 's' : '' }} selected
          </span>
          <div class="pip-footer-actions">
            <button class="cancel" @click="emit('close')">Cancel</button>
            <button class="primary" :disabled="selectedCount === 0" @click="handleAdd">
              Add {{ selectedCount > 0 ? selectedCount : '' }} Item{{
                selectedCount !== 1 ? 's' : ''
              }}
            </button>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style>
.pip-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1100;
}

.pip-modal {
  width: 820px;
  max-width: 95vw;
  max-height: 88vh;
  background: #ffffff;
  border-radius: 20px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

/* HEADER */
.pip-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px 28px 16px;
  border-bottom: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.pip-title {
  font-size: 20px;
  font-weight: 700;
  color: #0f172a;
  margin: 0 0 4px;
}

.pip-subtitle {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

.pip-close {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #64748b;
  padding: 8px;
  border-radius: 8px;
  flex-shrink: 0;
}

.pip-close:hover {
  background: #f3f4f6;
}

/* LOADING / ERROR */
.pip-loading,
.pip-error {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #64748b;
  padding: 48px;
}

.pip-error {
  color: #dc2626;
}

/* TABS */
.pip-tabs {
  display: flex;
  gap: 4px;
  padding: 0 28px;
  border-bottom: 2px solid #e5e7eb;
  flex-shrink: 0;
}

.pip-tab {
  padding: 12px 20px;
  background: none;
  border: none;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: color 0.15s;
}

.pip-tab:hover {
  color: #0f172a;
}

.pip-tab-active {
  font-weight: 700;
}

/* CONTENT */
.pip-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px 28px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.pip-group-label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: #94a3b8;
  text-transform: uppercase;
  margin: 0 0 10px;
}

.pip-items {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.pip-item {
  position: relative;
  text-align: left;
  background: #f8fafc;
  border: 1.5px solid #e2e8f0;
  border-radius: 12px;
  padding: 14px 36px 14px 14px;
  cursor: pointer;
  transition:
    border-color 0.15s,
    background 0.15s;
  width: 100%;
}

.pip-item:hover {
  border-color: #94a3b8;
  background: #f1f5f9;
}

.pip-item-selected {
  border-color: #0f172a !important;
  background: #f0f4ff !important;
}

.pip-item-top {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-bottom: 6px;
}

.pip-item-name {
  font-size: 13px;
  font-weight: 600;
  color: #0f172a;
  line-height: 1.4;
  flex: 1;
}

.pip-badge {
  font-size: 10px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 999px;
  white-space: nowrap;
  flex-shrink: 0;
}

.pip-badge-high {
  background: #fee2e2;
  color: #991b1b;
}

.pip-badge-low {
  background: #e5e7eb;
  color: #374151;
}

.pip-item-desc {
  font-size: 12px;
  color: #64748b;
  line-height: 1.5;
  margin: 0;
}

.pip-indicator {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 22px;
  height: 22px;
  border-radius: 50%;
  background: #e2e8f0;
  color: #1e293b;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  transition:
    background 0.15s,
    color 0.15s;
}

.pip-item-selected .pip-indicator {
  background: #0f172a;
  color: white;
}

/* FOOTER */
.pip-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 28px;
  border-top: 1px solid #e5e7eb;
  flex-shrink: 0;
}

.pip-count {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.pip-footer-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}
</style>
