package vti.group10.football_booking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL /uploads/** -> thư mục {projectDir}/uploads/
        String fullPath = "file:" + System.getProperty("user.dir") + "/" + uploadDir + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(fullPath);
    }
}
