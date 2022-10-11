package controller

import (
	"context"
	"fmt"
	"strings"

	v1 "k8s.io/api/core/v1"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/apis/meta/v1/unstructured"
	types2 "k8s.io/apimachinery/pkg/types"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/reconcile"
)

func getStrLiteral(env string) string {
	if env == "" {
		return "null"
	}
	return "\"" + env + "\""
}

func (r HelmOperatorReconciler) patchNamespaceLabel(namespace string, istioEnv string, revision string) {
	mergePatch := []byte(`{
		"metadata": {
			"labels": {
               "istio-env": ` + getStrLiteral(istioEnv) + `,
               "istio.io/rev": ` + getStrLiteral(revision) + `
			}
		}
	}`)
	namespaceObj := &v1.Namespace{ObjectMeta: metav1.ObjectMeta{Name: namespace}}
	if err := r.Client.Patch(context.TODO(), namespaceObj, client.RawPatch(types2.StrategicMergePatchType, mergePatch)); err != nil {
		log.Error(err, "Failed to patch label to ")
	}
}

func getIstioEnv(o *unstructured.Unstructured) (string, string) {
	istioEnv, _, _ := unstructured.NestedString(o.Object, "metadata", "labels", "istio-env")
	revision, _, _ := unstructured.NestedString(o.Object, "metadata", "labels", "revision")
	return istioEnv, revision
}

func (r HelmOperatorReconciler) setupNamespaceAndIstioEnv(o *unstructured.Unstructured, req reconcile.Request) {
	namespace := getPowerfulNamespace(req)
	o.SetNamespace(namespace)
	istioEnv, revision := getIstioEnv(o)
	r.setupIstioEnv(o, istioEnv, revision, namespace)
}

func (r HelmOperatorReconciler) setupIstioEnv(o *unstructured.Unstructured, istioEnv string, revision string, namespace string) {
	if revision != "" {
		istioEnv = ""
	}
	if err := unstructured.SetNestedField(o.Object, istioEnv, "spec", "istioEnv"); err != nil {
		log.Error(err, "Failed to set istioEnv to CR's spec.")
	}
	if err := unstructured.SetNestedField(o.Object, istioEnv, "metadata", "labels", "istio-env"); err != nil {
		log.Error(err, "Failed to set istioEnv to CR's labels.")
	}
	if err := unstructured.SetNestedField(o.Object, revision, "spec", "revision"); err != nil {
		log.Error(err, "Failed to set revision to CR's spec.")
	}
	if err := unstructured.SetNestedField(o.Object, revision, "metadata", "labels", "revision"); err != nil {
		log.Error(err, "Failed to set revision to CR's labels.")
	}
	r.patchNamespaceLabel(namespace, istioEnv, revision)
}

func getPowerfulNamespace(req reconcile.Request) string {
	return req.Name
}

func getAppList(o *unstructured.Unstructured) []string {
	apps, _, _ := unstructured.NestedSlice(o.Object, "spec", "apps")
	if apps == nil || len(apps) == 0 {
		return []string{"a", "b(v1,v2)", "c"}
	}
	ret := []string{}
	for _, appObj := range apps {
		if app, ok := appObj.(map[string]interface{}); ok {
			appName, _, _ := unstructured.NestedString(app, "Name")
			if appName == "" {
				appName, _, _ = unstructured.NestedString(app, "name")
			}
			if appName != "" {
				deploys, _, _ := unstructured.NestedSlice(app, "deploys")
				if deploys == nil || len(deploys) == 0 || len(deploys) == 1 && getDeployVersion(deploys[0]) == "default" {
					ret = append(ret, appName)
					continue
				}
				versions := []string{}
				for _, deploy := range deploys {
					version := getDeployVersion(deploy)
					if version != "" {
						versions = append(versions, version)
					}
				}
				ret = append(ret, fmt.Sprintf("%s(%s)", appName, strings.Join(versions, ",")))
			}
		}
	}
	return ret
}

func getDeployVersion(firstDeploy interface{}) string {
	if typed, ok := firstDeploy.(map[string]interface{}); ok {
		v, _, _ := unstructured.NestedString(typed, "version")
		return v
	}
	return ""
}

func withoutIstioEnv(o *unstructured.Unstructured, f func() error) error {
	istioEnv := removeIstioEnv(o)
	err := f()
	restoreIstioEnv(o, istioEnv)
	return err
}

func removeIstioEnv(o *unstructured.Unstructured) string {
	istioEnv, _, _ := unstructured.NestedString(o.Object, "spec", "istioEnv")
	spec, _, _ := unstructured.NestedMap(o.Object, "spec")
	delete(spec, "istioEnv")
	o.Object["spec"] = spec
	return istioEnv
}

func restoreIstioEnv(o *unstructured.Unstructured, istioEnv string) {
	if istioEnv != "" {
		_ = unstructured.SetNestedField(o.Object, istioEnv, "spec", "istioEnv")
	}
}
