plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

val junitVersion = "5.8.1"

dependencies {
    implementation      (libs.kotlinx.immutablecollections)
    compileOnly         (libs.jsr305)
    testImplementation  (libs.junit)
    testImplementation  (libs.kotest)
}