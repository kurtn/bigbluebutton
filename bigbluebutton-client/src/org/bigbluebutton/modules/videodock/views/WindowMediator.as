package org.bigbluebutton.modules.videodock.views
{
  import flash.events.MouseEvent;
  import flash.geom.Point;
  import flash.media.Video;
  
  import mx.core.UIComponent;
  
  import org.bigbluebutton.common.Images;
  import org.bigbluebutton.core.UsersUtil;

  public class WindowMediator
  {
    static public var PADDING_HORIZONTAL:Number = 6;
    static public var PADDING_VERTICAL:Number = 29;
    
    private var _videoHolder:UIComponent;
    // images must be static because it needs to be created *before* the PublishWindow creation
    static protected var images:Images = new Images();
       
    private var _minWidth:int = 160 + PADDING_HORIZONTAL;
    private var _minHeight:int = 120 + PADDING_VERTICAL;
    private var aspectRatio:Number = 1;
    private var keepAspect:Boolean = false;
    private var originalWidth:Number;
    private var originalHeight:Number;
        
    public var streamName:String;
    
    public var sharerUserID:String = null;
    
    protected var _controlButtons:ControlButtons = new ControlButtons();
    
    [Bindable] public var resolutions:Array;
    
    private var _window:WebcamWindow;
    
    public function WindowMediator(window:WebcamWindow) {
      _window = window;  
    }
    protected function switchRole(presenter:Boolean):void {
      _controlButtons.handleNewRoleEvent(presenter);
    }
    
    protected function getVideoResolution(stream:String):Array {
      var pattern:RegExp = new RegExp("(\\d+x\\d+)-([A-Za-z0-9]+)-\\d+", "");
      if (pattern.test(stream)) {
//        LogUtil.debug("The stream name is well formatted [" + stream + "]");
        var uid:String = UsersUtil.getMyUserID();
//        LogUtil.debug("Stream resolution is [" + pattern.exec(stream)[1] + "]");
//        LogUtil.debug("Userid [" + pattern.exec(stream)[2] + "]");
        sharerUserID = pattern.exec(stream)[2];
        addControlButtons();
        return pattern.exec(stream)[1].split("x");
      } else {
//        LogUtil.error("The stream name doesn't follow the pattern <width>x<height>-<userId>-<timestamp>.");
//        LogUtil.error("However, the video resolution will be set to the lowest defined resolution in the config.xml: " + resolutions[0]);
        return resolutions[0].split("x");
      }
    }
    
    protected function get paddingVertical():Number {
      return _window.borderMetrics.top + _window.borderMetrics.bottom;
    }
    
    protected function get paddingHorizontal():Number {
      return _window.borderMetrics.left + _window.borderMetrics.right;
    }
    
    static private var RESIZING_DIRECTION_UNKNOWN:int = 0; 
    static private var RESIZING_DIRECTION_VERTICAL:int = 1; 
    static private var RESIZING_DIRECTION_HORIZONTAL:int = 2; 
    static private var RESIZING_DIRECTION_BOTH:int = 3;
    private var resizeDirection:int = RESIZING_DIRECTION_BOTH;
    
    /**
     * when the window is resized by the user, the application doesn't know about the resize direction
     */
    public function onResizeStart():void {
      resizeDirection = RESIZING_DIRECTION_UNKNOWN;
    }
    
    /**
     * after the resize ends, the direction is set to BOTH because of the non-user resize actions - like when the 
     * window is docked, and so on
     */
    public function onResizeEnd():void {
      resizeDirection = RESIZING_DIRECTION_BOTH;
    }
    
    protected function onResize():void {
      if (_videoHolder == null || _window.minimized) return;
      
      // limits the window size to the parent size
      _window.width = (_window.parent != null? Math.min(_window.width, _window.parent.width): _window.width);
      _window.height = (_window.parent != null? Math.min(_window.height, _window.parent.height): _window.height); 
      
      var tmpWidth:Number = _window.width - PADDING_HORIZONTAL;
      var tmpHeight:Number = _window.height - PADDING_VERTICAL;
      
      // try to discover in which direction the user is resizing the window
      if (resizeDirection != RESIZING_DIRECTION_BOTH) {
        if (tmpWidth == _videoHolder.width && tmpHeight != _videoHolder.height)
          resizeDirection = (resizeDirection == RESIZING_DIRECTION_VERTICAL || resizeDirection == RESIZING_DIRECTION_UNKNOWN? RESIZING_DIRECTION_VERTICAL: RESIZING_DIRECTION_BOTH);
        else if (tmpWidth != _videoHolder.width && tmpHeight == _videoHolder.height)
          resizeDirection = (resizeDirection == RESIZING_DIRECTION_HORIZONTAL || resizeDirection == RESIZING_DIRECTION_UNKNOWN? RESIZING_DIRECTION_HORIZONTAL: RESIZING_DIRECTION_BOTH);
        else
          resizeDirection = RESIZING_DIRECTION_BOTH;
      }
      
      // depending on the direction, the tmp size is different
      switch (resizeDirection) {
        case RESIZING_DIRECTION_VERTICAL:
          tmpWidth = Math.floor(tmpHeight * aspectRatio);
          break;
        case RESIZING_DIRECTION_HORIZONTAL:
          tmpHeight = Math.floor(tmpWidth / aspectRatio);
          break;
        case RESIZING_DIRECTION_BOTH:
          // this direction is used also for non-user window resize actions
          tmpWidth = Math.min (tmpWidth, Math.floor(tmpHeight * aspectRatio));
          tmpHeight = Math.min (tmpHeight, Math.floor(tmpWidth / aspectRatio));
          break;
      }
      
      _videoHolder.width = tmpWidth;
      _videoHolder.height = tmpHeight;
      
      if (!keepAspect || _window.maximized) {
        // center the video in the window
        _videoHolder.x = Math.floor ((_window.width - PADDING_HORIZONTAL - tmpWidth) / 2);
        _videoHolder.y = Math.floor ((_window.height - PADDING_VERTICAL - tmpHeight) / 2);
      } else {
        // fit window dimensions on video
        _videoHolder.x = 0;
        _videoHolder.y = 0;
        _window.width = tmpWidth + PADDING_HORIZONTAL;
        _window.height = tmpHeight + PADDING_VERTICAL;
      }
      
      // reposition the window to fit inside the parent window
      if (_window.parent != null) {
        if (_window.x + _window.width > _window.parent.width)
          _window.x = this._window.width - _window.width;
        if (_window.x < 0)
          _window.x = 0;
        if (_window.y + _window.height > _window.parent.height)
          _window.y = _window.parent.height - _window.height;
        if (_window.y < 0)
          _window.y = 0;
      }
      
      updateButtonsPosition();
    }
    
    public function updateWidth():void {
      _window.width = Math.floor((_window.height - paddingVertical) * aspectRatio) + paddingHorizontal;
      onResize();
    }
    
    public function updateHeight():void {
      _window.height = Math.floor((_window.width - paddingHorizontal) / aspectRatio) + paddingVertical;
      onResize();
    }
    
    protected function setAspectRatio(width:int,height:int):void {
      aspectRatio = (width/height);
      _window.minHeight = Math.floor((_window.minWidth - PADDING_HORIZONTAL) / aspectRatio) + PADDING_VERTICAL;
    }

/*
    public function getPrefferedPosition():String{
      if (_controlButtonsEnabled)
        return MainCanvas.POPUP;
      else
        // the window is docked, so it should not be moved on reset layout
        return MainCanvas.ABSOLUTE;
    }
    
    override public function close(event:MouseEvent = null):void{
      var e:CloseWindowEvent = new CloseWindowEvent();
      e.window = this;
      dispatchEvent(e);
      
      super.close(event);
    }
*/
    
    private var _controlButtonsEnabled:Boolean = true;
    
    private var img_unlock_keep_aspect:Class = images.lock_open;
    private var img_lock_keep_aspect:Class = images.lock_close;
    private var img_fit_video:Class = images.arrow_in;
    private var img_original_size:Class = images.shape_handles;
    private var img_mute_icon:Class = images.sound_mute;
    private var signOutIcon:Class = images.signOutIcon;
    private var adminIcon:Class = images.admin;
    private var chatIcon:Class = images.chatIcon;
    
    [Bindable]
    protected var avatar:Class = images.avatar;
    
    protected function addControlButtons():void {
      _controlButtons.sharerUserID = sharerUserID;
      _controlButtons.visible = true;
      _window.addChild(_controlButtons);
    }
    
    protected function get controlButtons():ControlButtons {
      if (_controlButtons == null) {				
        _controlButtons.visible = false;							
      } 
      return _controlButtons;
    }
    
    protected function createButtons():void {      
      // creates the window keeping the aspect ratio 
      onKeepAspectClick();
    }
    
    protected function updateButtonsPosition():void {
      if (controlButtons.visible == false) {
        controlButtons.y = controlButtons.x = 0;
      } else {
        controlButtons.y = _videoHolder.y + _videoHolder.height - controlButtons.height - controlButtons.padding;
        controlButtons.x = _videoHolder.x + _videoHolder.width - controlButtons.width - controlButtons.padding;
      }
    }
    
    protected function showButtons(event:MouseEvent = null):void {
      if (_controlButtonsEnabled && controlButtons.visible == false) {
        controlButtons.visible = true;
        updateButtonsPosition();
      }
    }
    
    protected function hideButtons(event:MouseEvent = null):void {
      if (_controlButtonsEnabled && controlButtons.visible == true) {
        controlButtons.visible = false;
        updateButtonsPosition();
      }
    }
    
    protected function onDoubleClick(event:MouseEvent = null):void {
      // it occurs when the window is docked, for example
      if (! _window.maximizeRestoreBtn.visible) return;
      
      this.maximizeRestore();
    }
    
    public function maximizeRestore(event:MouseEvent = null):void {
      // if the user is maximizing the window, the control buttons should disappear
      buttonsEnabled = _window.maximized;
      _window.maximizeRestore(event);
    }
    
    public function set buttonsEnabled(enabled:Boolean):void {
      if (!enabled) 
        hideButtons();
      _controlButtonsEnabled = enabled;
    }
    
    protected function onOriginalSizeClick(event:MouseEvent = null):void {
      _videoHolder.width = originalWidth;
      _videoHolder.height = originalHeight;
      onFitVideoClick();
    }		
    
    protected function onFitVideoClick(event:MouseEvent = null):void {
      var newWidth:int = _videoHolder.width + paddingHorizontal;
      var newHeight:int = _videoHolder.height + paddingVertical;
      
      _window.x += (_window.width - newWidth)/2;
      _window.y += (_window.height - newHeight)/2;
      _window.width = newWidth;
      _window.height = newHeight;
      onResize();
    }
    
    protected function onKeepAspectClick(event:MouseEvent = null):void {
      keepAspect = !keepAspect;
      
      onFitVideoClick();
    }
  }
}