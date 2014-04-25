package ro.haospur.aplay.architecture.presentation;

import java.util.List;

import ro.haospur.aplay.R;
import ro.haospur.aplay.architecture.IController;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

public class GUIFrontController extends Activity implements ServiceConnection {

    private EditText fView_in_directoryToList;
    private Switch fView_sel_controllerConnectionState;
    private Button fView_act_listDirectory;
    private ListView fView_out_filesInDirectory;
    private ArrayAdapter<String> fView_out_filesInDirectory_adapter;
    private State fControllerConnectionState;

    // The client-side representation of the Controller object.
    private IController fController;

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
        // We've bound to a service that we know should be running in this current process / address space
        updateConnectionStatus((IController) service, State.BOUND_AND_CONNECTED);
    }

    // Called when the connection with the service is UNEXPECTEDLY lost, not when the client unbinds deliberately.
    @Override
    public void onServiceDisconnected(ComponentName className) {
        // The process hosting the service has crashed or has been killed at this point. As we're sharing the same process, we should never see this happen.
        updateConnectionStatus(null, State.BOUND);
    }

    private void updateConnectionStatus(IController newController, State newState) {
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
        List<String> dataSet = fController.listDirectory(fView_in_directoryToList.getText().toString());
        fView_out_filesInDirectory_adapter.clear();
        fView_out_filesInDirectory_adapter.addAll(dataSet);
    }
}
