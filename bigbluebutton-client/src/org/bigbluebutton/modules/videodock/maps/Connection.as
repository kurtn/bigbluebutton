package org.bigbluebutton.modules.videodock.maps
{
  import flash.events.AsyncErrorEvent;
  import flash.events.IEventDispatcher;
  import flash.events.IOErrorEvent;
  import flash.events.NetStatusEvent;
  import flash.events.SecurityErrorEvent;
  import flash.net.NetConnection;
  
  import org.bigbluebutton.common.LogUtil;
  import org.bigbluebutton.modules.videodock.events.ConnectionEvent;
  import org.bigbluebutton.modules.videodock.views.SubscribeStream;

  public class Connection
  {
    private var nc:NetConnection;   
    private var _dispatcher:IEventDispatcher;   
    private var _uri:String;
    
    public function Connection(dispatcher:IEventDispatcher)
    {
      _dispatcher = dispatcher;
      nc = new NetConnection();
      nc.client = this;
      nc.addEventListener(AsyncErrorEvent.ASYNC_ERROR, onAsyncError);
      nc.addEventListener(IOErrorEvent.IO_ERROR, onIOError);
      nc.addEventListener(NetStatusEvent.NET_STATUS, onNetStatus);
      nc.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onSecurityError);
    }
    
    public function connect(uri:String):void {
      _uri = uri;
      nc.connect(uri);  
    }
    
    private function broadcastConnectionDown():void {
      var event:ConnectionEvent = new ConnectionEvent(ConnectionEvent.DOWN);
      event.uri = _uri;
      _dispatcher.dispatchEvent(event);
    }
    private function onAsyncError(event:AsyncErrorEvent):void {
      LogUtil.debug("onAsyncError on [" + _uri + "]");
      broadcastConnectionDown();
    }
    
    private function onIOError(event:NetStatusEvent):void {
      LogUtil.debug("onIOError on [" + _uri + "]");
      broadcastConnectionDown();
    }
    
    private function onNetStatus(event:NetStatusEvent):void {
      switch(event.info.code){
        case "NetConnection.Connect.Success":
          var evt:ConnectionEvent = new ConnectionEvent(ConnectionEvent.UP);
          evt.uri = _uri;
          _dispatcher.dispatchEvent(evt);
          break;        
        case "NetConnection.Connect.Failed":
        case "NetConnection.Connect.Rejected":
        case "NetConnection.Connect.Closed":
        case "NetConnection.Connect.InvalidApp":
        case "NetConnection.Connect.AppShutdown":
          LogUtil.debug("[" + event.info.code + "] on [" + _uri + "]");
          broadcastConnectionDown();
          break;
        default:
          LogUtil.debug("[" + event.info.code + "] on [" + _uri + "]");
      }
    }
    
    public function attach(subscribeStream:SubscribeStream):void {
      subscribeStream.attach(nc);
    }
    
    private function onSecurityError(event:NetStatusEvent):void {
      LogUtil.debug("onSecurityError on [" + _uri + "]");
      broadcastConnectionDown();
    }
    
    public function onBWCheck(... rest):Number { 
      return 0; 
    } 
    
    public function onBWDone(... rest):void { 
      var p_bw:Number; 
      if (rest.length > 0) p_bw = rest[0]; 
      // your application should do something here 
      // when the bandwidth check is complete 
      trace("bandwidth = " + p_bw + " Kbps."); 
    }
  }
}