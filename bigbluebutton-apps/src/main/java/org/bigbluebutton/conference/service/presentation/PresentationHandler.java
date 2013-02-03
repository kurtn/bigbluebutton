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

import java.io.File;
import java.io.FileFilter;
import org.red5.server.adapter.IApplication;
import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;

import org.red5.server.api.scope.IScope;
import org.red5.server.api.so.ISharedObject;
import org.red5.server.adapter.ApplicationAdapter;
import org.red5.server.api.Red5;
import org.bigbluebutton.conference.BigBlueButtonSession;
import org.bigbluebutton.conference.Constants;
import org.bigbluebutton.conference.service.recorder.RecorderApplication;
import org.bigbluebutton.conference.service.recorder.presentation.PresentationEventRecorder;

public class PresentationHandler extends ApplicationAdapter implements IApplication{

	private static final String PRESENTATION = "PRESENTATION";
	private static final String PRESENTATION_SO = "presentationSO";   
	private static final String APP = "PRESENTATION";

	private RecorderApplication recorderApplication;
	private PresentationApplication presentationApplication;
	private ConversionUpdatesMessageListener conversionUpdatesMessageListener;
	

	@Override
	public boolean appStart(IScope scope) {
		conversionUpdatesMessageListener.start();
		return true;
	}

	@Override
	public void appStop(IScope scope) {
		conversionUpdatesMessageListener.stop();
	}

	@Override
	public boolean roomConnect(IConnection connection, Object[] params) {

		ISharedObject so = getSharedObject(connection.getScope(), PRESENTATION_SO);
		

		PresentationEventSender sender = new PresentationEventSender(so);
		PresentationEventRecorder recorder = new PresentationEventRecorder(connection.getScope().getName(), recorderApplication);
						

		presentationApplication.addRoomListener(connection.getScope().getName(), recorder);
		presentationApplication.addRoomListener(connection.getScope().getName(), sender);

		return true;
	}




	@Override
	public boolean roomStart(IScope scope) {

		presentationApplication.createRoom(scope.getName());
    	if (!hasSharedObject(scope, PRESENTATION_SO)) {
    		if (createSharedObject(scope, PRESENTATION_SO, false)) {    			
				
				try {
					// TODO: this is hard-coded, and not really a great abstraction.  need to fix this up later
					String folderPath = "/var/bigbluebutton/" + scope.getName() + "/" + scope.getName();
					File folder = new File(folderPath);
					
					if (folder.exists() && folder.isDirectory()) {
						File[] presentations = folder.listFiles(new FileFilter() {
							public boolean accept(File path) {
								
								return path.isDirectory();
							}
						});
						for (File presFile : presentations) {
							
							presentationApplication.sharePresentation(scope.getName(), presFile.getName(), true);
						}
					}
				} catch (Exception ex) {
					
				}
    			return true; 			
    		}    		
    	}  	
		
    	return false;
	}

	@Override
	public void roomStop(IScope scope) {
		
		presentationApplication.destroyRoom(scope.getName());
		if (!hasSharedObject(scope, PRESENTATION_SO)) {
    		clearSharedObjects(scope, PRESENTATION_SO);
    	}
	}
	
	public void setPresentationApplication(PresentationApplication a) {
		
		presentationApplication = a;
	}
	
	public void setRecorderApplication(RecorderApplication a) {
		
		recorderApplication = a;
	}
	
	public void setConversionUpdatesMessageListener(ConversionUpdatesMessageListener service) {
		
		conversionUpdatesMessageListener = service;
	}
	
}
