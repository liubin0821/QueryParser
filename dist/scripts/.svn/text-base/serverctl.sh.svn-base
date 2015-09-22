#!/bin/bash
export LANG=zh_CN.UTF-8
case $1 in
	"start")
        cd dictSynchronization/
		nohup python server.py >/dev/null 2>&1 & 
		echo "started."
		;;
	"stop")
		pid=`ps xf |grep server.py |grep -v grep | awk '{print $1}'` 
		test -n "$pid" && kill -9 $pid
		echo "stoped."
		;;
esac
