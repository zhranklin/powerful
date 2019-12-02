#!/bin/bash
cd `dirname $0`
if git diff-index --quiet HEAD --; then
  REVISION=$(git rev-parse --short HEAD)
else
  REVISION=$(git rev-parse --short HEAD)-dirty
fi

image=zhranklin/powerful
mvn install

image_with_tag=$image:mvc-$REVISION
docker build ./powerful-spring-mvc -t $image_with_tag
docker push $image_with_tag

image_with_tag=$image:sb1-$REVISION
docker build ./powerful-springboot-1 -t $image_with_tag
docker push $image_with_tag

image_with_tag=$image:sb2-$REVISION
docker build ./powerful-springboot-2 -t $image_with_tag
docker push $image_with_tag

