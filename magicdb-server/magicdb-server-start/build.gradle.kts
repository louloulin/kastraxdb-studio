/*
 * Build file for magicdb-server-start module
 */

plugins {
    id("buildlogic.java-conventions")
    id("org.springframework.boot")
    application
    kotlin("jvm")
    kotlin("plugin.spring")
    id("io.freefair.lombok")
}

application {
    mainClass.set("ai.magicdb.server.start.Application")
}

dependencies {
    api(libs.org.springframework.boot.spring.boot.starter.web)
    api(project(":magicdb-server-web-api"))
    api(project(":magicdb-server-domain-core"))
    api(libs.org.slf4j.jcl.over.slf4j)
    api(libs.org.slf4j.log4j.over.slf4j)
    api(libs.ch.qos.logback.logback.classic)
    api(libs.com.h2database.h2)
    api(libs.org.flywaydb.flyway.core)
    api(libs.org.flywaydb.flyway.mysql)
    api(libs.org.springframework.boot.spring.boot.starter.thymeleaf)
    api(libs.com.dtflys.forest.forest.spring)
    api(libs.com.dtflys.forest.forest.core)
    api(libs.org.zalando.logbook.spring.boot.starter)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.org.freemarker.freemarker)
    testImplementation(libs.com.baomidou.mybatis.plus.generator)

    // MapStruct and Lombok
    api(libs.org.mapstruct.mapstruct)

    // Lombok for Java
    compileOnly("org.projectlombok:lombok:1.18.30")

    // Annotation processors for Java - order matters for MapStruct and Lombok integration
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

// Configure Java annotation processor options
tasks.withType<JavaCompile>().configureEach {
    options.isIncremental = false
    options.compilerArgs.addAll(listOf(
        "-Amapstruct.defaultComponentModel=spring",
        "-Amapstruct.disableBuilders=true"
    ))
}

// Exclude conflicting SLF4J implementations
configurations.all {
    exclude(group = "org.slf4j", module = "slf4j-simple")
}

// Configure Lombok
lombok {
    version.set("1.18.30")
}

description = "magicdb-server-start"
