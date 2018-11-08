#!/bin/bash

classname=$1
sourcefile="$classname.java"
jarfile="$classname.jar"

rm -Rf classes
rm $jarfile
mkdir classes

export HDP_HOME="/usr/hdp/current"
export HADOOP_HOME="${HDP_HOME}/hadoop-client/"
export HADOOP_COMMON_HOME="${HDP_HOME}/hadoop-client/"
export HADOOP_HDFS_HOME="${HDP_HOME}/hadoop-hdfs-client/"
export HADOOP_MAPRED_HOME="${HDP_HOME}/hadoop-mapreduce-client"
export YARN_HOME="${HDP_HOME}/hadoop-yarn-client"
export JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.191.b12-0.el7_5.x86_64"


echo "Compiling ..."
javac -cp $HADOOP_COMMON_HOME/hadoop-common-3.1.1.3.0.1.0-187.jar:$HADOOP_MAPRED_HOME/hadoop-mapreduce-client-core-3.1.1.3.0.1.0-187.jar:. -d classes $sourcefile

echo "Creating jar ..."
jar -cvf $jarfile -C classes/ .
