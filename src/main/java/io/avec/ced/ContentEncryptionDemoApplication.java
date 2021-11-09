package io.avec.ced;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@Theme(value = "myapp")
@PWA(name = "My App", shortName = "My App", offlineResources = {"images/logo.png"}, offlinePath = "offline.html")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@NpmPackage(value = "leaflet", version = "^1.7.1")
@NpmPackage(value = "@types/leaflet", version = "^1.5.23")
public class ContentEncryptionDemoApplication extends SpringBootServletInitializer implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(ContentEncryptionDemoApplication.class, args);
    }

}
