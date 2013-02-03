/**
 * BigBlueButton open source conferencing system - http://www.bigbluebutton.org/
 *
 * Copyright (c) 2012 BigBlueButton Inc. and by respective authors (see below).
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
 * Author: Felipe Cecagno <felipe@mconf.org>
 */
package org.bigbluebutton.conference.service.layout;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LayoutRoomsManager {

	private final Map <String, LayoutRoom> rooms;
	
	public LayoutRoomsManager() {
		
		rooms = new ConcurrentHashMap<String, LayoutRoom>();
	}
	
	public void addRoom(LayoutRoom room) {
		
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
	private LayoutRoom getRoom(String name) {
		
		return rooms.get(name);
	}
	
	public void addRoomListener(String roomName, ILayoutRoomListener listener) {
		LayoutRoom r = getRoom(roomName);
		if (r != null) {
			r.addRoomListener(listener);
			return;
		}
		
	}
	
	public void removeRoomListener(String roomName, ILayoutRoomListener listener) {
		LayoutRoom r = getRoom(roomName);
		if (r != null) {
			r.removeRoomListener(listener);
			return;
		}	
		
	}
	
	public void lockLayout(String room, String userId, String layout) {
		LayoutRoom r = getRoom(room);
		if (r != null) {
			r.lockLayout(userId, layout);
		} else {
			
		}
	} 

	public void unlockLayout(String room) {
		LayoutRoom r = getRoom(room);
		if (r != null) {
			r.unlockLayout();
		} else {
			
		}
	}

	public List<Object> currentLayout(String room) {
		LayoutRoom r = getRoom(room);
		if (r != null) {
			return r.currentLayout();
		} else {
			
			return null;
		}
	}
}
