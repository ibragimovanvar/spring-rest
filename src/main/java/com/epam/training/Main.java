package com.epam.training;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

@Slf4j
public class Main {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "dev");
        System.setProperty("org.apache.catalina.startup.DEBUG", "true"); // Enable Tomcat debug
        System.setProperty("org.apache.coyote.http11.DEBUG", "true");

        String webappDirLocation = "src/main/webapp/";
        Tomcat tomcat = new Tomcat();

        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            webPort = "8080";
        }

        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(Integer.parseInt(webPort));
        connector.setProperty("address", "127.0.0.1");
        tomcat.setConnector(connector);
        tomcat.setHostname("localhost");

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", new File(webappDirLocation).getAbsolutePath());
        System.out.println("Configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

        File additionWebInfClasses = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                additionWebInfClasses.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        try {
            tomcat.start();
            log.info("Server running at http://{}:{}", "localhost", webPort);
        } catch (Exception e) {
            log.error("Failed to start Tomcat: {}", e.getMessage());
            return;
        }

        tomcat.getServer().await();
    }
}