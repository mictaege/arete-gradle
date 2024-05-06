# Arete-Gradle-Plugin

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.mictaege.arete)](https://plugins.gradle.org/plugin/io.github.mictaege.arete)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mictaege/arete-gradle.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.mictaege%22%20AND%20a:%22arete-gradle%22)
[![Maven Central](https://img.shields.io/maven-central/v/org.junit.jupiter/junit-jupiter/5.10.2.svg?color=25a162&label=Jupiter)](https://central.sonatype.com/artifact/org.junit.jupiter/junit-jupiter/5.10.2)
[![Apache License 2.0](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Gradle reporting plugin for the [Arete](https://github.com/mictaege/arete) JUnit5 testing framework.

![Report](Report.png)

## Usage

Add the `arete-gradle` plugin to your `build.gradle` file.

```Groovy
plugins {
    id 'java'
    id 'io.github.mictaege.arete' version '20xx.x.x'
}
```

## Reports

The reports generated for the `arete` specifications will be written to the `<your_project>/build/reports/arete/<task>/index.html` folder.
This is a parallel structure to the gradle test reporting structure `<your_project>/build/reports/test/<task>/index.html`.


## Screenshot-Taking

```Java
@Spec class WebshopSpec {

    private WebDriver webDriver;
    
    @RegisterExtension
    public ScreenshotExtension screenshots 
            = new ScreenshotExtension(new MySeleniumScreenshotTaker(webDriver));
    
    //...

}
```

```Java
public class MySeleniumScreenshotTaker implements ScreenshotTaker {

    private WebDriver webDriver;
    
    public MySeleniumScreenshotTaker(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
    
    @Override
    public Set<TestResult> takeWhen() {
        return Stream.of(SUCCESS, FAILURE).collect(toSet());
    }

    @Override
    public byte[] getImageBytes() {
        //Read PNG image bytes from web driver
    }

}
```


