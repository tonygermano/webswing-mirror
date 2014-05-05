#!/bin/bash
cd $(dirname $0)
if [[ -z $DISPLAY ]]; then
  if [[ `ps au | grep "X \:99" | wc -l` == 0 ]]; then
    xinit -- :99 &
  fi
  export XAUTHORITY=~/.Xauthority
  export DISPLAY=':99'
fi
java -jar webswing-server.war

