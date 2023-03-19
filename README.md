# UE Capability Parser [![tests](https://github.com/HandyMenny/uecapabilityparser/actions/workflows/test.yaml/badge.svg?branch=main)](https://github.com/HandyMenny/uecapabilityparser/actions/workflows/test.yaml) [![codecov](https://codecov.io/gh/HandyMenny/uecapabilityparser/branch/main/graph/badge.svg?token=looZqGVbgV)](https://codecov.io/gh/HandyMenny/uecapabilityparser)

UE Capability Parser is a tool that parses the LTE/NR capabilities of mobile devices and convertes them into a human-readable format.<br>

Despite its alpha/beta quality, it's the source of sites such as smartphonecombo.it, cacombos.com, mobilecombos.com

## Run locally

1. Install Java Runtime (JRE) 11 or above
2. Download last archive (the right archive starts with uecapabilityparser-) from [release page](https://github.com/HandyMenny/uecapabilityparser/releases)
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

## Run with Docker
> **Warning**<br>
This isn't the recommended way to run the parser. It currently complicates sending data to the parser and receiving data from the parser.

1. Create a directory that will store the input and output files.
2. Open a terminal in the folder created in step 1
3. If you're using Linux make sure that the folder created in step 1 and the files in it are readable and writable by UID/GID 2000.<br>
   An easy way is to change the owner recursively:

   ````
    chown 2000:2000 -R .
    ````
4. Run the application:

    ````
    docker run -it --rm -v `pwd`:/home/java ghcr.io/handymenny/uecapabilityparser:main
    ````

## Build

1. Install Git
2. Clone the repo:

    ````
    git clone https://github.com/HandyMenny/uecapabilityparser
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
        docker build -t ghcr.io/handymenny/uecapabilityparser:main .
        ````
