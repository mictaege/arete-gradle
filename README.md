# Arete-Gradle-Plugin

[![Maven Central](https://img.shields.io/maven-central/v/org.junit.jupiter/junit-jupiter/5.7.0.svg?color=25a162&label=Jupiter)](https://search.maven.org/search?q=g:org.junit.jupiter%20AND%20v:5.7.0)
[![Apache License 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Gradle reporting plugin for the [Arete](https://github.com/mictaege/arete) JUnit5 testing framework.

> Note that this gradle plugin is currently work in progress and under heavy development.
> So if you are using this plugin you may face unexpected behavior and bugs.
> Also significant changes in the future are very likely.

## Usage

Simply add the `arete-gradle` plugin to your `build.gradle` file.

```Groovy
plugins {
    id 'com.github.mictaege.arete-gradle' version '2021.4'
}
```

The reports generated for the `arete` specifications will be written to the `your_project\build\reports\arete\index.htlm` folder.
