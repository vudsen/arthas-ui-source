#!/bin/bash

# Initialize the environment.
tar -zxvf "$JAVA_VERSION.tar.gz"
mv $JAVA_VERSION java
dpkg -i unzip_6.0-20ubuntu1.1_amd64.deb

# Launch the process.
/usr/sbin/sshd -D &
PID1=$!
$JAVA_HOME/bin/java -jar math-game.jar &
PID2=$!

while true; do
  if ! kill -0 $PID1 2>/dev/null; then
    echo "[Warning] Process 1 exited, closing other process"
    kill $PID2 2>/dev/null
    wait $PID2 2>/dev/null
    exit 1
  fi

  if ! kill -0 $PID2 2>/dev/null; then
    echo "[Warning] Process 2 exited, closing other process"
    kill $PID1 2>/dev/null
    wait $PID1 2>/dev/null
    exit 1
  fi

  sleep 10
done