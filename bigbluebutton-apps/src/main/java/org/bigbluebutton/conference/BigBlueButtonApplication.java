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

import java.util.Iterator;
import java.util.Set;
import org.red5.server.api.Red5;import org.bigbluebutton.conference.service.participants.ParticipantsApplication;
import org.bigbluebutton.conference.service.recorder.RecorderApplication;
import org.bigbluebutton.live.IBigBlueButtonSystem;
import org.red5.server.adapter.IApplication;
import org.red5.server.adapter.MultiThreadedApplicationAdapter;
import org.red5.server.api.IConnection;
import org.red5.server.api.IContext;
import org.red5.server.api.scope.IScope;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.AbstractApplicationContext;

public class BigBlueButtonApplication extends MultiThreadedApplicationAdapter {
	private ParticipantsApplication participantsApplication;
	private RecorderApplication recorderApplication;
	private AbstractApplicationContext appCtx;
	private ConnectionInvokerService connInvokerService;
	private IBigBlueButtonSystem bbbSystem;
	
	private String version;
	
	@Override
    public boolean appStart(IScope app) {
        bbbSystem.hello();
        IContext context = app.getContext();
        appCtx = (AbstractApplicationContext) context.getApplicationContext();
        appCtx.addApplicationListener(new ShutdownHookListener());
        appCtx.registerShutdownHook();
        return super.appStart(app);
    }
    
	public void setBigBlueButtonSystem(IBigBlueButtonSystem s) {
		bbbSystem = s;
	}
	
	@Override
    public void appStop(IScope app) {
        super.appStop(app);
    }
    
	@Override
    public boolean roomStart(IScope room) {
    	assert participantsApplication != null;
    	connInvokerService.addScope(room.getName(), room);
    	return super.roomStart(room);
    }	
	
	@Override
    public void roomStop(IScope room) {
    	super.roomStop(room);
    	assert participantsApplication != null;
    	participantsApplication.destroyRoom(room.getName());
    	BigBlueButtonSession bbbSession = getBbbSession();
    	assert bbbSession != null;

    	/**
    	 * Need to figure out if the next 2 lines should be removed. (ralam nov 25, 2010).
    	 */
		assert recorderApplication != null;
		recorderApplication.destroyRecordSession(bbbSession.getSessionName());
		connInvokerService.removeScope(room.getName());
    }
    
	@Override
	public boolean roomConnect(IConnection connection, Object[] params) {
        String remoteHost = Red5.getConnectionLocal().getRemoteAddress();
        int remotePort = Red5.getConnectionLocal().getRemotePort();
        String username = ((String) params[0]).toString();
        String role = ((String) params[1]).toString();
        String conference = ((String)params[2]).toString();

        /*
         * Convert the id to Long because it gets converted to ascii decimal
         * equivalent (i.e. zero (0) becomes 48) if we don't.
         */
        long clientID = Long.parseLong(Red5.getConnectionLocal().getClient().getId());
        String sessionName = connection.getScope().getName();

        String voiceBridge = ((String) params[4]).toString();
		String room = sessionName;
		assert recorderApplication != null;
		boolean record = (Boolean)params[5];

    	String externalUserID = ((String) params[6]).toString();
    	String internalUserID = ((String) params[7]).toString();
    	    	
		if (record == true) {
			recorderApplication.createRecordSession(sessionName);
		}
			
    	BigBlueButtonSession bbbSession = new BigBlueButtonSession(sessionName, clientID, internalUserID,  username, role, 
    			conference, room, voiceBridge, record, externalUserID);
        connection.setAttribute(Constants.SESSION, bbbSession);        
        
        String debugInfo = "internalUserID=" + internalUserID + ",username=" + username + ",role=" +  role + ",conference=" + conference + "," + 
        					"session=" + sessionName + ",voiceConf=" + voiceBridge + ",room=" + room + ",externalUserid=" + externalUserID;

		participantsApplication.createRoom(room);
        super.roomConnect(connection, params);
        
        connInvokerService.addConnection(bbbSession.getInternalUserID(), connection);
    	return true;
	}

	@Override
	public void roomDisconnect(IConnection conn) {
        String remoteHost = Red5.getConnectionLocal().getRemoteAddress();
        int remotePort = Red5.getConnectionLocal().getRemotePort();    	
        String clientId = Red5.getConnectionLocal().getClient().getId();

    	connInvokerService.removeConnection(getBbbSession().getInternalUserID());
    	
		BigBlueButtonSession bbbSession = (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
	
		super.roomDisconnect(conn);
	}
	
	public String getMyUserId() {
		BigBlueButtonSession bbbSession = (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
		assert bbbSession != null;
		//return Long.toString(bbbSession.getClientID());
		return bbbSession.getInternalUserID();
	}
	
	public void setParticipantsApplication(ParticipantsApplication a) {
		participantsApplication = a;
	}
	
	public void setRecorderApplication(RecorderApplication a) {
		recorderApplication = a;
	}
	
	public void setApplicationListeners(Set<IApplication> listeners) {
		Iterator<IApplication> iter = listeners.iterator();
		while (iter.hasNext()) {
			super.addListener((IApplication) iter.next());
		}
	}
	
	public void setVersion(String v) {
		version = v;
	}
	
	private BigBlueButtonSession getBbbSession() {
		return (BigBlueButtonSession) Red5.getConnectionLocal().getAttribute(Constants.SESSION);
	}

	public void setConnInvokerService(ConnectionInvokerService connInvokerService) {
		System.out.print("Setting conn invoket service!!!!");
		this.connInvokerService = connInvokerService;
	}
	
	private class ShutdownHookListener implements ApplicationListener<ApplicationEvent> {

		@Override
		public void onApplicationEvent(ApplicationEvent event) {
			if (event instanceof org.springframework.context.event.ContextStoppedEvent) {

				participantsApplication.destroyAllRooms();
			}			
		}
		
	}
}
