import React, {Fragment, useEffect, useState} from 'react';
import {JsonForms} from '@jsonforms/react';
import Grid from '@material-ui/core/Grid';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import withStyles, {WithStyles} from '@material-ui/core/styles/withStyles';
import createStyles from '@material-ui/core/styles/createStyles';
import './App.css';
import schema from './schema.json';
import uischema from './uischema.json';
import {materialCells, materialRenderers} from '@jsonforms/material-renderers';
import {Store} from 'redux';
import {get} from 'lodash';
import MyArrayLayout from "./MyArrayLayout";
import * as $ from "jquery"
import {ListItemText, TextField} from "@material-ui/core";
import SaveTools from "./SaveTools";
import {fromYaml, toYaml} from './yaml';
import {executeCase, postToView, viewToPost} from './data';

const styles = createStyles({
  container: {
    padding: '1em'
  },
  title: {
    textAlign: 'center',
    padding: '0.25em'
  },
  dataContent: {
    display: 'flex',
    justifyContent: 'flex-start',
    padding: '10px',
    borderRadius: '0.25em',
    backgroundColor: '#cecece'
  },
  demoform: {
    margin: 'auto',
    padding: '1rem'
  }
});

export interface AppProps extends WithStyles<typeof styles> {
  store: Store;
}

const getDataAsStringFromStore = (store: Store) =>
  store
    ? JSON.stringify(
        get(store.getState(), ['jsonforms', 'core', 'data']),
        null,
        2
      )
    : '';

var init = false;

const App = ({ store, classes }: AppProps) => {
  const [displayDataAsString, setDisplayDataAsString] = useState('');
  const [displayData, setDisplayData] = useState<any>({ trace: [{}] });
  const [consoleData, setConsoleData] = useState('');
  const [cases, setCases] = useState(["1", "2"]);
  const [yamlStr, setYamlStr] = useState("");
  const [yamlTextFocused, setYamlTextFocused] = useState(false);

  if (!init) {
    $.ajax({
      url: '/c',
      success: data1 => {
        setCases(data1)
      }
    });
    init = true;
  }
  useEffect(() => {
    const updateStringData = () => {
      const stringData = getDataAsStringFromStore(store);
      setDisplayDataAsString(stringData);
    };
    store.subscribe(updateStringData);
    updateStringData();
  }, [store]);

  useEffect(() => {
    setDisplayDataAsString(JSON.stringify(displayData, null, 2));
  }, [displayData]);

  useEffect(() => {
    if (!yamlTextFocused) {
      setYamlStr(toYaml(viewToPost(displayData)))
    }
  }, [displayData]);

  function getCase(name: string) {
    $.ajax({
      method: "Post",
      url: "/c/" + name,
      success: data1 => {
        var data = JSON.parse(data1);
        console.log(data);
        setDisplayData(data)
      }
    })
  }

  function submit(setResult: (_: string) => void) {
    setResult("loading...");
    window.console.log(yamlStr);
    var json = fromYaml(yamlStr);

    executeCase(json, displayData.expect ? "validate=true" : undefined, setResult);
  }
  function editYaml(content: string) {
    setYamlStr(content);
    try {
      var obj = fromYaml(content);
      if (obj) {
        var nul = obj.trace.indexOf(null);
        if (nul === -1) {
          setDisplayData(postToView(obj));
        } else {
          obj.trace[nul] = {}
        }
      }
    } catch {

    }
  }

  return (
    <Fragment>
      <Grid
        container
        justify={'center'}
        spacing={1}
        className={classes.container}
      >
        <Grid item sm={2}>
          <Typography variant="h6" className={classes.title}>
            Cases
          </Typography>
          <List dense={true}>
            {cases.map(i => (
              <ListItem button onClick={event => getCase(i)}>
                <ListItemText primary={i}/>
              </ListItem>
            ))}
          </List>
        </Grid>
        <Grid item sm={5}>
          <Grid
            container
            justify={'flex-end'}
            spacing={1}
            className={classes.container}
          >
            <Button
              color="primary"
              variant="contained"
              onClick={() => submit(setConsoleData)}
            >Submit</Button>
          </Grid>
          <div className={classes.demoform}>
            <JsonForms
              schema={schema}
              uischema={uischema}
              data={displayData}
              renderers={[
                ...materialRenderers,
                //register custom renderer
                MyArrayLayout
              ]}
              cells={materialCells}
              onChange={({errors, data}) => {
                if (data.traceNodeTmpl === '') {
                  delete data.traceNodeTmpl;
                }
                setDisplayData(data)
              }}
            />
          </div>
          <Grid
            container
            justify={'flex-end'}
            spacing={1}
            className={classes.container}
          >
            <SaveTools
              displayData={displayData}
              setDisplayString={setConsoleData}
            />
          </Grid>
        </Grid>
        <Grid item sm={5}>
          <Typography variant={'h6'} className={classes.title}>
            Edit as yaml
          </Typography>
          <TextField
            id="outlined-multiline-flexible"
            label="Multiline"
            multiline
            rows="10"
            value={yamlStr}
            onChange={event => editYaml(event.target.value)}
            onFocus={event => setYamlTextFocused(true)}
            onBlur={event => setYamlTextFocused(false)}
            variant="outlined"
            fullWidth
          />
          <p/>
          <div className={classes.dataContent}>
            <pre id='resultData'>{consoleData}</pre>
          </div>
        </Grid>
      </Grid>
    </Fragment>
  );
};

export default withStyles(styles)(App);
