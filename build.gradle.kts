import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.net.URI
import java.nio.file.Files
import java.util.zip.ZipInputStream


plugins {
    id("java")
//    id("org.jetbrains.intellij.platform") version "2.6.0"
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.changelog)
    alias(libs.plugins.qodana)
    alias(libs.plugins.kover)
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()


// Set the JVM language level used to build the project.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get().toInt())
    }
}
kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}




repositories {
    mavenCentral()
    maven("https://jitpack.io")
    intellijPlatform {
        defaultRepositories()
    }
}


//// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
    testImplementation(libs.junit)
    implementation(libs.org.json)


    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })
//        plugins("com.redhat.devtools.lsp4ij:0.13.0")
//        pluginVerifier()
//        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}


// Configure IntelliJ Platform Gradle Plugin - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-extension.html
intellijPlatform {
    pluginConfiguration {
        name = providers.gradleProperty("pluginName")
        version = providers.gradleProperty("pluginVersion")

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with(it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }

        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = providers.gradleProperty("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased()).withHeader(false).withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
    }
    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
        // The pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels = providers.gradleProperty("pluginVersion")
            .map { listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" }) }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }
}
// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    groups.empty()
    repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}


// Configure Gradle Kover Plugin - read more: https://github.com/Kotlin/kotlinx-kover#configuration
kover {
    reports {
        total {
            xml {
                onCheck = true
            }
        }
    }
}

fun downloadAndExtractRepoZip(
    repoZipUrl: String, targetDir: File, allowedPaths: Set<String>? = null, allowedPrefixes: List<String>? = null
) {
    println("Downloading and extracting repository ZIP from: $repoZipUrl to $targetDir")
    targetDir.deleteRecursively()
    targetDir.mkdirs()
    val zipStream = URI(repoZipUrl).toURL().openStream()
    ZipInputStream(zipStream).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            val name = entry.name.substringAfter('/')
            val isAllowed = when {
                allowedPaths == null && allowedPrefixes == null -> name.isNotEmpty()
                else -> name.isNotEmpty() && (allowedPaths?.contains(name) == true || allowedPrefixes?.any {
                    name.startsWith(
                        it
                    )
                } == true)
            }
            if (isAllowed) {
                val outFile = File(targetDir, name)
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile.mkdirs()
                    Files.copy(zip, outFile.toPath())
                }
            }
            zip.closeEntry()
            entry = zip.nextEntry
        }
    }
}

tasks {
    val dlTextMateBundle by registering(Copy::class) {
        downloadAndExtractRepoZip(
            "https://github.com/pawrequest/redscript-syntax-highlighting/archive/refs/heads/master.zip",
            file("src/main/resources/textmate"),
            allowedPaths = setOf(
                "package.json", "language-configuration.json", "syntaxes/redscript.tmLanguage.json"
            ),
            allowedPrefixes = listOf("images/")
        )
    }

    processResources {
        dependsOn(dlTextMateBundle, "generateRedscriptIdeVersion")
    }
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }

}

intellijPlatformTesting {
    runIde {
        register("runIdeForUiTests") {
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf(
                        "-Drobot-server.port=8082",
                        "-Dide.mac.message.dialogs.as.sheets=false",
                        "-Djb.privacy.policy.text=<!--999.999-->",
                        "-Djb.consents.confirmation.enabled=false",
                    )
                }
            }

            plugins {
                robotServerPlugin()
            }
        }
    }
}

tasks.register("generateRedscriptIdeVersion") {
    val outputDir = layout.buildDirectory.dir("generated/resources")
    outputs.dir(outputDir)
    doLast {
        val file = outputDir.get().file("redscript-ide-version.txt").asFile
        file.parentFile.mkdirs()
        file.writeText(libs.versions.redscriptide.get())
    }
}
sourceSets {
    main {
        resources.srcDir(layout.buildDirectory.dir("generated/resources"))
    }
}