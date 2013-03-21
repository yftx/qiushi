package douzi.android.caodan;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigHelper {
	
	private static final String KEY = "config";
	
	public static void saveNickName(Context ctx,String nickName){
		SharedPreferences settings = ctx.getSharedPreferences(KEY, 0);
		Editor editor = settings.edit();
		editor.putString("nick", nickName);
		editor.commit();
	}
	
	public static String getNickName(Context ctx){
		SharedPreferences settings = ctx.getSharedPreferences(KEY, 0);
		return settings.getString("nick", "");
	}
	
	public static void saveContent(Context ctx,String content){
		SharedPreferences settings = ctx.getSharedPreferences(KEY, 0);
		Editor editor = settings.edit();
		editor.putString("content", content);
		editor.commit();
	}
	
	public static String getContent(Context ctx){
		SharedPreferences settings = ctx.getSharedPreferences(KEY, 0);
		return settings.getString("content", "");
	}
}
