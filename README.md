# UE Capability Parser [![tests](https://github.com/HandyMenny/uecapabilityparser/actions/workflows/test.yaml/badge.svg?branch=main)](https://github.com/HandyMenny/uecapabilityparser/actions/workflows/test.yaml) [![codecov](https://codecov.io/gh/HandyMenny/uecapabilityparser/branch/main/graph/badge.svg?token=looZqGVbgV)](https://codecov.io/gh/HandyMenny/uecapabilityparser)

UE Capability Parser is a tool that parses the LTE/NR capabilities of mobile devices and convertes them into a human-readable format.<br>

Despite its beta quality, it's the source of sites such as smartphonecombo.it, cacombos.com, mobilecombos.com

## Run locally

1. Install Java Runtime (JRE) 11 or above
2. Download last archive (the right archive starts with uecapabilityparser-) from [release page](https://github.com/HandyMenny/uecapabilityparser/releases?q=v0)
3. Decompress the archive
4. Open a terminal in the folder where the archive was extracted
5. If you're using Linux make the uecapabilityparser script executable:

    ````
    chmod +x uecapabilityparser
    ````
6. Run the application:
    > **Note**<br>
    if you're using Windows Command Prompt (cmd) omit "./"
    ````
    ./uecapabilityparser
    ````
   a. Server mode example:
   ````
   ./uecapabilityparser -s 8080
    ````
   b. CLI mode example:
   ````
   ./uecapabilityparser -t C -i input.xml -c output.csv
    ````

## Run with Docker - Server Mode
1. Open a terminal
2. Run container in detached mode

   Example:

   ````
   docker run --name uecapabilityparser -p 8081:8080 -d ghcr.io/handymenny/uecapabilityparser:latest -s 8080
   ````
   Where:
   - ```--name``` sets the name of the container
   - ```-p 8081:8080``` map the port 8080 of the container to the port 8081 of the host
   - ```-d``` starts the container in detached mode (background)
   - ```ghcr.io/handymenny/uecapabilityparser:latest``` is the container image to use
   - ```-s 8080``` are the options to be passed to the container, in this case start a server on port 8080

## Run with Docker - Cli Mode
> **Warning**<br>
This isn't the recommended way to run the parser. It currently complicates sending data to the parser and receiving data from the parser.

1. Create a directory that will store the input and output files.
2. Open a terminal in the folder created in step 1
3. If you're using Linux make sure that the folder created in step 1 and the files in it are readable and writable by UID/GID 2000.<br>
   An easy way is to change the owner recursively:

   ````
    chown 2000:2000 -R .
    ````
4. Run container in interactive mode:

   Example:

    ````
    docker run --name uecapabilityparser -it --rm -v `pwd`:/home/java ghcr.io/handymenny/uecapabilityparser:latest -h
    ````
   Where:
   - ```--name``` sets the name of the container
   - ```-it``` starts the container in interactive mode (attached to the shell)
   - ```--rm``` remove the container after the execution
   - ``-v `pwd`:/home/java`` maps the current directory to /home/java (in container)
   - ```ghcr.io/handymenny/uecapabilityparser:latest``` is the container image to use
   - ```-h``` are the options to be passed to the container, in this case print help

## Build

1. Install Git
2. Clone the repo:
   > **Note**<br>
   > If you have already cloned this before, you can update it and its submodules with these commands:
   > ````
   > git fetch
   > get pull
   > git submodule update --init
   > ````

    ````
    git clone --recurse-submodules https://github.com/HandyMenny/uecapabilityparser
    ````
3. Move to `uecapabilityparser` folder:

    ````
    cd uecapabilityparser
    ````
4. Build the application:
    - build with gradle (requires JDK 11 or above):

        > **Note**<br>
        if you're using Windows Command Prompt (cmd) omit "./"
        ````
        ./gradlew build
        ````
        You will find the build artifacts in `build/distributions/`
    - or build the docker image (requires Docker):

        ````
        docker build -t ghcr.io/handymenny/uecapabilityparser:latest .
        ````

## Tips

In each release zip you can find:
- Some useful type script definitions in ```uecapabilityparser.d.ts```
- OpenAPI spec in ```openapi.json```

The server (alongside the uecapabilityparser API) also serves:
- Web UI (DEMO) at ```/```
- Swagger UI at ```/swagger```
- OpenAPI spec at ```/openapi```

Useful links:
- [Swagger UI (online)](https://handymenny.github.io/uecapabilityparser-swagger/)
- [Web UI source code](https://github.com/handymenny/uecapabilityparser-web)
