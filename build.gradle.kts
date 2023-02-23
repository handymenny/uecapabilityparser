plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.graalvm.buildtools.native") version "0.9.20"
    id("com.diffplug.spotless") version "6.15.0"
    application
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    val kotlinVersion = "1.8.10"

    implementation("commons-cli:commons-cli:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("com.github.handymenny.mts-asn1:mts-asn1-converter:7784a9f")
    implementation("com.github.handymenny.mts-asn1:mts-asn1-per:7784a9f")
    implementation("com.github.handymenny.mts-asn1:mts-asn1-kotlinx-json:7784a9f")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    implementation("org.slf4j:slf4j-nop:2.0.6")
}

group = "parser"

version = "0.0.5-alpha"

description = "uecapabilityparser"

tasks {
    // Disable default dist zip
    distZip { enabled = false }
    // Disable default dist tar
    distTar { enabled = false }
    // Disable shadow dist tar (zip is enough)
    shadowDistTar { enabled = false }
    // Disable shadow default startScript
    startShadowScripts { enabled = false }
    // Disable default startScript
    startScripts { enabled = false }

    // Enable Junit test
    test { useJUnitPlatform() }

    // Enable shadow minify
    shadowJar { minimize { exclude(dependency("org.slf4j:slf4j-nop:.*")) } }
}

application { mainClass.set("it.smartphonecombo.uecapabilityparser.MainCli") }

kotlin { jvmToolchain(8) }

graalvmNative {
    binaries.all { resources.autodetect() }

    binaries {
        named("main") {
            fallback.set(false)
            verbose.set(false)
            imageName.set("uecapabilityparser")
        }
    }
}

spotless {
    format("misc") {
        target("*.md", ".gitignore", "**/*.csv")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    java {
        importOrder()
        removeUnusedImports()
        googleJavaFormat().aosp().reflowLongStrings()
        formatAnnotations()
    }

    kotlin { ktfmt().kotlinlangStyle() }

    kotlinGradle { ktfmt().kotlinlangStyle() }
}

distributions {
    named("shadow") {
        distributionBaseName.set(project.name)
        contents {
            from("src/main/dist")
            from("LICENSE")
            from("README.md")
            eachFile {
                // Move all files to root and rename jar to uecapabilityparser.jar
                this.path =
                    this.path
                        .replace("${project.name}-${project.version}/", "")
                        .replace(""".*\.jar""".toRegex(), "${project.name}.jar")
            }
        }
    }
}
