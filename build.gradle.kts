import com.google.protobuf.gradle.id

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("com.google.protobuf") version "0.9.6"
}

group = "com.bravos.recruitment"
version = "1.0.0"

repositories {
    mavenCentral()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.34.0"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.80.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
            }
        }
    }
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    api("com.google.protobuf:protobuf-java:4.34.0")
    api("io.grpc:grpc-protobuf:1.80.0")
    api("io.grpc:grpc-stub:1.80.0")
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.named<org.gradle.jvm.tasks.Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

publishing {
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            groupId = group.toString()
            artifactId = "proto-model"
            version = version.toString()
        }
    }
    repositories {
        mavenLocal()
    }
}