Learning Scala
==============

This is a project where I plan to aggregate small pieces of code to help
communicate what I've learned while studying [Scala](http://scala-lang.org).


Organization
------------

I'm going to try to keep the project clean enough that the code is
self-documenting.  The project divides into three variants of code:

* tests under src/test/scala

* main methods under src/main/scala that print to the console upon execution

* code that is instructive just in that it compiles (see
  src/main/scala/curryhoward.scala).

There are a few modules designed for reuse, but in general, I've tried to map
complete discussions to single files so they read more like short articles.

However, this project is not a tutorial.  It's not designed to teach any kind
of cirruculum.  It's just a diverse collection of things I found interesting
and wrote up.


Using This Project
------------------

This project builds with [Simple Build Tool (SBT)](http://www.scala-sbt.org),
which is extremely powerful and for simple projects like this one pretty easy
to use.  The documentation for SBT much improved now, so see [SBT's Getting
Starting Guide](http://www.scala-sbt.org/release/docs/Getting-Started)
for details on running the tool.

This project provides binaries and scripts to bootstrap SBT, so all you need
installed on your system is a recent release of a Java runtime.

For Windows users run

    $ .\sbt.cmd

or for Macintosh or GNU/Linux users run

    $ ./sbt

which will download dependencies and launch into SBT's interactive shell.  At
the shell's prompt you can issue commands to work with the code.  Some useful
commands are

* `compile`:  to validate that everything compiles
* `test`:  to run tests under src/test/scala
* `run`:  to run main methods under src/main/scala
* `doc`:  to generate Scaladocs for code under src/main/scala
* `test:doc`:  to generate Scaladocs for code under src/test/scala
* `console`:  to play around with the code in a Scala REPL
* `exit`:  to exit SBT

But really, you shouldn't need all this fanciness to follow along.  Just
reading the code will probably suffice.
