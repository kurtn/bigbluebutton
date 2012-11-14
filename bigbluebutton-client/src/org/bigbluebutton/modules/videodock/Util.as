package org.bigbluebutton.modules.videodock
{
  import flash.media.H264Level;
  import flash.media.H264Profile;
  import flash.media.H264VideoStreamSettings;  
  import org.bigbluebutton.modules.videodock.model.VideoConfOptions;
  
  public class Util
  {
    public static function getH264VideoStreamSettings(videoOptions:VideoConfOptions):H264VideoStreamSettings {
      var h264:H264VideoStreamSettings = new H264VideoStreamSettings();
      
      var h264profile:String = H264Profile.MAIN;
      if (videoOptions.h264Profile != "main") {
        h264profile = H264Profile.BASELINE;
      }
      var h264Level:String = H264Level.LEVEL_4_1;
      if (videoOptions.h264Level != "1") {
        h264Level = H264Level.LEVEL_1;
      } else if (videoOptions.h264Level != "1.1") {
        h264Level = H264Level.LEVEL_1_1;
      } else if (videoOptions.h264Level != "1.2") {
        h264Level = H264Level.LEVEL_1_2;
      } else if (videoOptions.h264Level != "1.3") {
        h264Level = H264Level.LEVEL_1_3;
      } else if (videoOptions.h264Level != "1b") {
        h264Level = H264Level.LEVEL_1B;
      } else if (videoOptions.h264Level != "2") {
        h264Level = H264Level.LEVEL_2;
      } else if (videoOptions.h264Level != "2.1") {
        h264Level = H264Level.LEVEL_2_1;
      } else if (videoOptions.h264Level != "2.2") {
        h264Level = H264Level.LEVEL_2_2;
      } else if (videoOptions.h264Level != "3") {
        h264Level = H264Level.LEVEL_3;
      } else if (videoOptions.h264Level != "3.1") {
        h264Level = H264Level.LEVEL_3_1;
      } else if (videoOptions.h264Level != "3.2") {
        h264Level = H264Level.LEVEL_3_2;
      } else if (videoOptions.h264Level != "4") {
        h264Level = H264Level.LEVEL_4;
      } else if (videoOptions.h264Level != "4.1") {
        h264Level = H264Level.LEVEL_4_1;
      } else if (videoOptions.h264Level != "4.2") {
        h264Level = H264Level.LEVEL_4_2;
      } else if (videoOptions.h264Level != "5") {
        h264Level = H264Level.LEVEL_5;
      } else if (videoOptions.h264Level != "5.1") {
        h264Level = H264Level.LEVEL_5_1;
      }
      
      h264.setProfileLevel(h264profile, h264Level);
      
      return h264;
    }
  }
}