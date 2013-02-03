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


import org.bigbluebutton.conference.ClientMessage;
import org.bigbluebutton.conference.ConnectionInvokerService;

import org.red5.server.api.Red5;import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PresentationApplication {

		
	private PresentationRoomsManager roomsManager;
	private ConnectionInvokerService connInvokerService;
	
	public boolean createRoom(String name) {
		roomsManager.addRoom(new PresentationRoom(name));
		return true;
	}
	
	public boolean destroyRoom(String name) {
		if (roomsManager.hasRoom(name)) {
			roomsManager.removeRoom(name);
		}
		return true;
	}
	
	public boolean hasRoom(String name) {
		return roomsManager.hasRoom(name);
	}
	
	public boolean addRoomListener(String room, IPresentationRoomListener listener) {
		if (roomsManager.hasRoom(room)){
			roomsManager.addRoomListener(room, listener);
			return true;
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void sendUpdateMessage(Map message){
	
		String room = (String) message.get("room");
		if (roomsManager.hasRoom(room)){
			roomsManager.sendUpdateMessage(message);
			return;
		}
		
	}
		
	public ArrayList<String> getPresentations(String room){
	   if (roomsManager.hasRoom(room)){
            return roomsManager.getPresentations(room);           
        }
        
        return null;
	}
	
	public void removePresentation(String room, String name){
       if (roomsManager.hasRoom(room)){
            roomsManager.removePresentation(room, name);           
        } else {
        	
        }
    }
	
	public int getCurrentSlide(String room){
		if (roomsManager.hasRoom(room)){
			return roomsManager.getCurrentSlide(room);			
		}
		
		return -1;
	}
	
	public String getCurrentPresentation(String room){
		if (roomsManager.hasRoom(room)){
			return roomsManager.getCurrentPresentation(room);			
		}
		
		return null;
	}
	
	public Map getPresenterSettings(String room){
		if (roomsManager.hasRoom(room)){
			return roomsManager.getPresenterSettings(room);			
		}
		
		return null;
	}
	
	public Boolean getSharingPresentation(String room){
		if (roomsManager.hasRoom(room)){
			return roomsManager.getSharingPresentation(room);			
		}
		
		return null;
	}
	
	public void sendCursorUpdate(String room, Double xPercent, Double yPercent) {	
		if (roomsManager.hasRoom(room)){
			
			roomsManager.sendCursorUpdate(room, xPercent, yPercent);
			
			Map<String, Object> message = new HashMap<String, Object>();	
			message.put("xPercent", xPercent);
			message.put("yPercent", yPercent);
			ClientMessage m = new ClientMessage(ClientMessage.BROADCAST, getMeetingId(), "PresentationCursorUpdateCommand", message);
			connInvokerService.sendMessage(m);
			
			return;
		}
				
		
	}
	
	public void resizeAndMoveSlide(String room, Double xOffset, Double yOffset, Double widthRatio, Double heightRatio) {
		if (roomsManager.hasRoom(room)){
			
			roomsManager.resizeAndMoveSlide(room, xOffset, yOffset, widthRatio, heightRatio);
			return;
		}
			
	}
		
	public void gotoSlide(String room, int slide){
		if (roomsManager.hasRoom(room)){
			
			roomsManager.gotoSlide(room, slide);
			return;
		}
		
	}
	
	public void sharePresentation(String room, String presentationName, Boolean share){
		if (roomsManager.hasRoom(room)){
			
			roomsManager.sharePresentation(room, presentationName, share);
			return;
		}
		
	}
	
	public void setRoomsManager(PresentationRoomsManager r) {
		
		roomsManager = r;
		
	}

	private String getMeetingId(){
		return Red5.getConnectionLocal().getScope().getName();
	}
	
	
	public void setConnInvokerService(ConnectionInvokerService connInvokerService) {
		this.connInvokerService = connInvokerService;
	}	
}
