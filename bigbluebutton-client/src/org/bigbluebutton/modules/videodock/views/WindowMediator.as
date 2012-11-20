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
    
    private var _displayContainer:UIComponent;
    
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
    
//    private var _controlButtons:ControlButtons = new ControlButtons();
    
    [Bindable] public var resolutions:Array;
    
    private var _window:WebcamWindow;
    
    public function WindowMediator(window:WebcamWindow) {
      _window = window;  
    }
    
    public function setDisplayContainer(display:UIComponent):void {
      _displayContainer = display;
    }
           
    private function get paddingVertical():Number {
      return _window.borderMetrics.top + _window.borderMetrics.bottom;
    }
    
    private function get paddingHorizontal():Number {
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
    
    private function onResize():void {
      if (_displayContainer == null || _window.minimized) return;
      
      // limits the window size to the parent size
      _window.width = (_window.parent != null? Math.min(_window.width, _window.parent.width): _window.width);
      _window.height = (_window.parent != null? Math.min(_window.height, _window.parent.height): _window.height); 
      
      var tmpWidth:Number = _window.width - PADDING_HORIZONTAL;
      var tmpHeight:Number = _window.height - PADDING_VERTICAL;
      
      // try to discover in which direction the user is resizing the window
      if (resizeDirection != RESIZING_DIRECTION_BOTH) {
        if (tmpWidth == _displayContainer.width && tmpHeight != _displayContainer.height)
          resizeDirection = (resizeDirection == RESIZING_DIRECTION_VERTICAL || resizeDirection == RESIZING_DIRECTION_UNKNOWN? RESIZING_DIRECTION_VERTICAL: RESIZING_DIRECTION_BOTH);
        else if (tmpWidth != _displayContainer.width && tmpHeight == _displayContainer.height)
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
      
      _displayContainer.width = tmpWidth;
      _displayContainer.height = tmpHeight;
      
      if (!keepAspect || _window.maximized) {
        // center the video in the window
        _displayContainer.x = Math.floor ((_window.width - PADDING_HORIZONTAL - tmpWidth) / 2);
        _displayContainer.y = Math.floor ((_window.height - PADDING_VERTICAL - tmpHeight) / 2);
      } else {
        // fit window dimensions on video
        _displayContainer.x = 0;
        _displayContainer.y = 0;
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
      
//      updateButtonsPosition();
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
  
    private var _controlButtonsEnabled:Boolean = true;
    
    
    [Bindable]
    protected var avatar:Class = images.avatar;
    
    protected function addControlButtons():void {
//      _controlButtons.sharerUserID = sharerUserID;
//      _controlButtons.visible = true;
//      _window.addChild(_controlButtons);
    }
    
    protected function get controlButtons():ControlButtons {
//      if (_controlButtons == null) {				
//        _controlButtons.visible = false;							
//      } 
 //     return _controlButtons;
      
      return null;
    }
    
    protected function createButtons():void {      
      // creates the window keeping the aspect ratio 
      onKeepAspectClick();
    }
            
    protected function onOriginalSizeClick(event:MouseEvent = null):void {
      _displayContainer.width = originalWidth;
      _displayContainer.height = originalHeight;
      onFitVideoClick();
    }		
    
    protected function onFitVideoClick(event:MouseEvent = null):void {
      var newWidth:int = _displayContainer.width + paddingHorizontal;
      var newHeight:int = _displayContainer.height + paddingVertical;
      
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