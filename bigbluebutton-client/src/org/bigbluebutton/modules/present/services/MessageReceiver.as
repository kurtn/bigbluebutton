package org.bigbluebutton.modules.present.services
{
  import org.bigbluebutton.common.LogUtil;
  import org.bigbluebutton.core.BBB;
  import org.bigbluebutton.main.model.users.IMessageListener;

  public class MessageReceiver implements IMessageListener
  {
    public function MessageReceiver()
    {
      BBB.initConnectionManager().addMessageListener(this);
    }
    
    public function onMessage(messageName:String, message:Object):void {
 //     LogUtil.debug("Presentation: received message " + messageName);
    }  
  }
}