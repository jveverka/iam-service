#!/bin/bash

NOCOLOR='\033[0m'
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'

function eval_result_exit  () {
  MESSAGE_OK=$2
  MESSAGE_ER=$3
  if [ $1 = 0  ]; then
     echo -e "${MESSAGE_OK}"
  else
     echo -e "${MESSAGE_ER}"
     exit 1
  fi
}
