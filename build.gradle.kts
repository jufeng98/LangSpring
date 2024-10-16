import java.net.URI

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("org.jetbrains.intellij") version "1.16.0"
}

group = "org.javamaster"
version = "1.0.0"

repositories {
    maven { url = URI("https://maven.aliyun.com/nexus/content/groups/public/") }
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation("com.jcraft:jsch:0.1.54")
    testImplementation("junit:junit:4.13.1")
}

sourceSets["main"].java.srcDirs("src/main/gen")

intellij {
    version.set("2022.1.2")
    type.set("IC")
    plugins.set(
        listOf(
            "tasks",
            "com.intellij.java",
            "com.intellij.properties",
        )
    )
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.encoding = "UTF-8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    jar {
        // kt文件不知道被哪个配置影响导致被编译了两次,所以这里暂时配置下
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    runIde {
        systemProperties["idea.auto.reload.plugins"] = true
        jvmArgs = listOf(
            "-Xms1024m",
            "-Xmx2048m",
        )
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("243.*")
    }


    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}

