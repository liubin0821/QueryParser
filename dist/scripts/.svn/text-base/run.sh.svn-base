#!/bin/bash

cd ..

while true
do
    qppid=`ps xf |grep QueryParser-1.0.0.jar|grep -v grep|awk '{print $1}'`

    if [ -n "$qppid" ] ; then
        echo "already running .."
    else
        echo "starting .."
        ~/jdk/bin/java -Xmn1024M -Xms3g -Xmx3g -XX:PermSize=256M -XX:MaxPermSize=256M -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=4 -XX:InitialTenuringThreshold=15 -XX:MaxTenuringThreshold=15 -XX:+HandlePromotionFailure -XX:-DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=70 -cp config/:QueryParser-1.0.0.jar com.myhexin.server.ParserServerStart     
    fi
    sleep 25
done
