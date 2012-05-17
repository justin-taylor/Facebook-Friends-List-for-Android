package org.tayloredapps.facebook_ex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.*;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.google.gson.Gson;


public class FacebookEx{
	
	public static final String[] permissions = new String[] { "email", "read_stream", "offline_access" };
	
	public static final String PREFERENCES  	= "UserPreferences";
	public static final String ACCESSTOKENPREF 	= "FBTOKEN";
	public static final String ACCESSEXPIREPREF = "FBEXPIRATION";
	
	private static FacebookEx instance;
	private FacebookEx(){}
	
	private Activity uiActivity;
	
	private Facebook facebook;
	
	private String _appId;
	
	// Parameters to be used with the post dialogs
	private String _androidUrl;
	private String _properties;
	private String _name;
	private String _picture;
	private String _description;
	private String _message;
	private String _caption;
	private String _redirectUri;
	private String _link;
	
	
	public void setFacebookAppId(String appId)
	{
		_appId = appId;
	}
	
	public String getFacebookAppId()
	{
		return _appId;
	}
	
	public void setAndroidUrl(String androidUrl)
	{
		_androidUrl = androidUrl;
	}
	
	public void setProperties(String properties)
	{
		_properties = properties;
	}
	
	public void setName(String name)
	{
		_name = name;
	}
	
	public void setPicture(String picture)
	{
		_picture = picture;
	}
	
	public void setDescription(String description)
	{
		_description = description;
	}
	
	public void setMessage(String message)
	{
		_message = message;
	}
	
	public void setCaption(String caption)
	{
		_caption = caption;
	}
	
	public void setRedirectUrl(String redirectUri)
	{
		_redirectUri = redirectUri;
	}
	
	public void setLink(String link)
	{
		_link = link;
	}
	
	public Bundle parameters(){
	
		Bundle parameters = new Bundle();
		
		parameters.putString("app_id", _appId);
    	parameters.putString("redirect_uri", _redirectUri);
    	parameters.putString("message", _message);
    	parameters.putString("link", _link);
    	parameters.putString("caption", _caption);
    	parameters.putString("description", _description);
    	parameters.putString("name", _name);
    	parameters.putString("picture", _picture);
    	parameters.putString("properties", _properties);
		
		return parameters;
	}
	
	
	
	private void init()
	{
		facebook = new Facebook(_appId);
	}
	
	DialogListener dListener = new DialogListener(){

		//@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			
		}

		//@Override
		public void onFacebookError(FacebookError e) {
			// TODO Auto-generated method stub
			
		}

		//@Override
		public void onError(DialogError e) { }

		//@Override
		public void onCancel() { }
	};
	
	protected void postToWall(Activity activity)
	{
		facebook.dialog(activity, "feed", this.parameters(), dListener);
	}
	
	protected void shareToWall(Activity activity, String friendUid)
	{
		Bundle params = this.parameters();
		params.putString("to", friendUid);
		facebook.dialog(activity, "feed", params, dListener);
	}
	
	
	public static synchronized FacebookEx Instance()
	{
		
		if(instance == null){
			instance = new FacebookEx();
			instance.init();
		}
		
		return instance;
	}
	
	public void setActivity(Activity activity)
	{
		uiActivity = activity;
	}
	
	public void startList(Activity activity)
	{
		
		uiActivity = activity;

		if( isLoggedIn() )
			getFriendList();
		else
			facebookLogin();

	}
	

/*************************************************************************************************************************************
 * 
 *  		SESSION METHODS
 *  
 *  
 *************************************************************************************************************************************/
	
	
	private boolean isLoggedIn()
	{	
		if( facebook.isSessionValid() )
        	return true;
		else{
			
			SharedPreferences settings = uiActivity.getApplicationContext().getSharedPreferences(PREFERENCES, 0);
        	String token = settings.getString(ACCESSTOKENPREF, "");	
        	if( ! token.equalsIgnoreCase("") ){
        		facebook.setAccessToken(token);
        		return true;
        	}else
        		return false;
        }
	}
	
	public void logout()
	{
		try
		{
			facebook.logout( uiActivity.getBaseContext() );
		}
		
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		SharedPreferences settings = uiActivity.getApplicationContext().getSharedPreferences(PREFERENCES, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(ACCESSTOKENPREF, "");
		editor.putLong(ACCESSEXPIREPREF, 0);
		editor.commit();
	}
	
	private void facebookLogin()
	{
        facebook.authorize(uiActivity , permissions, Facebook.FORCE_DIALOG_AUTH,
        		new DialogListener() {
            
        	public void onComplete(Bundle values) {
        		SharedPreferences settings = uiActivity.getApplicationContext().getSharedPreferences(PREFERENCES, 0);
        		SharedPreferences.Editor editor = settings.edit();
        		
        		editor.putLong( ACCESSEXPIREPREF , facebook.getAccessExpires());
        		editor.putString( ACCESSTOKENPREF , facebook.getAccessToken());
        		editor.commit();
        		
        		getFriendList();
        	}

            public void onFacebookError(FacebookError error) { 
            	Log.e("onFacebookError", "onFacebookError");
            }

            public void onError(DialogError e) { 
            	Log.e("onError", "onError");
            }

            public void onCancel() {
            	Log.e("onError", "onError");
            }
        });
    }
	
/*************************************************************************************************************************************
 * 
 *  		Action methods
 *  
 *  
 *************************************************************************************************************************************/
	
	
	// Small class to wrap an arraylist for parsing with the Gson library
	private class FriendsArrayList
	{
		private ArrayList<FbFriend> friends;
	}
	
	private void getFriendList()
	{		
		Bundle params = new Bundle();
		
		
		params.putString("method", "fql.query");
		params.putString("query", "SELECT name,uid,pic_square FROM user WHERE uid IN ( SELECT uid2 FROM friend WHERE uid1=me() )");
		
		
		AsyncFacebookRunner runner = new AsyncFacebookRunner(this.facebook);	
		
		runner.request(params, new RequestListener(){
			public void onComplete(String response, Object state) {
				
				Gson gson = new Gson();
				FriendsArrayList friendsFromResponse = gson.fromJson( "{\"friends\":"+response+"}", FriendsArrayList.class);
				
				// create FbFriends with the friendsFromResponse
				FbFriends friends = new FbFriends( friendsFromResponse.friends );
				FbFriendsListActivity.setFriends(friends);
				
				// open new activity containing the list
				Intent intent = new Intent(uiActivity, FbFriendsListActivity.class);
				
				// add the friends to the intent
				uiActivity.startActivity( intent );
			}

			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub
				
			}

			public void onFileNotFoundException(FileNotFoundException e, Object state) {
				// TODO Auto-generated method stub
				
			}

			public void onMalformedURLException(MalformedURLException e, Object state) {
				// TODO Auto-generated method stub
				
			}

			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub
				Log.e("Facebook Error", ""+e.getLocalizedMessage());
			}
		});
	}
}

