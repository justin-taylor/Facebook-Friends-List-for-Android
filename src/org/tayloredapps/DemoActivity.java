package org.tayloredapps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.tayloredapps.facebook_ex.FacebookEx;

import com.facebook.android.R;

public class DemoActivity extends Activity {
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        FacebookEx.Instance().setActivity( this );
        FacebookEx.Instance().setFacebookAppId("170682503059245");
        
        FacebookEx.Instance().setMessage("message");
        FacebookEx.Instance().setCaption("caption");
        FacebookEx.Instance().setAndroidUrl("http://google.com");
        FacebookEx.Instance().setDescription("description");
        FacebookEx.Instance().setLink("http://google.com");
        FacebookEx.Instance().setName("nombre");
        FacebookEx.Instance().setPicture("http://www.prelovac.com/vladimir/wp-content/uploads/2008/03/example.jpg");
        FacebookEx.Instance().setProperties("{\"Become a fan\":{\"text\":\"Offical Site Fan Page\", \"href\":\"http://www.google.com/\"},\"Download for iPhone/iPad/iPod\":{\"text\":\"iTunes\", \"href\":\"http://www.google.com\"}}");
        FacebookEx.Instance().setRedirectUrl("http://www.google.com");
        
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener(){

			public void onClick(View v) {
				FacebookEx.Instance().startList( DemoActivity.this );
			}
        	
        });
        
        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener(){

			public void onClick(View v) {
				FacebookEx.Instance().logout();
			}
        	
        });
    }
}
