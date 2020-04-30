#!/usr/bin/env bash

IFSBAK=$IFS
IFS=""
# Load config from files
ROUTER1_OPTIONS=$(cat ./router1.conf)
ROUTER2_OPTIONS=$(cat ./router2.conf)
ROUTER3_OPTIONS=$(cat ./router3.conf)
ROUTER4_OPTIONS=$(cat ./router4.conf)
IFS=$IFSBAK
IP=$(ip addr | grep 'state UP' -A2 | tail -n1 | awk '{print $2}' | cut -f1  -d'/')

router4=$(podman run --name router4 -p 10004:10004 -e QDROUTERD_CONFIG_OPTIONS="$ROUTER4_OPTIONS" -d docker.io/scholzj/qpid-dispatch:1.10.0)
router3=$(podman run --name router3 --add-host=router4:$IP -p 10003:10003 -e QDROUTERD_CONFIG_OPTIONS="$ROUTER3_OPTIONS" -d docker.io/scholzj/qpid-dispatch:1.10.0)
router2=$(podman run --name router2 --add-host=router4:$IP -p 10002:10002 -e QDROUTERD_CONFIG_OPTIONS="$ROUTER2_OPTIONS" -d docker.io/scholzj/qpid-dispatch:1.10.0)
router1=$(podman run --name router1 --add-host=router2:$IP --add-host=router3:$IP -p 2009:2009 -e QDROUTERD_CONFIG_OPTIONS="$ROUTER1_OPTIONS" -d docker.io/scholzj/qpid-dispatch:1.10.0)
