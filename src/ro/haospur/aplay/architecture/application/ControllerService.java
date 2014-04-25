package ro.haospur.aplay.architecture.application;

import ro.haospur.aplay.R;
import ro.haospur.aplay.architecture.presentation.GUIFrontController;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * LOCAL service exposing the Controller directly as an IBinder, for later downcasting
 *
 * This service and the Controller it exposes should run in the same process / address space as their clients. As such, the clients will
 * be able to downcast the IBinder they receive to an IController, for doing local object interaction.
 */
public class ControllerService extends Service {

    // The object that receives interactions from clients
    private final IBinder fBinder = new Controller();

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
