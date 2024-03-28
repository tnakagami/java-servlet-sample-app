#!/bin/bash

ls -vF | grep "/" | while read target_dir; do
  pushd ${target_dir}
    mvn clean package -DcompilerArgument=-Xlint:deprecation,unchecked
  popd
done
