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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LayoutRoom {

	private final String name;
	private final Map<String, ILayoutRoomListener> listeners;
	private boolean locked = false;
	private String modifierId = "-1";
	private String currentLayout = "";
	
	public LayoutRoom(String name) {
		this.name = name;
		this.listeners = new ConcurrentHashMap<String, ILayoutRoomListener>();
	}
	
	public String getName() {
		return name;
	}
	
	public void addRoomListener(ILayoutRoomListener listener) {
		if (! listeners.containsKey(listener.getName())) {
			
			listeners.put(listener.getName(), listener);			
		}
	}
	
	public void removeRoomListener(ILayoutRoomListener listener) {
		
		listeners.remove(listener);		
	}
	
	private void updateLayout() {
		for (Iterator<ILayoutRoomListener> iter = listeners.values().iterator(); iter.hasNext();) {
			
			ILayoutRoomListener listener = (ILayoutRoomListener) iter.next();
			
			listener.updateLayout(currentLayout());
		}
	}
		
	public void lockLayout(String userId, String layout) {
		locked = true;
		modifierId = userId;
		currentLayout = layout;
		updateLayout();
	}

	public void unlockLayout() {
		locked = false;
		modifierId = "-1";
		currentLayout = "";
		updateLayout();
	}

	public List<Object> currentLayout() {
		List<Object> args = new ArrayList<Object>();
		args.add(locked);
		args.add(modifierId);
		args.add(currentLayout);
		return args;
	}
}
