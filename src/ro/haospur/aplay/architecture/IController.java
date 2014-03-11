package ro.haospur.aplay.architecture;

import java.util.List;

public interface IController {

    /**
     * @param path
     * @return The files at the specified location.
     */
    List<String> listDirectory(String path);
}