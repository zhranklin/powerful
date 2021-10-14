{{- define "namespace" -}}
powerful{{- if not (eq .Release.Name "default") }}-{{ .Release.Name }}{{ end -}}
{{- end -}}

{{- define "deployName" -}}
  {{- if .name -}}
    -{{ .name -}}
  {{- else -}}
    {{- if .mark -}}
      -{{ .mark -}}
    {{- end -}}
    {{- if and .version (not (eq .version "default")) -}}
      -{{ .version -}}
    {{- end -}}
  {{- if or .agent.enabled .inject -}}
  .
  {{- if .inject -}}s{{- end -}}
  {{- if .agent.enabled -}}a{{- end -}}
  {{- end -}}
  {{- end -}}
{{- end -}}

{{- define "dubboDependsOn" -}}
  {{- range $_, $app := append .apps .cases -}}
    {{- $hasDubbo := false -}}
    {{- range $_, $deploy := $app.deploys -}}
      {{- if $deploy.dubbo -}}
      {{- if $deploy.dubbo.enabled -}}
        {{- $hasDubbo = true -}}
      {{- end -}}
      {{- end -}}
    {{- end -}}
    {{- if $hasDubbo -}}
      {{- if $app.Name }}{{ $app.Name }}{{ else }}{{ $app.name }}{{ end -}},
    {{- end -}}
  {{- end -}}
{{- end -}}
