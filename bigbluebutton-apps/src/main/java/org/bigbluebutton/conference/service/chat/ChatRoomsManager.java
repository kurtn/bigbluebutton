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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomsManager {

	private final Map <String, ChatRoom> rooms;
	
	public ChatRoomsManager() {

		rooms = new ConcurrentHashMap<String, ChatRoom>();
	}
	
	public void addRoom(ChatRoom room) {

		rooms.put(room.getName(), room);
	}
	
	public void removeRoom(String name) {

		rooms.remove(name);
	}
		
	public boolean hasRoom(String name) {

		return rooms.containsKey(name);
	}
	
	
	/**
	 * Keeping getRoom private so that all access to ChatRoom goes through here.
	 */
	private ChatRoom getRoom(String name) {

		return rooms.get(name);
	}
	
	public List<ChatMessageVO> getChatMessages(String room) {
		ChatRoom r = getRoom(room);
		if (r != null) {
			return r.getChatMessages();
		}
		
		return null;
	}
	
	public void sendMessage(String room, ChatMessageVO chatobj) {
		ChatRoom r = getRoom(room);
		if (r != null) {
			r.sendMessage(chatobj);
		} else {
			
		}
	} 
	
	public void addRoomListener(String roomName, IChatRoomListener listener) {
		ChatRoom r = getRoom(roomName);
		if (r != null) {
			r.addRoomListener(listener);
			return;
		}
		
	}
	
	//TODO: roomName?	
//	public void removeRoomListener(IChatRoomListener listener) {
//		ChatRoom r = getRoom(roomName);
//		if (r != null) {
//			r.removeRoomListener(listener);
//			return;
//		}	
//		log.warn("Removing listener from a non-existing room ${roomName}");
//	}
	
}
