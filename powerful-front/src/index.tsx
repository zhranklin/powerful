import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import registerServiceWorker, {unregister} from './registerServiceWorker';
import { combineReducers, createStore, Reducer, AnyAction } from 'redux';
import schema from './schema.json';
import uischema from './uischema.json';
import { Actions, jsonformsReducer, JsonFormsState } from '@jsonforms/core';
import {
  materialCells,
  materialRenderers
} from '@jsonforms/material-renderers';
import { devToolsEnhancer } from 'redux-devtools-extension';

// Setup Redux store
const data = {
  name: 'Send email to Adrian',
  description: 'Confirm if you have passed the subject\nHereby ...',
  done: true,
  recurrence: 'Daily',
  rating: 3
};

const initState: JsonFormsState = {
  jsonforms: {
    cells: materialCells,
    renderers: materialRenderers
  }
};

const rootReducer: Reducer<JsonFormsState, AnyAction> = combineReducers({
  jsonforms: jsonformsReducer()
});
const store = createStore(rootReducer, initState, devToolsEnhancer({}));
store.dispatch(Actions.init(data, schema, uischema));

ReactDOM.render(<App store={store} />, document.getElementById('root'));
unregister()
