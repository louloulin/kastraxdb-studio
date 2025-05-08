/*
 * Build file for magicdb-server-web-start module
 */

plugins {
    id("buildlogic.java-conventions")
    id("org.springframework.boot")
}

dependencies {
    api(libs.org.springframework.boot.spring.boot.starter.web)
    api(project(":magicdb-server-web-api"))
    api(project(":magicdb-server-admin-api"))
    api(project(":magicdb-server-domain-core"))
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
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.org.freemarker.freemarker)
    testImplementation(libs.com.baomidou.mybatis.plus.generator)
}

description = "magicdb-server-web-start"
