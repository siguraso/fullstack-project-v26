import { clearAuthSession, getAuthSession, refreshToken } from '@/services/auth'

export async function apiFetch(url: string, options: RequestInit = {}) {
  let token = getAuthSession()?.token

  let res = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    },
  })

  if (res.status === 403) {
    const newToken = await refreshToken()

    if (!newToken) {
      clearAuthSession()
      window.location.href = '/login'
      return res
    }

    // retry request
    res = await fetch(url, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${newToken}`,
        ...options.headers,
      },
    })
  }

  return res
}
