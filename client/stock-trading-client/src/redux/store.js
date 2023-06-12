import { legacy_createStore as createStore, applyMiddleware } from "redux";
import rootReducer from "./rootReducer";
import logger from "redux-logger";
import { composeWithDevTools } from "redux-devtools-extension";
import thunk from "redux-thunk";
// import storage from 'redux-persist/lib/storage';
import { persistReducer, persistStore } from 'redux-persist';
import storageSession from 'reduxjs-toolkit-persist/lib/storage/session';

const persistConfig = {
    key: 'root',
    // storage,
    storage: storageSession,
};

const persistedReducer = persistReducer(persistConfig, rootReducer)

export const store = createStore(
    // rootReducer,
    persistedReducer, 
    composeWithDevTools(applyMiddleware(logger, thunk))
);

// export default store;
export const persistor = persistStore(store);