package net.net76.mannan.callrecorder.model;

/**
 * Created by Shahid on 7/16/2016.
 */
public class CallAllDetails {
    public String myfilepath;
    public String filename;
    public CallAllDetails(String myfilepath, String filename) {
        this.myfilepath= myfilepath;
        this.filename= filename;
    }
    public String getMyfilepath() {
        return myfilepath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setMyfilepath(String myfilepath) {
        this.myfilepath = myfilepath;
    }
}