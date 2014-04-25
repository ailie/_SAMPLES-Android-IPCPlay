package ro.haospur.aplay.architecture;

interface IController {

    /**
     * @param path
     * @return The files at the specified location.
     */
    List<String> listDirectory(String path);
}
