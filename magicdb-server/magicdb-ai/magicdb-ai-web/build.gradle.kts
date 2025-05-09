dependencies {
    // Spring
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot-starter-web")
    
    // Jakarta
    implementation("jakarta.validation:jakarta.validation-api")
    
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    // MagicDB
    implementation(project(":magicdb-ai:magicdb-ai-api"))
    implementation(project(":magicdb-ai:magicdb-ai-core"))
    implementation(project(":magicdb-server-tools-base"))
    implementation(project(":magicdb-server-tools-common"))
    implementation(project(":magicdb-server-domain-api"))
    
    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
}
