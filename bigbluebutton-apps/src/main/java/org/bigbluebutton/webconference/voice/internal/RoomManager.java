/** 
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
*/
package org.bigbluebutton.webconference.voice.internal;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.bigbluebutton.webconference.voice.ConferenceService;
import org.bigbluebutton.webconference.voice.Participant;
import org.bigbluebutton.webconference.voice.VoiceEventRecorder;
import org.bigbluebutton.webconference.voice.events.ConferenceEvent;
import org.bigbluebutton.webconference.voice.events.ParticipantJoinedEvent;
import org.bigbluebutton.webconference.voice.events.ParticipantLeftEvent;
import org.bigbluebutton.webconference.voice.events.ParticipantLockedEvent;
import org.bigbluebutton.webconference.voice.events.ParticipantMutedEvent;
import org.bigbluebutton.webconference.voice.events.ParticipantTalkingEvent;
import org.bigbluebutton.webconference.voice.events.StartRecordingEvent;


public class RoomManager {

	
	private final ConcurrentHashMap<String, RoomImp> rooms;
	private ConferenceService confService;
	private VoiceEventRecorder recorder;
		
	public RoomManager() {
		rooms = new ConcurrentHashMap<String, RoomImp>();
	}
	
	public void createRoom(String name,boolean record, String meetingid) {
		
		RoomImp r = new RoomImp(name,record,meetingid);
		rooms.putIfAbsent(name, r);
	}
	
	public boolean hasRoom(String name) {
		return rooms.containsKey(name);
	}
	
	public boolean hasParticipant(String room, Integer participant) {
		RoomImp rm = rooms.get(room);
		if (rm == null) return false;
		return rm.hasParticipant(participant);
	}
	
	public void destroyRoom(String name) {
		
		RoomImp r = rooms.remove(name);
		if (r != null) r = null;
	}
	
	public void mute(String room, boolean mute) {
		RoomImp rm = rooms.get(room);
		if (rm != null) rm.mute(mute);
	}
	
	public boolean isRoomMuted(String room){
		RoomImp rm = rooms.get(room);
		if (rm != null) return rm.isMuted();
		else return false;
	}
	
	public ArrayList<Participant> getParticipants(String room) {
		
		RoomImp rm = rooms.get(room);		
		if (rm == null) {
			
		}
		return rm.getParticipants();
	}
		
	public boolean isParticipantMuteLocked(Integer participant, String room) {
		RoomImp rm = rooms.get(room);
		if (rm != null) {
			Participant p = rm.getParticipant(participant);
			return p.isMuteLocked();
		}
		return false;
	}

	private void lockParticipant(String room, Integer participant, Boolean lock) {
		RoomImp rm = rooms.get(room);
		if (rm != null) {
			ParticipantImp p = (ParticipantImp) rm.getParticipant(participant);
			if (p != null)
				p.setLock(lock);
		}
	}
	
	/**
	 * Process the event from the voice conferencing server.
	 * @param event
	 */
	public void processConferenceEvent(ConferenceEvent event) {
		
		RoomImp rm = rooms.get(event.getRoom());
		if (rm == null) {
			
			return;
		}
		
		if (event instanceof ParticipantJoinedEvent) {
			handleParticipantJoinedEvent(event, rm);
		} else if (event instanceof ParticipantLeftEvent) {		
			handleParticipantLeftEvent(event, rm);
		} else if (event instanceof ParticipantMutedEvent) {
			handleParticipantMutedEvent(event, rm);
		} else if (event instanceof ParticipantTalkingEvent) {
			handleParticipantTalkingEvent(event, rm);
		} else if (event instanceof ParticipantLockedEvent) {
			handleParticipantLockedEvent(event, rm);
		} else if (event instanceof StartRecordingEvent) {
			// do nothing but let it through.
			// later on we need to dispatch an event to the client that the voice recording has started.
		} else {
			
			return;
		}
		
		/**
		 * Record the event if the meeting is being recorded.
		 */
		recorder.recordConferenceEvent(event, rm.getMeeting());
	}

	private void handleParticipantJoinedEvent(ConferenceEvent event, RoomImp rm) {
		
		ParticipantJoinedEvent pje = (ParticipantJoinedEvent) event;
		ParticipantImp p = new ParticipantImp(pje.getParticipantId(), pje.getCallerIdName());
		p.setMuted(pje.getMuted());
		p.setTalking(pje.getSpeaking());
		
		
		rm.add(p);
		
		if (rm.numParticipants() == 1) {
			if (rm.record() && !rm.isRecording()) {
				/**
				 * Start recording when the first user joins the voice conference.
				 * WARNING: Works only with FreeSWITCH for now. We need to come up with a generic way to
				 * trigger recording for both Asterisk and FreeSWITCH.
				 */
				rm.recording(true);
				confService.recordSession(event.getRoom(), rm.getMeeting());
			}
			
			// Broadcast the audio
			confService.broadcastSession(event.getRoom(), rm.getMeeting());
		}
		
		
		
		if (rm.isMuted() && !p.isMuted()) {
			confService.mute(p.getId(), event.getRoom(), true);
		}		
	}
	
	private void handleParticipantLeftEvent(ConferenceEvent event, RoomImp rm) {
		
		rm.remove(event.getParticipantId());	
		
		if ((rm.numParticipants() == 0) && (rm.record())) {
			rm.recording(false);
		}			
	}
	
	private void handleParticipantMutedEvent(ConferenceEvent event, RoomImp rm) {

		ParticipantMutedEvent pme = (ParticipantMutedEvent) event;
		ParticipantImp p = (ParticipantImp) rm.getParticipant(event.getParticipantId());
		if (p != null) p.setMuted(pme.isMuted());		
	}
	
	private void handleParticipantTalkingEvent(ConferenceEvent event, RoomImp rm) {
		
		ParticipantTalkingEvent pte = (ParticipantTalkingEvent) event;
		ParticipantImp p = (ParticipantImp) rm.getParticipant(event.getParticipantId());
		if (p != null) p.setTalking(pte.isTalking());		
	}
	
	private void handleParticipantLockedEvent(ConferenceEvent event, RoomImp rm) {
		ParticipantLockedEvent ple = (ParticipantLockedEvent) event;
		lockParticipant(ple.getRoom(), ple.getParticipantId(), ple.isLocked());		
	}
	
	public void setVoiceEventRecorder(VoiceEventRecorder recorder) {
		this.recorder = recorder;
	}	
	
	public void setConferenceService(ConferenceService service) {
		this.confService = service;
	}
}
