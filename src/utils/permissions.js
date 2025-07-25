// Centralized permissions and role normalization
export const normalizeRole = (role) => {
  if (!role) return null;
  let r = role.toUpperCase();
  if (!r.startsWith('ROLE_')) r = 'ROLE_' + r;
  return r;
};

export const PERMISSIONS = {
  VIEW_ALERTS: ['ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_ANALYST', 'ROLE_VIEWER'],
  EXPORT_ALERTS: ['ROLE_ADMIN', 'ROLE_SUPERVISOR'],
  CREATE_USER: ['ROLE_ADMIN'],
  UPLOAD_TRANSACTIONS: ['ROLE_ADMIN', 'ROLE_ANALYST'],
  ESCALATE_ALERTS: ['ROLE_SUPERVISOR'],
  VIEW_DASHBOARD: ['ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_ANALYST', 'ROLE_VIEWER'],
  MANAGE_USERS: ['ROLE_ADMIN'],
  VIEW_USERS: ['ROLE_ADMIN', 'ROLE_SUPERVISOR'],
  // Add more as needed
};

export const canAccess = (role, action) => {
  const normRole = normalizeRole(role);
  if (!PERMISSIONS[action]) return false;
  return PERMISSIONS[action].includes(normRole);
}; 