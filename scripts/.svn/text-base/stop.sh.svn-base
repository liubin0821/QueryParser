#!/bin/bash

runpid=`ps -aef |grep run.sh|grep -v grep | awk '{print $2}'`
qppid=`ps -aef |grep QueryParser|grep -v grep|awk '{print $2}'`

test -n "$qppid" && kill -9 $qppid
test -n "$runpid" && kill -9 $runpid

sleep 1
echo "stoped."


