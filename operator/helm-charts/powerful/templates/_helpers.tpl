{{- define "namespace" -}}
powerful{{- if not (eq .Release.Name "default") }}-{{ .Release.Name }}{{ end -}}
{{- end -}}

