package org.bigbluebutton.conference.service.recorder.presentation;


import java.util.Map;
import org.bigbluebutton.conference.service.recorder.RecorderApplication;
import org.bigbluebutton.conference.service.presentation.IPresentationRoomListener;


public class PresentationEventRecorder implements IPresentationRoomListener {
		
	private static final String GENERATED_SLIDE_KEY = "GENERATED_SLIDE";
	private static final String CONVERSION_COMPLETED_KEY = "CONVERSION_COMPLETED";
	
	String APP_NAME = "RECORDER:PRESENTATION";
	private final RecorderApplication recorder;
	private final String session;
	
	public PresentationEventRecorder(String session, RecorderApplication recorder) {
		this.recorder = recorder;
		this.session = session;
	}

	@Override
	public String getName() {
		return APP_NAME;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void sendUpdateMessage(Map<String, Object> message) {
    	String messageKey = (String) message.get("messageKey");

		if(messageKey.equalsIgnoreCase(GENERATED_SLIDE_KEY)){
			handleGeneratedSlideEvent(message);
		}
		else if(messageKey.equalsIgnoreCase(CONVERSION_COMPLETED_KEY)){
			handleConversionCompletedEvent(message);
		}
		else{
			
		}
	}
	
	private void handleGeneratedSlideEvent(Map<String, Object> message) {
		
		
		GenerateSlidePresentationRecordEvent event = new GenerateSlidePresentationRecordEvent();
		event.setMeetingId(session);
		event.setTimestamp(System.currentTimeMillis());
		event.setPresentationName((String)message.get("presentationName"));
		event.setNumberOfPages((Integer)message.get("numberOfPages"));
		event.setPagesCompleted((Integer)message.get("pagesCompleted"));
		recorder.record(session, event);
	}

	private void handleConversionCompletedEvent(Map<String, Object> message) {
		
		
		ConversionCompletedPresentationRecordEvent event = new ConversionCompletedPresentationRecordEvent();
		event.setMeetingId(session);
		event.setTimestamp(System.currentTimeMillis());
		event.setPresentationName((String)message.get("presentationName"));
		event.setSlidesInfo((String)message.get("slidesInfo"));
		recorder.record(session, event);
	}
	
	@Override
	public void gotoSlide(int curslide) {
		
		GotoSlidePresentationRecordEvent event = new GotoSlidePresentationRecordEvent();
		event.setMeetingId(session);
		event.setTimestamp(System.currentTimeMillis());
		event.setSlide(curslide);
		recorder.record(session, event);
	}

	@Override
	public void resizeAndMoveSlide(Double xOffset, Double yOffset, Double widthRatio, Double heightRatio) {
		

		ResizeAndMoveSlidePresentationRecordEvent event = new ResizeAndMoveSlidePresentationRecordEvent();
		event.setMeetingId(session);
		event.setTimestamp(System.currentTimeMillis());
		event.setXOffset(xOffset.doubleValue());
		event.setYOffset(yOffset.doubleValue());
		event.setWidthRatio(widthRatio.doubleValue());
		event.setHeightRatio(heightRatio.doubleValue());
		
		recorder.record(session, event);
	}

	@Override
	public void removePresentation(String name) {
				
		RemovePresentationPresentationRecordEvent event = new RemovePresentationPresentationRecordEvent();
		event.setMeetingId(session);
		event.setTimestamp(System.currentTimeMillis());
		event.setPresentationName(name);
		
		recorder.record(session, event);
	}

	@Override
	public void sharePresentation(String presentationName, Boolean share) {
				
		SharePresentationPresentationRecordEvent event = new SharePresentationPresentationRecordEvent();
		event.setMeetingId(session);
		event.setTimestamp(System.currentTimeMillis());
		event.setPresentationName(presentationName);
		event.setShare(share.booleanValue());
		recorder.record(session, event);
	}

	@Override
	public void sendCursorUpdate(Double xPercent, Double yPercent) {
		

		CursorUpdateRecordEvent event = new CursorUpdateRecordEvent();
		event.setMeetingId(session);
		event.setTimestamp(System.currentTimeMillis());
		event.setXPercent(xPercent.doubleValue());
		event.setYPercent(yPercent.doubleValue());
		
		recorder.record(session, event);
	}

}
