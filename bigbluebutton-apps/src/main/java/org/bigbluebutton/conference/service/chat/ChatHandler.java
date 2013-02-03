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

package org.bigbluebutton.conference.service.chat;

import org.red5.server.adapter.IApplication;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.adapter.ApplicationAdapter;
import org.bigbluebutton.conference.service.recorder.RecorderApplication;
import org.bigbluebutton.conference.service.recorder.chat.ChatEventRecorder;

public class ChatHandler extends ApplicationAdapter implements IApplication{

	private RecorderApplication recorderApplication;
	private ChatApplication chatApplication;

	

	@Override
	public boolean roomConnect(IConnection connection, Object[] params) {
		ChatEventRecorder recorder = new ChatEventRecorder(connection.getScope().getName(), recorderApplication);
		chatApplication.addRoomListener(connection.getScope().getName(), recorder);

		return true;
	}



	@Override
	public boolean roomStart(IScope scope) {
		
		chatApplication.createRoom(scope.getName());
    	return true;
	}

	@Override
	public void roomStop(IScope scope) {
	
		chatApplication.destroyRoom(scope.getName());
	}
	
	public void setChatApplication(ChatApplication a) {
		
		chatApplication = a;
		chatApplication.handler = this;
	}
	
	public void setRecorderApplication(RecorderApplication a) {
		
		recorderApplication = a;
	}
}