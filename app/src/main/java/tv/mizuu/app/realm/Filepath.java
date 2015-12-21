package tv.mizuu.app.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Filepath extends RealmObject {

    @PrimaryKey
    private String filepath;
    private Filesource filesource;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Filesource getFilesource() {
        return filesource;
    }

    public void setFilesource(Filesource filesource) {
        this.filesource = filesource;
    }

}