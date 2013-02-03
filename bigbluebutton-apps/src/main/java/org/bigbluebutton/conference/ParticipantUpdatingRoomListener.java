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

import java.util.ArrayList;
import java.util.HashMap;

import org.bigbluebutton.conference.service.messaging.MessagingConstants;
import org.bigbluebutton.conference.service.messaging.MessagingService;


import com.google.gson.Gson;


public class ParticipantUpdatingRoomListener implements IRoomListener{


	MessagingService messagingService;
	private Room room;
	
	public ParticipantUpdatingRoomListener(Room room, MessagingService messagingService) {
		this.room = room;
		this.messagingService=messagingService;
	}
	
	public String getName() {
		return "PARTICIPANT:UPDATE:ROOM";
	}
	
	public void participantStatusChange(User p, String status, Object value){
		if (messagingService != null) {
			HashMap<String,String> map= new HashMap<String, String>();
			map.put("meetingId", this.room.getName());
			map.put("messageId", MessagingConstants.USER_STATUS_CHANGE_EVENT);
			
			map.put("internalUserId", p.getInternalUserID());
			map.put("status", status);
			map.put("value", value.toString());
			
			Gson gson= new Gson();
			messagingService.send(MessagingConstants.PARTICIPANTS_CHANNEL, gson.toJson(map));
			
		}
	}
	
	public void participantJoined(User p) {
		if (messagingService != null) {
			HashMap<String,String> map= new HashMap<String, String>();
			map.put("meetingId", this.room.getName());
			map.put("messageId", MessagingConstants.USER_JOINED_EVENT);
			map.put("internalUserId", p.getInternalUserID());
			map.put("externalUserId", p.getExternalUserID());
			map.put("fullname", p.getName());
			map.put("role", p.getRole());
			
			Gson gson= new Gson();
			messagingService.send(MessagingConstants.PARTICIPANTS_CHANNEL, gson.toJson(map));
			
		}
	}
	
	public void participantLeft(User p) {		
		if (messagingService != null) {
			HashMap<String,String> map= new HashMap<String, String>();
			map.put("meetingId", this.room.getName());
			map.put("messageId", MessagingConstants.USER_LEFT_EVENT);
			map.put("internalUserId", p.getInternalUserID());
			
			Gson gson= new Gson();
			messagingService.send(MessagingConstants.PARTICIPANTS_CHANNEL, gson.toJson(map));
			
		}
	}

	public void assignPresenter(ArrayList<String> presenter) {
		// Do nothing.
	}
	
	public void endAndKickAll() {
		// no-op
	}
	
	
}
