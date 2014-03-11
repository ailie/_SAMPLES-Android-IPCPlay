package ro.haospur.aplay.architecture.application;

import java.util.Arrays;
import java.util.List;

import ro.haospur.aplay.architecture.IController;
import android.os.SystemClock;

public enum Controller implements IController {

    INSTANCE;

    @Override
    public List<String> listDirectory(String path) {
        SystemClock.sleep(2000);
        String prefix = path.replaceFirst("/?$", "/");
        return Arrays.asList(prefix + "file123", prefix + "file345");
    }

}
