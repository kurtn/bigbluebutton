package org.bigbluebutton.modules.videodock.maps
{
  import flash.media.Video;
  
  import mx.collections.ArrayCollection;
  
  import org.bigbluebutton.modules.videodock.business.VideoWindowItf;
  import org.bigbluebutton.modules.videodock.views.VideoWindow;
  import org.bigbluebutton.modules.videodock.views.WebcamWindow;

  public class WindowManager
  {
    private var webcamWindows:ArrayCollection = new ArrayCollection();
    
    public function addWindow(userID:String):WebcamWindow {      
      var win:WebcamWindow = new WebcamWindow();
      win.userID = userID;
      webcamWindows.addItem(win);
    
      return win;
    }
    
    public function removeWindow(userID:String):WebcamWindow {
      for (var i:int = 0; i < webcamWindows.length; i++) {
        var win:WebcamWindow = webcamWindows.removeItemAt(i) as WebcamWindow;
        if (win.userID == userID) return win;
      }      
      
      return null;
    }
    
    public function hasWindow(userID:String):Boolean {
      for (var i:int = 0; i < webcamWindows.length; i++) {
        var win:WebcamWindow = webcamWindows.getItemAt(i) as WebcamWindow;
        if (win.userID == userID) return true;
      }
      
      return false;
    }

    public function getWindow(userID:String):WebcamWindow {
      for (var i:int = 0; i < webcamWindows.length; i++) {
        var win:WebcamWindow = webcamWindows.getItemAt(i) as WebcamWindow;
        if (win.userID == userID) return win;
      }      
      
      return null;      
    }
  }
}