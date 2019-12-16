(this["webpackJsonpjsonforms-react-seed"]=this["webpackJsonpjsonforms-react-seed"]||[]).push([[0],{166:function(e){e.exports=JSON.parse('{"type":"object","properties":{"trace":{"type":"array","items":{"type":"object","properties":{"call":{"type":"string"},"by":{"type":"string","enum":["http"]},"times":{"type":"integer","minimum":1,"default":1},"threads":{"type":"integer","minimum":1,"default":1},"delay":{"type":"number","minimum":0,"default":0},"okByRoundRobin":{"type":"integer","minimum":1,"default":1},"errorByPercent":{"type":"integer","minimum":0,"maximum":100,"default":0},"callTestMethod":{"type":"integer","minimum":0,"maximum":100,"default":0},"headers":{"type":"array","items":{"type":"object","properties":{"name":{"type":"string"},"value":{"type":"string"}}}},"queries":{"type":"array","items":{"type":"object","properties":{"name":{"type":"string"},"value":{"type":"string"}}}}}}},"times":{"type":"integer","minimum":1},"threads":{"type":"integer","minimum":1},"traceNodeTmpl":{"type":"string"}}}')},167:function(e){e.exports=JSON.parse('{"type":"VerticalLayout","elements":[{"type":"HorizontalLayout","elements":[{"type":"Control","scope":"#/properties/times","label":"times"},{"type":"Control","scope":"#/properties/threads","label":"threads"},{"type":"Control","scope":"#/properties/traceNodeTmpl","label":"template"}]},{"type":"Control","scope":"#/properties/trace","options":{"detail":{"type":"VerticalLayout","elements":[{"type":"Categorization","elements":[{"type":"Category","label":"Base","elements":[{"type":"HorizontalLayout","elements":[{"type":"Control","scope":"#/properties/call","label":"call"},{"type":"Control","scope":"#/properties/by","label":"by"},{"type":"Control","scope":"#/properties/delay","label":"delay"},{"type":"Control","scope":"#/properties/errorByPercent","label":"error"},{"type":"Control","scope":"#/properties/okByRoundRobin","label":"ok round robin"}]}]},{"type":"Category","label":"Headers","elements":[{"type":"Control","scope":"#/properties/headers"}]},{"type":"Category","label":"Queries","elements":[{"type":"Control","scope":"#/properties/queries"}]}]}]}}}]}')},439:function(e,t,n){e.exports=n(757)},444:function(e,t,n){},606:function(e,t){},614:function(e,t){},616:function(e,t){},693:function(e,t,n){},757:function(e,t,n){"use strict";n.r(t);var a=n(0),r=n.n(a),o=n(13),i=n.n(o),c=(n(444),n(410)),l=n(73),s=n(11),u=n(842),m=n(768),p=n(844),d=n(344),f=n(92),y=n(408),g=n.n(y),b=n(407),v=n.n(b),h=(n(693),n(166)),j=n(167),E=n(113),O=n(232),C=n(401),S=n(411),x=n(402),w=n(412),N=n(12),k=n(848),J=function(e){function t(){var e,n;Object(C.a)(this,t);for(var a=arguments.length,r=new Array(a),o=0;o<a;o++)r[o]=arguments[o];return(n=Object(S.a)(this,(e=Object(x.a)(t)).call.apply(e,[this].concat(r)))).isExpanded=function(e){return!0},n}return Object(w.a)(t,e),t}(n(233).MaterialArrayLayout),A={tester:Object(N.rankWith)(5,N.isObjectArrayWithNesting),renderer:Object(s.withJsonFormsArrayLayoutProps)((function(e){var t=e.visible,n=e.enabled,r=e.id,o=e.uischema,i=e.schema,c=e.label,l=e.rootSchema,s=e.renderers,u=e.data,m=e.path,p=e.errors,d=e.addItem,f=Object(a.useCallback)((function(e,t){return d(e,t)}),[d]);return a.createElement(k.a,{xsUp:!t},a.createElement(J,{label:c,uischema:o,schema:i,id:r,rootSchema:l,errors:p,enabled:n,visible:t,data:u,path:m,addItem:f,renderers:s}))}))},I=n(136),R=n(845),T=n(351),q=function(e){var t=document.createElement("textarea");t.value=e,t.style.position="fixed",document.body.appendChild(t),t.focus(),t.select();try{var n=document.execCommand("copy")?"successful":"unsuccessful";console.log("Fallback: Copying text command was "+n)}catch(a){console.error("Fallback: Oops, unable to copy",a)}document.body.removeChild(t)};var B=n(237),L=n(414),W=n(177),D=n(841),F=n(403),P=n.n(F),H=n(404),z=n.n(H),M=n(405),U=n.n(M),V=n(406),K=n.n(V),Q=n(165),$={noArrayIndent:!0,sortKeys:!0};function G(e){return Q.dump(e,$)}function X(e){return Q.load(e)}function Y(e){var t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:"",n=JSON.parse(JSON.stringify(e));return n.trace.forEach((function(e){var t,n=e.headers,a=e.queries;(delete e.headers,delete e.queries,n)&&((t=_(n))&&(e.headers=t));a&&((t=_(a))&&(e.queries=t))})),""!=t&&(n.name=t),n}function Z(e){if(void 0!==e){var t=[];for(var n in e)t.push({name:n,value:e[n]});return t.length>0?t:void 0}}function _(e){var t={};for(var n in e.forEach((function(e){e.name&&e.value&&e.value.length>0&&(t[e.name]=e.value)})),t)return t}var ee=function(e){var t=e.displayData,n=e.setDisplayString,o="powerful-cases",i=Object(a.useState)(""),c=Object(l.a)(i,2),s=c[0],u=c[1];function m(){var e=localStorage.getItem(o);n(null==e?"":e)}function p(){var e,t=localStorage.getItem(o);null!=t&&(e=t,navigator.clipboard?navigator.clipboard.writeText(e).then((function(){console.log("Async: Copying to clipboard was successful!")}),(function(e){console.error("Async: Could not copy text: ",e)})):q(e)),n("Successfully copied to clipboard.")}return console.log(t),r.a.createElement(B.a,{component:"form",style:{padding:"2px 4px",display:"flex",alignItems:"center"}},r.a.createElement(L.a,{style:{padding:"10px"},placeholder:"Add As Name...",value:s,onChange:function(e){return u(e.target.value)}}),r.a.createElement(W.a,{onClick:function(){return function(e){var n=localStorage.getItem(o),a=null==n?[]:X(n);a.push(Y(t,e)),localStorage.setItem(o,G(a)),m()}(s)}},r.a.createElement(P.a,null)),r.a.createElement(D.a,{orientation:"vertical"}),r.a.createElement(W.a,{onClick:function(){return m()}},r.a.createElement(z.a,null)),r.a.createElement(D.a,{orientation:"vertical"}),r.a.createElement(W.a,{onClick:function(){return p()}},r.a.createElement(U.a,null)),r.a.createElement(D.a,{orientation:"vertical"}),r.a.createElement(W.a,{onClick:function(){return localStorage.removeItem(o),void m()}},r.a.createElement(K.a,null)))},te=v()({container:{padding:"1em"},title:{textAlign:"center",padding:"0.25em"},dataContent:{display:"flex",justifyContent:"flex-start",padding:"10px",borderRadius:"0.25em",backgroundColor:"#cecece"},demoform:{margin:"auto",padding:"1rem"}}),ne=!1,ae=g()(te)((function(e){var t=e.store,n=e.classes,o=Object(a.useState)(""),i=Object(l.a)(o,2),y=(i[0],i[1]),g=Object(a.useState)({trace:[{}]}),b=Object(l.a)(g,2),v=b[0],C=b[1],S=Object(a.useState)(""),x=Object(l.a)(S,2),w=x[0],N=x[1],k=Object(a.useState)(["1","2"]),J=Object(l.a)(k,2),q=J[0],B=J[1],L=Object(a.useState)(""),W=Object(l.a)(L,2),D=W[0],F=W[1],P=Object(a.useState)(!1),H=Object(l.a)(P,2),z=H[0],M=H[1];function U(e){e("loading..."),window.console.log(D),function(e,t){var n="/e";n+="?validate=true",I.ajax({method:"POST",url:n,dataType:"text",data:JSON.stringify(e),contentType:"application/json",success:function(e){t(e)},error:function(e){t(e.responseText)}})}(X(D),e)}function V(e){F(e);try{var t=X(e);if(t){var n=t.trace.indexOf(null);-1===n?C(function(e){if(e.trace.filter((function(e){return e.headers||e.queries})).length>0){var t=JSON.parse(JSON.stringify(e));return t.trace.forEach((function(e){e.headers=Z(e.headers),e.queries=Z(e.queries)})),t}return e}(t)):t.trace[n]={}}}catch(a){}}return ne||(I.ajax({url:"/c",success:function(e){B(e)}}),ne=!0),Object(a.useEffect)((function(){var e=function(){var e=function(e){return e?JSON.stringify(Object(O.get)(e.getState(),["jsonforms","core","data"]),null,2):""}(t);y(e)};t.subscribe(e),e()}),[t]),Object(a.useEffect)((function(){y(JSON.stringify(v,null,2))}),[v]),Object(a.useEffect)((function(){z||F(G(Y(v)))}),[v]),r.a.createElement(a.Fragment,null,r.a.createElement(u.a,{container:!0,justify:"center",spacing:1,className:n.container},r.a.createElement(u.a,{item:!0,sm:2},r.a.createElement(f.a,{variant:"h6",className:n.title},"Cases"),r.a.createElement(m.a,{dense:!0},q.map((function(e){return r.a.createElement(p.a,{button:!0,onClick:function(t){return n=e,void I.ajax({method:"Post",url:"/c/"+n,success:function(e){var t=JSON.parse(e);console.log(t),C(t)}});var n}},r.a.createElement(R.a,{primary:e}))})))),r.a.createElement(u.a,{item:!0,sm:5},r.a.createElement(u.a,{container:!0,justify:"flex-end",spacing:1,className:n.container},r.a.createElement(d.a,{color:"primary",variant:"contained",onClick:function(){return U(N)}},"Submit")),r.a.createElement("div",{className:n.demoform},r.a.createElement(s.JsonForms,{schema:h,uischema:j,data:v,renderers:[].concat(Object(c.a)(E.materialRenderers),[A]),cells:E.materialCells,onChange:function(e){e.errors;var t=e.data;return C(t)}})),r.a.createElement(u.a,{container:!0,justify:"flex-end",spacing:1,className:n.container},r.a.createElement(ee,{displayData:v,setDisplayString:N}))),r.a.createElement(u.a,{item:!0,sm:5},r.a.createElement(f.a,{variant:"h6",className:n.title},"Edit as yaml"),r.a.createElement(T.a,{id:"outlined-multiline-flexible",label:"Multiline",multiline:!0,rows:"10",value:D,onChange:function(e){return V(e.target.value)},onFocus:function(e){return M(!0)},onBlur:function(e){return M(!1)},variant:"outlined",fullWidth:!0}),r.a.createElement("p",null),r.a.createElement("div",{className:n.dataContent},r.a.createElement("pre",{id:"resultData"},w)))))})),re=Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));function oe(e){navigator.serviceWorker.register(e).then((function(e){e.onupdatefound=function(){var t=e.installing;t.onstatechange=function(){"installed"===t.state&&(navigator.serviceWorker.controller?console.log("New content is available; please refresh."):console.log("Content is cached for offline use."))}}})).catch((function(e){console.error("Error during service worker registration:",e)}))}var ie=n(77),ce=n(409),le={jsonforms:{cells:E.materialCells,renderers:E.materialRenderers}},se=Object(ie.combineReducers)({jsonforms:Object(N.jsonformsReducer)()}),ue=Object(ie.createStore)(se,le,Object(ce.devToolsEnhancer)({}));ue.dispatch(N.Actions.init({name:"Send email to Adrian",description:"Confirm if you have passed the subject\nHereby ...",done:!0,recurrence:"Daily",rating:3},h,j)),i.a.render(r.a.createElement(ae,{store:ue}),document.getElementById("root")),function(){if("serviceWorker"in navigator){if(new URL("",window.location).origin!==window.location.origin)return;window.addEventListener("load",(function(){var e="".concat("","/service-worker.js");re?function(e){fetch(e).then((function(t){404===t.status||-1===t.headers.get("content-type").indexOf("javascript")?navigator.serviceWorker.ready.then((function(e){e.unregister().then((function(){window.location.reload()}))})):oe(e)})).catch((function(){console.log("No internet connection found. App is running in offline mode.")}))}(e):oe(e)}))}}()}},[[439,1,2]]]);
//# sourceMappingURL=main.5d29432f.chunk.js.map