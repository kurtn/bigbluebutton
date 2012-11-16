package org.bigbluebutton.modules.videodock.views
{
  public class WindowMediator
  {
    protected var _video:Video;
    protected var _videoHolder:UIComponent;
    // images must be static because it needs to be created *before* the PublishWindow creation
    static protected var images:Images = new Images();
    
    static public var PADDING_HORIZONTAL:Number = 6;
    static public var PADDING_VERTICAL:Number = 29;
    protected var _minWidth:int = 160 + PADDING_HORIZONTAL;
    protected var _minHeight:int = 120 + PADDING_VERTICAL;
    protected var aspectRatio:Number = 1;
    protected var keepAspect:Boolean = false;
    protected var originalWidth:Number;
    protected var originalHeight:Number;
    
    protected var mousePositionOnDragStart:Point;
    
    public var streamName:String;
    
    public var sharerUserID:String = null;
    
    protected var _controlButtons:ControlButtons = new ControlButtons();
    
    [Bindable] public var resolutions:Array;
    
    protected function switchRole(presenter:Boolean):void {
      _controlButtons.handleNewRoleEvent(presenter);
    }
    
    protected function getVideoResolution(stream:String):Array {
      var pattern:RegExp = new RegExp("(\\d+x\\d+)-([A-Za-z0-9]+)-\\d+", "");
      if (pattern.test(stream)) {
        LogUtil.debug("The stream name is well formatted [" + stream + "]");
        var uid:String = UsersUtil.getMyUserID();
        LogUtil.debug("Stream resolution is [" + pattern.exec(stream)[1] + "]");
        LogUtil.debug("Userid [" + pattern.exec(stream)[2] + "]");
        sharerUserID = pattern.exec(stream)[2];
        addControlButtons();
        return pattern.exec(stream)[1].split("x");
      } else {
        LogUtil.error("The stream name doesn't follow the pattern <width>x<height>-<userId>-<timestamp>.");
        LogUtil.error("However, the video resolution will be set to the lowest defined resolution in the config.xml: " + resolutions[0]);
        return resolutions[0].split("x");
      }
    }
    
    protected function get paddingVertical():Number {
      return this.borderMetrics.top + this.borderMetrics.bottom;
    }
    
    protected function get paddingHorizontal():Number {
      return this.borderMetrics.left + this.borderMetrics.right;
    }
    
    static private var RESIZING_DIRECTION_UNKNOWN:int = 0; 
    static private var RESIZING_DIRECTION_VERTICAL:int = 1; 
    static private var RESIZING_DIRECTION_HORIZONTAL:int = 2; 
    static private var RESIZING_DIRECTION_BOTH:int = 3;
    private var resizeDirection:int = RESIZING_DIRECTION_BOTH;
    
    /**
     * when the window is resized by the user, the application doesn't know about the resize direction
     */
    public function onResizeStart(event:MDIWindowEvent = null):void {
      resizeDirection = RESIZING_DIRECTION_UNKNOWN;
    }
    
    /**
     * after the resize ends, the direction is set to BOTH because of the non-user resize actions - like when the 
     * window is docked, and so on
     */
    public function onResizeEnd(event:MDIWindowEvent = null):void {
      resizeDirection = RESIZING_DIRECTION_BOTH;
    }
    
    protected function onResize():void {
      if (_video == null || _videoHolder == null || this.minimized) return;
      
      // limits the window size to the parent size
      this.width = (this.parent != null? Math.min(this.width, this.parent.width): this.width);
      this.height = (this.parent != null? Math.min(this.height, this.parent.height): this.height); 
      
      var tmpWidth:Number = this.width - PADDING_HORIZONTAL;
      var tmpHeight:Number = this.height - PADDING_VERTICAL;
      
      // try to discover in which direction the user is resizing the window
      if (resizeDirection != RESIZING_DIRECTION_BOTH) {
        if (tmpWidth == _video.width && tmpHeight != _video.height)
          resizeDirection = (resizeDirection == RESIZING_DIRECTION_VERTICAL || resizeDirection == RESIZING_DIRECTION_UNKNOWN? RESIZING_DIRECTION_VERTICAL: RESIZING_DIRECTION_BOTH);
        else if (tmpWidth != _video.width && tmpHeight == _video.height)
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
      
      _video.width = _videoHolder.width = tmpWidth;
      _video.height = _videoHolder.height = tmpHeight;
      
      if (!keepAspect || this.maximized) {
        // center the video in the window
        _video.x = Math.floor ((this.width - PADDING_HORIZONTAL - tmpWidth) / 2);
        _video.y = Math.floor ((this.height - PADDING_VERTICAL - tmpHeight) / 2);
      } else {
        // fit window dimensions on video
        _video.x = 0;
        _video.y = 0;
        this.width = tmpWidth + PADDING_HORIZONTAL;
        this.height = tmpHeight + PADDING_VERTICAL;
      }
      
      // reposition the window to fit inside the parent window
      if (this.parent != null) {
        if (this.x + this.width > this.parent.width)
          this.x = this.parent.width - this.width;
        if (this.x < 0)
          this.x = 0;
        if (this.y + this.height > this.parent.height)
          this.y = this.parent.height - this.height;
        if (this.y < 0)
          this.y = 0;
      }
      
      updateButtonsPosition();
    }
    
    public function updateWidth():void {
      this.width = Math.floor((this.height - paddingVertical) * aspectRatio) + paddingHorizontal;
      onResize();
    }
    
    public function updateHeight():void {
      this.height = Math.floor((this.width - paddingHorizontal) / aspectRatio) + paddingVertical;
      onResize();
    }
    
    protected function setAspectRatio(width:int,height:int):void {
      aspectRatio = (width/height);
      this.minHeight = Math.floor((this.minWidth - PADDING_HORIZONTAL) / aspectRatio) + PADDING_VERTICAL;
    }
    
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
      this.addChild(_controlButtons);
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
        controlButtons.y = _video.y + _video.height - controlButtons.height - controlButtons.padding;
        controlButtons.x = _video.x + _video.width - controlButtons.width - controlButtons.padding;
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
      if (!this.maximizeRestoreBtn.visible) return;
      
      this.maximizeRestore();
    }
    
    override public function maximizeRestore(event:MouseEvent = null):void {
      // if the user is maximizing the window, the control buttons should disappear
      buttonsEnabled = this.maximized;
      super.maximizeRestore(event);
    }
    
    public function set buttonsEnabled(enabled:Boolean):void {
      if (!enabled) 
        hideButtons();
      _controlButtonsEnabled = enabled;
    }
    
    protected function onOriginalSizeClick(event:MouseEvent = null):void {
      _video.width = _videoHolder.width = originalWidth;
      _video.height = _videoHolder.height = originalHeight;
      onFitVideoClick();
    }		
    
    protected function onFitVideoClick(event:MouseEvent = null):void {
      var newWidth:int = _video.width + paddingHorizontal;
      var newHeight:int = _video.height + paddingVertical;
      
      this.x += (this.width - newWidth)/2;
      this.y += (this.height - newHeight)/2;
      this.width = newWidth;
      this.height = newHeight;
      onResize();
    }
    
    protected function onKeepAspectClick(event:MouseEvent = null):void {
      keepAspect = !keepAspect;
      
      onFitVideoClick();
    }
  }
}