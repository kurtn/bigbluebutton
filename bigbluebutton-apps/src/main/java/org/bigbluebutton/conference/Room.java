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


import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class Room implements Serializable {
	
	ArrayList<String> currentPresenter = null;
	private String name;
	private Map <String, User> participants;

	// these should stay transient so they're not serialized in ActiveMQ messages:	
	//private transient Map <Long, Participant> unmodifiableMap;
	private transient final Map<String, IRoomListener> listeners;

	public Room(String name) {
		this.name = name;
		participants = new ConcurrentHashMap<String, User>();
		//unmodifiableMap = Collections.unmodifiableMap(participants);
		listeners   = new ConcurrentHashMap<String, IRoomListener>();
	}

	public String getName() {
		return name;
	}

	public void addRoomListener(IRoomListener listener) {
		if (! listeners.containsKey(listener.getName())) {
			listeners.put(listener.getName(), listener);			
		}
	}

	public void removeRoomListener(IRoomListener listener) {
		listeners.remove(listener);		
	}

	public void addParticipant(User participant) {
		synchronized (this) {
			participants.put(participant.getInternalUserID(), participant);
//			unmodifiableMap = Collections.unmodifiableMap(participants)
		}
		
		for (Iterator it = listeners.values().iterator(); it.hasNext();) {
			IRoomListener listener = (IRoomListener) it.next();
			
			listener.participantJoined(participant);
		}
	}

	public void removeParticipant(String userid) {
		boolean present = false;
		User p = null;
		synchronized (this) {
			present = participants.containsKey(userid);
			if (present) {
				
				p = participants.remove(userid);
			}
		}
		if (present) {
			for (Iterator it = listeners.values().iterator(); it.hasNext();) {
				IRoomListener listener = (IRoomListener) it.next();
				
				listener.participantLeft(p);
			}
		}
	}

	public void changeParticipantStatus(String userid, String status, Object value) {
		boolean present = false;
		User p = null;
		synchronized (this) {
			present = participants.containsKey(userid);
			if (present) {
				
				p = participants.get(userid);
				p.setStatus(status, value);
				//participants.put(userid, p);
				//unmodifiableMap = Collections.unmodifiableMap(participants);
			}
		}
		if (present) {
			for (Iterator it = listeners.values().iterator(); it.hasNext();) {
				IRoomListener listener = (IRoomListener) it.next();
				
				listener.participantStatusChange(p, status, value);
			}
		}		
	}

	public void endAndKickAll() {
		for (Iterator it = listeners.values().iterator(); it.hasNext();) {
			IRoomListener listener = (IRoomListener) it.next();
			
			listener.endAndKickAll();
		}
	}

	public Map getParticipants() {
		return participants;//unmodifiableMap;
	}	

	public Collection<User> getParticipantCollection() {
		return participants.values();
	}

	public int getNumberOfParticipants() {
		
		return participants.size();
	}

	public int getNumberOfModerators() {
		int sum = 0;
		for (Iterator<User> it = participants.values().iterator(); it.hasNext(); ) {
			User part = it.next();
			if (part.isModerator()) {
				sum++;
			}
		} 
		
		return sum;
	}

	public ArrayList<String> getCurrentPresenter() {
		return currentPresenter;
	}
	
	public void assignPresenter(ArrayList<String> presenter){
		currentPresenter = presenter;
		for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {
			
			IRoomListener listener = (IRoomListener) iter.next();
			
			listener.assignPresenter(presenter);
		}	
	}
}