package org.bigbluebutton.modules.videodock.events
{
  import flash.events.Event;
  
  public class VideoDisplayReadyEvent extends Event
  {
    public static const DISPLAY_READY:String = "webcam video display ready";
    public static const DISPLAY_NOT_READY:String = "webcam video display not ready";
    
    public var userID:String;
    public var resolutions:Array;
    
    public function VideoDisplayReadyEvent(type:String, bubbles:Boolean=true, cancelable:Boolean=false)
    {      
      super(type, bubbles, cancelable);
    }
  }
}