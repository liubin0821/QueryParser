#!/bin/bash

curl http://192.168.201.179:12548/ltp?reload=1
echo ": 179"
sleep 1

curl http://192.168.201.167:12548/ltp?reload=1
echo ": 167"
sleep 1

curl http://192.168.201.168:12548/ltp?reload=1
echo ": 168"
sleep 1

curl http://192.168.201.180:12548/ltp?reload=1
echo ": 180"
sleep 1

curl http://192.168.201.205:12548/ltp?reload=1
echo ": 205"
sleep 1

curl http://192.168.201.206:12548/ltp?reload=1
echo ": 206"
sleep 1

curl http://192.168.201.116:12548/ltp?reload=1
echo ": 116"
sleep 1

curl http://192.168.201.117:12548/ltp?reload=1
echo ": 117"

curl http://192.168.201.12:13548/ltp?reload=1
echo ": 12"

echo "Done"

