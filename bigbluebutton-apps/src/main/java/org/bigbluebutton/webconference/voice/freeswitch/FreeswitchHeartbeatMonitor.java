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
package org.bigbluebutton.webconference.voice.freeswitch;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.freeswitch.esl.client.manager.ManagerConnection;
import org.freeswitch.esl.client.transport.event.EslEvent;
import org.jboss.netty.channel.ExceptionEvent;

import org.freeswitch.esl.client.inbound.Client;
import org.freeswitch.esl.client.inbound.InboundConnectionFailure;


public class FreeswitchHeartbeatMonitor implements Observer {

    public static final String EVENT_HEARTBEAT = "HEARTBEAT";

    private final Executor exec = Executors.newSingleThreadExecutor();
    private Runnable monitorProcess;
    private volatile boolean running = false;

    private static final long DEFAULT_INTERVAL = 20 * 1000L;
    private long interval = DEFAULT_INTERVAL;
    private long lastHeartbeat = System.currentTimeMillis();
    private long maxHeartbeatInterval = 25 * 1000L;
    private int intervalLoopCount = 0;
    
    private final ManagerConnection connection;
    private final FreeswitchApplication eventListner;

    public FreeswitchHeartbeatMonitor(ManagerConnection connection, FreeswitchApplication eventListner) {
    	this.connection = connection;
        this.eventListner = eventListner;
    }

    public void start() {
        if (!running) {
            running = true;
            monitorProcess = new Runnable() {
                public void run() {
                    monitorFreeswitchServer();
                }
            };
            exec.execute(monitorProcess);
        }	   
    }

    public void stop() {
        running = false;
    }

    private void monitorFreeswitchServer() {
        long timeDiff;
        
        while(running) {
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) { }

            if(!running) {
                break;
            }

            //Check to see if update happened recently
            timeDiff = System.currentTimeMillis() - lastHeartbeat;
            if(timeDiff > maxHeartbeatInterval) {
                //if not reconnect
                try {
                    Client c = connection.getESLClient();

                    if(c.canSend()) { //Otherwise disconnect will throw ISE
                      
                        connection.disconnect();
                    }
                   
                    try {
                        connection.connect();
                        eventListner.startup(); //Re-call startup to setup eventListner and filters...
                        lastHeartbeat = System.currentTimeMillis(); //Reset 
                    } catch ( InboundConnectionFailure ce ) {
                        
                    }
                } catch (IllegalStateException ise) {
                    
                } catch (Exception e) {
                    
                }
            }

            if(intervalLoopCount == 5) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(lastHeartbeat);

                
                intervalLoopCount = 0;
            }
            intervalLoopCount++;
        }
    }

    public void update(Observable o, Object arg) {
        if (arg instanceof EslEvent) {
            //Heartbeat
            EslEvent event = (EslEvent)arg;
            /*
            Map<String, String> eventHeaders = event.getEventHeaders();
            StringBuilder sb = new StringBuilder("\n");
            for (Iterator it=eventHeaders.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry)it.next();
                sb.append(entry.getKey());
                sb.append(" => '");
                sb.append(entry.getValue());
                sb.append("'\n");
            }
            log.debug ("HeartbeaMonitor EslEvent Headers:\n{}\nEND", sb.toString());

             *
            Event-Name => 'HEARTBEAT'
            Event-Calling-Function => 'send_heartbeat'
            Event-Date-GMT => 'Thu, 01 Jul 2010 04:35:21 GMT'
            Core-UUID => '0ec65230-2b40-4cde-9d6f-e49830c653f1'
            FreeSWITCH-IPv4 => '192.168.1.10'
            Session-Since-Startup => '3'
            Up-Time => '0 years, 1 day, 23 hours, 0 minutes, 59 seconds, 738 milliseconds, 899 microseconds'
            Event-Date-Local => '2010-07-01 00:35:21'
            Session-Per-Sec => '30'
            FreeSWITCH-IPv6 => '::1'
            Event-Calling-File => 'switch_core.c'
            Event-Date-Timestamp => '1277958921191815'
            Session-Count => '0'
            Idle-CPU => '97.000000'
            FreeSWITCH-Hostname => 'ljackson.jjcons.com'
            Event-Calling-Line-Number => '65'
            Event-Info => 'System Ready'

            */
            //this.lastHeartbeat = Long.parseLong(event.getEventHeaders().get("Event-Date-Timestamp"));
            
            lastHeartbeat = System.currentTimeMillis();
            return;

        } else if (arg instanceof ExceptionEvent) {
            //Exception....
            
            return;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
