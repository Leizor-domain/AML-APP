// Centralized permissions map
export const PERMISSIONS = {
  upload_transactions: ['ROLE_ADMIN', 'ROLE_ANALYST'],
  view_alerts: ['ROLE_ADMIN', 'ROLE_ANALYST', 'ROLE_SUPERVISOR', 'ROLE_VIEWER'],
  export_alerts: ['ROLE_ADMIN', 'ROLE_SUPERVISOR'],
  escalate_alerts: ['ROLE_ADMIN', 'ROLE_SUPERVISOR'],
  create_user: ['ROLE_ADMIN'],
  view_dashboard: ['ROLE_ADMIN', 'ROLE_ANALYST', 'ROLE_SUPERVISOR', 'ROLE_VIEWER'],
  generate_report: ['ROLE_ADMIN'],
  system_settings: ['ROLE_ADMIN'],
  user_management: ['ROLE_ADMIN'],
};

export function normalizeRole(role) {
  if (!role) return null;
  let r = role.toUpperCase();
  if (!r.startsWith('ROLE_')) r = 'ROLE_' + r;
  return r;
}

export function canAccess(role, action) {
  if (!role || !action) return false;
  const normRole = normalizeRole(role);
  return PERMISSIONS[action]?.includes(normRole) || false;
} 