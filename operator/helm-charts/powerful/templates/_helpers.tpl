{{- define "namespace" -}}
powerful{{- if not (eq .Release.Name "default") }}-{{ .Release.Name }}{{ end -}}
{{- end -}}

{{- define "deployName" -}}
  {{- if .name -}}
    -{{ .name -}}
  {{- else -}}
    {{- if and .version (not (eq .version "default")) -}}
      -{{ .version -}}
    {{- end -}}
    {{- if .color -}}
      -{{ .color -}}
    {{- end -}}
  {{- end -}}
{{- end -}}
