import org.jreleaser.model.Active
import org.jreleaser.model.Signing

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "2.0.0"
    signing
    id("org.jreleaser") version "1.22.0"
}

group = "io.github.mictaege"
version = "2026.1"

gradlePlugin {
    website.set("https://github.com/mictaege/arete-gradle")
    vcsUrl.set("https://github.com/mictaege/arete-gradle.git")
    plugins {
        create("aretePlugin") {
            id = "io.github.mictaege.arete"
            displayName = "Arete Plugin"
            description = "Gradle reporting plugin for the Arete JUnit6 testing framework."
            implementationClass = "com.github.mictaege.arete_gradle.AretePlugin"
            tags.set(listOf("testing", "junit6", "bdd", "agile"))
        }
    }
}

tasks.wrapper {
    gradleVersion = "8.7"
    distributionType = Wrapper.DistributionType.ALL
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.junit.platform:junit-platform-launcher:6.0.2")
    implementation("io.github.mictaege:arete:2026.1")
    implementation("org.fusesource.jansi:jansi:2.4.2")
    implementation("com.google.guava:guava:33.5.0-jre")
    implementation("org.freemarker:freemarker:2.3.34")
    implementation("net.sourceforge.plantuml:plantuml-mit:1.2025.10")
    implementation("org.commonmark:commonmark:0.27.0")
}

tasks.register("generateResources") {
    val propFile = file("$buildDir/generated/arete-gradle.properties")
    outputs.file(propFile)
    doLast {
        mkdir(propFile.parentFile)
        propFile.writeText("version=${project.version}")
    }
}

tasks.processResources {
    from(files(tasks.getByName("generateResources")))
}

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
    }
}
tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("arete-gradle")
                description.set("This repository contains the Arete Gradle Plugin.")
                url.set("https://github.com/mictaege/arete-gradle")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("mictaege")
                        name.set("Michael Taege")
                        email.set("mictaege@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/mictaege/arete-gradle.git")
                    url.set("https://github.com/mictaege/arete-gradle")
                }
            }
        }
    }
    repositories {
        maven {
            name = "staging"
            url = uri(layout.buildDirectory.dir("staging").get().asFile.toURI())
        }
    }
}

jreleaser {
    project {
        copyright.set("Michael Taege")
        description.set("This repository contains the Arete Gradle Plugin.")
    }
    signing {
        active.set(Active.ALWAYS)
        armored.set(true)
        checksums.set(true)
        mode.set(Signing.Mode.FILE)
        passphrase.set(if (hasProperty("centralPortalKeyPwd")) property("centralPortalKeyPwd") as String else "")
        publicKey.set(if (hasProperty("centralPortalPublicKey")) property("centralPortalPublicKey") as String else "")
        secretKey.set(if (hasProperty("centralPortalSecretKey")) property("centralPortalSecretKey") as String else "")
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active.set(Active.ALWAYS)
                    url = "https://central.sonatype.com/api/v1/publisher"
                    username.set(if (hasProperty("centralPortalUsr")) property("centralPortalUsr") as String else "")
                    password.set(if (hasProperty("centralPortalPwd")) property("centralPortalPwd") as String else "")
                    stagingRepository(layout.buildDirectory.dir("staging").get().asFile.path)
                }
            }
        }
    }
    release {
        github {
            enabled.set(false)
        }
    }
}
