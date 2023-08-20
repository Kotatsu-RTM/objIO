plugins {
    kotlin("jvm") version "1.9.0"
    `maven-publish`
    signing
}

group = "dev.siro256.objio"
version = "0.1.0-SNAPSHOT"

repositories {
    maven { url = uri("https://repo.siro256.dev/repository/maven-public/") }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("dev.siro256.modelio:ModelIO:0.1.0-SNAPSHOT")

    testImplementation(kotlin("test"))
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, TimeUnit.SECONDS)
}

kotlin {
    jvmToolchain(8)
}

tasks {
    create<Copy>("includeReadmeAndLicense") {
        destinationDir = File(project.buildDir, "resources/main/")

        from(project.file("LICENSE")) {
            rename { "LICENSE_${project.name}" }
        }
        from(project.file("README.md")) {
            rename { "README_${project.name}.md" }
        }

        processResources.get().finalizedBy(this)
    }

    compileKotlin {
        kotlinOptions.allWarningsAsErrors = true
    }

    withType<Jar> {
        dependsOn("includeReadmeAndLicense")
    }

    create<Jar>("sourcesJar") {
        archiveBaseName.set(project.name)
        archiveAppendix.set("")
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("sources")
        archiveExtension.set("jar")

        from(sourceSets.main.get().allSource)
    }

    test {
        useJUnitPlatform()
    }
}

publishing {
    publications {
        create<MavenPublication>("publication") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(project.components.getByName("java"))
            artifact(project.tasks.getByName("sourcesJar"))

            pom {
                name.set(artifactId)
                description.set(project.description)
                url.set("https://github.com/Kotatsu-RTM/objIO")

                licenses {
                    license {
                        name.set("The mIT License")
                        url.set("https://opensource.org/license/mit/")
                    }
                }

                developers {
                    developer {
                        id.set("Siro256")
                        name.set("Siro_256")
                        email.set("siro@siro256.dev")
                        url.set("https://github.com/Siro256")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/Kotatsu-RTM/objIO.git")
                    developerConnection.set("scm:git:ssh://github.com/Kotatsu-RTM/objIO.git")
                    url.set("https://github.com/Kotatsu-RTM/objIO")
                }
            }
        }
    }

    repositories {
        maven {
            url =
                if (version.toString().endsWith("SNAPSHOT")) {
                    uri("https://repo.siro256.dev/repository/maven-snapshots")
                } else {
                    uri("https://repo.siro256.dev/repository/maven-public")
                }
            credentials {
                username = System.getenv("RepositoryUsername")
                password = System.getenv("RepositoryPassword")
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        System.getenv("SigningKeyId"),
        System.getenv("SigningKey"),
        System.getenv("SigningKeyPassword")
    )
    sign(publishing.publications.getByName("publication"))
}
