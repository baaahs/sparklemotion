import React, { createContext, useReducer } from 'react';

const initialState = {
  currentNode: {
    direction: 'row',
    first: 'Sheep Visualizer',
    second: 'Simulator Settings',
    splitPercentage: 80,
  },
  currentTheme: 'Blueprint',
  sheepSimulator: null,
};

const store = createContext(initialState);
const { Provider } = store;

const StateProvider = ({ children }) => {
  const [state, dispatch] = useReducer((state, action) => {
    switch (action.type) {
      case 'SET_ENTIRE_STATE':
        return {
          ...state,
          ...action.payload,
        };
      case 'SET_SHEEP_SIMULATOR':
        return {
          ...state,
          sheepSimulator: action.payload.sheepSimulator,
        };
      default:
        throw new Error();
    }
  }, initialState);

  const value = { state, dispatch };
  return <Provider value={value}>{children}</Provider>;
};

export { store, StateProvider };
