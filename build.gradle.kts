import java.util.*

plugins {
    val kotlinVersion = "1.8.22"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.diffplug.spotless") version "6.19.0"
    id("org.jetbrains.kotlinx.kover") version "0.7.1"
    application
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

sourceSets {
    create("tstypes") {
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

val tstypesImplementation: Configuration by
    configurations.getting { extendsFrom(configurations.implementation.get()) }

dependencies {
    val mtsAsn1Version = "c4394ce"
    val kmemVersion = "4.0.6"
    val javalinVersion = "5.6.1"

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("com.github.handymenny.mts-asn1:mts-asn1-converter:$mtsAsn1Version")
    implementation("com.github.handymenny.mts-asn1:mts-asn1-per:$mtsAsn1Version")
    implementation("com.github.handymenny.mts-asn1:mts-asn1-kotlinx-json:$mtsAsn1Version")
    implementation("org.slf4j:slf4j-nop:2.0.7")
    implementation("com.soywiz.korlibs.kmem:kmem-jvm:$kmemVersion")
    implementation("com.github.ajalt.clikt:clikt:3.5.4")
    implementation("io.javalin:javalin:$javalinVersion")
    testImplementation("io.javalin:javalin-testtools:$javalinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    tstypesImplementation("dev.adamko.kxstsgen:kxs-ts-gen-core:0.2.1")
}

group = "parser"

val properties =
    Properties().apply {
        load(rootProject.file("src/main/resources/application.properties").reader())
    }

version = properties["project.version"]!!

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

    test {
        // Enable Junit test
        useJUnitPlatform()
        // generate kover report
        finalizedBy(named("koverXmlReport"))
    }

    shadowJar {
        // Omit jna dependencies (required by kmem)
        dependencies { exclude(dependency("net.java.dev.jna:.*")) }

        // Enable shadow minify
        minimize { exclude(dependency("org.slf4j:slf4j-nop:.*")) }
    }

    register("genTsTypes", JavaExec::class) {
        mainClass.set("it.smartphonecombo.uecapabilityparser.TsTypesGenerator")
        classpath = sourceSets["tstypes"].runtimeClasspath
    }
}

application { mainClass.set("it.smartphonecombo.uecapabilityparser.cli.Main") }

kotlin { jvmToolchain(11) }

spotless {
    format("misc") {
        target("*.md", ".gitignore", "**/*.csv")
        trimTrailingWhitespace()
        indentWithSpaces()
        endWithNewline()
    }

    kotlin { ktfmt().kotlinlangStyle() }

    kotlinGradle { ktfmt().kotlinlangStyle() }
}

distributions {
    named("shadow") {
        distributionBaseName.set(project.name)
        contents {
            from("src/main/dist")
            from("src/main/resources/swagger/openapi.json")
            from("uecapabilityparser.d.ts")
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
