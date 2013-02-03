/**
* BigBlueButton open source conferencing system - http://www.bigbluebutton.org/
*
* Copyright (c) 2010 BigBlueButton Inc. and by respective authors (see below).
*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License as published by the Free Software
* Foundation; either version 2.1 of the License, or (at your option) any later
* version.
*
* BigBlueButton is distributed in the hope that it will be useful, but WITHOUT ANY
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License along
* with BigBlueButton; if not, see <http://www.gnu.org/licenses/>.
* 
*/

package org.bigbluebutton.conference.service.chat;
import java.util.concurrent.ConcurrentHashMap;import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatRoom {
	
	private final String name;
	private final Map<String, IChatRoomListener> listeners;
	ArrayList<ChatMessageVO> messages;
	
	public ChatRoom(String name) {
		this.name = name;
		listeners   = new ConcurrentHashMap<String, IChatRoomListener>();
		this.messages = new ArrayList<ChatMessageVO>();
	}
	
	public String getName() {
		return name;
	}
	
	public void addRoomListener(IChatRoomListener listener) {
		if (! listeners.containsKey(listener.getName())) {
			listeners.put(listener.getName(), listener);			
		}
	}
	
	public void removeRoomListener(IChatRoomListener listener) {
		listeners.remove(listener);		
	}
	
	public List<ChatMessageVO> getChatMessages(){
		return messages;
	}
	
	@SuppressWarnings("unchecked")
	public void sendMessage(ChatMessageVO chatobj){
		messages.add(chatobj);
		
		for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {

			IChatRoomListener listener = (IChatRoomListener) iter.next();

			listener.newChatMessage(chatobj);
		}
	}
		
}
