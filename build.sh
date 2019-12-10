#!/bin/bash
cd `dirname $0`
if git diff-index --quiet HEAD --; then
  tag=$(git rev-parse --short HEAD)
else
  tag=$(git rev-parse --short HEAD)-dirty
fi

hub=hub.c.163.com/qingzhou
mvn install

for module in cases spring-mvc springboot-1 springboot-2; do
  image=powerful-$(echo $module | sed 's/springboot-/sb/g; s/spring-//g'):$tag
  docker build ./powerful-$module -t $hub/$image
  docker push $hub/$image
  docker tag $hub/$image $hub/istio/$image
  docker push $hub/istio/$image
done
