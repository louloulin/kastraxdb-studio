package ai.magicdb.data.service.web.controller

import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.util.StreamUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * Controller for serving static resources for data service
 */
@Controller
@RequestMapping("/data-service")
class DataServiceStaticController {
    
    /**
     * Serve the main data service page
     */
    @GetMapping
    fun index(): String {
        return "forward:/data-service/index.html"
    }
    
    /**
     * Serve the data service test page
     */
    @GetMapping("/test")
    fun test(): String {
        return "forward:/data-service/test.html"
    }
    
    /**
     * Serve the data service documentation page
     */
    @GetMapping("/doc")
    fun doc(): String {
        return "forward:/data-service/doc.html"
    }
    
    /**
     * Serve static HTML files
     */
    @GetMapping("/{file}.html")
    @ResponseBody
    fun serveHtml(@PathVariable file: String): ResponseEntity<String> {
        try {
            val resource = ClassPathResource("static/data-service/${file}.html")
            val html = StreamUtils.copyToString(resource.inputStream, StandardCharsets.UTF_8)
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html)
        } catch (e: IOException) {
            return ResponseEntity.notFound().build()
        }
    }
    
    /**
     * Serve static JavaScript files
     */
    @GetMapping("/{file}.js")
    @ResponseBody
    fun serveJs(@PathVariable file: String): ResponseEntity<String> {
        try {
            val resource = ClassPathResource("static/data-service/${file}.js")
            val js = StreamUtils.copyToString(resource.inputStream, StandardCharsets.UTF_8)
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/javascript"))
                .body(js)
        } catch (e: IOException) {
            return ResponseEntity.notFound().build()
        }
    }
    
    /**
     * Serve static CSS files
     */
    @GetMapping("/{file}.css")
    @ResponseBody
    fun serveCss(@PathVariable file: String): ResponseEntity<String> {
        try {
            val resource = ClassPathResource("static/data-service/${file}.css")
            val css = StreamUtils.copyToString(resource.inputStream, StandardCharsets.UTF_8)
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/css"))
                .body(css)
        } catch (e: IOException) {
            return ResponseEntity.notFound().build()
        }
    }
    
    /**
     * Serve static JSON files
     */
    @GetMapping("/{file}.json")
    @ResponseBody
    fun serveJson(@PathVariable file: String): ResponseEntity<String> {
        try {
            val resource = ClassPathResource("static/data-service/${file}.json")
            val json = StreamUtils.copyToString(resource.inputStream, StandardCharsets.UTF_8)
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
        } catch (e: IOException) {
            return ResponseEntity.notFound().build()
        }
    }
}
