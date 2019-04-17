#!/bin/bash

module=$1
echo $module
if [[ ! -n $module  ]]
then
    echo "please input \"./upload.sh player \" or \"./upload.sh glview \" "
    exit 1
fi

verion="PLAYER_TOOL_VERSION"
moduleName=alpha-mp4-player
if [[ "$module" == "glview" ]]
then
    echo "success"
    version="GLVIEW_TOOL_VERSION"
    moduleName=multiple-stream-glview
fi
echo "version: $verion"
echo "moduleName: $moduleName"

# 查找TOOL_VERSION字符
origin=`grep $verion gradle.properties | awk '{print $1}'`
notNeedChange=`grep $verion gradle.properties | cut -d '.' -f 1`
secondVersion=`grep $verion gradle.properties | cut -d '.' -f 2`
lastVersion=`grep $verion gradle.properties | cut -d '.' -f 3 `

str=${notNeedChange}"."${secondVersion}"."$[lastVersion+1]
echo ${str}

sed -i "" "s/${origin}/${str}/g" gradle.properties
echo "begin upload please wait!!!~~~"

bintaryUser=`grep "PbintrayUser" local.properties | cut -d '=' -f 2`
bintaryKey=`grep "PbintrayKey" local.properties | cut -d '=' -f 2`
./gradlew clean :$moduleName:build :$moduleName:bintrayUpload -PbintrayUser=${bintaryUser} -PbintrayKey=${bintaryKey} -PdryRun=false 2>&1 | tee log.txt

uploadStr=`grep "BUILD SUCCESSFUL" log.txt`

echo ${uploadStr}

if [[ ${uploadStr} =~ "BUILD SUCCESSFUL" ]]
then
    echo "========UPLOAD SUCCESS "
    git status
    git add ./gradle.properties
    git commit -m "feature: upload version code $str"
else
    echo "Failed~~~~ please wait~~"
    sed -i "" "s/${str}/${origin}/g" gradle.properties
 fi
rm -f log.txt

