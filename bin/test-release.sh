#!/usr/bin/env bash
set -eux

version=$1

coursier fetch \
  ch.epfl.scala:bsp4j:$version \
  ch.epfl.scala:bsp4s_2.12:$version \
  ch.epfl.scala:bsp-testkit_2.12:$version \
  -r sonatype:public
