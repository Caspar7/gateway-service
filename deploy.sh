#!/usr/bin/env bash

BUILD_NUMBER=$1
env=$2
serviceName="gateway-service"
running=`docker ps | grep ${serviceName} | awk '{print $1}'`
if [ ! -z "$running" ]; then
    docker stop $running
fi

container=`docker ps -a | grep ${serviceName} | awk '{print $1}'`
if [ ! -z "$container" ]; then
    docker rm $container -f
fi

imagesid=`docker images|grep -i ${serviceName}|awk '{print $3}'`
if [ ! -z "$imagesid" ]; then
    docker rmi $imagesid -f
fi

docker build -t ${serviceName}:$BUILD_NUMBER .

docker run --env env=${env} -it -d -p 8888:8888 --name ${serviceName} ${serviceName}:$BUILD_NUMBER