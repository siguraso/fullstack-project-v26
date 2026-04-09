# API Contract Reference
---

## 1) Shared Response Envelope

Most JSON endpoints use the shared `ApiResponse<T>` envelope:

```json
{
  "success": true,
  "message": "Optional message",
  "data": { }
}
```

### Contract rules
- `success` indicates whether the request completed as expected.
- `message` is optional and may be used for user-facing feedback.
- `data` contains the typed response payload when one exists.
- The frontend treats any `2xx` response as success via `response.ok`.
- Empty bodies are acceptable for delete operations that return `204 No Content`.

---

## 2) Status-Code Semantics

| Scenario | Preferred Status | Body | Frontend expectation |
|---|---:|---|---|
| Read/list/detail | `200 OK` | `ApiResponse<T>` | Parse `data` |
| Create | `201 Created` | `ApiResponse<T>` | Parse `data` if present |
| Update | `200 OK` | `ApiResponse<T>` | Parse `data` if present |
| Delete | `204 No Content` | None | No JSON parsing |
| Validation/auth errors | `400` / `401` / `403` / `404` | Error envelope or message | Show error state |

> The current frontend is already tolerant of `201` and `204` because it checks `response.ok` instead of hard-coding `200`.

---

## 3) Frontend-Facing Endpoints

### Authentication
| Method | Path | Response | Frontend consumer |
|---|---|---|---|
| `POST` | `/api/auth/login` | `ApiResponse<LoginResponse>` | `frontend/src/services/auth.ts` |
| `POST` | `/api/auth/register` | `ApiResponse<LoginResponse>` | `frontend/src/services/auth.ts` |
| `POST` | `/api/auth/refresh` | `ApiResponse<RefreshResponse>` | `frontend/src/services/auth.ts` |

### Invitations
| Method | Path | Response | Frontend consumer |
|---|---|---|---|
| `GET` | `/api/invitations/validate?token=...` | `ApiResponse<InviteValidationResponse>` | `frontend/src/services/invitation.ts` |

### Dashboard
| Method | Path | Response | Frontend consumer |
|---|---|---|---|
| `GET` | `/api/dashboard/overview` | `ApiResponse<DashboardOverviewDTO>` | `frontend/src/services/dashboard.ts` |

### Deviations
| Method | Path | Response | Frontend consumer |
|---|---|---|---|
| `GET` | `/api/deviations` | `ApiResponse<DeviationDTO[]>` | `frontend/src/services/deviation.ts` |
| `POST` | `/api/deviations` | `ApiResponse<DeviationDTO>` | `frontend/src/services/deviation.ts` |
| `PATCH` | `/api/deviations/{id}` | `ApiResponse<DeviationDTO>` | `frontend/src/services/deviation.ts` |

### Temperature zones
| Method | Path | Response | Frontend consumer |
|---|---|---|---|
| `GET` | `/api/ikfood/temperature-zones` | `ApiResponse<TemperatureZoneDTO[]>` | `frontend/src/services/temperatureZone.ts` |
| `POST` | `/api/ikfood/temperature-zones` | `ApiResponse<TemperatureZoneDTO>` | `frontend/src/services/temperatureZone.ts` |
| `PUT` | `/api/ikfood/temperature-zones/{id}` | `ApiResponse<TemperatureZoneDTO>` | `frontend/src/services/temperatureZone.ts` |
| `DELETE` | `/api/ikfood/temperature-zones/{id}` | `204 No Content` | `frontend/src/services/temperatureZone.ts` |

### Temperature logs
| Method | Path | Response | Frontend consumer |
|---|---|---|---|
| `GET` | `/api/ikfood/temperature-logs` | `ApiResponse<TemperatureLogDTO[]>` | `frontend/src/services/temperatureLog.ts` |
| `GET` | `/api/ikfood/temperature-logs/{id}` | `ApiResponse<TemperatureLogDTO>` | `frontend/src/services/temperatureLog.ts` |
| `POST` | `/api/ikfood/temperature-logs` | `ApiResponse<TemperatureLogDTO>` | `frontend/src/services/temperatureLog.ts` |
| `DELETE` | `/api/ikfood/temperature-logs/{id}` | `204 No Content` | `frontend/src/services/temperatureLog.ts` |

### Alcohol logs
| Method | Path | Response | Frontend consumer |
|---|---|---|---|
| `GET` | `/api/ikalcohol/logs` | `ApiResponse<AlcoholLogDTO[]>` | `frontend/src/services/alcoholLog.ts` |
| `POST` | `/api/ikalcohol/logs` | `ApiResponse<AlcoholLogDTO>` | `frontend/src/services/alcoholLog.ts` |
| `GET` | `/api/ikalcohol/logs/{id}` | `ApiResponse<AlcoholLogDTO>` | `frontend/src/services/alcoholLog.ts` |
| `DELETE` | `/api/ikalcohol/logs/{id}` | `204 No Content` preferred | `frontend/src/services/alcoholLog.ts` |

### Checklists
| Method | Path | Response | Frontend consumer |
|---|---|---|---|
| `GET` | `/api/checklists/today` | `ApiResponse<ChecklistInstanceDTO[]>` | `frontend/src/services/checklist.ts` |
| `GET` | `/api/checklists/templates` | `ApiResponse<ChecklistTemplateDTO[]>` | `frontend/src/services/checklist.ts` |
| `POST` | `/api/checklists/templates` | `ApiResponse<ChecklistTemplateDTO>` | `frontend/src/services/checklist.ts` |
| `POST` | `/api/checklists/templates/{id}/generate` | `ApiResponse<ChecklistInstanceDTO>` | `frontend/src/services/checklist.ts` |
| `PUT` | `/api/checklists/templates/{id}` | `ApiResponse<ChecklistTemplateDTO>` | `frontend/src/services/checklist.ts` |
| `PATCH` | `/api/checklists/items/{id}` | `ApiResponse<Void>` | `frontend/src/services/checklist.ts` |
| `PATCH` | `/api/checklists/templates/{id}/toggle` | `ApiResponse<Void>` | `frontend/src/services/checklist.ts` |
| `DELETE` | `/api/checklists/templates/{id}` | `204 No Content` preferred | `frontend/src/services/checklist.ts` |
| `GET` | `/api/checklist-library` | `ApiResponse<ChecklistItemLibraryDTO[]>` | `frontend/src/services/checklist.ts` |
| `POST` | `/api/checklist-library` | `ApiResponse<ChecklistItemLibraryDTO>` | `frontend/src/services/checklist.ts` |
| `PUT` | `/api/checklist-library/{id}` | `ApiResponse<ChecklistItemLibraryDTO>` | `frontend/src/services/checklist.ts` |
| `DELETE` | `/api/checklist-library/{id}` | `204 No Content` preferred | `frontend/src/services/checklist.ts` |
