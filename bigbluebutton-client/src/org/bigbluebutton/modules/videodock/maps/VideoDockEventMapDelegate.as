package org.bigbluebutton.modules.videodock.maps
{
  import flash.events.IEventDispatcher;
  
  import mx.collections.ArrayCollection;
  
  import org.bigbluebutton.common.LogUtil;
  import org.bigbluebutton.common.events.OpenWindowEvent;
  import org.bigbluebutton.common.events.ToolbarButtonEvent;
  import org.bigbluebutton.core.UsersUtil;
  import org.bigbluebutton.main.model.users.events.BroadcastStartedEvent;
  import org.bigbluebutton.main.model.users.events.BroadcastStoppedEvent;
  import org.bigbluebutton.modules.videodock.events.ConnectionEvent;
  import org.bigbluebutton.modules.videodock.events.OpenVideoWindowEvent;
  import org.bigbluebutton.modules.videodock.events.StartBroadcastEvent;
  import org.bigbluebutton.modules.videodock.model.VideoConfOptions;
  import org.bigbluebutton.modules.videodock.views.PublishWindow;
  import org.bigbluebutton.modules.videodock.views.SubscribeStream;
  import org.bigbluebutton.modules.videodock.views.ToolbarButton;
  import org.bigbluebutton.modules.videodock.views.VideoDock;
  import org.bigbluebutton.modules.videodock.views.VideoWindow;
  import org.bigbluebutton.modules.videodock.views.WebcamWindow;

  public class VideoDockEventMapDelegate
  {
    private var _dispatcher:IEventDispatcher;
    private var _conn:Connection;
    private var _options:VideoConfOptions = new VideoConfOptions();
    private var videoDock:VideoDock;
    private var toolbarButton:ToolbarButton;
    
    private var webcamWindows:WindowManager = new WindowManager();
    private var publishWindow:PublishWindow;
    
    public function VideoDockEventMapDelegate(dispatcher:IEventDispatcher)
    {
      _dispatcher = dispatcher;
    }
    
    public function start():void {      
      _conn =  new Connection(_dispatcher);
      _conn.connect(_options.uri);
    }
    
    public function connectionUp(event:ConnectionEvent):void {
      videoDock = new VideoDock();			
      addToolbarButton();
      openVideoDockWindow();
    }
    
    public function openWebcamWindows():void {
      var uids:ArrayCollection = UsersUtil.getUserIDs();
      
      for (var i:int = 0; i < uids.length; i++) {
        var u:String = uids.getItemAt(i) as String;
        
//        if ((! _options.displayAllUsers && UsersUtil.hasWebcamStream(u)) || _options.displayAllUsers) {
          openWebcamWindow(u);
//        }       
      }
    }
/*    
    private function openPublishWindow():void{
      publishWindow = new PublishWindow();
      publishWindow.quality = _options.videoQuality;
      publishWindow.resolutions = _options.resolutions.split(",");
      
      var windowEvent:OpenWindowEvent = new OpenWindowEvent(OpenWindowEvent.OPEN_WINDOW_EVENT);
      windowEvent.window = publishWindow;
      _dispatcher.dispatchEvent(windowEvent);
    }
    
    private function closePublishWindow():void{
      publishWindow.close();
    }
    
    private function startPublishing(e:StartBroadcastEvent):void{
      proxy.startPublishing(e);
      var broadcastEvent:BroadcastStartedEvent = new BroadcastStartedEvent();
      broadcastEvent.stream = e.stream;
      broadcastEvent.userid = UsersUtil.getMyUserID();
      _dispatcher.dispatchEvent(broadcastEvent);
      publishWindow.title = UsersUtil.getMyUsername() + " (you)";
      _publishing = true;
      toolbarButton.publishingStatus(toolbarButton.START_PUBLISHING);
      
    }
    
    private function stopPublishing(e:StopBroadcastEvent):void{
      if (_publishing) {
        proxy.stopBroadcasting();
        
        var broadcastEvent:BroadcastStoppedEvent = new BroadcastStoppedEvent();
        broadcastEvent.stream = publishWindow.streamName;
        broadcastEvent.userid = UsersUtil.getMyUserID();
        _dispatcher.dispatchEvent(broadcastEvent);
        _publishing = false;
      }
      
      //Make toolbar button enabled again
      
      toolbarButton.publishingStatus(toolbarButton.STOP_PUBLISHING);
      //button.show();
    }
*/    
    private function openWebcamWindow(userID:String):void {
//      if (UsersUtil.isMe(userID)) return;
      
      if (webcamWindows.hasWindow(userID)) return;
      
      var window:WebcamWindow = webcamWindows.addWindow(userID);
      window.userID = userID;
      window.title = UsersUtil.getUserName(userID);
      
      LogUtil.debug("*************** OPENING WINDOW FOR [" + userID + "]");
      openWidow(window);
      dockWindow(window);
      playWebcamStream(window, userID);
  
    }
    
    private function playWebcamStream(window:WebcamWindow, userID:String):void {
      if (UsersUtil.hasWebcamStream(userID)) {
        var subscribeStream:SubscribeStream = new SubscribeStream();
        _conn.attach(subscribeStream);
        var streamName:String = UsersUtil.getWebcamStream(userID);
        if (streamName != null) {
//          window.startVideo(subscribeStream, streamName);
        }
      }        
    }
    
    private function openWidow(window:WebcamWindow):void {
      var windowEvent:OpenWindowEvent = new OpenWindowEvent(OpenWindowEvent.OPEN_WINDOW_EVENT);
      windowEvent.window = window;
      _dispatcher.dispatchEvent(windowEvent);      
    }
    
    private function dockWindow(window:WebcamWindow):void {
      // this event will dock the window, if it's enabled
      var openVideoEvent:OpenVideoWindowEvent = new OpenVideoWindowEvent();
      openVideoEvent.window = window;
      _dispatcher.dispatchEvent(openVideoEvent);         
    }
    
    private function openVideoDockWindow():void {      
      var windowEvent:OpenWindowEvent = new OpenWindowEvent(OpenWindowEvent.OPEN_WINDOW_EVENT);
      windowEvent.window = videoDock;
      _dispatcher.dispatchEvent(windowEvent);      
    }
    
    private function addToolbarButton():void {           
      if (_options.showButton) {
        toolbarButton = new ToolbarButton();	  
        toolbarButton.isPresenter = ! _options.presenterShareOnly;
        var event:ToolbarButtonEvent = new ToolbarButtonEvent(ToolbarButtonEvent.ADD);
        event.button = toolbarButton;
        _dispatcher.dispatchEvent(event);
      }
    }
  }
}