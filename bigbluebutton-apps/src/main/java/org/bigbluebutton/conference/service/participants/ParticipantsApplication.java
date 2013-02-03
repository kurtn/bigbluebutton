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
package org.bigbluebutton.conference.service.participants;


import org.red5.server.api.Red5;
import java.util.ArrayList;
import java.util.Map;
import org.bigbluebutton.conference.ConnectionInvokerService;
import org.bigbluebutton.conference.RoomsManager;
import org.bigbluebutton.conference.Room;import org.bigbluebutton.conference.User;import org.bigbluebutton.conference.IRoomListener;

public class ParticipantsApplication {

	private ConnectionInvokerService connInvokerService;
	
	private RoomsManager roomsManager;
	
	public boolean createRoom(String name) {
		if(!roomsManager.hasRoom(name)){
			
			roomsManager.addRoom(new Room(name));
			return true;
		}
		return false;
	}
	
	public boolean destroyRoom(String name) {
		if (roomsManager.hasRoom(name)) {
			
			roomsManager.removeRoom(name);
		} else {
			
		}
		return true;
	}
	
	public void destroyAllRooms() {
		roomsManager.destroyAllRooms();
	}
	
	public boolean hasRoom(String name) {
		return roomsManager.hasRoom(name);
	}
	
	public boolean addRoomListener(String room, IRoomListener listener) {
		if (roomsManager.hasRoom(room)){
			roomsManager.addRoomListener(room, listener);
			return true;
		}
		
		return false;
	}
	
	public void setParticipantStatus(String room, String userid, String status, Object value) {
		roomsManager.changeParticipantStatus(room, userid, status, value);
	}
	
	public Map getParticipants(String roomName) {
		
		if (! roomsManager.hasRoom(roomName)) {
			
			return null;
		}

		return roomsManager.getParticipants(roomName);
	}
	
	public boolean participantLeft(String roomName, String userid) {
		
		if (roomsManager.hasRoom(roomName)) {
			Room room = roomsManager.getRoom(roomName);
			
			room.removeParticipant(userid);
			return true;
		}

		return false;
	}
	
	@SuppressWarnings("unchecked")
	public boolean participantJoin(String roomName, String userid, String username, String role, String externUserID, Map status) {
		
		if (roomsManager.hasRoom(roomName)) {
			User p = new User(userid, username, role, externUserID, status);			
			Room room = roomsManager.getRoom(roomName);
			room.addParticipant(p);
			
			return true;
		}
		
		return false;
	}
	
	public ArrayList<String> getCurrentPresenter(String room){
		if (roomsManager.hasRoom(room)){
			return roomsManager.getCurrentPresenter(room);			
		}
		
		return null;
	}
	
	public void assignPresenter(String room, ArrayList presenter){
		if (roomsManager.hasRoom(room)){
			roomsManager.assignPresenter(room, presenter);
			return;
		}
		
	}
	
	public void setRoomsManager(RoomsManager r) {
		
		roomsManager = r;
	}
	
	private String getMeetingId(){
		return Red5.getConnectionLocal().getScope().getName();
	}
		
	public void setConnInvokerService(ConnectionInvokerService connInvokerService) {
		this.connInvokerService = connInvokerService;
	}
}
