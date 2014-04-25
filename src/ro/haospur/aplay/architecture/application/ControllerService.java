package ro.haospur.aplay.architecture.application;

import ro.haospur.aplay.R;
import ro.haospur.aplay.architecture.presentation.GUIFrontController;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.widget.Toast;

/**
 * REMOTE service exposing the Controller as a Messenger (an IBinder abstraction)
 *
 * This service and the Controller it exposes may run in a separate process / address space than their clients. As such, the clients will
 * use the Controller via IPC: they will communicate with it by delivering "messages" the Controller receives in its handleMessage() callback.
 */
public class ControllerService extends Service {

    // Target we publish for other objects to send messages to us. This allows for the implementation of message-based IPC,
    // by creating a Messenger (pointing to a Handler) in one process, and handing that Messenger to another process.
    private final Messenger fMessenger = new Messenger(new Handler(new Controller()));

    // The object that receives interactions from clients and will route their messages to its Messenger, which  will further route them to its Handler.
    private final IBinder fBinder = fMessenger.getBinder();

    private NotificationManager fNotificationManager;

    @Override
    public void onCreate() {

        fNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Create the notification (set the icon, scrolling text, timestamp, etc)
        Notification notification = new Notification.Builder(this).
                setSmallIcon(R.drawable.ic_launcher).
                setTicker("tikerText: service_started").
                setContentTitle("contentTitle: service_label").
                setContentText("contentText: service_started").
                setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, GUIFrontController.class), 0)).
                setWhen(System.currentTimeMillis()).build();

        // Send the notification in the status bar
        fNotificationManager.notify(getClass().getName().hashCode(), notification);
        Toast.makeText(getApplicationContext(), "The service is being created.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        // Cancel all previously shown notifications
        fNotificationManager.cancelAll();
        Toast.makeText(getApplicationContext(), "The service is being destroyed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return fBinder;
    }
}
