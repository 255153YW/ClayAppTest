package com.myclay.claytest.Interfaces;

import org.json.JSONObject;

/**
 * Created by Yohandi on 16/12/2015.
 */
public interface UserManager {
    void assignDoorToUser (JSONObject data);
    void revokeDoorFromUser (JSONObject data);
}
