/*
 * Build file for magicdb-server-web-start module
 */

plugins {
    id("buildlogic.java-conventions")
    id("org.springframework.boot")
    id("io.freefair.lombok")
}

dependencies {
    api(libs.org.springframework.boot.spring.boot.starter.web)
    api(project(":magicdb-server-web-api"))
    api(project(":magicdb-server-admin-api"))
    api(project(":magicdb-server-domain-core"))
    api(project(":magicdb-script-runtime"))
    api(project(":magicdb-data-service-web"))
    api(libs.org.slf4j.jcl.over.slf4j)
    api(libs.org.slf4j.log4j.over.slf4j)
    api(libs.ch.qos.logback.logback.classic)
    api(libs.com.h2database.h2)
    api(libs.org.flywaydb.flyway.core)
    api(libs.org.flywaydb.flyway.mysql)
    api(libs.cn.dev33.sa.token.spring.boot3.starter)
    api(libs.cn.dev33.sa.token.jwt)
    api(libs.org.springframework.boot.spring.boot.starter.thymeleaf)
    api(libs.com.dtflys.forest.forest.spring)
    api(libs.com.dtflys.forest.forest.core)
    api(libs.org.zalando.logbook.spring.boot.starter)
    api(libs.com.baomidou.mybatis.plus.boot.starter)
    api(libs.org.mybatis.spring.mybatis.spring)
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
        "-Amapstruct.disableBuilders=true",
        "-Amapstruct.verbose=true",
        "-Amapstruct.suppressGeneratorTimestamp=true",
        "-Amapstruct.suppressGeneratorVersionInfoComment=true",
        "-Amapstruct.unmappedTargetPolicy=IGNORE",
        "-Amapstruct.defaultInjectionStrategy=constructor",
        "-Amapstruct.annotationProcessorPathOptions=true"
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

description = "magicdb-server-web-start"
