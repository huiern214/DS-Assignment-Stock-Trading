import { LOGIN_SUCCESS, LOGIN_FAILURE, LOGOUT } from './userTypes';

const initialState = {
  userId: null,
  error: null,
};

const userReducer = (state = initialState, action) => {
  switch (action.type) {
    case LOGIN_SUCCESS:
      return {
        ...state,
        userId: action.payload,
        error: null,
      };
    case LOGIN_FAILURE:
      return {
        ...state,
        userId: null,
        error: action.payload,
      };
    case LOGOUT:
      return {
        ...state,
        userId: null,
        error: null,
      };
    default:
      return state;
  }
};

export default userReducer;
