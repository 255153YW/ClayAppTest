package com.myclay.claytest.Connections;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.myclay.claytest.Interfaces.AsyncRequestCaller;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yohandi on 15/12/2015.
 */
public class AsyncTaskRequest extends AsyncTask<JSONObject, Integer, Boolean> {
    private AsyncRequestCaller delegate = null;
    private String reqType = null;
    private SQLiteHelper db;
    private Context ctx;
    private JSONObject finalResult;

    public AsyncTaskRequest(AsyncRequestCaller caller, String reqType, Context callerContext){
        delegate = caller;
        this.reqType = reqType;
        this.ctx = callerContext;
    }

    @Override
    protected Boolean doInBackground(JSONObject... params) {
        this.db = new SQLiteHelper(this, ctx);

        switch (this.reqType){
            case "unlockDoor":
                this.finalResult = db.unlockDoor(params[0]);
                break;
            case "lockDoor":
                this.finalResult = db.lockDoor(params[0]);
                break;
            case "getDoorDetails":
                this.finalResult = db.getDetails(params[0]);
                break;
            case "assignDoorToUser":
                this.finalResult = db.assignDoorToUser(params[0]);
                break;
            case "revokeDoorFromUser":
                this.finalResult = db.revokeDoorFromUser(params[0]);
                break;
            case "getDoorListAtProperty":
                this.finalResult = db.getDoorListAtProperty(params[0]);
                break;
            case "getPropertyList":
                this.finalResult = db.getPropertyList(params[0]);
                break;
            case "getHistoryList":
                this.finalResult = db.getHistoryList(params[0]);
                break;
            case "getAuthorizedUserAt":
                this.finalResult = db.getAuthorizedUserAt(params[0]);
                break;
        }

        try {
            this.finalResult.put("reqType",reqType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... progress){

    }

    @Override
    protected void onPostExecute(Boolean result){
        delegate.requestProcessFinish(result,"200",finalResult);
    }

    @Override
    protected void onCancelled(){
        //do nothing
    }
}