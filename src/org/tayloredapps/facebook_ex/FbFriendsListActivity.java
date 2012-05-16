package org.tayloredapps.facebook_ex;

import org.tayloredapps.ImageLoader.ImageLoader;
import org.tayloredapps.fbfriends.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class FbFriendsListActivity extends Activity implements OnClickListener, OnItemClickListener{

	private static FbFriends _friends;
	public ImageLoader imageLoader;
	
	FbFriendsAdapter fbAdapter;
		
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fb_friends_list_activity);
        
		imageLoader = new ImageLoader(FbFriendsListActivity.this.getApplicationContext());
        
        Button button = (Button) findViewById(R.id.cancelButton);
        button.setOnClickListener( this );
        
        button = (Button) findViewById(R.id.inviteButton);
        button.setOnClickListener( this );
        
        button = (Button) findViewById(R.id.shareButton);
        button.setOnClickListener( this );
                
        ListView lv = (ListView) findViewById(R.id.friendsList);
        lv.setFastScrollEnabled( true );
        fbAdapter = new FbFriendsAdapter(this, 0);
        lv.setAdapter( fbAdapter );
        lv.setOnItemClickListener( this );
        
        _friends.printList();
	}

	public void onStop()
	{
		super.onStop();
		
		_friends = null;
		imageLoader.clearCache();
	}
	
	//@Override
	public void onClick(View v) {
		
		if( v.getId() == R.id.cancelButton )
		{
			finish();
		}
		
		else if( v.getId() == R.id.inviteButton )
		{
			shareToWall();
		}
		
		else if( v.getId() == R.id.shareButton )
		{
			postToWall();
		}
	}
	
	private void postToWall()
	{
		FacebookEx.Instance().postToWall( this );
	}
	
	private void shareToWall()
	{
		FbFriend f = fbAdapter.getSelectedFriend();
		FacebookEx.Instance().shareToWall( this, f.uid);
	}

	
	//@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		fbAdapter.setSelectedPosition( arg2 );
	}
	
	public static void setFriends(FbFriends friends)
	{
		_friends = friends;
	}
	
	public static FbFriends getFriends()
	{
		return _friends;
	}
	
	/*
	 * 
	 * FbFriendsAdapter
	 * 
	 * Takes advantage of the FbFriends structure to
	 * index the alphabetical names
	 * and to display the names in the proper section
	 * 
	 */
	
	private class FbFriendsAdapter extends ArrayAdapter<FbFriend> implements SectionIndexer
	{
		// Used to map the total number of items in each section
		int[] sectionPositions;
		int[] headerPositions;

		private int selectedPosition = -1;
		private int selectedSection = -1;
		
		public FbFriendsAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			
			sectionPositions = new int[ _friends.sortedKeys.size() ];
			headerPositions = new int[ _friends.sortedKeys.size() ];			
			int i = 0;
			int pos = 0;
			for(String key : _friends.sortedKeys)
			{
				sectionPositions[i] = _friends.friendsList.get(key).size() + 1;
				headerPositions[i] = pos;
				
				i++;
				pos += 1 + _friends.friendsList.get(key).size();
			}
			
			for(int x : headerPositions)
			{
				Log.e("Position", ""+x);
			}
		}
		
		public void setSelectedPosition( int position )
		{
			boolean isHeader = false;
			int section = 0;
			for( int i : headerPositions )
			{
				if(i == position)
				{
					isHeader = true;
					
					break;
				}

				else if( i > position)
				{
					break;
				}
				section++;
			}
			
			if(!isHeader)
			{
				selectedPosition = position;
				selectedSection = section;
			}
		}
		
		public FbFriend getSelectedFriend()
		{
			Log.e("SELECTED SECTION",""+selectedSection);
			int row = selectedPosition - headerPositions[selectedSection-1];
			return _friends.getFriend(selectedSection-1, row-1);
		}

		//@Override
		public int getPositionForSection(int section) {
			return headerPositions[section];
		}

		//@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		//@Override
		public Object[] getSections() 
		{
			return _friends.sortedKeys.toArray();
		}
		
		public int getCount() 
		{
			return _friends.totalFriends + _friends.sortedKeys.size();
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
			View v = convertView;
			
			int resource = R.layout.friend_row;
			int section = 0;
			boolean isHeader = false;
			
			//check if the position should be a header
			for( int i : headerPositions )
			{
				if(i == position)
				{
					isHeader = true;
					resource = R.layout.fb_section_header;
					break;
				}

				else if( i > position)
				{
					break;
				}
				
				section++;
			}

			
			LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate( resource, null );
			
			
			if(isHeader)
			{
				TextView tv = (TextView) v.findViewById( R.id.section_header);
				tv.setText(_friends.sortedKeys.get(section));
			}
			
			
			else
			{
				int row = position - headerPositions[section-1];
				FbFriend friend = _friends.getFriend(section-1, row-1);
				TextView tv = (TextView) v.findViewById( R.id.friendName );
				
				ImageView iv = (ImageView) v.findViewById(R.id.friendPhoto);
				iv.setTag(friend.pictureUrl);
				imageLoader.DisplayImage(friend.pictureUrl, FbFriendsListActivity.this, iv);
				
				
				tv.setText(friend.name);
				
				if(selectedPosition == position)
				{
					v.setBackgroundResource(R.drawable.blue_bar_gradient);
				}
				
				else
				{
					v.setBackgroundResource(0);
				}
			}
			
			return v;
		}
		
	}
}
