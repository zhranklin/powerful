import {copyTextToClipboard} from "./clipboard";
import {Divider, IconButton, InputBase, Paper} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import VisibilityIcon from "@material-ui/icons/Visibility";
import FileCopyIcon from "@material-ui/icons/FileCopy";
import ClearIcon from "@material-ui/icons/Clear";
import React, {useState} from "react";
import {fromYaml, toYaml} from "./yaml";
import {viewToPost} from "./data";

export interface SaveToolsProps {
  displayData: any,
  setDisplayString: (value: (string)) => void
}
export default ({ displayData, setDisplayString }: SaveToolsProps) => {
  const cacheKey = "powerful-cases";
  const [saveName, setSaveName] = useState("");
  console.log(displayData);

  function saveToCache(name: string) {
    var cache = localStorage.getItem(cacheKey);
    var data: Array<any> = cache == null ? [] : fromYaml(cache);
    data.push(viewToPost(displayData, name));
    localStorage.setItem(cacheKey, toYaml(data));
    showCache();
  }

  function showCache() {
    var str = localStorage.getItem(cacheKey);
    setDisplayString(str == null ? "" : str);
  }

  function copyToClipboard() {
    var data = localStorage.getItem(cacheKey);
    if (data != null) {
      copyTextToClipboard(data);
    }
    setDisplayString("Successfully copied to clipboard.");
  }

  function clearCache() {
    localStorage.removeItem(cacheKey);
    showCache();
  }

  return <Paper component="form" style={{padding: '2px 4px', display: 'flex', alignItems: 'center'}}>
    <InputBase
      style={{padding: "10px"}}
      placeholder="Add As Name..."
      value={saveName}
      onChange={e => setSaveName(e.target.value)}
    />
    <IconButton onClick={() => saveToCache(saveName)}>
      <SaveIcon/>
    </IconButton>
    <Divider orientation="vertical"/>
    <IconButton onClick={() => showCache()}>
      <VisibilityIcon/>
    </IconButton>
    <Divider orientation="vertical"/>
    <IconButton onClick={() => copyToClipboard()}>
      <FileCopyIcon/>
    </IconButton>
    <Divider orientation="vertical"/>
    <IconButton onClick={() => clearCache()}>
      <ClearIcon/>
    </IconButton>
  </Paper>;
}
