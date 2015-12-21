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
import android.widget.ArrayAdapter;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

public class BrowseRegistryListener extends DefaultRegistryListener {

    private final Activity mActivity;
    private final ArrayAdapter<DeviceDisplay> mListAdapter;

    public BrowseRegistryListener(Activity activity, ArrayAdapter<DeviceDisplay> listAdapter) {
        mActivity = activity;
        mListAdapter = listAdapter;
    }

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        deviceAdded(device);
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        deviceRemoved(device);
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device) {
        deviceAdded(device);
    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice device) {
        deviceRemoved(device);
    }

    public void deviceAdded(final Device device) {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                DeviceDisplay d = new DeviceDisplay(device);
                int position = mListAdapter.getPosition(d);
                if (position >= 0) {
                    // Device already in the list, re-set new value at same position
                    mListAdapter.remove(d);
                    mListAdapter.insert(d, position);
                } else {
                    mListAdapter.add(d);
                }
            }
        });
    }

    public void deviceRemoved(final Device device) {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mListAdapter.remove(new DeviceDisplay(device));
            }
        });
    }
}