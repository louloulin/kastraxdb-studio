dependencies {
    // Spring
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    
    // Jakarta
    implementation("jakarta.validation:jakarta.validation-api")
    
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    // MagicDB
    implementation(project(":magicdb-server-tools-base"))
    implementation(project(":magicdb-server-tools-common"))
}
