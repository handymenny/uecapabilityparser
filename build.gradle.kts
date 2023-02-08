import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.serialization") version "1.8.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.graalvm.buildtools.native") version "0.9.19"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("com.github.cvb941:kotlin-parallel-operations:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("com.github.handymenny.mts-asn1:mts-asn1-converter:d9687dc")
    implementation("com.github.handymenny.mts-asn1:mts-asn1-per:d9687dc")
    implementation("com.github.handymenny.mts-asn1:mts-asn1-kotlinx-json:d9687dc")
    implementation("com.github.holgerbrandl:jsonbuilder:0.10")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    implementation("org.slf4j:slf4j-nop:2.0.5")
}

group = "parser"
version = "0.0.4-alpha"
description = "uecapabilityparser"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks{
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "it.smartphonecombo.uecapabilityparser.MainCli"))
        }
        minimize {
            exclude(dependency("org.slf4j:slf4j-nop:.*"))
        }
    }
}

graalvmNative {
    binaries.all {
        resources.autodetect()
    }

    binaries {
        named("main") {
            mainClass.set("it.smartphonecombo.uecapabilityparser.MainCli")
            fallback.set(false)
            verbose.set(false)
            imageName.set("uecapabilityparser")
        }
    }
}
