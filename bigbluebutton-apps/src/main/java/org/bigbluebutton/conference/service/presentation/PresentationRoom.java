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

package org.bigbluebutton.conference.service.presentation;


import java.util.concurrent.ConcurrentHashMap;import java.util.concurrent.CopyOnWriteArrayList;import java.util.ArrayList;
import java.util.Collections;import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PresentationRoom {
	
	
	private final String name;
	private final Map<String, IPresentationRoomListener> listeners;
	
	int currentSlide = 0;
	Boolean sharing = false;
	String currentPresentation = "";
	Double xOffset = 0D;
	Double yOffset = 0D;
	Double widthRatio = 0D;
	Double heightRatio = 0D;
	
	/* cursor location */
	Double xPercent = 0D;
	Double yPercent = 0D;
	
	ArrayList<String> presentationNames = new ArrayList<String>();
	
	public PresentationRoom(String name) {
		this.name = name;
		listeners   = new ConcurrentHashMap<String, IPresentationRoomListener>();
	}
	
	public String getName() {
		return name;
	}
	
	public void addRoomListener(IPresentationRoomListener listener) {
		if (! listeners.containsKey(listener.getName())) {
			
			listeners.put(listener.getName(), listener);			
		}
	}
	
	public void removeRoomListener(IPresentationRoomListener listener) {
		
		listeners.remove(listener);		
	}
	
	@SuppressWarnings("unchecked")
	public void sendUpdateMessage(Map message){
		for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {
			
			IPresentationRoomListener listener = (IPresentationRoomListener) iter.next();
			
			listener.sendUpdateMessage(message);
		}	
		
		storePresentationNames(message);
	}

	@SuppressWarnings("unchecked")
	private void storePresentationNames(Map message){
        String presentationName = (String) message.get("presentationName");
        String messageKey = (String) message.get("messageKey");
             
        if (messageKey.equalsIgnoreCase("CONVERSION_COMPLETED")) {            
           
            presentationNames.add(presentationName);                                
        }           
    }
	
	public void sendCursorUpdate(Double xPercent, Double yPercent) {
		this.xPercent = xPercent;
		this.yPercent = yPercent;
		
		for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {
			
			IPresentationRoomListener listener = (IPresentationRoomListener) iter.next();
			
			listener.sendCursorUpdate(xPercent,yPercent);
		}
	}
	
	public void resizeAndMoveSlide(Double xOffset, Double yOffset, Double widthRatio, Double heightRatio) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.widthRatio = widthRatio;
		this.heightRatio = heightRatio;
		
		for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {
			
			IPresentationRoomListener listener = (IPresentationRoomListener) iter.next();
			
			listener.resizeAndMoveSlide(xOffset, yOffset, widthRatio, heightRatio);
		}		
	}
		
	@SuppressWarnings("unchecked")
	public void gotoSlide(int curslide){
		
		currentSlide = curslide;
		for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {
			
			IPresentationRoomListener listener = (IPresentationRoomListener) iter.next();
			
			listener.gotoSlide(curslide);
		}			
	}	
	
	@SuppressWarnings("unchecked")
	public void sharePresentation(String presentationName, Boolean share){
		
		sharing = share;
		if (share) {
		  currentPresentation = presentationName;
		  presentationNames.add(presentationName);   
		} else {
		  currentPresentation = "";
		}
		 
		for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {
			
			IPresentationRoomListener listener = (IPresentationRoomListener) iter.next();
			
			listener.sharePresentation(presentationName, share);
		}			
	}
	    
    public void removePresentation(String presentationName){
        
        int index = presentationNames.indexOf(presentationName);
        
        if (index < 0) {
            
            return;
        }
        
        presentationNames.remove(index);
        
        for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {
            
            IPresentationRoomListener listener = (IPresentationRoomListener) iter.next();
            
            listener.removePresentation(presentationName);
        }   
        
        if (currentPresentation == presentationName) {
            sharePresentation(presentationName, false);
        }        
    }
    
    public String getCurrentPresentation() {
		return currentPresentation;
	}

	public int getCurrentSlide() {
		return currentSlide;
	}

	public Boolean getSharing() {
		return sharing;
	}

	public ArrayList<String> getPresentationNames() {
		return presentationNames;
	}

	public Double getxOffset() {
		return xOffset;
	}

	public Double getyOffset() {
		return yOffset;
	}

	public Double getWidthRatio() {
		return widthRatio;
	}

	public Double getHeightRatio() {
		return heightRatio;
	}

	
}
