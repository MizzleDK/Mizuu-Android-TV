/*
 * Copyright (c) 2015 Michell Bak
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.mizuu.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Realm realm = Realm.getInstance(this);
        //long filepathCount = realm.where(Filepath.class).count();
        //realm.close();

        // Check if there's any filepaths in the database
        // - if not, show the "Let's get started" screen.
        //if (filepathCount == 0) {
            startActivity(new Intent(this, AddUpnpSourceActivity.class));

            finish();
            return;
        //}

        //setContentView(R.layout.activity_main);
    }
}
