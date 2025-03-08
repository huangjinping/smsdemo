package con.fire.smsdemo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            for (SmsMessage msg : messages) {
                String sender = msg.getOriginatingAddress();
                String body = msg.getMessageBody();
                Log.d("SMS", "收到短信 - 发送者: " + sender + ", 内容: " + body);
            }
        }
    }
}