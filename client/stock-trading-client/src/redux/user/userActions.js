import { LOGIN_SUCCESS, LOGIN_FAILURE, LOGOUT } from './userTypes';

export const loginSuccess = (userId) => {
  return {
    type: LOGIN_SUCCESS,
    payload: userId,
  };
};

export const loginFailure = (error) => {
  return {
    type: LOGIN_FAILURE,
    payload: error,
  };
};

export const logout = () => {
  return {
    type: LOGOUT,
  };
};

