/** 
* ===License Header===
*
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
* ===License Header===
*/
package org.bigbluebutton.conference.service.presentation;


import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


import java.util.concurrent.ConcurrentHashMap;

public class PresentationRoomsManager {
	
	private final Map <String, PresentationRoom> rooms;
	
	public PresentationRoomsManager() {
	
		rooms = new ConcurrentHashMap<String, PresentationRoom>();
	}
	
	public void addRoom(PresentationRoom room) {
		
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
	private PresentationRoom getRoom(String name) {
		
		return rooms.get(name);
	}
		
	public void addRoomListener(String roomName, IPresentationRoomListener listener) {
		PresentationRoom r = getRoom(roomName);
		if (r != null) {
			r.addRoomListener(listener);
			return;
		}
		
	}
	//TODO: where is roomName???
	/*public void removeRoomListener(IPresentationRoomListener listener) {
		PresentationRoom r = getRoom(roomName);
		if (r != null) {
			r.removeRoomListener(listener);
			return;
		}	
		log.warn("Removing listener from a non-existing room ${roomName}");
	}*/
	
	public void sendUpdateMessage(Map message){
		String room = (String) message.get("room");
		PresentationRoom r = getRoom(room);
		if (r != null) {
			r.sendUpdateMessage(message);
			return;
		}	
			
	}
		
	public Boolean getSharingPresentation(String room){
		PresentationRoom r = getRoom(room);
		if (r != null) {
			return r.getSharing();			
		}	
		
		return null;
	}
		
	@SuppressWarnings("unchecked")
	public Map getPresenterSettings(String room){
		PresentationRoom r = getRoom(room);
		if (r != null){
			Map settings = new HashMap();
			settings.put("xOffset", r.getxOffset());
			settings.put("yOffset", r.getyOffset());
			settings.put("widthRatio", r.getWidthRatio());
			settings.put("heightRatio", r.getHeightRatio());
			return settings;			
		}
		
		return null;
	}
	
	public void sendCursorUpdate(String room, Double xPercent, Double yPercent) {
		PresentationRoom r = getRoom(room);
		if (r != null){
			
			r.sendCursorUpdate(xPercent, yPercent);
			return;
		}
		
	}
	
	public void resizeAndMoveSlide(String room, Double xOffset, Double yOffset, Double widthRatio, Double heightRatio) {
		PresentationRoom r = getRoom(room);
		if (r != null){
			
			r.resizeAndMoveSlide(xOffset, yOffset, widthRatio, heightRatio);
			return;
		}
			
	}
	
	public void gotoSlide(String room, int slide){
		PresentationRoom r = getRoom(room);
		if (r != null) {
			
			r.gotoSlide(slide);
			return;
		}	
		
	}
	
	public void sharePresentation(String room, String presentationName, Boolean share){
		PresentationRoom r = getRoom(room);
		if (r != null) {
			
			r.sharePresentation(presentationName, share);
			return;
		}	
		
	}
	
	public int getCurrentSlide(String room){
		PresentationRoom r = getRoom(room);
		if (r != null) {
			return r.getCurrentSlide();
		}	
		
		return -1;
	}
	
	public String getCurrentPresentation(String room){
		PresentationRoom r = getRoom(room);
		if (r != null) {
			return r.getCurrentPresentation();
		}	
		
		return null;
	}
	
	public ArrayList<String> getPresentations(String room){
        PresentationRoom r = getRoom(room);
        if (r != null) {
            return r.getPresentationNames();
        }   
        
        return null;
    }
    
    public void removePresentation (String room, String name){
        PresentationRoom r = getRoom(room);
        if (r != null) {
            r.removePresentation(name);
        } else {  
        	
        }
    }

}
