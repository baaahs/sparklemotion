import React, {createContext, useReducer} from 'react';

const initialState = {
  currentNode: {
    direction: 'row',
    splitPercentage: 30,
    first: {
      direction: 'column',
      splitPercentage: 50,
      first: 'Sheep Visualizer',
      second: 'Simulator Console',
    },
    second: 'Web UI',
  },
  selectedShow: '',
  currentTheme: 'Blueprint',
  simulator: null,
  isConnected: false,
};

const store = createContext(initialState);
const { Provider } = store;

const StateProvider = ({ simulator, children }) => {
  initialState.simulator = simulator;

  const [state, dispatch] = useReducer((state, action) => {
    switch (action.type) {
      case 'SET_STATE':
        return {
          ...state,
          ...action.payload,
        };
      default:
        throw new Error(`Action with Unknown Type: "${action.type}"`);
    }
  }, initialState);

  const value = { state, dispatch };
  return <Provider value={value}>{children}</Provider>;
};

export { store, StateProvider };
