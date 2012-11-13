package org.bigbluebutton.modules.videoconf.maps
{
  import flash.media.Video;
  
  import mx.collections.ArrayCollection;
  
  import org.bigbluebutton.modules.videoconf.business.VideoWindowItf;
  import org.bigbluebutton.modules.videoconf.views.VideoWindow;

  public class WindowManager
  {
    private var webcamWindows:ArrayCollection = new ArrayCollection();
    
    public function addWindow(userID:String):VideoWindow {      
      var win:VideoWindow = new VideoWindow();
      win.sharerUserID = userID;
      webcamWindows.addItem(win);
    
      return win;
    }
    
    public function removeWindow(userID:String):VideoWindow {
      for (var i:int = 0; i < webcamWindows.length; i++) {
        var win:VideoWindow = webcamWindows.removeItemAt(i) as VideoWindow;
        if (win.sharerUserID == userID) return win;
      }      
      
      return null;
    }
    
    public function hasWindow(userID:String):Boolean {
      for (var i:int = 0; i < webcamWindows.length; i++) {
        var win:VideoWindow = webcamWindows.getItemAt(i) as VideoWindow;
        if (win.sharerUserID == userID) return true;
      }
      
      return false;
    }

    public function getWindow(userID:String):VideoWindow {
      for (var i:int = 0; i < webcamWindows.length; i++) {
        var win:VideoWindow = webcamWindows.getItemAt(i) as VideoWindow;
        if (win.sharerUserID == userID) return win;
      }      
      
      return null;      
    }
  }
}