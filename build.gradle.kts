import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
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

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
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
        minimize()
    }
}

graalvmNative {
    binaries {
        named("main") {
            mainClass.set("it.smartphonecombo.uecapabilityparser.MainCli")

            fallback.set(false)
            verbose.set(true)

            buildArgs.add("--initialize-at-build-time=kotlin")

            buildArgs.add("-H:+InstallExitHandlers")
            buildArgs.add("-H:+ReportUnsupportedElementsAtRuntime")
            buildArgs.add("-H:+ReportExceptionStackTraces")


            imageName.set("graal-main")
        }
    }
}


