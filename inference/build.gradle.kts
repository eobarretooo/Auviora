import java.util.Properties
plugins { id("com.android.library") }
android {
    namespace = "app.auviora.inference"
    compileSdk { version = release(36) { minorApiLevel = 1 } }
    ndkVersion = "27.0.12077973"
    defaultConfig {
        minSdk = 24
        ndk { abiFilters.add("arm64-v8a") }
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild { cmake {
            targets += "auviora_inference"
            arguments += listOf("-DCMAKE_BUILD_TYPE=Release", "-DBUILD_SHARED_LIBS=ON", "-DLLAMA_BUILD_COMMON=ON", "-DLLAMA_BUILD_TOOLS=ON", "-DLLAMA_OPENSSL=OFF", "-DGGML_NATIVE=OFF", "-DGGML_LLAMAFILE=ON", "-DLLAMA_CURL=OFF")
        } }
    }
    externalNativeBuild { cmake { path = file("src/main/cpp/CMakeLists.txt"); version = "4.1.2" } }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
}
dependencies { api(project(":core")) }
fun runCmd(vararg args: String) {
    val proc = ProcessBuilder(*args).redirectErrorStream(true).start()
    proc.inputStream.bufferedReader().useLines { lines -> lines.forEach { logger.lifecycle(it) } }
    val rc = proc.waitFor()
    if (rc != 0) error("Command failed (rc=$rc): ${args.joinToString(" ")}")
}

val sdkRoot: String = System.getenv("ANDROID_HOME")?.takeIf { it.isNotBlank() }
    ?: System.getenv("ANDROID_SDK_ROOT")?.takeIf { it.isNotBlank() }
    ?: rootProject.file("local.properties").takeIf { it.exists() }?.let { propertiesFile ->
        Properties().apply { propertiesFile.inputStream().use { load(it) } }.getProperty("sdk.dir")
    }
    ?: error("Cannot locate Android SDK — set ANDROID_HOME or local.properties")

tasks.register("buildGgmlCpu_v86") {
    group = "native"
    description = "Build libggml-cpu optimised for armv8.6-a+i8mm+bf16"

    val destSo = file("src/main/jniLibs/arm64-v8a/libggml-cpu-v86.so")
    val ompSo = file("src/main/jniLibs/arm64-v8a/libomp.so")
    outputs.file(destSo)
    outputs.file(ompSo)
    inputs.dir(rootProject.file("vendor/llama.cpp-omni/ggml"))
    inputs.file(rootProject.file("vendor/llama.cpp-omni/CMakeLists.txt"))
    inputs.dir(file("src/main/cpp"))
    inputs.dir(rootProject.file("patches/llama.cpp-omni"))
    inputs.file(rootProject.file("scripts/apply-native-patches.cmake"))
    inputs.file(project.buildFile)

    doLast {
        val executableSuffix = if (System.getProperty("os.name").startsWith("Windows")) ".exe" else ""
        val cmake = "$sdkRoot/cmake/4.1.2/bin/cmake$executableSuffix"
        val ninja = "$sdkRoot/cmake/4.1.2/bin/ninja$executableSuffix"
        val toolchain = "$sdkRoot/ndk/27.0.12077973/build/cmake/android.toolchain.cmake"
        val bd = File(project.layout.buildDirectory.asFile.get(), "v86-cmake/arm64-v8a")
        bd.mkdirs()

        runCmd(
            cmake,
            "-G", "Ninja",
            "-DCMAKE_MAKE_PROGRAM=$ninja",
            "-DCMAKE_TOOLCHAIN_FILE=$toolchain",
            "-DANDROID_ABI=arm64-v8a",
            "-DANDROID_PLATFORM=android-24",
            "-DCMAKE_BUILD_TYPE=Release",
            "-DBUILD_SHARED_LIBS=ON",
            "-DLLAMA_BUILD_COMMON=ON",
            "-DLLAMA_OPENSSL=OFF",
            "-DLLAMA_CURL=OFF",
            "-DLLAMA_BUILD_TOOLS=ON",
            "-DGGML_NATIVE=OFF",
            "-DGGML_LLAMAFILE=ON",
            "-DGGML_CPU_ARM_ARCH=armv8.6-a+dotprod+i8mm+fp16+bf16",
            "-S", file("src/main/cpp").absolutePath,
            "-B", bd.absolutePath,
        )

        runCmd(
            cmake,
            "--build", bd.absolutePath,
            "--target", "ggml-cpu",
            "-j", Runtime.getRuntime().availableProcessors().toString(),
        )

        val builtSo = fileTree(bd).matching { include("**/libggml-cpu.so") }.singleFile
        destSo.parentFile.mkdirs()
        builtSo.copyTo(destSo, overwrite = true)
        val hostTag = when {
            System.getProperty("os.name").startsWith("Windows") -> "windows-x86_64"
            System.getProperty("os.name").startsWith("Mac") -> "darwin-x86_64"
            else -> "linux-x86_64"
        }
        // OpenMP is an imported NDK runtime and must accompany both CPU variants.
        file("$sdkRoot/ndk/27.0.12077973/toolchains/llvm/prebuilt/$hostTag/lib/clang/18/lib/linux/aarch64/libomp.so").copyTo(ompSo, overwrite = true)
        logger.lifecycle("Copied v86 ggml-cpu -> ${destSo.absolutePath} (${destSo.length() / 1024}K)")
    }
}

afterEvaluate {
    listOf("Debug", "Release").forEach { buildType ->
        tasks.findByName("merge${buildType}JniLibFolders")?.dependsOn("buildGgmlCpu_v86")
    }
}
