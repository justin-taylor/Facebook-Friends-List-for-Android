package org.tayloredapps.facebook_ex;

import com.google.gson.annotations.SerializedName;

public class FbFriend {

	@SerializedName("name")
	public String name;
	
	@SerializedName("uid")
	public String uid;
	
	@SerializedName("pic_square")
	public String pictureUrl;
}
