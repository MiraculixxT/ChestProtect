@file:Suppress("SpellCheckingInspection")

plugins {
    kotlin("jvm") version "1.6.21"
    id("io.papermc.paperweight.userdev") version "1.3.5"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.20"
}

group = "de.miraculixx"
version = ""

sourceSets {
    main {
        java {
            srcDirs("source", "source-gen")
            exclude("de/miraculixx/mutils/modules/challenge/mods/ALPHA/**")
        }
        resources {
            srcDirs("source", "source-gen")
            exclude("**/ALPHA/**")
        }
    }
}

repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
    mavenCentral()
}

dependencies {
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
    implementation("net.axay:kspigot:1.19.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}
tasks {
    runServer {
        minecraftVersion("1.18.2")
    }
}
