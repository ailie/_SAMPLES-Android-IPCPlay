package ro.haospur.aplay.architecture.application;

import java.util.Arrays;
import java.util.List;

import ro.haospur.aplay.architecture.ControllerPublicMethod;
import ro.haospur.aplay.architecture.IController;
import ro.haospur.aplay.commons.MethodArgument;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;

class Controller implements IController, Handler.Callback {

    @Override
    public List<String> listDirectory(String path) {
        SystemClock.sleep(2000);
        String prefix = path.replaceFirst("/?$", "/");
        return Arrays.asList(prefix + "file123", prefix + "file345");
    }

    @Override
    public boolean handleMessage(Message requestMessage) {

        Bundle requestMessagePayload = requestMessage.getData();

        switch (ControllerPublicMethod.values()[requestMessage.what]) {

        case DELETE_FILES:
            // unimplemented yet
            break;

        case LIST_DIRECTORY:
            // Read the method call arguments (only the in/inout arguments).
            String arg1 = requestMessagePayload.getString(MethodArgument.ARG01.toString());

            // Call the method, saving its return value
            List<String> arg0 = listDirectory(arg1);

            // Write the method call arguments (only the out/inout arguments).
            requestMessagePayload.putStringArray(MethodArgument.ARG00.toString(), arg0.toArray(new String[0]));

            try {
                // Send the original Message, updated, back to to the recipient specified in its replyTo field.
                requestMessage.replyTo.send(requestMessage);
            } catch (RemoteException e) {
                // The recipient didn't receive our call because its process died.
            }
            break;

        case COPY_FILES:
            // unimplemented yet
            break;

        default:
            return false;
        }
        return true;
    }

}
