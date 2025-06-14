import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import java.net.URI


plugins {
//    `maven-publish`
    id("java") // Java support
    alias(libs.plugins.kotlin) // IntelliJ Platform Gradle Plugin
    alias(libs.plugins.intelliJPlatform) // IntelliJ Platform Gradle Plugin
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
}

// Set the JVM language level used to build the project.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get().toInt())
    }
}
kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}


group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

// BEGINS
// pawrequest custom GitHub repo/dependency adder ASSUMES REPO_URL/.../VENDOR/ASSET
val thisArtifactID = providers.gradleProperty("pluginRepositoryUrl").get().substringAfterLast("/")
val thisVendorName =
    providers.gradleProperty("pluginRepositoryUrl").get().substringBeforeLast("/").substringAfterLast("/")

val theseCustomDependencies =
    providers.gradleProperty("customDependencies").orNull // Returns null if the property is missing
        ?.split(",") // Split only if the property is present
        ?.filter { it.isNotBlank() } // Filter out empty strings
        ?: emptyList() // Provide an empty list if the property is missing

fun githubPackageUri(vendor: String = thisVendorName, artifactID: String = thisArtifactID): URI {
    return URI.create("https://maven.pkg.github.com/$vendor/$artifactID")
}

fun addRepoUri(repositoryHandler: RepositoryHandler, uri: URI) {
    repositoryHandler.maven {
        url = uri
        name = "GitHubPackages"

        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME_GITHUB")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("PUBLISH_TOKEN_GITHUB")
        }
    }
}

fun addCustomRepos(repositoryHandler: RepositoryHandler) {
    for (dep in theseCustomDependencies) {
        val depVals = dep.split(" ")
        val asset = depVals[1]
        val vendor = depVals[0]
        val repoUri = githubPackageUri(vendor, asset)
        println("Adding custom repo: $repoUri")
        addRepoUri(repositoryHandler, repoUri)
    }
}


fun addCustomDependencies(dependencyHandler: DependencyHandler) {
    for (dep in theseCustomDependencies) {
        val depVals = dep.split(" ")
        val group = depVals[2]
        val asset = depVals[1]
        val version = depVals[3]
        val dependency = "$group:$asset:$version"
        println("Adding custom dependency: $dependency")
        dependencyHandler.implementation(dependency)
    }
}


fun addPublication(publicationContainer: PublicationContainer) {
    publicationContainer.create<MavenPublication>("mavenJava") {
        from(components["java"])
        groupId = providers.gradleProperty("pluginGroup").get()
        artifactId = thisArtifactID
        version = providers.gradleProperty("pluginVersion").get()
    }
}
// pawrequest custom github repo/dependency adder ASSUMES HTTPS://REPO_URL/.../VENDOR/ASSET
// ENDS


repositories {
    mavenCentral()
//    addCustomRepos(this)
    maven("https://jitpack.io")
    intellijPlatform {
        defaultRepositories()
    }
}


//// Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
dependencies {
    testImplementation(libs.junit)
    implementation("com.github.pawrequest:github:v0.0.1")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))

        // Plugin Dependencies. Uses `platformBundledPlugins` property from the gradle.properties file for bundled IntelliJ Platform plugins.
        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

        // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file for plugin from JetBrains Marketplace.
//        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })
        plugins("com.redhat.devtools.lsp4ij:0.13.0")
        pluginVerifier()
        zipSigner()
        testFramework(TestFrameworkType.Platform)
    }
}


//publishing {
//    repositories {
//        addRepoUri(this, githubPackageUri())
//    }
//    publications {
//        addPublication(this)
//    }
//}


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
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
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
//        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
//        privateKey = providers.environmentVariable("PRIVATE_KEY")
//        certificateChain = File(System.getenv("CERTIFICATE_CHAIN") ?: "./.keys/chain.crt").readText(Charsets.UTF_8)
//        privateKey = File(System.getenv("PRIVATE_KEY") ?: "./.keys/private_64.pem").readText(Charsets.UTF_8)
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
//        privateKey = providers.environmentVariable("PRIVATE_KEY").orElse(File("./.keys/private_64.pem").readText(Charsets.UTF_8))
//        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN").orElse(File("./.keys/chain.crt").readText(Charsets.UTF_8))
//
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
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

tasks {
    val copyVscodeTextMateBundle by registering(Copy::class) {
        from("./redscript-syntax-highlighting")
        include("package.json")
        include("language-configuration.json")
        include("syntaxes/redscript.tmLanguage.json")
        include("images/**")
        into(layout.buildDirectory.dir("resources/main/textmate"))
    }

    processResources {
        dependsOn(copyVscodeTextMateBundle)
    }

    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }
    publishPlugin {
        dependsOn(patchChangelog)
    }
}
