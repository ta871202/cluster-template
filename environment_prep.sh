#!/bin/bash
set -x
sudo yum -y install ntp
sudo systemctl enable ntpd
sudo systemctl disable firewalld
sudo service firewalld stop
sudo setenforce 0
sudo umask 0022

sudo yum -y install java-1.8.0-openjdk-devel
sudo wget -nv http://public-repo-1.hortonworks.com/ambari/centos7/2.x/updates/2.7.1.0/ambari.repo -O /etc/yum.repos.d/ambari.repo
