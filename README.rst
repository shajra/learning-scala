Learning Scala
==============

This is a project where I plan to aggregate small pieces of code to help
communicate what I've learned while studying `Scala <http://scala-lang.org>`_.


Organization
------------

I'm going to try to keep the project clean enough that the code is
self-documenting.  The project divides into three variants of code:

- tests under src/test/scala

- main methods under src/main/scala that print to the console upon execution

- code that is instructive just in that it compiles (see
  src/main/scala/curryhoward.scala).

There are a few modules designed for reuse, but in general, I've tried to map
complete discussions to single files so they read more like short articles.


Build/Execution
---------------

This project builds with `SBT <https://github.com/harrah/xsbt>`_, which is
extremely powerful and for simple projects like this one pretty easy to use.
The documentation for SBT is pretty good now, so see `SBT's Getting Starting
Guide <https://github.com/harrah/xsbt/wiki/Getting-Started-Welcome>`_ for
details on running the tool.

The nice part about SBT is you really don't need much on your system beyond a
JDK and the sbt-launcher.jar, which bootstraps your system to download the rest
from the public internet (include the Scala compiler and core libraries).

Here's a few steps to help you get started.

1. Make sure you have a late edition JDK installed.

2. Download SBT's sbt-launcher.

3. Design a script to run the launcher like this one.

.. code-block::

   #!/bin/sh


   JAVA_BIN="$HOME/opt/jdk/jdk7/bin/java"

   JAVA_OPTS="
       -Xmx2048M
       -XX:MaxPermSize=350m
       -XX:+CMSClassUnloadingEnabled
       -XX:+DoEscapeAnalysis
   "

   SBT_JAR="$HOME/opt/sbt/sbt-launch.jar"


   $JAVA_BIN $JAVA_OPTS -jar "$SBT_JAR" $@

4. Run the script, which gets you into a very nice console from which you can
   execute useful commands like "compile", "test", "run", "doc", and "console".

But really, you shouldn't need all this fanciness to follow along.  Just
reading the code will probably suffice.
