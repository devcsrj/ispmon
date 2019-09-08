# ISPMON

Your persistent ISP bandwidth monitor.

![](img/home.png)

As a software engineer who relies on [managed dependencies](https://i.redd.it/tfugj4n3l6ez.png), the least of your 
concerns SHOULD be the managed dependencies. This is not the case in the [country I am living in](https://www.rappler.com/technology/news/171680-philippines-akamai-broadband-adoption-internet-speed-rankings).

I am currently subscribed to my [ISP's fiber connection offering](https://www.convergeict.com/fiber-x/). It promises 
no data cap, maxed at 25Mbps, but with a teeny tiny asterisk that says:

> 30% minimum speed at 80% service reliability.

Then came that night, when our internet speed dropped down to 1Mbps. I could've tolerated it if it only 
happened rarely. But it happened consistently at certain times of the day, and it made me realize that 
they _might_ be throttling connections at **specified times of the day**.

To track that down (and soon nag about it), I started this project.

## Installing

The first version is still not released, so the only option is to build the project.

My end goal is to build a tiny docker image, and keep that running on my Raspberry Pi.

## Configuring

The application reads the following from the environment variables:

* `ISPMON_INTERVAL=15` - the interval at which, speed test should be done
* `ISPMON_PORT=5000` - the port to run the web server

---

## Developing

### Prerequisites

- [GraalVM 19.2.0](https://www.graalvm.org)
- [Docker](https://docs.docker.com/install/)

**Tip**: Use [sdkman](https://sdkman.io/install).

```shell script
$ sdk install java 19.2.0-grl
```

> You may also use a plain old JDK8 distribution, but this means that 
> you can only build `jar` files, and not a [native image](https://www.graalvm.org/docs/reference-manual/aot-compilation/).

Finally, install `native-image`:

```shell script
$ gu install native-image
```

### Backend

To run the server:

```shell script
$ ./gradlew run
```

It will then start a server at port `5000`.

### Frontend

The frontend resides under `src/main/frontend`. It also expects that the 
backend is running at port [5000](src/main/frontend/webpack.config.js).

```shell script
$ cd src/main/frontend
$ yarn run start
```

This will start the webpack server at port `5001`.

### Building

The [build configuration](https://www.graalvm.org/docs/reference-manual/aot-compilation/) is configured 
to produce various artifacts.

#### Vanilla

To build a `jar`:

```shell script
$ ./gradlew build
```

You can then run this with `java -jar build/lib/ispmon-$version-fat.jar`.

#### Binary

But what we really want, is to build a binary. One that does not depend 
on the massive JRE. To do that:

```shell script
$ ./gradlew nativeImage
```

This will produce an executable named `ispmon`. This can then be run with:

```shell script
$ ./ispmon
```

#### Docker

On ARM architecture such as the Raspberry Pi, the native image won't work. So 
our best option is to build a docker image, that wraps the native image. To build 
that:

```shell script
$ ./gradlew docker
```

This will then produce a 46MB~ image, called `devcsrj/ispmon:1.0.0`, which you 
can then run with:

```shell script
$ docker run -p 5000:5000 devcsrj/ispmon:1.0.0
```

> Note: There's still [an issue](http://github.com/devcsrj/ispmon/issues/1) with the produced docker image.

---

## FAQ

* Have you tried switching to another ISP?

    Well, there's another [ISP here](https://pldthome.com/fibr), but I'd argue it's not any better. At 
    work, I ran `ispmon` to see the results for comparison. I am staggered:

![](img/work.png)

* Did you really have to go this far?

    I also needed an excuse to play with new shiny things. This is my first project that involved:
    
    - The ultra-fast and lean [Vertx](https://vertx.io)
    - The well-loved [React](https://reactjs.org)
    - The smart bundler [webpack](https://webpack.js.org)
    - and the bleeding edge [Graal Native Image](https://www.graalvm.org/docs/reference-manual/aot-compilation/)
