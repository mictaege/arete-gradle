plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    `java-gradle-plugin`
    `maven-publish`
    signing
}

group = "com.github.mictaege"
version = "2022.2"

gradlePlugin {
    plugins {
        create("simplePlugin") {
            id = "com.github.mictaege.arete"
            implementationClass = "com.github.mictaege.arete_gradle.AretePlugin"
        }
    }
}

tasks.wrapper {
    gradleVersion = "7.5.1"
    distributionType = Wrapper.DistributionType.ALL
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.junit.platform:junit-platform-launcher:1.7.1")
    implementation("com.github.mictaege:arete:2022.2")
    implementation("org.fusesource.jansi:jansi:2.3.2")
    implementation("com.google.guava:guava:30.1.1-jre")
    implementation("org.freemarker:freemarker:2.3.31")
    implementation("net.lingala.zip4j:zip4j:2.9.0")
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
    withJavadocJar()
    withSourcesJar()
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
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = if (hasProperty("ossrhUsername")) property("ossrhUsername") as String else ""
                password = if (hasProperty("ossrhPassword")) property("ossrhPassword") as String else ""
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}