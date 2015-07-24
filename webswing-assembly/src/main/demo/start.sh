#!/bin/sh
#
# Startup script for the Webswing 
#
# pidfile: /home/webswing/webswing.pid

# Set environment.
export HOME=/home/webswing
export OPTS="-h 0.0.0.0 -j $HOME/jetty.properties -u $HOME/user.properties -c $HOME/webswing.config"
export JAVA_HOME=/opt/java7
export JAVA_OPTS="-Xmx128M"

export LOG=$HOME/webswing.out
export PID_PATH_NAME=$HOME/webswing.pid

if [ ! -f $HOME/webswing-server.war ]; then
    echo "Webswing executable not found in $HOME folder" 
    exit 0
fi

if [ ! -f $JAVA_HOME/bin/java ]; then
    echo "Java installation not found in $JAVA_HOME folder" 
    exit 0
fi

# See how we were called.
case "$1" in
    start)
        # Start daemon.
        echo -n "Starting Webswing: "
        if [ ! -f $PID_PATH_NAME ]; then
            if [ -z $DISPLAY ]; then
              if [ `ps au | grep "X \:99" | wc -l` -eq 0 ]; then
                xinit -- :99 &
              fi
              export XAUTHORITY=~/.Xauthority
              export DISPLAY=':99'
            fi
            nohup $JAVA_HOME/bin/java $JAVA_OPTS -jar webswing-server.war $OPTS 2>> $LOG >> $LOG & echo $! > $PID_PATH_NAME
        echo "STARTED"
        else
            PID=$(cat $PID_PATH_NAME);
            if [ `ps -axo pid | grep "$PID" | wc -l` -eq 0 ]; then
                rm $PID_PATH_NAME
                echo "Webswing is NOT running, but stale PID [$PID] found. Clearing..."
            else
                echo "Webswing is already running with pid $PID..."
            fi
        fi
        ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "Webswing stoping ..."
            kill $PID;
            echo "Webswing stopped ..."
            rm $PID_PATH_NAME
        else
            echo "Webswing is not running ..."
        fi
    ;;
    status)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            if [ `ps axo pid | grep "$PID" | wc -l` -eq 0 ]; then
                rm $PID_PATH_NAME
            else
                echo "Webswing is running with pid $PID."
            fi
        else
            echo "Webswing is not running ..."
        fi
    ;;
    restart)
        $0 stop
        $0 start
    ;;
    *)
        echo "Usage: $0 {start|stop|restart|status}"
        exit 1
esac

exit 0
