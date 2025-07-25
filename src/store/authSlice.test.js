import authReducer, { loginSuccess, logout } from './authSlice';

describe('authSlice', () => {
  it('normalizes roles to ROLE_ prefix and uppercase', () => {
    const initialState = undefined;
    const action = loginSuccess({
      token: 'mock.jwt.token',
      user: { username: 'admin', role: 'admin' },
    });
    const state = authReducer(initialState, action);
    expect(state.user.role).toBe('ROLE_ADMIN');
  });

  it('handles logout', () => {
    const initialState = {
      isAuthenticated: true,
      token: 'mock.jwt.token',
      user: { username: 'admin', role: 'ROLE_ADMIN' },
      loading: false,
      error: null,
    };
    const state = authReducer(initialState, logout());
    expect(state.isAuthenticated).toBe(false);
    expect(state.token).toBeNull();
    expect(state.user).toBeNull();
  });
}); 