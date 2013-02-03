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
import org.red5.server.api.scope.IScope;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;import org.bigbluebutton.conference.User;

public class ParticipantsService {

	
	private ParticipantsApplication application;

	@SuppressWarnings("unchecked")
	public void assignPresenter(String userid, String name, Long assignedBy) {
		
		IScope scope = Red5.getConnectionLocal().getScope();
		ArrayList<String> presenter = new ArrayList<String>();
		presenter.add(userid);
		presenter.add(name);
		presenter.add(assignedBy.toString());
		ArrayList<String> curPresenter = application.getCurrentPresenter(scope.getName());
		application.setParticipantStatus(scope.getName(), userid, "presenter", true);
		
		if (curPresenter != null){ 
			String curUserid = (String) curPresenter.get(0);
			if (! curUserid.equals(userid)){
				
				application.setParticipantStatus(scope.getName(), curPresenter.get(0), "presenter", false);
			}
		} else {
			
		}
		application.assignPresenter(scope.getName(), presenter);
	}
	
	@SuppressWarnings("unchecked")
	public Map getParticipants() {
		String roomName = Red5.getConnectionLocal().getScope().getName();
		
		Map p = application.getParticipants(roomName);
		Map participants = new HashMap();
		if (p == null) {
			participants.put("count", 0);
			
		} else {		
			
			participants.put("count", p.size());
			
			if (p.size() > 0) {
				/**
				 * Somehow we need to convert to Map so the client will be
				 * able to decode it. Need to figure out if we can send Participant
				 * directly. (ralam - 2/20/2009)
				 */
				Collection pc = p.values();
	    		Map pm = new HashMap();
	    		for (Iterator it = pc.iterator(); it.hasNext();) {
	    			User ap = (User) it.next();
	    			pm.put(ap.getInternalUserID(), ap.toMap()); 
	    		}  
				participants.put("participants", pm);
			}			
		}
		return participants;
	}
	
	public void setParticipantStatus(String userid, String status, Object value) {
		String roomName = Red5.getConnectionLocal().getScope().getName();
		
		application.setParticipantStatus(roomName, userid, status, value);
	}
	
	public void setParticipantsApplication(ParticipantsApplication a) {
		
		application = a;
	}
}
