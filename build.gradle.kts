import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.jfrog.bintray.gradle.BintrayPlugin
import com.jfrog.bintray.gradle.BintrayExtension
import java.util.Date

plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.dokka") version "0.10.1"
    id("com.jfrog.bintray") version "1.8.5"
}

repositories {
    mavenCentral()
}

val junitVersion = "5.8.1"

dependencies {
    implementation      (kotlin("stdlib-jdk8"))
    implementation      ("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.4")

    compileOnly         ("com.google.code.findbugs:jsr305:3.0.2")

    testImplementation  ("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly     ("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
    configuration {
        sourceLink {
            path = "src/main/kotlin"
            url = "https://github.com/virtlink/disjoint-map/tree/master/src/main/kotlin"
            lineSuffix = "#L"
        }
    }
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    classifier = "javadoc"
    from(tasks.dokka)
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

val githubRepo: String by project
val githubReadme: String by project

val pomUrl: String by project
val pomIssueUrl: String by project
val pomLicenseName: String by project
val pomLicenseUrl: String by project
val pomLicenseDist: String by project
val pomDeveloperId: String by project
val pomDeveloperName: String by project
val pomScmUrl: String by project
val pomScmConnection: String by project
val pomScmDevConnection: String by project

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components["java"])
            artifact(dokkaJar)
            artifact(sourcesJar)

            pom.withXml {
                asNode().apply {
                    appendNode("name", rootProject.name)
                    appendNode("description", project.description)
                    appendNode("url", pomUrl)
                    appendNode("licenses").appendNode("license").apply {
                        appendNode("name", pomLicenseName)
                        appendNode("url", pomLicenseUrl)
                        appendNode("distribution", pomLicenseDist)
                    }
                    appendNode("developers").appendNode("developer").apply {
                        appendNode("id", pomDeveloperId)
                        appendNode("name", pomDeveloperName)
                    }
                    appendNode("scm").apply {
                        appendNode("url", pomScmUrl)
                        appendNode("connection", pomScmConnection)
                        appendNode("developerConnection", pomScmDevConnection)
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/virtlink/disjoint-map")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

bintray {
    user = project.findProperty("bintrayUser").toString()
    key = project.findProperty("bintrayKey").toString()
    setPublications("lib")
    publish = true

    pkg.apply {
        name = project.name
        desc = project.description
        websiteUrl = pomUrl
        issueTrackerUrl = pomIssueUrl
        vcsUrl = pomUrl + ".git"
        githubRepo = githubRepo
        setLicenses("Apache-2.0")
        repo = project.findProperty("bintrayRepo").toString()
        publicDownloadNumbers = true
        setLabels("kotlin", "disjoint", "map", "set", "collection")

        githubReleaseNotesFile = githubReadme

        version.apply {
            name = project.version.toString()
            desc = project.description
            released = Date().toString()
            vcsTag = project.version.toString()
            gpg.apply {
                sign = true
                passphrase = project.findProperty("gpgPassphrase").toString()
            }
            mavenCentralSync.apply {
                sync = true
                user = project.findProperty("sonatypeUsername").toString()
                password = project.findProperty("sonatypePassword").toString()
            }
        }
    }
}
