{{- define "namespace" -}}
powerful{{- if not (eq .Release.Name "default") }}-{{ .Release.Name }}{{ end -}}
{{- end -}}

{{- define "hub" -}}
{{ default .Values.defaultHub .Values.hub }}
{{- end -}}

{{- define "framewEnv" -}}
{{ default .Values.defaultFramewEnv .Values.framewEnv }}
{{- end -}}

{{- define "framewProject" -}}
{{ default .Values.defaultFramewProject .Values.framewProject }}
{{- end -}}
