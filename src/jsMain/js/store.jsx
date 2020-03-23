import React, { createContext, useReducer } from 'react';

const initialState = {
  currentNode: {
    direction: 'row',
    splitPercentage: 30,
    first: {
      direction: 'column',
      splitPercentage: 50,
      first: 'Sheep Visualizer',
      second: 'Glsl Preview',
    },
    second: {
      direction: 'row',
      splitPercentage: 35,
      first: 'Simulator Settings',
      second: 'Show Editor',
    },
  },
  selectedShow: '',
  currentTheme: 'Blueprint',
  sheepSimulator: null,
  isConnected: false,
};

const store = createContext(initialState);
const { Provider } = store;

const StateProvider = ({ children }) => {
  const [state, dispatch] = useReducer((state, action) => {
    switch (action.type) {
      case 'SET_STATE':
        return {
          ...state,
          ...action.payload,
        };
      case 'SET_SHEEP_SIMULATOR':
        const { sheepSimulator } = action.payload;
        return {
          ...state,
          sheepSimulator,
        };
      default:
        throw new Error(`Action with Unknown Type: "${action.type}"`);
    }
  }, initialState);

  const value = { state, dispatch };
  return <Provider value={value}>{children}</Provider>;
};

export { store, StateProvider };
