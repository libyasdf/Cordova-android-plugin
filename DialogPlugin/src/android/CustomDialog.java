package org.apache.cordova.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 可爱的大白 on 2016/12/26.
 */

public class CustomDialog extends CordovaPlugin {
    @Override
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
        if(action.equals("show")){//调用show方法
            this.show(args.getString(0),callbackContext);
        } else if(action.equals("save_data")){//保存用户名密码
           this.save_data(args.getString(0),args.getString(1),args.getBoolean(2));
        } else if(action.equals("show_data")){//提取用户名密码
            this.show_data();
        }else if(action.equals("chat_video")){
			this.chat_video();
		}else {
            return false;
        }
        return super.execute(action, args, callbackContext);
    }
    /**
     * 自定义提示框
     * @param message
     * @param callbackContext
     */
    public void show(final String message ,final CallbackContext callbackContext){
        AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity());
        builder.setTitle("IMClient-提示");
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callbackContext.success("点击了确定");
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callbackContext.error("点击了取消");
            }
        });
        builder.create().show();
    }
    /**
     * 保存用户名（JID）密码
     * @param jid
     * @param password
     * @param is_remember
     */

    public void save_data(final String jid ,final String password ,final boolean is_remember){
        final Context mcontext=this.cordova.getActivity().getApplicationContext();

        SharedPreferences.Editor editor = mcontext.getSharedPreferences("save_data",MODE_PRIVATE).edit();
        if (is_remember){
            editor.putString("jid",jid);
            editor.putString("password",password);
            editor.putBoolean("remember_password",is_remember);
            editor.commit();
        }else{
            editor.clear();
        }

    }

    /**
     * 提取用户名（jid）密码：还不知道好不好使
     */
    public String[] show_data(){
        final Context mcontext=this.cordova.getActivity().getApplicationContext();

        SharedPreferences pref = mcontext.getSharedPreferences("save_data",MODE_PRIVATE);

        String jid_show = pref.getString("jid","");
        String password_show = pref.getString("password","");

        String show[] = new String[1];
        show[0] = jid_show;
        show[1] = password_show;

        return show;
    }
	
    public void chat_video(){


    }

}
