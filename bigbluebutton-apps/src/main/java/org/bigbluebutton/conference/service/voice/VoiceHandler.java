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

package org.bigbluebutton.conference.service.voice;

import org.red5.server.adapter.IApplication;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;

import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.Red5;import org.bigbluebutton.conference.BigBlueButtonSession;import org.bigbluebutton.conference.Constants;
import org.bigbluebutton.webconference.voice.ConferenceService;
import org.bigbluebutton.webconference.red5.voice.ClientNotifier; 
public class VoiceHandler extends ApplicationAdapter implements IApplication{

	private static final String VOICE = "VOICE";
	private static final String VOICE_SO = "meetMeUsersSO";
	private static final String APP = "VOICE";

	private ClientNotifier clientManager;
	private ConferenceService conferenceService;
	


	@Override
	public boolean appStart(IScope scope) {

		return conferenceService.startup();
	}

	@Override
	public void appStop(IScope scope) {

		conferenceService.shutdown();
	}

	@Override
	public boolean roomConnect(IConnection connection, Object[] params) {

		ISharedObject so = getSharedObject(connection.getScope(), VOICE_SO);
		    		
		String voiceBridge = getBbbSession().getVoiceBridge();
		String meetingid = getBbbSession().getConference(); 
		Boolean record = getBbbSession().getRecord();
		

		clientManager.addSharedObject(connection.getScope().getName(), voiceBridge, so);
		conferenceService.createConference(voiceBridge, meetingid, record); 		
		return true;
	}



	@Override
	public boolean roomStart(IScope scope) {

    	if (!hasSharedObject(scope, VOICE_SO)) {
    		if (createSharedObject(scope, VOICE_SO, false)) {    			
    			return true; 			
    		}    		
    	}  	
		
    	return false;
	}

	@Override
	public void roomStop(IScope scope) {
		
		/**
		 * Remove the voicebridge from the list of running
		 * voice conference.
		 */
		String voiceBridge = getBbbSession().getVoiceBridge();
		conferenceService.destroyConference(voiceBridge);
		clientManager.removeSharedObject(scope.getName());
		if (!hasSharedObject(scope, VOICE_SO)) {
    		clearSharedObjects(scope, VOICE_SO);
    	}
	}
	
	public void setClientNotifier(ClientNotifier c) {
	
		clientManager = c;
	}
	
	public void setConferenceService(ConferenceService s) {
		
		conferenceService = s;
		
	}

	
	private BigBlueButtonSession getBbbSession() {
		return (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
	}
}
