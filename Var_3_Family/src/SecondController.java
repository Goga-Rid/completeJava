plugins {
    id 'java'
    id 'application'
    id 'org.javamodularity.moduleplugin' version '1.8.12'
    id 'org.openjfx.javafxplugin' version '0.0.13'
    id 'org.beryx.jlink' version '2.25.0'
}

group 'com.example'
version '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

ext {
    junitVersion = '5.10.2'
}

sourceCompatibility = '21'
targetCompatibility = '21'

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

application {
    mainModule = 'com.example.demoexam'
    mainClass = 'com.example.demoexam.HelloApplication'
}

javafx {
    version = '23.0.1'
    modules = ['javafx.controls', 'javafx.fxml']
}

dependencies {
    implementation('org.openjfx:javafx-controls:23.0.1')
    implementation('org.openjfx:javafx-fxml:23.0.1')
    implementation('org.postgresql:postgresql:42.5.0')
    implementation('org.controlsfx:controlsfx:11.2.1')

    compileOnly 'org.projectlombok:lombok:1.18.34'
    annotationProcessor 'org.projectlombok:lombok:1.18.34'

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

test {
    useJUnitPlatform()
}

jlink {
    imageZip = project.file("${buildDir}/distributions/app-${javafx.platform.classifier}.zip")
    options = ['--strip-debug', '--compress', '2', '--no-header-files', '--no-man-pages']
    launcher {
        name = 'app'
    }
}

jlinkZip {
    group = 'distribution'
}