package org.bigbluebutton.webconference.voice.freeswitch.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.bigbluebutton.webconference.voice.events.ConferenceEventListener;
import org.bigbluebutton.webconference.voice.events.ParticipantJoinedEvent;
import org.bigbluebutton.webconference.voice.freeswitch.response.ConferenceMember;
import org.bigbluebutton.webconference.voice.freeswitch.response.XMLResponseConferenceListParser;
import org.freeswitch.esl.client.transport.message.EslMessage;

import org.xml.sax.SAXException;

public class RecordConferenceCommand extends FreeswitchCommand {

	private boolean record;
	private String recordPath;
	
	public RecordConferenceCommand(String room, Integer requesterId, boolean record, String recordPath){
		super(room, requesterId);
		this.record = record;
		this.recordPath = recordPath;
	}
	

	@Override
	public String getCommandArgs() {
		String action = "norecord";
		if (record)
			action = "record";
		
		return SPACE + getRoom() + SPACE + action + SPACE + recordPath;
	}

	public void handleResponse(EslMessage response, ConferenceEventListener eventListener) {

        //Test for Known Conference

        String firstLine = response.getBodyLines().get(0);

        //E.g. Conference 85115 not found
        
        if(!firstLine.startsWith("<?xml")) {
            
            return;
        }


        XMLResponseConferenceListParser confXML = new XMLResponseConferenceListParser();

        //get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();
        try {

            //get a new instance of parser
            SAXParser sp = spf.newSAXParser();

            //Hack turning body lines back into string then to ByteStream.... BLAH!
            
            String responseBody = org.springframework.util.StringUtils.collectionToDelimitedString(response.getBodyLines(), "\n");

           

            //http://mark.koli.ch/2009/02/resolving-orgxmlsaxsaxparseexception-content-is-not-allowed-in-prolog.html
            //This Sux!
            responseBody = responseBody.trim().replaceFirst("^([\\W]+)<","<");

            ByteArrayInputStream bs = new ByteArrayInputStream(responseBody.getBytes());
            sp.parse(bs, confXML);

            //Maybe move this to XMLResponseConferenceListParser, sendConfrenceEvents ?
            ParticipantJoinedEvent pj;

            for(ConferenceMember member : confXML.getConferenceList()) {
                
                //Foreach found member in conference create a JoinedEvent
                pj = new ParticipantJoinedEvent(member.getId(), confXML.getConferenceRoom(),
                                member.getCallerId(), member.getCallerIdName(), member.getMuted(), member.getSpeaking());
                eventListener.handleConferenceEvent(pj);
            }

        }catch(SAXException se) {
            
        }catch(ParserConfigurationException pce) {
            
        }catch (IOException ie) {
            
        }
    }
}
