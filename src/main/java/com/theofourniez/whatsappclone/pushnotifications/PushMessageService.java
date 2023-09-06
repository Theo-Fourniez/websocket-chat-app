package com.theofourniez.whatsappclone.pushnotifications;

import com.theofourniez.whatsappclone.user.ChatUser;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import nl.martijndwars.webpush.Encoding;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.concurrent.ExecutionException;

@Service
public class PushMessageService {

    @Value("${vapid.public}")
    private String publicKey;
    @Value("${vapid.private}")
    private String privateKey;

    private PushService pushService;

    @PostConstruct
    private void init() throws GeneralSecurityException {
        Security.addProvider(new BouncyCastleProvider());
        pushService = new PushService(publicKey, privateKey);
    }

    @Transactional
    public ChatUser subscribe(ChatUser user, Subscription subscription) {
        System.out.println("Subscribed " + user.getUsername() +" to " + subscription.endpoint);
        user.setPushSubscription(subscription);
        return user;
    }
    @Transactional
    public void unsubscribe(ChatUser user) {
        System.out.println("Unsubscribed " + user.getUsername());
        user.setPushSubscription(null);
    }

    public void sendNotification(Subscription subscription, PushNotification pushNotification) {
        if (subscription == null) {
            System.out.println("Subscription is null");
            return;
        }
        try {
            System.out.println("Sending notification to " + subscription.keys.auth);
            HttpResponse resp = pushService.send(new Notification(subscription, pushNotification.toJson().toString()),
                    Encoding.AES128GCM); // AES128 GCM Works on Chrome
            System.out.println("Response of push service is " + resp.getStatusLine().getStatusCode() + " " + resp.toString());
        } catch (GeneralSecurityException | IOException | JoseException | ExecutionException |
                 InterruptedException | JSONException e) {
            e.printStackTrace();
            System.out.println("Error sending notification");
        }
    }

}
