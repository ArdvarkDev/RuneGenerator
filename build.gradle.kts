plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:31.1-jre")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = project.extra["mainClass"] as String
    }
}

application {
    mainClass.set(project.extra["mainClass"] as String)
}

