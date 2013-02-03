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

import org.red5.server.adapter.IApplication;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.Red5;
import java.util.HashMap;
import java.util.Map;import org.bigbluebutton.conference.BigBlueButtonSession;import org.bigbluebutton.conference.Constants;import org.bigbluebutton.conference.service.recorder.RecorderApplication;
import org.bigbluebutton.conference.service.recorder.participants.ParticipantsEventRecorder;

public class ParticipantsHandler extends ApplicationAdapter implements IApplication{

	private static final String PARTICIPANTS_SO = "participantsSO";   
	private static final String APP = "PARTICIPANTS";

	private ParticipantsApplication participantsApplication;
	private RecorderApplication recorderApplication;
	


	@Override
	public boolean roomConnect(IConnection connection, Object[] params) {

		ISharedObject so = getSharedObject(connection.getScope(), PARTICIPANTS_SO);
		ParticipantsEventSender sender = new ParticipantsEventSender(so);
		ParticipantsEventRecorder recorder = new ParticipantsEventRecorder(connection.getScope().getName(), recorderApplication);
		
		participantsApplication.addRoomListener(connection.getScope().getName(), recorder);
		participantsApplication.addRoomListener(connection.getScope().getName(), sender);

		
		return true;
	}



	@Override
	public boolean roomJoin(IClient client, IScope scope) {
		
		participantJoin();
		return true;
	}

	@Override
	public void roomLeave(IClient client, IScope scope) {
		
		BigBlueButtonSession bbbSession = getBbbSession();
		if (bbbSession == null) {
			
		} else {
			participantsApplication.participantLeft(bbbSession.getSessionName(), bbbSession.getInternalUserID());
		}		
	}
	
	@Override
	public boolean roomStart(IScope scope) {
		log.debug(APP + " - roomStart "+scope.getName());
    	// create ParticipantSO if it is not already created
    	if (!hasSharedObject(scope, PARTICIPANTS_SO)) {
    		if (createSharedObject(scope, PARTICIPANTS_SO, false)) {   
    			return true; 			
    		}    		
    	}  	
		log.error("Failed to start room " + scope.getName());
    	return false;
	}

	@Override
	public void roomStop(IScope scope) {
		
		if (!hasSharedObject(scope, PARTICIPANTS_SO)) {
    		clearSharedObjects(scope, PARTICIPANTS_SO);
    	}
	}
	
	public boolean participantJoin() {
		
		BigBlueButtonSession bbbSession = getBbbSession();
		if (bbbSession != null) {
			String userid = bbbSession.getInternalUserID();
			String username = bbbSession.getUsername();
			String role = bbbSession.getRole();
			String room = bbbSession.getRoom();
			log.debug(APP + ":participantJoin - [" + room + "] [" + userid + ", " + username + ", " + role + "]");
			
			Map<String, Boolean> status = new HashMap<String, Boolean>();
			status.put("raiseHand", false);
			status.put("presenter", false);
			status.put("hasStream", false);	
			return participantsApplication.participantJoin(room, userid, username, role, bbbSession.getExternUserID(), status);
		}
		
		return false;
	}
	
	public void setParticipantsApplication(ParticipantsApplication a) {
		participantsApplication = a;
	}
	
	public void setRecorderApplication(RecorderApplication a) {
		recorderApplication = a;
	}
	
	private BigBlueButtonSession getBbbSession() {
		return (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
	}
}
