import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
    kotlin("jvm") version "2.0.0"
}

group = project.group
version = project.version

repositories {
    mavenCentral()
    maven(url = "https://hub.spigotmc.org/nexus/content/groups/public/")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")

    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.16-R0.4")
    implementation ("net.dv8tion:JDA:5.0.1") {
        exclude(module = "opus-java")
    }

    implementation("ch.qos.logback:logback-classic:1.4.12")

    testImplementation("junit:junit:4.13.1")
}

tasks.withType<Jar> {
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of("17"))
    }
}