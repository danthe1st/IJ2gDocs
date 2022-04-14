import org.jetbrains.changelog.markdownToHTML

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    // Java support
    id("java")
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.5.2"
    // gradle-changelog-plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "1.3.1"
    // Gradle Qodana Plugin
    id("org.jetbrains.qodana") version "0.1.13"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()
}
dependencies {
    implementation("com.google.api-client:google-api-client:1.34.0")
    implementation("com.google.api-client:google-api-client-java6:1.34.0")
    implementation("com.google.api-client:google-api-client-gson:1.34.0")
    implementation("com.google.apis:google-api-services-docs:v1-rev20220331-1.32.1")
    implementation("com.google.http-client:google-http-client:1.41.7")
    implementation("com.google.http-client:google-http-client-jackson2:1.41.7")
    implementation("com.google.oauth-client:google-oauth-client:1.33.2")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.33.3")
    implementation("com.google.oauth-client:google-oauth-client-java6:1.33.2")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))

    // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

// Configure gradle-changelog-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    version.set(properties("pluginVersion"))
    groups.set(emptyList())
}

qodana {
    cachePath.set(projectDir.resolve(".qodana").canonicalPath)
    reportPath.set(projectDir.resolve("build/reports/inspections").canonicalPath)
    saveReport.set(true)
    showReport.set(System.getenv("QODANA_SHOW_REPORT").toBoolean())
}

tasks {
    // Set the compatibility versions to 11
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        version.set(properties("pluginVersion"))
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))

        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        pluginDescription.set(
            File(projectDir, "README.md").readText().lines().run {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end))
            }.joinToString("\n").run { markdownToHTML(this) }
        )

        // Get the latest available change notes from the changelog file
        changeNotes.set(provider { changelog.getLatest().toHTML() })
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        dependsOn("patchChangelog")
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }
}
