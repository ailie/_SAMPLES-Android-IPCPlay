package ro.haospur.aplay.architecture.presentation;

import java.util.List;

import ro.haospur.aplay.R;
import ro.haospur.aplay.architecture.IController;
import ro.haospur.aplay.architecture.application.Controller;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

public class GUIFrontController extends Activity {

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
        updateConnectionStatus(Controller.INSTANCE, State.BOUND_AND_CONNECTED);
    }

    // Disconnect with the Controller
    private void unbind() {
        if (fControllerConnectionState == State.UNBOUND) {
            return;
        }
        updateConnectionStatus(null, State.UNBOUND);
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
