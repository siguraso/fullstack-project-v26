/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL?: string
  readonly VITE_TENANT_ID?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue'

  const component: DefineComponent<Record<string, unknown>, Record<string, unknown>, unknown>
  export default component
}

declare module 'vue-chartjs' {
  import type { DefineComponent } from 'vue'

  export const Bar: DefineComponent<Record<string, unknown>, {}, any>
  export const Doughnut: DefineComponent<Record<string, unknown>, {}, any>
}

declare module 'chart.js' {
  export const ArcElement: unknown
  export const Tooltip: unknown
  export const Legend: unknown
  export const CategoryScale: unknown
  export const LinearScale: unknown
  export const BarElement: unknown

  export class Chart {
    static register(...items: unknown[]): void
  }
}
