plugins {
    // https://kotlinlang.org/
    kotlin("jvm") version "1.7.21"
    // https://kotlinlang.org/docs/serialization.html
    kotlin("plugin.serialization") version "1.7.21"
    // https://github.com/johnrengelman/shadow
    id("com.github.johnrengelman.shadow") version "7.1.2"
    // https://github.com/jpenilla/run-paper
    id("xyz.jpenilla.run-paper") version "2.0.1"
    // https://github.com/jlleitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    // https://detekt.dev/
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

val version: String by project
val group: String by project

val ktlintVersion: String by project

val minecraftVersion: String by project
val paperApiRevision: String by project
val kordexVersion: String by project
val lixyVersion: String by project
val kamlVersion: String by project
val ktorVersion: String by project
val hopliteVersion: String by project

val jvmVersion: String by project
val kotlinLanguageVersion: String by project
val kotlinApiVersion: String by project

project.group = group
project.version = version

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        name = "PaperMC"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "Kotlin Discord"
        url = uri("https://maven.kotlindiscord.com/repository/maven-public/")
    }
    maven {
        name = "Jitpack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    // Paper plugin development API.
    compileOnly("io.papermc.paper:paper-api:$minecraftVersion-$paperApiRevision-SNAPSHOT")
    // Config library that provides nice parsing error descriptions.
    implementation("com.sksamuel.hoplite:hoplite-core:$hopliteVersion")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hopliteVersion")
    // Library that supports loading and (more importantly) saving to YAML files.
    implementation("com.charleskorn.kaml:kaml:$kamlVersion")
    // HTTP client library.
    // Disabled until I implement update checking.
//    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
//    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    // Library for Discord bots.
    implementation("com.kotlindiscord.kord.extensions:kord-extensions:$kordexVersion")
    // Lexer library that supports regex.
    implementation("guru.zoroark.lixy:lixy:$lixyVersion")
}

ktlint {
    version.set(ktlintVersion)
}

detekt {
    config = files("config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = jvmVersion
            apiVersion = kotlinApiVersion
            languageVersion = kotlinLanguageVersion
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
        }
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "version" to version
            )
        }
    }

    jar.configure {
        onlyIf { false }
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
        archiveVersion.set(rootProject.version.toString())
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        val minecraftVersion: String by project

        this.minecraftVersion(minecraftVersion)
    }

    detekt.configure {
        mustRunAfter(ktlintFormat)
    }

    create("runChecks") {
        dependsOn(ktlintFormat, detekt)
    }

    clean {
        delete("build")
    }
}
