package com.myclay.claytest.Interfaces;

import org.json.JSONObject;

/**
 * Created by Yohandi on 15/12/2015.
 */
public interface AsyncRequestCaller {
    void requestProcessFinish(Boolean success, String message, JSONObject data);
}
