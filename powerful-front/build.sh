#!/bin/bash
cd `dirname $0`
TARGET=../powerful-core/src/main/resources/static
npm run build
rm -rf $TARGET
cp -r build $TARGET
cat $TARGET/index.html |
  sed 's/\.js"/.js?scope=${scope}"/g' |
  sed 's/\.css"/.css?scope=${scope}"/g' |
  sed 's/\.json"/.json?scope=${scope}"/g' |
  sed 's/\.ico"/.ico?scope=${scope}"/g' > $TARGET/../templates/scoped_index.ftl
#CHUNK="$TARGET/static/js/main.*.chunk.js"
#cat $CHUNK | sed 's/"\/service-worker\.js"/"\/service-worker.js?scope=${scope}"/g' > $CHUNK.1
#rm -f $CHUNK
#mv $CHUNK.1 $CHUNK
git add $TARGET
git add $TARGET/../templates/scoped_index.ftl
