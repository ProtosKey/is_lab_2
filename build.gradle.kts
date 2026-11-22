plugins {
    java
    war
}

group = "dev.protoskey.cityapp"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

val lombokVersion = "1.18.48"

dependencies {
    compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    compileOnly("org.eclipse.persistence:org.eclipse.persistence.jpa:4.0.4")
    runtimeOnly("org.postgresql:postgresql:42.7.14")

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

tasks.test {
    useJUnitPlatform()
}