#TCP Manager

The TCP manager allows you to perform batches of operations without using JMX. Jooflux starts an internal TCP server and waits for commands from the client.

##The client

The TCP client implements the same actions as the JMX clients does :

* `changeCallSiteTarget(String methodType, String oldTarget, String newTarget)`

* `applyBeforeAspect(String callSitesKeyBefore, String aspectClassBefore, String aspectMethodBefore)`

* `applyAfterAspect(String callSitesKeyAfter, String aspectClassAfter, String aspectMethodAfter)`

* `getRegisteredCallSiteKeys()`

* `getNumberOfRegisteredCallSites()`

* `getCallSiteType(String target)`

##The protocol

The protocol is quite simple. Let's take an exemple with the call of "changeCallSiteTarget" in the nominal case :

* The client sends a JSON object that contains the name of the method to call and its arguments : `{"call":"changeCallSiteTarget", "methodType":"virtual", "oldTarget":"HelloWorld.tick:(HelloWorld)void", "newTarget":"HelloWorld.tack:()V"}`

* If everything is okay, the server executes the method and answers with a JSON object saying "ok" and the method that has been called : `{"result":"ok", "calledMethod":"changeCallSiteTarget"}`

Every message ends by `--QUIT--` in order to separate the different messages in the buffers.

###Edge cases

If the called method doesn't exist, the server answers the following JSON : `{"result":"ko", "error":"unknownMethod", "message":"Method '--the wrong method--' doesn't exist."}`

If the arguments are not correct, the server answers the following JSON : `{"result":"ko", "error":"unexpectedAttribut", "message":"--the JSON sent to the server--"}`

In the case of the call of the method `getCallSiteType` with an unregistered target, the server will answer `{"result":"ko","error":"unknownTarget","message":"The target '--the unknown target--' is not registered."}`

##Full example

Let's take the exemple of a simple program that prints "ticks" :

```java
    public class TickTack {

        public static Object[] onCall(Object[] args) {
            System.out.println("Tick is going to be performed");
            return args;
        }

        public static Object onReturn(Object arg) {
            System.out.println("Tick has been performed");
            return arg;
        }

        public void tick() {
            System.out.println("[tick]");
        }

        public void tack() {
            System.out.println("[tack]");
        }

        public static void main(String[] args) throws InterruptedException {
            TickTack hw = new TickTack();
            while (true) {
                hw.tick();
                Thread.sleep(2000);
            }
        }
    }
```

We launch it with Jooflux as a Java agent : `java -noverify -javaagent:jooflux-r2-SNAPSHOT.jar -classpath ticktack.jar TickTack`

It will print "\[Tick\]" every two seconds.

    [Tick]
    [Tick]
    [Tick]
    [Tick]
    ...

Now we are going to use the TCP client :

```java
    public static void main(String[] args) {
        Client c = new Client("localhost", 8080);

        c.changeCallSiteTarget("virtual", "HelloWorld.tick:(HelloWorld)void", "HelloWorld.tack:()V");
        c.applyBeforeAspect("HelloWorld.tick:(HelloWorld)void", "HelloWorld", "onCall");
        c.applyAfterAspect("HelloWorld.tick:(HelloWorld)void", "HelloWorld", "onReturn");

        List<String> registeredCallSiteKeys = c.getRegisteredCallSiteKeys();
        String callSiteType = c.getCallSiteType("HelloWorld.tick:(HelloWorld)void");
        String numberOfRegisteredCallSites = c.getNumberOfRegisteredCallSites();

        System.out.println(registeredCallSiteKeys);
        System.out.println(callSiteType);
        System.out.println(numberOfRegisteredCallSites);

        c.disconnect();
    }
```

The TickTack program now displays :

     Tick is going to be performed
     [tack]
     Tick has been performed
     Tick is going to be performed
     [tack]
     Tick has been performed
     Tick is going to be performed
     [tack]
     ...

And the standard output prints :

    ["HelloWorld.tick:(HelloWorld)void"]
    virtual
    1

