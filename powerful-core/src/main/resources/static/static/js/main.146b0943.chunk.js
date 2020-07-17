(this["webpackJsonpjsonforms-react-seed"]=this["webpackJsonpjsonforms-react-seed"]||[]).push([[0],{166:function(e){e.exports=JSON.parse('{"type":"object","properties":{"trace":{"type":"array","items":{"type":"object","properties":{"call":{"type":"string"},"by":{"type":"string","enum":["http","grpc","dubbo","thrift"]},"times":{"type":"integer","minimum":1,"default":1},"threads":{"type":"integer","minimum":1,"default":1},"delay":{"type":"number","minimum":0,"default":0},"okByRoundRobin":{"type":"integer","minimum":1,"default":1},"errorByPercent":{"type":"integer","minimum":0,"maximum":100,"default":0},"callTestMethod":{"type":"integer","minimum":0,"maximum":100,"default":0},"headers":{"type":"array","items":{"type":"object","properties":{"name":{"type":"string"},"value":{"type":"string"}}}},"queries":{"type":"array","items":{"type":"object","properties":{"name":{"type":"string"},"value":{"type":"string"}}}}}}},"times":{"type":"integer","minimum":1},"threads":{"type":"integer","minimum":1},"traceNodeTmpl":{"type":"string"},"propagateHeaders":{"type":"string"}}}')},167:function(e){e.exports=JSON.parse('{"type":"VerticalLayout","elements":[{"type":"HorizontalLayout","elements":[{"type":"Control","scope":"#/properties/times","label":"times"},{"type":"Control","scope":"#/properties/threads","label":"threads"},{"type":"Control","scope":"#/properties/propagateHeaders","label":"propagateHeaders"},{"type":"Control","scope":"#/properties/traceNodeTmpl","label":"template"}]},{"type":"Control","scope":"#/properties/trace","options":{"detail":{"type":"VerticalLayout","elements":[{"type":"Categorization","elements":[{"type":"Category","label":"Base","elements":[{"type":"HorizontalLayout","elements":[{"type":"Control","scope":"#/properties/call","label":"call"},{"type":"Control","scope":"#/properties/by","label":"by"},{"type":"Control","scope":"#/properties/delay","label":"delay"},{"type":"Control","scope":"#/properties/errorByPercent","label":"error"},{"type":"Control","scope":"#/properties/okByRoundRobin","label":"ok round robin"}]}]},{"type":"Category","label":"Headers","elements":[{"type":"Control","scope":"#/properties/headers"}]},{"type":"Category","label":"Queries","elements":[{"type":"Control","scope":"#/properties/queries"}]}]}]}}}]}')},439:function(e,t,a){e.exports=a(757)},444:function(e,t,a){},606:function(e,t){},614:function(e,t){},616:function(e,t){},693:function(e,t,a){},757:function(e,t,a){"use strict";a.r(t);var n=a(0),r=a.n(n),o=a(13),i=a.n(o),c=(a(444),a(410)),l=a(73),s=a(11),u=a(842),m=a(768),p=a(844),d=a(344),f=a(92),y=a(408),b=a.n(y),g=a(407),v=a.n(g),h=(a(693),a(166)),j=a(167),E=a(113),O=a(232),C=a(401),x=a(411),S=a(402),N=a(412),w=a(12),k=a(848),J=function(e){function t(){var e,a;Object(C.a)(this,t);for(var n=arguments.length,r=new Array(n),o=0;o<n;o++)r[o]=arguments[o];return(a=Object(x.a)(this,(e=Object(S.a)(t)).call.apply(e,[this].concat(r)))).isExpanded=function(e){return!0},a}return Object(N.a)(t,e),t}(a(233).MaterialArrayLayout),A={tester:Object(w.rankWith)(5,w.isObjectArrayWithNesting),renderer:Object(s.withJsonFormsArrayLayoutProps)((function(e){var t=e.visible,a=e.enabled,r=e.id,o=e.uischema,i=e.schema,c=e.label,l=e.rootSchema,s=e.renderers,u=e.data,m=e.path,p=e.errors,d=e.addItem,f=Object(n.useCallback)((function(e,t){return d(e,t)}),[d]);return n.createElement(k.a,{xsUp:!t},n.createElement(J,{label:c,uischema:o,schema:i,id:r,rootSchema:l,errors:p,enabled:a,visible:t,data:u,path:m,addItem:f,renderers:s}))}))},T=a(136),I=a(845),R=a(351),q=function(e){var t=document.createElement("textarea");t.value=e,t.style.position="fixed",document.body.appendChild(t),t.focus(),t.select();try{var a=document.execCommand("copy")?"successful":"unsuccessful";console.log("Fallback: Copying text command was "+a)}catch(n){console.error("Fallback: Oops, unable to copy",n)}document.body.removeChild(t)};var B=a(237),H=a(414),D=a(177),F=a(841),L=a(403),P=a.n(L),W=a(404),z=a.n(W),M=a(405),V=a.n(M),Q=a(406),U=a.n(Q),$=a(165),G={noArrayIndent:!0};function K(e){return $.dump(e,G)}function X(e){return $.load(e)}function Y(e){var t=JSON.parse(JSON.stringify(e));return t.trace.forEach((function(e){var t,a=e.headers,n=e.queries;(delete e.headers,delete e.queries,a)&&((t=ee(a))&&(e.headers=t));n&&((t=ee(n))&&(e.queries=t))})),delete t.validate,t}function Z(e){if(e.trace.filter((function(e){return e.headers||e.queries})).length>0){var t=JSON.parse(JSON.stringify(e));return t.trace.forEach((function(e){e.headers=_(e.headers),e.queries=_(e.queries)})),t}return e}function _(e){if(void 0!==e){var t=[];for(var a in e)t.push({name:a,value:e[a]});return t.length>0?t:void 0}}function ee(e){var t={};for(var a in e.forEach((function(e){e.name&&e.value&&e.value.length>0&&(t[e.name]=e.value)})),t)return t}function te(e){var t=function(e){for(var t=window.location.search.substring(1).split("&"),a=0;a<t.length;a++){var n=t[a].split("=");if(n[0]==e)return n[1]}}("scope");if(void 0==t)return e;var a=-1==e.indexOf("?")?"?":"&";return e+a+"scope="+t}var ae=function(e){var t=e.displayData,a=e.setDisplayString,o="powerful-cases",i=Object(n.useState)(""),c=Object(l.a)(i,2),s=c[0],u=c[1];function m(){var e=localStorage.getItem(o);a(null==e?"":e)}function p(){var e,t=localStorage.getItem(o);null!=t&&(e=t,navigator.clipboard?navigator.clipboard.writeText(e).then((function(){console.log("Async: Copying to clipboard was successful!")}),(function(e){console.error("Async: Could not copy text: ",e)})):q(e)),a("Successfully copied to clipboard.")}return console.log(t),r.a.createElement(B.a,{component:"form",style:{padding:"2px 4px",display:"flex",alignItems:"center"}},r.a.createElement(H.a,{style:{padding:"10px"},placeholder:"Add As Name...",value:s,onChange:function(e){return u(e.target.value)}}),r.a.createElement(D.a,{onClick:function(){return function(e){if(e&&""!==e){var a=localStorage.getItem(o),n=null==a?{cases:{}}:X(a);n.cases[e]=Y(t),localStorage.setItem(o,K(n)),m()}}(s)}},r.a.createElement(P.a,null)),r.a.createElement(F.a,{orientation:"vertical"}),r.a.createElement(D.a,{onClick:function(){return m()}},r.a.createElement(z.a,null)),r.a.createElement(F.a,{orientation:"vertical"}),r.a.createElement(D.a,{onClick:function(){return p()}},r.a.createElement(V.a,null)),r.a.createElement(F.a,{orientation:"vertical"}),r.a.createElement(D.a,{onClick:function(){return localStorage.removeItem(o),void m()}},r.a.createElement(U.a,null)))},ne=v()({container:{padding:"1em"},title:{textAlign:"center",padding:"0.25em"},dataContent:{display:"flex",justifyContent:"flex-start",padding:"10px",borderRadius:"0.25em",backgroundColor:"#cecece"},demoform:{margin:"auto",padding:"1rem"}}),re=!1,oe=b()(ne)((function(e){var t=e.store,a=e.classes,o=Object(n.useState)(""),i=Object(l.a)(o,2),y=(i[0],i[1]),b=Object(n.useState)({trace:[{}]}),g=Object(l.a)(b,2),v=g[0],C=g[1],x=Object(n.useState)(""),S=Object(l.a)(x,2),N=S[0],w=S[1],k=Object(n.useState)(["1","2"]),J=Object(l.a)(k,2),q=J[0],B=J[1],H=Object(n.useState)(""),D=Object(l.a)(H,2),F=D[0],L=D[1],P=Object(n.useState)(!1),W=Object(l.a)(P,2),z=W[0],M=W[1];function V(e){e("loading..."),window.console.log(F),function(e,t,a){var n="/e";t&&(n=n+"?"+t),T.ajax({method:"POST",url:te(n),dataType:"text",data:JSON.stringify(e),contentType:"application/json",success:function(e){a(e)},error:function(e){a(e.responseText)}})}(X(F),v.expect?"validate=true":void 0,e)}return re||(T.ajax({url:te("/c"),success:function(e){B(e)}}),re=!0),Object(n.useEffect)((function(){var e=function(){var e=function(e){return e?JSON.stringify(Object(O.get)(e.getState(),["jsonforms","core","data"]),null,2):""}(t);y(e)};t.subscribe(e),e()}),[t]),Object(n.useEffect)((function(){y(JSON.stringify(v,null,2))}),[v]),Object(n.useEffect)((function(){z||L(K(Y(v)))}),[v]),r.a.createElement(n.Fragment,null,r.a.createElement(u.a,{container:!0,justify:"center",spacing:1,className:a.container},r.a.createElement(u.a,{item:!0,sm:2},r.a.createElement(f.a,{variant:"h6",className:a.title},"Cases"),r.a.createElement(m.a,{dense:!0},q.map((function(e){return r.a.createElement(p.a,{button:!0,onClick:function(t){return a=e,void T.ajax({method:"Post",url:te("/c/"+a),success:function(e){var t=JSON.parse(e);console.log(t),C(Z(t))}});var a}},r.a.createElement(I.a,{primary:e}))})))),r.a.createElement(u.a,{item:!0,sm:5},r.a.createElement(u.a,{container:!0,justify:"flex-end",spacing:1,className:a.container},r.a.createElement(d.a,{color:"primary",variant:"contained",onClick:function(){return V(w)}},"Submit")),r.a.createElement("div",{className:a.demoform},r.a.createElement(s.JsonForms,{schema:h,uischema:j,data:v,renderers:[].concat(Object(c.a)(E.materialRenderers),[A]),cells:E.materialCells,onChange:function(e){e.errors;var t=e.data;""===t.traceNodeTmpl&&delete t.traceNodeTmpl,C(t)}})),r.a.createElement(u.a,{container:!0,justify:"flex-end",spacing:1,className:a.container},r.a.createElement(ae,{displayData:v,setDisplayString:w}))),r.a.createElement(u.a,{item:!0,sm:5},r.a.createElement(f.a,{variant:"h6",className:a.title},"Edit as yaml"),r.a.createElement(R.a,{id:"outlined-multiline-flexible",label:"Multiline",multiline:!0,rows:"10",value:F,onChange:function(e){return function(e){L(e);try{var t=X(e);if(t){var a=t.trace.indexOf(null);-1===a?C(Z(t)):t.trace[a]={}}}catch(n){}}(e.target.value)},onFocus:function(e){return M(!0)},onBlur:function(e){return M(!1)},variant:"outlined",fullWidth:!0}),r.a.createElement("p",null),r.a.createElement("div",{className:a.dataContent},r.a.createElement("pre",{id:"resultData"},N)))))}));Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));var ie=a(77),ce=a(409),le={jsonforms:{cells:E.materialCells,renderers:E.materialRenderers}},se=Object(ie.combineReducers)({jsonforms:Object(w.jsonformsReducer)()}),ue=Object(ie.createStore)(se,le,Object(ce.devToolsEnhancer)({}));ue.dispatch(w.Actions.init({name:"Send email to Adrian",description:"Confirm if you have passed the subject\nHereby ...",done:!0,recurrence:"Daily",rating:3},h,j)),i.a.render(r.a.createElement(oe,{store:ue}),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then((function(e){e.unregister()}))}},[[439,1,2]]]);
//# sourceMappingURL=main.146b0943.chunk.js.map