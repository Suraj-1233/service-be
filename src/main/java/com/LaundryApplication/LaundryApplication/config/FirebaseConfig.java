package com.LaundryApplication.LaundryApplication.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;

@Component
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // 🔹 First try environment variable (used in Docker/EC2)
            String keyPath = System.getenv("FIREBASE_KEY_PATH");
            InputStream serviceAccount;

            if (keyPath != null && !keyPath.isBlank()) {
                System.out.println("📁 Using Firebase key from: " + keyPath);
                serviceAccount = new FileInputStream(keyPath);
            } else {
                // 🔹 Fallback for local development
                System.out.println("📁 Using local Firebase key (src/main/resources/serviceAccountKey.json)");
                serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ FirebaseApp initialized successfully");
            } else {
                System.out.println("ℹ️ FirebaseApp already initialized, skipping re-initialization");
            }

        } catch (Exception e) {
            System.out.println("❌ Firebase initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
