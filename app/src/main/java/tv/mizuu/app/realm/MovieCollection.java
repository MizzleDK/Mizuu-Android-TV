package tv.mizuu.app.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class MovieCollection extends RealmObject {

        @PrimaryKey
        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }