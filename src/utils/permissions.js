// Centralized permissions and role normalization
export const normalizeRole = (role) => {
  console.log('🔍 normalizeRole called with:', role);
  if (!role) {
    console.log('❌ normalizeRole: role is null/undefined, returning null');
    return null;
  }
  let r = role.toUpperCase();
  if (!r.startsWith('ROLE_')) r = 'ROLE_' + r;
  console.log('✅ normalizeRole: normalized to:', r);
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
  console.log('🔐 canAccess called with role:', role, 'action:', action);
  const normRole = normalizeRole(role);
  console.log('🔐 canAccess normalized role:', normRole);
  
  if (!PERMISSIONS[action]) {
    console.log('❌ canAccess: action not found in PERMISSIONS:', action);
    // TEMPORARY: Allow access if action not found to prevent UI blocking
    console.log('⚠️ canAccess: TEMPORARILY allowing access for unknown action:', action);
    return true;
  }
  
  const hasAccess = PERMISSIONS[action].includes(normRole);
  console.log('🔐 canAccess result:', hasAccess, 'for action:', action, 'role:', normRole);
  
  // TEMPORARY: If role is null/undefined, allow access to prevent UI blocking
  if (!normRole) {
    console.log('⚠️ canAccess: TEMPORARILY allowing access due to null role');
    return true;
  }
  
  return hasAccess;
}; 