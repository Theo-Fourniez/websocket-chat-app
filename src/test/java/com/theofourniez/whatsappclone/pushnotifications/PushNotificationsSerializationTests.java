package com.theofourniez.whatsappclone.pushnotifications;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PushNotificationsSerializationTests
{
    @Test
    public void testSerialize_FullNotification() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("test-data", "data");

        List<JSONObject> actions = new ArrayList<>();
        JSONObject action = new JSONObject();
        action.put("custom-action", "action-description");
        actions.add(action);

        ArrayList<Integer> vibrate = new ArrayList<>();
        vibrate.add(50);
        vibrate.add(100);
        vibrate.add(50);

        PushNotification notification = new PushNotification("title", "body", "testicon.png","a tag",
                vibrate ,
                data,
                actions);

        JSONObject jsonResult = notification.toJson();
        Assertions.assertEquals("{\"notification\":{\"title\":\"title\",\"body\":\"body\",\"icon\":\"testicon.png\",\"vibrate\":\"[50, 100, 50]\",\"tag\":\"a tag\",\"data\":{\"test-data\":\"data\"},\"actions\":[{\"custom-action\":\"action-description\"}]}}",jsonResult.toString());

    }

    @Test
    public void testSerialize_MinimalNotification() throws JSONException {
        PushNotification notification = new PushNotification("title", "body", "testicon.png",null, null,
                null,
                null);

        JSONObject jsonResult = notification.toJson();

        Assertions.assertEquals("{\"notification\":{\"title\":\"title\",\"body\":\"body\",\"icon\":\"testicon.png\",\"data\":{},\"actions\":[]}}"
                ,jsonResult.toString());
    }

    @Test
    public void testSerialize_NullFields() throws JSONException {
        PushNotification notification = new PushNotification(null, null,null,null,null,
                null,
                null);
        JSONObject jsonResult = notification.toJson();
        Assertions.assertEquals("{\"notification\":{\"title\":\"\",\"body\":\"\",\"icon\":\"\",\"data\":{},\"actions\":[]}}",
                jsonResult.toString());
    }

}

