#!/bin/bash
cd `dirname $0`
if git diff-index --quiet HEAD --; then
  tag=$(git rev-parse --short HEAD)
else
  tag=$(git rev-parse --short HEAD)-dirty
fi

hub=hub.c.163.com/qingzhou
mvn install

modules="springboot-2"

if [[ $1 == "-a" ]]; then
  modules="$modules spring-mvc springboot-1"
fi

for module in $modules; do
  image=powerful-$(echo $module | sed 's/springboot-/sb/g; s/spring-//g'):$tag
  docker build ./powerful-$module -t $hub/$image
  docker push $hub/$image
  docker tag $hub/$image $hub/istio/$image
  docker push $hub/istio/$image
done
docker tag $hub/powerful-sb2:$tag $hub/powerful:$tag
docker tag $hub/powerful-sb2:$tag $hub/istio/powerful:$tag
docker push $hub/powerful:$tag
docker push $hub/istio/powerful:$tag
