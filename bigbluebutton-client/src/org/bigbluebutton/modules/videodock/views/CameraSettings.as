package org.bigbluebutton.modules.videodock.views
{
  import flash.events.ActivityEvent;
  import flash.events.StatusEvent;
  import flash.events.TimerEvent;
  import flash.media.Camera;
  import flash.utils.Timer;  
  import org.bigbluebutton.core.UsersUtil;
  import org.bigbluebutton.modules.videodock.model.VideoConfOptions;

  public class CameraSettings
  {
    public var camWidth:Number = 320;
    public var camHeight:Number = 240;
    private var _camera:Camera = null;
    
    // Timer to auto-publish webcam. We need this timer to delay the auto-publishing until after the Viewers's window has loaded
    // to receive the publishing events. Otherwise, the user joining next won't be able to view the webcam.
    private var autoPublishTimer:Timer = null;
    
    // Timer used to enable the start publishing button, only after get any activity on the camera. It avoids the problem of publishing
    // a blank video
    private var _activationTimer:Timer = null;
    private var _waitingForActivation:Boolean = false;
    
    static private var _cameraAccessDenied:Boolean = false;
    
    private var showWarning:Function;
    
    public function CameraSettings(showWarning:Function)
    {
      this.showWarning = showWarning;
    }
    
    public function useCamera(name:String):void {
      var videoOptions:VideoConfOptions = new VideoConfOptions();
      
      _camera = Camera.getCamera(name);
      if (_camera == null) {
        showWarning('bbb.video.publish.hint.cantOpenCamera');
        return;
      }
      
      _camera.setMotionLevel(5, 1000);
      
      if (_camera.muted) {
        if (_cameraAccessDenied) {
          onCameraAccessDisallowed();
          return;
        } else {
          showWarning('bbb.video.publish.hint.waitingApproval');
        }
      } else {
        // if the camera isn't muted, that is because the user has
        // previously allowed the camera capture on the flash privacy box
        onCameraAccessAllowed();
      }
      
      _camera.addEventListener(ActivityEvent.ACTIVITY, onActivityEvent);
      _camera.addEventListener(StatusEvent.STATUS, onStatusEvent);
      
      
      
      _camera.setKeyFrameInterval(videoOptions.camKeyFrameInterval);
      _camera.setMode(camWidth, camHeight, videoOptions.camModeFps);
      _camera.setQuality(videoOptions.camQualityBandwidth, videoOptions.camQualityPicture);
      
      if (_camera.width != camWidth || _camera.height != camHeight) {
//        LogUtil.debug("Resolution " + camWidth + "x" + camHeight + " is not supported, using " + _camera.width + "x" + _camera.height + " instead");
//        setResolution(_camera.width, _camera.height);
      }	
    }
    
    private function onActivityEvent(e:ActivityEvent):void {
      if (_waitingForActivation && e.activating) {
        _activationTimer.stop();
        showWarning('bbb.video.publish.hint.videoPreview', false, "0xFFFF00");
//        controls.btnStartPublish.enabled = true;
        _waitingForActivation = false;
      }
    }
    
    private function onStatusEvent(e:StatusEvent):void {
      if (e.code == "Camera.Unmuted") {
        onCameraAccessAllowed();
        // this is just to overwrite the message of waiting for approval
        showWarning('bbb.video.publish.hint.openingCamera');
      } else if (e.code == "Camera.Muted") {
        onCameraAccessDisallowed();
      }
    }
    
    private function onCameraAccessAllowed():void {
      // set timer to ensure that the camera activates.  If not, it might be in use by another application
      _waitingForActivation = true;
      if (_activationTimer != null) {
        _activationTimer.stop();
      }
        
      
      _activationTimer = new Timer(10000, 1);
      _activationTimer.addEventListener(TimerEvent.TIMER, activationTimeout);
      _activationTimer.start();
    }
    
    private function onCameraAccessDisallowed():void {
      showWarning('bbb.video.publish.hint.cameraDenied');
      _cameraAccessDenied = true;
    }
    
    private function activationTimeout(e:TimerEvent):void {
      showWarning('bbb.video.publish.hint.cameraIsBeingUsed');
      // it will try to reopen the camera after the timeout
//      updateCamera();
    }     
  }
}