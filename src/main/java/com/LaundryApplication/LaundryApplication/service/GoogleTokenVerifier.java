package com.LaundryApplication.LaundryApplication.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier() {
        verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                new JacksonFactory()
        )
                .setAudience(Collections.singletonList("355133834187-i5bgnaku9l1vgm0d6shl05m660smamko.apps.googleusercontent.com"))
                .build();
    }

    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = GoogleIdToken.parse(new JacksonFactory(), idTokenString);
            return verifier.verify(idToken) ? idToken.getPayload() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
