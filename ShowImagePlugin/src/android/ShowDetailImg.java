package org.apache.cordova.showImage;

import android.content.Intent;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class ShowDetailImg extends CordovaPlugin{
    @Override  
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("show")) {
            String picture_adress=(String) args.get(0).toString();
            Log.i("picture_adress:",picture_adress);
            Intent intent = new Intent(cordova.getActivity().getApplication(), ShowImgActivity.class);//
            intent.putExtra("picture_adress",picture_adress);
            cordova.getActivity().startActivity(intent);//启动activity
        }
            return false;
    }
}