import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.72"
}

group = "com.virtlink"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
}

dependencies {
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.2")

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.named<Test>("test") {
    useJUnitPlatform()
}

