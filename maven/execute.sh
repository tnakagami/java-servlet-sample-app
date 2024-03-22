#!/bin/bash

is_running=1

function handler() {
  echo sigterm accepted

  is_running=0
}
trap handler 1 2 3 15

echo "[maven]" $(date "+%Y/%m/%d-%H:%M:%S") start

while [ ${is_running} -eq 1 ]; do
  sleep 1
done
