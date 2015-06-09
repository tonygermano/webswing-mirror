#!/bin/sh
#
#  Copyright (C) 2010 Andreas Reichel <andreas@manticore-projects.com>
#
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or (at
#  your option) any later version.
#
#  This program is distributed in the hope that it will be useful, but
#  WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
#  General Public License for more details.
#
#  You should have received a copy of the GNU General Public License along
#  with this program; if not, write to the Free Software Foundation, Inc.,
#  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
#
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


JAVA_HOME=/opt/java7
APP="WebSwing"
BASE=..
LOGGING="-Djava.util.logging.config.file=$HOME/.manticore/logging.properties"

PATH=$JAVA_HOME/bin:$PATH
JAVA_OPTS="-Xmx2G -Xms256M -Xss264k -Djava.security.egd=file:///dev/urandom -d64 -server -XX:+AggressiveOpts -XX:+UseStringCache -XX:AllocatePrefetchLines=1 -XX:AllocatePrefetchStyle=1 -XX:+OptimizeStringConcat -XX:UseSSE=2 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC"

NICE=10
PID=$BASE/logs/$APP.pid
LOG=$BASE/logs/$APP.log
ERROR=$BASE/logs/$APP-error.log

usage="
$(basename "$0") -- WebSwing Server Shell-Script

Synopsis:
    $(basename "$0") [-h]
or  $(basename "$0") { start | stop | restart | status }

with Parameters:
    -h|--help           show this help text"

export UNIX95= 

for arg
do
    delim=""
    case "$arg" in
       --help) args="${args}-h ";;
       start|restart|status|stop) ACTION="$arg";;
       # pass through anything else
       *) [[ "${arg}" = "-*" ]] || delim="\""
           args="${args}${delim}${arg}${delim} ";;
    esac
done
# reset the translated args
eval set -- $args
# now we can process with getopt
while getopts ":hp:1:2:3:" opt; do
    case $opt in
        h|\?)  echo "$usage" 
            exit 2
            ;;
        :)
            echo "Parameter -$OPTARG requires an argument"
            echo "$usage" 
            exit 2
            ;;
    esac
done

if [[ -z $DISPLAY ]]; then
  if [[ `ps au | grep "X \:99" | wc -l` = 0 ]]; then
    xinit -- :99 &
  fi
  export XAUTHORITY=~/.Xauthority
  export DISPLAY=':99'
fi

CMD='java'
COMMAND="$CMD $JAVA_OPTS $LOGGING -jar webswing-server.war -j jetty.properties"    

function status {
    echo
    echo "==== Status of $APP"

    if [ -f $PID ]
    then
        echo
        echo "Pid from file $PID: [$( cat $PID )]"
        echo
        
        if UNIX95= ps -p $( cat $PID ) > /dev/null
		then
   			echo "$APP is running with PID [$( cat $PID )]"
   			UNIX95= ps -ef | grep -v grep | grep $( cat $PID )	
   		else 
   			echo "$APP is NOT running, but stale PID [$PID] found"
		fi
    else
        echo
        echo "$APP is NOT running"
    fi
}

function start {
    if [ -f $PID ] && ps -p $( cat $PID ) > /dev/null
    then
		echo
        echo "$APP already started with PID [$( cat $PID )]"      	
    else
        echo "==== Starting $APP"
        touch $PID
        if [ -f $LOG ]
        then
          cp $LOG "${LOG}_$(date '+%Y%m%d%H%M%S')"; >$LOG
        fi
        if nohup nice -n$NICE sh -c " $COMMAND; sleep 1; echo \"$(date '+%Y-%m-%d %X'): $APP STOPPED\" >>$LOG; rm -f $PID " >>$LOG 2>&1 &
        then 
        	 sleep 1

        	 #UNIX95=  ps -o ppid -p $! | tail +2 > $PID
        	 pgrep -P $! > $PID

        	 echo "Started with PID [$( cat $PID )]"
             echo "$(date '+%Y-%m-%d %X'): $APP STARTED" >>$LOG
             tail -f $LOG
        else echo "Error... "
             /bin/rm $PID
        fi
    fi
}

function stop1 {
    echo "==== Stopping $APP"

    if [ -f $PID ]
    then
        if kill $(cat $PID)
        then
        	echo "Killed PID: [$( cat $PID )]"
            echo "$(date '+%Y-%m-%d %X'): $APP STOPPED" >>$LOG
            /bin/rm -f $PID
        fi
    else
        echo "No pid file found, $APP already stopped?"
    fi
}

case "$ACTION" in
    'status')
            status
            ;;
    'start')
            start
            ;;
    'stop')
            stop1
            ;;
    'restart')
            stop1; echo "Sleeping..."; sleep 3; start
            ;;  
    *)
            echo "Action $ACTION not defined."
            echo "$usage"
            exit 1
            ;;
esac

exit 0