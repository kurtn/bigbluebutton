package org.bigbluebutton.modules.videodock.views
{
  import com.asfusion.mate.events.Dispatcher;
  
  import flash.events.AsyncErrorEvent;
  import flash.events.NetStatusEvent;
  import flash.media.Video;
  import flash.net.NetConnection;
  import flash.net.NetStream;
  
  import org.bigbluebutton.common.LogUtil;
  import org.bigbluebutton.main.events.BBBEvent;

  public class SubscribeStream
  {
    private var ns:NetStream;
    private var metadataListener:Function;
    
    public function SubscribeStream(connection:NetConnection)
    {
      ns = new NetStream(connection);
      ns.addEventListener(NetStatusEvent.NET_STATUS, onNetStatus);
      ns.addEventListener(AsyncErrorEvent.ASYNC_ERROR, onAsyncError);
      ns.client = this;
      ns.bufferTime = 0;
      ns.receiveVideo(true);
      ns.receiveAudio(false);
    }
    
    public function attachVideo(video:Video):void {
      video.attachNetStream(ns);
    }
    
    private var stream:String;
    
    public function play(stream:String):void {
      this.stream = stream;
      LogUtil.debug("************* Playing webcam stream[" + stream + "]");
      ns.play(stream);
    }
    
    public function close():void {
      ns.close();
    }
    
    public function onMetaData(info:Object):void{
      LogUtil.debug("************************** metadata: width=" + info.width + " height=" + info.height);
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