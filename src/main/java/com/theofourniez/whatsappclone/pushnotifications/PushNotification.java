package com.theofourniez.whatsappclone.pushnotifications;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class PushNotification {
    /*
                           "notification": {
                            "title": "Message from ",
                            "body": "Newsletter Available!",
                            "icon": "assets/main-page-logo-small-hat.png",
                            "vibrate": [100, 50, 100],
                            "data": {
                                "test": "lol"
                            },
                            "actions": [{
                                "action": "explore",
                                "title": "Go to the site"
                            }]

                   */

    private String title;

    private String body;

    private String icon;

    private String tag;

    private List<Integer> vibrate;

    private JSONObject data;

    private List<JSONObject> actions;

    public PushNotification(String title, String body, String icon, String tag, List<Integer> vibrate,
                            JSONObject data,
                            List<JSONObject> actions) {
        this.title = Objects.requireNonNullElseGet(title, String::new);
        this.body = Objects.requireNonNullElseGet(body, String::new);
        this.icon = Objects.requireNonNullElseGet(icon, String::new);
        this.tag = Objects.requireNonNullElseGet(tag, String::new);
        this.vibrate = Objects.requireNonNullElseGet(vibrate, ArrayList::new);
        this.data = Objects.requireNonNullElseGet(data, JSONObject::new);
        this.actions = Objects.requireNonNullElseGet(actions, ArrayList::new);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject notification = new JSONObject();

        JSONObject inner = new JSONObject();
        inner.put("title", title);
        inner.put("body", body);
        inner.put("icon", icon);
        if(!vibrate.isEmpty()){
            inner.put("vibrate", vibrate);
        }

        if(!tag.isEmpty()){
            inner.put("tag", tag);
        }

        inner.put("data", data);
        inner.put("actions", new JSONArray(actions));

        notification.put("notification", inner);
        return notification;
    }
}
