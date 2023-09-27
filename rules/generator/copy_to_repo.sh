#!/usr/bin/env bash
set -e

if [ $# -ne 2 ]; then
  echo "Usage: $0 <source_dir> <target_dir>"
  exit 1
fi

SOURCE_DIR=$1
TARGET_DIR=$BUILD_WORKSPACE_DIRECTORY/$2

rm -rf $TARGET_DIR
mkdir -p $TARGET_DIR

cp -R $SOURCE_DIR/. $TARGET_DIR
chmod -R u+rw $TARGET_DIR
