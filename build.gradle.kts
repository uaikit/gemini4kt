import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    application
    id("org.jetbrains.dokka") version "2.0.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("com.gradleup.shadow") version "9.0.1"
    id("com.github.spotbugs") version "6.2.4"
    id("com.github.jk1.dependency-license-report") version "2.9"
    id("com.diffplug.spotless") version "7.2.1"
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
    jacoco
    `maven-publish`
    signing
}

group = "io.github.takanori-ugai"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.takanori-ugai"
            artifactId = "Gemini4KT"
            version = "0.3-SNAPSHOT"
            from(components["java"])
            pom {
                name = "Gemini4KT"
                description = "A lightweight Kotlin library for the Gemini API."
                url = "https://github.com/takanori-ugai/Gemini4KT"
                properties =
                    mapOf(
                        "myProp" to "value",
                        "prop.with.dots" to "anotherValue",
                    )
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "takanori-ugai"
                        name = "Takanori Ugai"
                        email = "ugai@fujitsu.com"
                    }
                }
                scm {
                    connection = "scm:https://github.com/takanori-ugai/Gemini4KT.git"
                    developerConnection = "scm:https://github.com/takanori-ugai/Gemini4KT.git"
                    url = "https://github.com/takanori-ugai/Gemini4KT"
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://maven.pkg.jetbrains.space/fujitsu/p/main/maven")
            credentials {
                username = property("spaceUID") as String
                password = property("spacePWD") as String
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.11")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

application {
    mainClass.set("org.example.ITTestKt")
}

tasks {
    "wrapper"(Wrapper::class) {
        distributionType = Wrapper.DistributionType.ALL
    }

    withType<Jar> {
        manifest {
            attributes(mapOf("Main-Class" to application.mainClass))
        }
    }

    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
        doLast { println("Finished compiling Kotlin source code") }
    }

    compileTestKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
        doLast { println("Finished compiling Kotlin Test source code") }
    }

    compileJava {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:deprecation"))
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    compileTestJava {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:deprecation"))
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
            csv.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }

    withType<JacocoReport> {
        dependsOn("test")
        executionData(withType<Test>())
        classDirectories.setFrom(files(listOf("build/classes/kotlin/main")))
        //  sourceDirectories = files(listOf("src/main/java", "src/main/kotlin"))
        sourceDirectories.setFrom(files(listOf("src/main/java", "src/main/kotlin")))
    }

    test {
        testLogging {
//            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = true
        }
        useJUnitPlatform()
    }

    withType<Detekt>().configureEach {
        // Target version of the generated JVM bytecode. It is used for type resolution.
        jvmTarget = "11"
        reports {
            // observe findings in your browser with structure and code snippets
            html.required.set(true)
            // checkstyle like format mainly for integrations like Jenkins
            xml.required.set(true)
            // similar to the console output, contains issue signature to manually edit baseline files
            txt.required.set(true)
            // standardized SARIF format (https://sarifweb.azurewebsites.net/) to support integrations
            // with Github Code Scanning
            sarif.required.set(true)
        }
    }

    withType<ShadowJar> {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }
}

ktlint {
    setVersion("1.6.0")
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(ReporterType.CHECKSTYLE)
        reporter(ReporterType.JSON)
        reporter(ReporterType.HTML)
    }
    filter {
        exclude("**/style-violations.kt")
    }
}

detekt {
    source.from(files("src/**/kotlin"))
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    // point to your custom config defining rules to run, overwriting default behavior
    config.from(files("$projectDir/config/detekt/detekt.yml"))
//    baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
}

jacoco {
    toolVersion = "0.8.13"
}

spotbugs {
    ignoreFailures.set(true)
}

spotless {
    java {
        target("src/*/java/**/*.java")
        targetExclude("src/jte-classes/**/*.java", "jte-classes/**/*.java")
        // Use the default importOrder configuration
        importOrder()
        removeUnusedImports()

        // Choose one of these formatters.
        googleJavaFormat("1.27.0") // has its own section below
        formatAnnotations() // fixes formatting of type annotations, see below
    }
}

dokka.dokkaSourceSets {
    configureEach {
        jdkVersion.set(17)
        enableJdkDocumentationLink.set(false)
        enableKotlinStdLibDocumentationLink.set(false)
    }
}

kotlin {
    jvmToolchain(17)
}
