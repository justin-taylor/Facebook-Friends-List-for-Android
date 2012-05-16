package org.tayloredapps.facebook_ex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;

public class FbFriends {
	
	/*
	 * The purpose of this class is to take the list of friends returned from facebook
	 * and organize them into a data structure that can be used by the list activity
	 */

	HashMap<String, ArrayList<FbFriend>> friendsList;
	ArrayList<String> sortedKeys;
	
	public int totalFriends;
	
	public FbFriends( ArrayList<FbFriend> friends )
	{
		
		// init the friendsList to the size of the friends
		friendsList = new HashMap< String, ArrayList< FbFriend> >( friends.size() );
		sortedKeys = new ArrayList<String>( friends.size() );
		
		// sort friends alphabetically
		Collections.sort( friends, new FbFriendComparator() );
		
		totalFriends = 0;
		// Organize the friends into a hashmap indexed by the friends name
		Iterator<FbFriend> stepper = friends.iterator();
		while(stepper.hasNext())
		{
			FbFriend f = stepper.next();			
			String firstChar = f.name.substring(0, 1);
			
			// if this character has not been used to
			// create an arraylist create a new one
			if( ! friendsList.containsKey(firstChar) )
			{
				friendsList.put(firstChar, new ArrayList<FbFriend>() );
				sortedKeys.add(firstChar);
			}
			
			ArrayList<FbFriend> friendList = friendsList.get( firstChar );
			friendList.add( f );
			totalFriends++;
		}
	}
	
	public void printList()
	{
		for(String key : sortedKeys)
		{
			Log.e("Key ---", key);
			for(FbFriend f : friendsList.get(key))
			{
				Log.e("   ", f.name);
			}
		}
	}
	
	public FbFriend getFriend(int section, int row)
	{
		String key = sortedKeys.get(section);
		return friendsList.get(key).get(row);
	}
	
	// Private comparator used to sort list of friends alphabetically
	private class FbFriendComparator implements Comparator<FbFriend>
	{

		//@Override
		/*compares the names using the java string compare method
		 *
		 * returns 0 if the names are equal
		 * returns number less than 0 if object2.name comes after object 1
		 * returns number greater than 0 if object2.name comes after object 1
		 */
		public int compare(FbFriend object1, FbFriend object2) {
			return object1.name.compareTo( object2.name );
		}
		
	}
}
