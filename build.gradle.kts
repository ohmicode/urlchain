import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.2.51"
    id("org.springframework.boot") version "2.0.2.RELEASE"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("io.spring.dependency-management") version "1.0.4.RELEASE"
}

version = "1.0.0"

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compile ("org.jetbrains.kotlin:kotlin-reflect")
    compile ("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    compile ("org.springframework.boot:spring-boot-starter-data-jpa")
    compile ("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-thymeleaf")
//    compile ("org.springframework.boot:spring-boot-devtools")

    compile ("org.postgresql:postgresql")

    testCompile ("org.springframework.boot:spring-boot-starter-test")
    testCompile ("com.h2database:h2")
}
