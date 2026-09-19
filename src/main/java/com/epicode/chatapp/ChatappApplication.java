package com.epicode.chatapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChatappApplication {

    public static void main(String[] args) {
        // Su Windows usa i certificati di sistema (antivirus/proxy che ispezionano il TLS rompono l'SMTP verso Gmail)
        if (System.getProperty("os.name", "").toLowerCase().contains("win")
                && System.getProperty("javax.net.ssl.trustStoreType") == null) {
            System.setProperty("javax.net.ssl.trustStoreType", "WINDOWS-ROOT");
        }
        SpringApplication.run(ChatappApplication.class, args);
    }

}
