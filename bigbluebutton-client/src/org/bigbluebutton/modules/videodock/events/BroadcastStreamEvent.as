package org.bigbluebutton.modules.videodock.events
{
  import flash.events.Event;
  
  public class BroadcastStreamEvent extends Event
  {
    public static const STARTED:String = "broadcast stream started";
    public static const STOPPED:String = "broadcast stream stopped";
    
    public var stream:String;
    public var userID:String;
    
    public function BroadcastStreamEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
    {
      super(type, bubbles, cancelable);
    }
  }
}