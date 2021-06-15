plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("xyz.jpenilla.run-paper") version "1.0.1-SNAPSHOT"
}

group = "com.rose"
version = "1.3.2"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        name = "PaperMC"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "Kord Emojis"
        url = uri("https://dl.bintray.com/kordlib/Kord")
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
    compileOnly(group = "com.destroystokyo.paper", name = "paper-api", version = "1.16.5-R0.1-SNAPSHOT")
    implementation(group = "com.uchuhimo", name = "konf-yaml", version = "1.1.2")
    implementation(group = "com.gitlab.kordlib", name = "kordx.emoji", version = "0.4.0")
    implementation(
        group = "com.kotlindiscord.kord.extensions",
        name = "kord-extensions",
        version = "1.4.1-SNAPSHOT"
    )
    implementation(group = "guru.zoroark.lixy", name = "lixy-jvm", version = "master-SNAPSHOT")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "16"
            apiVersion = "1.5"
            languageVersion = "1.5"
            freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
        }
    }

    jar.configure {
        onlyIf { false }
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
        archiveVersion.set(rootProject.version.toString())
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.16.5")
    }

    clean {
        delete("build")
    }
}
