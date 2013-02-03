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
package org.bigbluebutton.conference;


import org.bigbluebutton.conference.service.messaging.MessageListener;
import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.service.messaging.MessagingService;
import org.bigbluebutton.conference.service.presentation.ConversionUpdatesMessageListener;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RoomsManager {

	private final Map <String, Room> rooms;

	MessagingService messagingService;
	ConversionUpdatesMessageListener conversionUpdatesMessageListener;
	
	public RoomsManager() {
		rooms = new ConcurrentHashMap<String, Room>();		
	}
	
	public void addRoom(Room room) {
		
		room.addRoomListener(new ParticipantUpdatingRoomListener(room,messagingService)); 	
		
		if (checkPublisher()) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("meetingId", room.getName());
			map.put("messageId", MessagingConstants.MEETING_STARTED_EVENT);
			
			Gson gson = new Gson();
			messagingService.send(MessagingConstants.SYSTEM_CHANNEL, gson.toJson(map));
			
			
		}
		rooms.put(room.getName(), room);
	}
	
	public void removeRoom(String name) {
	
		Room room = rooms.remove(name);
		if (checkPublisher() && room != null) {
			room.endAndKickAll();
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("meetingId", room.getName());
			map.put("messageId", MessagingConstants.MEETING_ENDED_EVENT);
			
			Gson gson = new Gson();
			messagingService.send(MessagingConstants.SYSTEM_CHANNEL, gson.toJson(map));
			
			
		}
	}

	public void destroyAllRooms() {
		for (Map.Entry<String,Room> entry : rooms.entrySet()) {
		    Room room = entry.getValue();
		    room.endAndKickAll();
		}
	}
	
	private boolean checkPublisher() {
		return messagingService != null;
	}

		
	public boolean hasRoom(String name) {
		return rooms.containsKey(name);
	}
	
	public int numberOfRooms() {
		return rooms.size();
	}
	
	/**
	 * Keeping getRoom private so that all access to Room goes through here.
	 */
	//TODO: this method becomes public for ParticipantsApplication, ask if it's right? 
	public Room getRoom(String name) {
		
		return rooms.get(name);
	}
	
	public Map getParticipants(String roomName) {
		Room r = getRoom(roomName);
		if (r != null) {
			return r.getParticipants();
		}
		
		return null;
	}
	
	public void addRoomListener(String roomName, IRoomListener listener) {
		Room r = getRoom(roomName);
		if (r != null) {
			r.addRoomListener(listener);
			return;
		}
		
	}
	
	// TODO: this must be broken, right?  where is roomName? (JRT: 9/25/2009)
//	public void removeRoomListener(IRoomListener listener) {
//		
//		Room r = getRoom(roomName);
//		if (r != null) {
//			r.removeRoomListener(listener)
//			return
//		}	
//		log.warn("Removing listener from a non-existing room ${roomName}")
//	}

	public void addParticipant(String roomName, User participant) {
		
		Room r = getRoom(roomName);
		if (r != null) {
/*			if (checkPublisher()) {

				if (r.getNumberOfParticipants() == 0) {
					log.debug("Notified event listener of conference start");
					HashMap<String,String> map = new HashMap<String,String>();
					map.put("meetingId", roomName);
					map.put("messageId", MessagingConstants.USER_JOINED_EVENT);
					
					Gson gson = new Gson();
					publisher.publish(MessagingConstants.SYSTEM_CHANNEL, gson.toJson(map));
					
				}
			}
*/			r.addParticipant(participant);

			return;
		}
		
	}
	
	public void removeParticipant(String roomName, String userid) {
		
		Room r = getRoom(roomName);
		if (r != null) {
			if (checkPublisher()) {
				//conferenceEventListener.participantsUpdated(r);
				//missing method()?
			}
			r.removeParticipant(userid);

			return;
		}
		
	}
	
	public void changeParticipantStatus(String roomName, String userid, String status, Object value) {
		
		Room r = getRoom(roomName);
		if (r != null) {
			r.changeParticipantStatus(userid, status, value);
			return;
		}		
		
	}

	public void setMessagingService(MessagingService messagingService) {
		this.messagingService = messagingService;
		this.messagingService.addListener(new RoomsManagerListener());
		this.messagingService.start();
	}
	
	public ArrayList<String> getCurrentPresenter( String room){
		Room r = getRoom(room);
		if (r != null) {
			return r.getCurrentPresenter();		
		}	
		
		return null;
	}
	
	public void assignPresenter(String room, ArrayList presenter){
		Room r = getRoom(room);
		if (r != null) {
			r.assignPresenter(presenter);
			return;
		}	
		
	}
	
	public void setConversionUpdatesMessageListener(ConversionUpdatesMessageListener conversionUpdatesMessageListener) {
		this.conversionUpdatesMessageListener = conversionUpdatesMessageListener;
	}
	
	private class RoomsManagerListener implements MessageListener{

		@Override
		public void endMeetingRequest(String meetingId) {
			
			Room room = getRoom(meetingId); // must do this because the room coming in is serialized (no transient values are present)
			if (room != null)
				room.endAndKickAll();

		}
		
		@Override
		public void presentationUpdates(HashMap<String, String> map) {
			conversionUpdatesMessageListener.handleReceivedMessage(map);
		}
		
	}
	
}
