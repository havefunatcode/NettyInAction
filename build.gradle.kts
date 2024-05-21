plugins {
    id("java")
}

group = "book.netty"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.netty:netty-all:4.1.108.Final")
    implementation("com.google.protobuf:protobuf-java:4.26.1")
}

tasks.test {
    useJUnitPlatform()
}
