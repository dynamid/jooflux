# JooFlux: a Java agent for dynamic aspect-oriented middlewares

JooFlux a JVM agent that allows both the dynamic replacement of method implementations and the
application of aspect advices. Compared to existing approaches, JooFlux takes a novel route by
taking advantage of the new `invokedynamic` instruction added in Java SE 7. The runtime overhead of
JooFlux is marginal for method invocations, and fairly limited when aspects are being injected. In
any case, JooFlux shows interesting performance compared to related approaches such as AOP tools or
dynamic languages that rely on dynamic dispatch. More interestingly, JooFlux does not involve
reloading whole classes on either method replacement or advice injection, which keeps a large range
of just-in-time compilation optimizations valid.

JooFlux is being developed as part of a [larger academic research group](http://dynamid.citi-lab.fr/)
that focuses on the design and implementation of middlewares for dynamic environments.

## Current status

We demonstrated initial developments of JooFlux at the [Devoxx France 2012 conference](http://www.devoxx.fr/).

The current iteration of the source code is only a proof-of-concept of leveraging `invokedynamic`
for aspect-oriented programming and live code patching. **It is not production-ready**, and
further developments need to be made to turn JooFlux into a usable product.

While it is our intention to turn it into something easier to use, we opted to release the initial
developments source code to support the reproducibility and dissemination of our research results.

Please pardon the raw state of JooFlux, but the system we are in pushes us to publish research papers
first before focusing on the technology transfer of prototypes :-)

## Funding the JooFlux development

Our very own ability to progress on dynamic code replacement and aspect injection with JooFlux
depends on funding.

We have no external funding for the JooFlux project at the moment, so if you feel like sustaining
our research on this project, feel-free to contact us!

## License

The initial work was conducted at [INSA-Lyon](http://www.insa-lyon.fr/) in the
[CITI laboratory](http://www.citi-lab.fr/).

JooFlux is made available under the terms of the
[Mozilla Public License, v 2.0](http://mozilla.org/MPL/2.0/).

### Header notice

Any source file must carry the following header:

    JooFlux
    
    Copyright (c) 2012 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
    Copyright (c) 2012 Julien Ponge, INSA-Lyon
    Copyright (c) 2012 Frédéric Le Mouël, INSA-Lyon

    This Source Code Form is subject to the terms of the Mozilla Public
    License, v. 2.0. If a copy of the MPL was not distributed with this
    file, You can obtain one at http://mozilla.org/MPL/2.0/.

### Third-party dependencies

JooFlux embeds the following libraries:

* [ASM](http://asm.ow2.org/), [BSD-style licensed](http://asm.ow2.org/license.html)
* [Tinylog](http://www.tinylog.org/), [Apache 2.0 licensed](http://www.tinylog.org/license)

## Contact

You may contact us using our INSA-Lyon email addresses (see the `AUTHORS` file).

If you would like to report issues, please do so with the
[issue tracker on GitHub](https://github.com/dynamid/jooflux/issues).

## Contributing

We welcome contributions, see `CONTRIBUTING.markdown` for instructions.

## Building and dependencies

JooFlux requires:

* Java SE 7+
* [Apache Buildr](http://buildr.apache.org/)

Because Apache Buildr is written in [Ruby](http://www.ruby-lang.org/), you will need a working
Ruby installation. We suggest that you manage your environments using [rbenv](http://rbenv.org/)
and use [JRuby](http://jruby.org/) as your Ruby runtime.

Building JooFlux is straightforward using Apache Buildr:

    buildr clean install

We also have a bunch of custom Rake tasks for testing purposes, e.g.:

    buildr test_fibonacci
    buildr test_counterloop
    (...)

## Running JooFlux

JooFlux needs to be attached to a JVM like any other regular agent. JooFlux transforms bytecode
in a way that sometimes breaks the bytecode verifier expectations. Nevertheless, the transformed
bytecode remains correct at runtime, so it is *best* to run JooFlux while disabling the bytecode
verifier (`-noverify` flag). Other JVM agents sometimes ask you to do the same, too.

Here is how you could run a Java application with JooFlux wired into the JVM:

    java -noverify -javaagent:jooflux-{version}.jar -classpath lib1.jar:lib2.jar:lib3.jar some.Main

JooFlux uses [Tinylog](http://www.tinylog.org) as its logging framework. You may tweak the configuration
[by passing flags as documented](http://www.tinylog.org/user-manual). For instance, `-Dtinylog.level=ERROR`
filters most logs bar errors. You may also conveniently redirect the output to files.

Interactions with JooFlux currently happen through a JMX agent:
`fr.insalyon.telecom.jooflux.internal.jmx.JooFluxManagement`. It offers methods to get metrics,
update methods and apply aspects. While the interface is a bit raw at the moment, see the `demoing/`
folder for examples of valid parameters to perform demonstrations of JooFlux.
    
## Backlog towards turning JooFlux into a product

Here are the items that need to be addressed.

### Improving the code base

The source code went through scattered burst of interrupted developments. We had to show the
feasibility of our approach first and foremost while having few allocatable time, hence we did not
cover all corner cases and sometimes had to favor working code over cleaner code.

This is a general effort that any software project faces.

### Corner cases

The bytecode transformations made by JooFlux are not trivial. We are well aware that it breaks
some bytecode sequences, resulting in invalid stack frames (*a fancy expression for "crash"*).

We would greatly appreciate if you could isolate pathological corner cases so that we can
address those in subsequent releases, and eventually turn JooFlux into a tool usable on a larger
set of JVM applications.

Of course if you can both isolate corner cases *and* offer a fix, feel-free to do so!

### Thread safety

Our implementation is weak with respect to thread safety. More tests and fixes need to be made
in multi-threading settings.

### Memory leaks

We keep track of call sites in a registry. However, we keep references to the call sites *forever*,
hence call sites cannot be garbage collected. This is problematic, and we need to turn to weak
references and find the appropriate spot to occasionally remove expired entries from the registry.

### Management

JooFlux is currently manageable using JMX. This makes requesting operations such as patching
a method tedious using tools such as `jconsole`.

Instead, we should design a (TCP?) protocol for external interactions with scripts / tools.
Also, it should be possible to perform batches of operations (e.g., apply advice `foo` to all
the classes implementing `SomeInterface`, update all methods of class `Foo` with those from
`Foo` in `foo-1.1.jar`, etc).

### Classpath injection

When one applies an advice or patches a method, the corresponding class needs to be visible from
the JooFlux agent classpath. While this works fine for our demos, this is clearly not satisfying.

We need to be able to order the agent to resolve classes using dynamic classloaders.

### Hooks

Certain classes of applications may take advantage of JooFlux event notifications, for instance
to react to a class being patched and perform various operations such as additional housekeeping.

