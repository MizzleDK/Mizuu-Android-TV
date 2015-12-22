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

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Michell on 05-04-2015.
 */
public class LibraryUpdateService extends Service {

    private AndroidUpnpService mUpnpService;
    private SimpleBrowseRegistryListener mRegistryListener;
    private ServiceConnection mServiceConnection;
    private List<Filesource> mFilesources = new ArrayList<>();
    private HashSet<String> mUdns = new HashSet<>();
    private Handler mHandler;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mHandler = new Handler(Looper.getMainLooper());

        loadFilesources();

        if (mFilesources.size() > 0) {
            loadFilesFromFilesources();
        }

        // If we get killed, don't restart the service
        return START_NOT_STICKY;
    }

    private void loadFilesources() {
        // TODO Load file sources using something other than Realm
        //Realm realm = Realm.getInstance(this);
        //RealmResults<Filesource> results = realm.where(Filesource.class).findAll();
        //for (Filesource fs : results) {
        //    mFilesources.add(fs);
        //}
        //realm.close();
    }

    private void loadFilesFromFilesources() {

        for (Filesource fs : mFilesources) {
            mUdns.add(fs.getDeviceUdn());
        }

        System.out.println("UDN COUNT: " + mUdns.size());

        sup();
    }

    private void sup() {
        mRegistryListener = new SimpleBrowseRegistryListener();

        mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder service) {
                mUpnpService = (AndroidUpnpService) service;

                // Get ready for future device advertisements
                mUpnpService.getRegistry().addListener(mRegistryListener);

                // Now add all devices to the list we already know about
                for (Device device : mUpnpService.getRegistry().getDevices()) {
                    mRegistryListener.deviceAdded(device);
                }

                // Search asynchronously for all devices
                mUpnpService.getControlPoint().search();

                mUpnpService.getRegistry().getDevice(UDN.valueOf(""), false);
            }

            public void onServiceDisconnected(ComponentName className) {
                mUpnpService = null;
            }
        };

        bindService(
                new Intent(this, UpnpService.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    private class SimpleBrowseRegistryListener extends DefaultRegistryListener {

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
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "DEVICE: " + device.getDetails().getFriendlyName(), Toast.LENGTH_LONG).show();
                }
            });
        }

        public void deviceRemoved(final Device device) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("REMOVED: " + device.getDisplayString());
                }
            });
        }
    }

    private class BrowseCallback extends Browse {

        private final String mContainerId;

        public BrowseCallback(org.fourthline.cling.model.meta.Service service, String containerId, BrowseFlag flag) {
            super(service, containerId, flag);
            mContainerId = containerId;
        }

        @Override
        public void received(ActionInvocation actionInvocation, final DIDLContent didl) {

        }

        @Override
        public void updateStatus(Status status) {}

        @Override
        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {}
    }

}
