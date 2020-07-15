{{- define "namespace" -}}
powerful{{- if not (eq .Release.Name "default") }}-{{ .Release.Name }}{{ end -}}
{{- end -}}

{{- define "deployName" -}}
  {{- $deployName := (.name | default .version) -}}
  {{- if not (eq $deployName "default") -}}
    -{{ $deployName -}}
  {{- end -}}
{{- end -}}
