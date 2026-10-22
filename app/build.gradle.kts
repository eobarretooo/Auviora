import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

plugins { id("com.android.application"); id("org.jetbrains.kotlin.plugin.compose") }
val signingNames = listOf("AUVIORA_KEYSTORE", "AUVIORA_KEY_ALIAS", "AUVIORA_STORE_PASSWORD", "AUVIORA_KEY_PASSWORD")
val signingValues = signingNames.associateWith { providers.environmentVariable(it).orNull }
val hasReleaseIdentity = signingValues.values.all { !it.isNullOrBlank() }
val unsignedCandidate = providers.gradleProperty("auviora.unsignedCandidate").orNull == "true"
val releaseSourceCommit = providers.exec { commandLine("git", "rev-parse", "HEAD") }.standardOutput.asText.map { it.trim() }
abstract class WriteReleaseProvenance : DefaultTask() {
    @get:Input abstract val sourceCommit: Property<String>
    @get:OutputDirectory abstract val outputDirectory: DirectoryProperty
    @TaskAction fun generate() {
        val destination = outputDirectory.get().file("auviora-release-source.txt").asFile
        destination.parentFile.mkdirs()
        destination.writeText(sourceCommit.get() + "\n")
    }
}
val writeReleaseProvenance = tasks.register<WriteReleaseProvenance>("writeReleaseProvenance") {
    sourceCommit.set(releaseSourceCommit)
    outputDirectory.set(layout.buildDirectory.dir("generated/release-provenance"))
}
android {
    namespace = "app.auviora.android"
    compileSdk { version = release(36) { minorApiLevel = 1 } }
    defaultConfig {
        applicationId = "app.auviora.android"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0-alpha.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk { abiFilters.add("arm64-v8a") }
    }
    buildFeatures { compose = true; buildConfig = true }
    sourceSets.getByName("main").assets.srcDir(rootProject.file("docs/legal/apk-assets"))
    signingConfigs {
        if (hasReleaseIdentity) create("release") {
            storeFile = file(signingValues.getValue("AUVIORA_KEYSTORE")!!)
            keyAlias = signingValues.getValue("AUVIORA_KEY_ALIAS")
            storePassword = signingValues.getValue("AUVIORA_STORE_PASSWORD")
            keyPassword = signingValues.getValue("AUVIORA_KEY_PASSWORD")
        }
    }
    buildTypes { release {
        isMinifyEnabled = false
        if (hasReleaseIdentity) signingConfig = signingConfigs.getByName("release")
    } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
    packaging { jniLibs { useLegacyPackaging = true } }
}
val validateReleaseSigning by tasks.registering {
    doLast {
        check(hasReleaseIdentity || unsignedCandidate) {
            "Release signing is missing. Use the interactive release script, or explicitly build a non-distributable candidate with -Pauviora.unsignedCandidate=true."
        }
        check(signingValues.values.all { it.isNullOrBlank() } || hasReleaseIdentity) {
            "Release signing environment is incomplete; all four AUVIORA signing variables are required."
        }
        check(signingValues["AUVIORA_KEY_ALIAS"]?.lowercase() != "androiddebugkey") { "Debug key alias is prohibited for release." }
    }
}
tasks.matching { it.name == "preReleaseBuild" }.configureEach { dependsOn(validateReleaseSigning) }
androidComponents.onVariants(androidComponents.selector().withBuildType("release")) { variant ->
    variant.sources.assets?.addGeneratedSourceDirectory(writeReleaseProvenance, WriteReleaseProvenance::outputDirectory)
}

tasks.register("writeReleaseDependencyInventory") {
    doLast {
        val rows = configurations.getByName("releaseRuntimeClasspath").incoming.resolutionResult.allComponents
            .mapNotNull { it.id as? org.gradle.api.artifacts.component.ModuleComponentIdentifier }
            .sortedBy { "${it.group}:${it.module}:${it.version}" }.map { id ->
                val pom = configurations.detachedConfiguration(dependencies.create("${id.group}:${id.module}:${id.version}@pom"))
                    .singleFile
                listOf(id.group, id.module, id.version, pom.absolutePath).joinToString("\t")
            }
        val output = layout.buildDirectory.file("release-audit/dependencies.tsv").get().asFile
        output.parentFile.mkdirs()
        output.writeText(rows.joinToString("\n") + "\n")
    }
}
dependencies {
    implementation(project(":core"))
    implementation(project(":inference"))
    implementation(platform("androidx.compose:compose-bom:2025.08.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
