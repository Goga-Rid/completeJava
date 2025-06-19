import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    jacoco
    kotlin("jvm") version "1.9.23"
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.10.2"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("com.example.demoexam")
    mainClass.set("com.example.demoexam.HelloApplication")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("org.postgresql:postgresql:42.7.2")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    implementation(kotlin("stdlib"))
}

tasks.test {
    useJUnitPlatform()
}

jlink {
    imageZip = project.file("${buildDir}/distributions/app-${javafx.platform.classifier}.zip")
    options.addAll(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["Main-Class"] = "com.example.demoexam.HelloApplication"
    }
}