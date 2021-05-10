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
