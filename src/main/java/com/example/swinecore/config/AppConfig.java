package com.example.swinecore.config;

import com.example.swinecore.service.DataInitializerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
public class AppConfig implements WebMvcConfigurer {

    private final String uploadDir;
    private final ManagerFarmIsolationInterceptor managerFarmIsolationInterceptor;

    public AppConfig(org.springframework.core.env.Environment env,
                     ManagerFarmIsolationInterceptor managerFarmIsolationInterceptor) {
        this.uploadDir = env.getProperty("app.upload.dir",
            System.getProperty("user.home") + "/swinecore-uploads");
        this.managerFarmIsolationInterceptor = managerFarmIsolationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(managerFarmIsolationInterceptor)
                .addPathPatterns("/manager/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files (profile pics, pig photos, farm images)
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:" + uploadDir + "/");
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    /** Seed default admin on first startup */
    @Bean
    public ApplicationRunner initData(DataInitializerService initializer) {
        return args -> initializer.init();
    }
}
