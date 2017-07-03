#!/bin/bash

for i in "$@"
do
case $i in
    -n=*|--name=*)
    NAME="${i#*=}"
    shift
    ;;
    -v=*|--version=*)
    VERSION="${i#*=}"
    shift
    ;;
    -d=*|--dir=*)
    DIR="${i#*=}"
    shift
    ;;
esac
done

if [[ ${NAME} && ${VERSION} && ${DIR} ]]; then
	echo "assign the x to all files and dirs under current dir.."
	chmod +x -R .
	echo "begin to build image ${NAME}.."
	docker build --no-cache -t ${NAME}:${VERSION} . >/dev/null || { echo -e "\nBuild docker image failed!";exit 1; }
	docker rmi $(docker images | grep "^<none>" | awk '{print $3}') &>/dev/null
	docker save -o ${NAME}.tar ${NAME}:${VERSION} >/dev/null || { rm -f ${NAME}.tar &>/dev/null;echo -e "\nSave docker image failed!";exit 1; }
	if [ ! -d ${DIR} ]; then
		mkdir -p ${DIR}
	fi
	mv ${NAME}.tar ${DIR}/${NAME}.tar &>/dev/null
	echo "build completes!"
else
	echo "not all -n and -v and -d are provided!"
	exit 1
fi
