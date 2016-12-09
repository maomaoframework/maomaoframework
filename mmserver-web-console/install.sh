#!/bin/bash
registry=https://registry.npm.taobao.org

cd node_modules/

for path in *
do
  if [ "${path:0:3}" == "fdp" ]
  then
    echo $path
    cd $path
    npm install --registry=$registry
    cd ..
  fi
done