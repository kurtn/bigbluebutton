/**
 * BigBlueButton open source conferencing system - http://www.bigbluebutton.org/
 *
 * Copyright (c) 2012 BigBlueButton Inc. and by respective authors (see below).
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
 * Author: Felipe Cecagno <felipe@mconf.org>
 */
package org.bigbluebutton.conference.service.layout;


import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.adapter.IApplication;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;


public class LayoutHandler extends ApplicationAdapter implements IApplication {

	private static final String APP = "LAYOUT";
	private static final String LAYOUT_SO = "layoutSO";   

	private LayoutApplication layoutApplication;



	@Override
	public boolean roomConnect(IConnection connection, Object[] params) {
		
		ISharedObject so = getSharedObject(connection.getScope(), LAYOUT_SO);
		
		LayoutSender sender = new LayoutSender(so);
		String room = connection.getScope().getName();
		
		layoutApplication.addRoomListener(room, sender);
		
		return true;
	}


	
	@Override
	public boolean roomStart(IScope scope) {
		
		layoutApplication.createRoom(scope.getName());
    	if (!hasSharedObject(scope, LAYOUT_SO)) {
    		if (createSharedObject(scope, LAYOUT_SO, false)) {   
    			return true;
    		}    		
    	}  	
		
    	return false;
	}

	@Override
	public void roomStop(IScope scope) {
		
		layoutApplication.destroyRoom(scope.getName());
		if (!hasSharedObject(scope, LAYOUT_SO)) {
    		clearSharedObjects(scope, LAYOUT_SO);
    	}
	}
	
	public void setLayoutApplication(LayoutApplication a) {
		
		layoutApplication = a;
		layoutApplication.handler = this;
	}
	
}
