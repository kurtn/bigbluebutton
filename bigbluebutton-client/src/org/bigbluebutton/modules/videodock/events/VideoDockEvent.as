package org.bigbluebutton.modules.videodock.events
{
  import flash.events.Event;
  
  public class VideoDockEvent extends Event
  {
    public static const START:String = "start video dock module";
    public static const STOP:String = "stop video dock module";
    
    public function VideoDockEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
    {
      super(type, bubbles, cancelable);
    }
  }
}