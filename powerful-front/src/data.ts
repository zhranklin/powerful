import * as $ from "jquery"
export function viewToPost(display: any): any {
  let data = JSON.parse(JSON.stringify(display));
  data.trace.forEach((tr: any) => {
    let headers = tr.headers;
    let queries = tr.queries;
    delete tr.headers;
    delete tr.queries;
    if (headers) {
      var res = arrayToObj(headers);
      if (res) {
        tr.headers = res;
      }
    }
    if (queries) {
      var res = arrayToObj(queries);
      if (res) {
        tr.queries = res;
      }
    }
  });
  return data;
}

export function postToView(obj: any) {
  if (obj.trace.filter((tr: any) => tr.headers || tr.queries).length > 0) {
    let newObj = JSON.parse(JSON.stringify(obj));
    newObj.trace.forEach((tr: any) => {
      tr.headers = objToArray(tr.headers);
      tr.queries = objToArray(tr.queries);
    });
    return newObj;
  } else {
    return obj;
  }
}

function objToArray(obj: any) {
  if (obj === undefined) {
    return undefined;
  }
  let result = [];
  for (let k in obj) {
    result.push({name: k, value: obj[k]});
  }
  return result.length > 0 ? result : undefined;
}

function arrayToObj(kvs: Array<{name: string, value: string}>) {
  let result: any = {};
  kvs.forEach(h => {
    if (h.name && h.value && h.value.length > 0) {
      result[h.name] = h.value;
    }
  });
  for (let k in result) {
    return result;
  }
  return undefined;
}

export function executeCase(json: any, setResult: (_: string) => void) {
  var url = '/e';
  var params = "validate=true";
  if (params) {
    url = url + "?" + params
  }
  $.ajax({
    method: "POST",
    url: url,
    dataType: 'text',
    data: JSON.stringify(json),
    contentType: 'application/json',
    success: data1 => {
      setResult(data1)
    },
    error: jqXHR => {
      setResult(jqXHR.responseText)
    }
  });
}
