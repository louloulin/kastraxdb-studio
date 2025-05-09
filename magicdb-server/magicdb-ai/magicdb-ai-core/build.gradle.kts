dependencies {
    // Spring
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    
    // Jakarta
    implementation("jakarta.validation:jakarta.validation-api")
    
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    
    // MagicDB
    implementation(project(":magicdb-ai:magicdb-ai-api"))
    implementation(project(":magicdb-server-tools-base"))
    implementation(project(":magicdb-server-tools-common"))
    implementation(project(":magicdb-server-domain-api"))
    
    // Vector DB
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("redis.clients:jedis:4.3.1")
    
    // OpenAI
    implementation("com.theokanning.openai-gpt3-java:service:0.18.2")
}
