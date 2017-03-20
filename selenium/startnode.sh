#!/bin/bash

java -jar /opt/selenium-server-standalone-2.53.1.jar -role node -host localhost -port "$1" -hub http://localhost:4444/grid/register -browser browserName=firefox,version=45,firefox_binary=/opt/firefox/firefox,maxInstances=1,platform=LINUX

