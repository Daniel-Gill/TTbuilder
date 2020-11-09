#! /bin/bash
java -jar "/home/david/Documents/railways/Sydney Dev/TTBuilder/dist/TTBuilder.jar" -o=Strathfield8f.ttb -i=STR8_Head.xml -i=STR8-BM.xml -i=STR8-CCN-N.xml -i=STR8-T1.xml -i=STR8-T2.xml -i=STR8-T3.xml -i=STR8-T6andECS.xml -i=STR8-T7.xml 
sleep 1
java -jar "/home/david/Documents/railways/Sydney Dev/TTBuilder/dist/TTBuilder.jar" -t -o=STR8f.txt -i=STR8_Head.xml -i=STR8-BM.xml -i=STR8-CCN-N.xml -i=STR8-T1.xml -i=STR8-T2.xml -i=STR8-T3.xml   -i=STR8-T6andECS.xml -i=STR8-T7.xml


read -p "Pause: " name



