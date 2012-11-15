package org.bigbluebutton.modules.videodock.views
{
  import flash.events.AsyncErrorEvent;
  import flash.events.IEventDispatcher;
  import flash.events.IOErrorEvent;
  import flash.events.NetStatusEvent;
  import flash.media.Camera;
  import flash.net.NetConnection;
  import flash.net.NetStream;
  
  import org.bigbluebutton.common.LogUtil;
  import org.bigbluebutton.core.BBB;
  import org.bigbluebutton.modules.videodock.Util;
  import org.bigbluebutton.modules.videodock.events.BroadcastStreamEvent;
  import org.bigbluebutton.modules.videodock.model.VideoConfOptions;

  public class BroadcastStream
  {
    private var _nc:NetConnection;
    private var _dispatcher:IEventDispatcher;
    private var _ns:NetStream;
    private var _stream:String;
    private var _userID:String;
    
    public function BroadcastStream(userID:String, nc:NetConnection, dispatcher:IEventDispatcher)
    {
      _userID = userID;
      _nc = nc;
      _dispatcher = dispatcher;
    }
    
    public function publish(camera:Camera, stream:String, videoOptions:VideoConfOptions):void {
      _ns = new NetStream(_nc);
      _ns.addEventListener(NetStatusEvent.NET_STATUS, onNetStatus);
      _ns.addEventListener(IOErrorEvent.IO_ERROR, onIOError);
      _ns.addEventListener(AsyncErrorEvent.ASYNC_ERROR, onAsyncError);
      _ns.client = this;
      _ns.attachCamera(camera);
      if ((BBB.getFlashPlayerVersion() >= 11) && videoOptions.enableH264) {
        LogUtil.info("Using H264 codec for video.");
        _ns.videoStreamSettings = Util.getH264VideoStreamSettings(videoOptions);
      }
      
      _stream = stream;
      
      _ns.publish(stream);
    }
       
    public function stop():void{
      if (_ns != null) {
        _ns.attachCamera(null);
        _ns.close();
        _ns = null;
      }			
    }
    
    private function onIOError(event:NetStatusEvent):void {
      LogUtil.debug("onIOError on [" + _stream + "]");
      var evt:BroadcastStreamEvent = new BroadcastStreamEvent(BroadcastStreamEvent.STOPPED);
      evt.userID = _userID;
      evt.stream = _stream;
      
      _dispatcher.dispatchEvent(evt);
    }
    
    private function onAsyncError(event:AsyncErrorEvent):void {
      LogUtil.debug("onAsyncError on [" + _stream + "]");
      var evt:BroadcastStreamEvent = new BroadcastStreamEvent(BroadcastStreamEvent.STOPPED);
      evt.userID = _userID;
      evt.stream = _stream;
      
      _dispatcher.dispatchEvent(evt);
    }
    
    private function onNetStatus(e:NetStatusEvent):void{
      switch(e.info.code){
        case "NetStream.Publish.Start":
          LogUtil.debug("Start for broadcast stream [" + _stream + "]");
          break;
        case "NetStream.Play.UnpublishNotify":
          LogUtil.debug("Stopped for broadcast stream [" + _stream + "]");
          break;
        default:
          LogUtil.debug("Broadcast stream status [" + e.info.code + "] for [" + _userID + "] [" + _stream + "]");
      }
    }
    
    private function onAsyncError(e:AsyncErrorEvent):void{
      LogUtil.debug("[AsyncErrorEvent] for [" + _userID + "] [" + _stream + "]");
    }
  }
}