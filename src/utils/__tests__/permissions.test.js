import { canAccess, normalizeRole, PERMISSIONS } from '../permissions';

describe('normalizeRole', () => {
  it('normalizes roles to ROLE_*', () => {
    expect(normalizeRole('admin')).toBe('ROLE_ADMIN');
    expect(normalizeRole('ROLE_ADMIN')).toBe('ROLE_ADMIN');
    expect(normalizeRole('analyst')).toBe('ROLE_ANALYST');
    expect(normalizeRole('ROLE_SUPERVISOR')).toBe('ROLE_SUPERVISOR');
    expect(normalizeRole('viewer')).toBe('ROLE_VIEWER');
    expect(normalizeRole('')).toBeNull();
    expect(normalizeRole(null)).toBeNull();
  });
});

describe('canAccess', () => {
  const roles = ['ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_ANALYST', 'ROLE_VIEWER'];
  const actions = Object.keys(PERMISSIONS);

  it('allows only correct roles for each action', () => {
    actions.forEach(action => {
      roles.forEach(role => {
        const allowed = PERMISSIONS[action].includes(role);
        expect(canAccess(role, action)).toBe(allowed);
      });
    });
  });

  it('returns false for unknown actions', () => {
    expect(canAccess('ROLE_ADMIN', 'UNKNOWN_ACTION')).toBe(false);
    expect(canAccess('ROLE_VIEWER', 'NOTHING')).toBe(false);
  });

  it('returns false for null/undefined role', () => {
    expect(canAccess(null, 'VIEW_ALERTS')).toBe(false);
    expect(canAccess(undefined, 'VIEW_ALERTS')).toBe(false);
  });
}); 