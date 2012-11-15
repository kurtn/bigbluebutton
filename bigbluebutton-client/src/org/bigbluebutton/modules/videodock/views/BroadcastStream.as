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
  import org.bigbluebutton.modules.videodock.model.VideoConfOptions;

  public class BroadcastStream
  {
    private var _nc:NetConnection;
    private var _dispatcher:IEventDispatcher;
    private var _ns:NetStream;
    
    public function BroadcastStream(nc:NetConnection, dispatcher:IEventDispatcher)
    {
      _nc = nc;
      _dispatcher = dispatcher;
    }
    
    public function publish(camera:Camera, stream:String, videoOptions:VideoConfOptions):void{
      _ns = new NetStream(_nc);
      _ns.addEventListener(NetStatusEvent.NET_STATUS, onNetStatus );
      _ns.addEventListener(IOErrorEvent.IO_ERROR, onIOError );
      _ns.addEventListener(AsyncErrorEvent.ASYNC_ERROR, onAsyncError );
      _ns.client = this;
      _ns.attachCamera(camera);
      if ((BBB.getFlashPlayerVersion() >= 11) && videoOptions.enableH264) {
        LogUtil.info("Using H264 codec for video.");
        _ns.videoStreamSettings = Util.getH264VideoStreamSettings(videoOptions);
      }
      
      _ns.publish(stream);
    }
       
    public function stop():void{
      if (_ns != null) {
        _ns.attachCamera(null);
        _ns.close();
        _ns = null;
      }			
    }
    
    private function onNetStatus(e:NetStatusEvent):void{
      switch(e.info.code){
        case "NetStream.Publish.Start":
          LogUtil.debug("******************** NetStream.Publish.Start for broadcast stream " + stream + "]");
          break;
        case "NetStream.Play.UnpublishNotify":
          close();
          break;
        case "NetStream.Play.Start":
          LogUtil.debug("Netstatus: " + e.info.code);
          var globalDispatcher:Dispatcher = new Dispatcher();
          
          globalDispatcher.dispatchEvent(new BBBEvent(BBBEvent.VIDEO_STARTED));						
          break;
        case "NetStream.Play.FileStructureInvalid":
          LogUtil.debug("The MP4's file structure is invalid.");
          break;
        case "NetStream.Play.NoSupportedTrackFound":
          LogUtil.debug("The MP4 doesn't contain any supported tracks");
          break;
      }
    }
    
    private function onAsyncError(e:AsyncErrorEvent):void{
      LogUtil.debug("VideoWindow::asyncerror " + e.toString());
    }
  }
}