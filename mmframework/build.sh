#!/bin/sh


# 检查nodejs文件夹是否存在
workingPath=$PWD
workingDirName=`basename $PWD`

nodeModuleDirPath=$workingPath'/src/main/web/nodejs/modules/'$workingDirName
nodeModuleDirPath_subModule=$workingPath'/src/main/web/nodejs/modules/'$workingDirName'/node_modules'

if [ ! -d "$nodeModuleDirPath_subModule" ]; then
	mkdir -p "$nodeModuleDirPath_subModule"
fi

for icefile in `find ./ -name "*.ice"`;
do
        fileName=$(basename $icefile)
        filePath=$icefile
        

        slice2java -I $workingPath/src/main/idl --output-dir $workingPath/src/main/java $filePath
        slice2js -I $workingPath/src/main/idl --output-dir $nodeModuleDirPath $filePath
        slice2js -I $workingPath/src/main/idl --output-dir $nodeModuleDirPath/node_modules $filePath

done

mvn clean package install  
