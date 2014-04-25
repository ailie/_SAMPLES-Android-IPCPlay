package ro.haospur.aplay.architecture.presentation;


import ro.haospur.aplay.R;
import ro.haospur.aplay.architecture.ControllerPublicMethod;
import ro.haospur.aplay.architecture.IController;
import ro.haospur.aplay.commons.MethodArgument;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

public class GUIFrontController extends Activity implements ServiceConnection, Handler.Callback {

    private EditText fView_in_directoryToList;
    private Switch fView_sel_controllerConnectionState;
    private Button fView_act_listDirectory;
    private ListView fView_out_filesInDirectory;
    private ArrayAdapter<String> fView_out_filesInDirectory_adapter;
    private State fControllerConnectionState;

    // The client-side representation of the Controller object.
    private Messenger fController;

    // Target we publish for other objects to send messages to us. This allows for the implementation of message-based IPC,
    // by creating a Messenger (pointing to a Handler) in one process, and handing that Messenger to another process.
    private final Messenger fMessenger = new Messenger(new Handler(this));

    @Override
    public boolean handleMessage(Message responseMessage) {

        Bundle responseMessagePayload = responseMessage.getData();

        switch (ControllerPublicMethod.values()[responseMessage.what]) {

        case DELETE_FILES:
            // unimplemented yet
            break;

        case LIST_DIRECTORY:
            // Extract from the (reply) Message the output arguments for the method call.
            fView_out_filesInDirectory_adapter.clear();
            fView_out_filesInDirectory_adapter.addAll(responseMessagePayload.getStringArray(MethodArgument.ARG00.toString()));
            break;

        case COPY_FILES:
            // unimplemented yet
            break;

        default:
            return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getApplicationContext(), "The activity is being created.", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.main_activity);

        fView_sel_controllerConnectionState = (Switch) findViewById(R.id.switch1);

        fView_in_directoryToList = (EditText) findViewById(R.id.editText1);

        fView_act_listDirectory = (Button) findViewById(R.id.button1);

        fView_out_filesInDirectory = (ListView) findViewById(R.id.listView1);
        fView_out_filesInDirectory_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        fView_out_filesInDirectory.setAdapter(fView_out_filesInDirectory_adapter);

        updateConnectionStatus(null, State.UNBOUND);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "The activity is being RE-started.", Toast.LENGTH_SHORT).show();
        if (fView_sel_controllerConnectionState.isChecked()) {
            bind();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(), "The activity is being stopped.", Toast.LENGTH_SHORT).show();
        unbind();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "The activity is being destroyed.", Toast.LENGTH_SHORT).show();
    }

    // Connect with the Controller
    private void bind() {
        if (fControllerConnectionState != State.UNBOUND) {
            return;
        }
        Intent service = new Intent().
                setPackage(getPackageName()).
                addCategory(IController.class.getName());
        bindService(service, this, Context.BIND_AUTO_CREATE);
        updateConnectionStatus(null, State.BOUND);
    }

    // Disconnect with the Controller
    private void unbind() {
        if (fControllerConnectionState == State.UNBOUND) {
            return;
        }
        updateConnectionStatus(null, State.UNBOUND);
        unbindService(this);
    }

    // Called when the connection with the service has been established, giving us the IBinder implementation provided by the service.
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
        // We've bound to a service that may be running in another process / address space
        updateConnectionStatus(new Messenger(service), State.BOUND_AND_CONNECTED);
    }

    // Called when the connection with the service is UNEXPECTEDLY lost, not when the client unbinds deliberately.
    @Override
    public void onServiceDisconnected(ComponentName className) {
        // The process hosting the service has crashed or has been killed at this point.
        updateConnectionStatus(null, State.BOUND);
    }

    private void updateConnectionStatus(Messenger newController, State newState) {
        fController = newController;
        fControllerConnectionState = newState;
        fView_act_listDirectory.setEnabled(fController != null);
        Toast.makeText(getApplicationContext(), newState.toString(), Toast.LENGTH_SHORT).show();
    }

    public void handleHI_controllerConnectionSwitch(View v) {
        if (fView_sel_controllerConnectionState.isChecked()) {
            bind();
        } else {
            unbind();
        }
    }

    public void handleHI_listButton(View v) {
        try {
            Bundle requestMessagePayload = new Bundle();
            requestMessagePayload.putString(MethodArgument.ARG01.toString(), fView_in_directoryToList.getText().toString());

            Message requestMessage = Message.obtain();
            requestMessage.what = ControllerPublicMethod.LIST_DIRECTORY.ordinal();
            requestMessage.replyTo = fMessenger;
            requestMessage.setData(requestMessagePayload);
            fController.send(requestMessage);
        } catch (RemoteException e) {
            Log.e(getClass().getSimpleName(), "RemoteException", e);
        }
    }
}
