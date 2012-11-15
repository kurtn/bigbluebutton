package org.bigbluebutton.modules.videodock.events
{
  import flash.events.Event;
  
  public class ConnectionEvent extends Event
  {
    public static const UP:String = "video connection up";
    public static const DOWN:String = "video connection down";

    public function ConnectionEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
    {
      super(type, bubbles, cancelable);
    }
  }
}