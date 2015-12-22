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
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.FixedAndroidLogHandler;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.container.Container;
import org.seamless.util.logging.LoggingUtil;

import java.util.HashMap;

import static tv.mizuu.app.AnimationUtils.animateTextViewTextChange;

public class AddUpnpSourceActivity extends Activity {

    private Button mRefreshDevicesButton;
    private View mEmptyView;
    private TextView mTitle, mDescription;
    private ListView mListView;
    private ArrayAdapter<DeviceDisplay> mListAdapter;
    private ArrayAdapter<ContainerDisplay> mContainerListAdapter;
    private AndroidUpnpService mUpnpService;
    private BrowseRegistryListener mRegistryListener;
    private ServiceConnection mServiceConnection;
    private DeviceDisplay mSelectedDevice;
    private boolean mBrowsingDevices = true, mBrowsingDevicesLastResult = true, mMadeChanges = false;
    private HashMap<String, String> mParentIdMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_upnp_source);

        mRefreshDevicesButton = (Button) findViewById(R.id.update_devices_button);
        mRefreshDevicesButton.setOnClickListener(mOnRefreshClickListener);
        mEmptyView = findViewById(R.id.no_devices_found);
        mTitle = (TextView) findViewById(R.id.title);
        mDescription = (TextView) findViewById(R.id.description);
        mListView = (ListView) findViewById(R.id.add_video_files_listview);
        mListView.setOnItemClickListener(mOnDeviceClickListener);
        mListView.setOnItemLongClickListener(mOnLongClickListener);
        mListView.setEmptyView(mEmptyView);

        // Fix the logging integration between java.util.logging and Android internal logging
        LoggingUtil.resetRootHandler(new FixedAndroidLogHandler());

        mContainerListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        searchForDevices();
    }

    private View.OnClickListener mOnRefreshClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            animateCompoundDrawable();
            searchForDevices();
        }
    };

    private void animateCompoundDrawable() {
        for (Drawable drawable : mRefreshDevicesButton.getCompoundDrawables()) {
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
            }
        }
    }

    private void searchForDevices() {
        mListView.setAdapter(mListAdapter);

        mRegistryListener = new BrowseRegistryListener(this, mListAdapter);

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
            }

            public void onServiceDisconnected(ComponentName className) {
                mUpnpService = null;
            }
        };

        getApplicationContext().bindService(
                new Intent(this, UpnpService.class),
                mServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    private void updateTitleAndDescription() {
        if (mBrowsingDevicesLastResult != mBrowsingDevices) {
            animateTextViewTextChange(mTitle, 150, mBrowsingDevices ? getString(R.string.add_video_files_title) : getString(R.string.browsing_folders_on) + mSelectedDevice.toString());
            animateTextViewTextChange(mDescription, 150, mBrowsingDevices ? getString(R.string.add_video_files_description) : getString(R.string.long_press_folder_add_file_source));
            mRefreshDevicesButton.setVisibility(mBrowsingDevices ? View.VISIBLE : View.GONE);
        }

        mBrowsingDevicesLastResult = mBrowsingDevices;
    }

    private AdapterView.OnItemClickListener mOnDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mBrowsingDevices) {
                mSelectedDevice = mListAdapter.getItem(position);
                browse(mSelectedDevice, "0");
            } else {
                ContainerDisplay containerDisplay = mContainerListAdapter.getItem(position);
                browse(mSelectedDevice, containerDisplay.getContainer().getId());
            }
        }
    };

    private AdapterView.OnItemLongClickListener mOnLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (mBrowsingDevices || position == 0)
                return false;

            showFileSourceDialog(position);

            return true;
        }
    };

    private void showFileSourceDialog(int position) {
        final Container container = mContainerListAdapter.getItem(position).getContainer();

        if (filesourceExists(container)) {
            // Show an error message if the filesource already exists
            Toast.makeText(AddUpnpSourceActivity.this,
                    getString(R.string.folder_already_exists_as_file_source), Toast.LENGTH_LONG).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_file_source);
        builder.setMessage(String.format(getString(R.string.add_file_source_description),
                container.getTitle()));
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Add the file source
                addFileSourceToDatabase(container);
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No need to do anything as the dialog dismisses itself upon clicking the button
            }
        });
        builder.show();
    }

    private boolean filesourceExists(Container container) {
        //Realm realm = Realm.getInstance(this);
        //long count = realm.where(Filesource.class)
        //        .equalTo("containerId", container.getId())
        //        .equalTo("deviceUdn", mSelectedDevice.getDevice().getIdentity().getUdn().toString())
        //        .count();
        //realm.close();

        //return count > 0;

        // TODO implement this using something other than Realm
        return false;
    }

    private void addFileSourceToDatabase(Container container) {
        // TODO implement this using something other than Realm
        //Realm realm = Realm.getInstance(this);

        // Create the file source
        //realm.beginTransaction();

        //Filesource filesource = realm.createObject(Filesource.class);
        //filesource.setContainerId(container.getId());
        //filesource.setFolderName(container.getTitle());
        //filesource.setDeviceUdn(mSelectedDevice.getDevice().getIdentity().getUdn().toString());
        //filesource.setFilesourceName(mSelectedDevice.toString());

        //realm.commitTransaction();

        // Show success message
        //Toast.makeText(this, getString(R.string.added_file_source), Toast.LENGTH_SHORT).show();

        // The user has added a file source, so we change the flag
        //mMadeChanges = true;

        //realm.close();
    }

    private void browse(DeviceDisplay device, String id) {

        mBrowsingDevices = id == null;

        updateTitleAndDescription();

        if (mBrowsingDevices) {
            mParentIdMap.clear();
            mListView.setAdapter(mListAdapter);
        } else {
            Service foundService = null;
            for (Service s : device.getDevice().getServices())
                for (Action a : s.getActions())
                    if ("browse".equalsIgnoreCase(a.getName())) {
                        foundService = s;
                        break;
                    }

            if (foundService != null) {
                mUpnpService.getControlPoint().execute(
                        new BrowseCallback(foundService, id, BrowseFlag.DIRECT_CHILDREN));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mUpnpService != null) {
            mUpnpService.getRegistry().removeListener(mRegistryListener);
        }

        // This will stop the UPnP service if nobody else is bound to it
        getApplicationContext().unbindService(mServiceConnection);
    }

    private class BrowseCallback extends Browse {

        private final String mContainerId;

        public BrowseCallback(Service service, String containerId, BrowseFlag flag) {
            super(service, containerId, flag);
            mContainerId = containerId;
        }

        @Override
        public void received(ActionInvocation actionInvocation, final DIDLContent didl) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContainerListAdapter.clear();

                    Container c = new Container();
                    c.setTitle("...");
                    c.setId(mParentIdMap.get(mContainerId));
                    mContainerListAdapter.add(new ContainerDisplay(c));

                    for (final Container container : didl.getContainers()) {
                        mParentIdMap.put(container.getId(), container.getParentID());
                        mContainerListAdapter.add(new ContainerDisplay(container));
                    }

                    mListView.setAdapter(mContainerListAdapter);
                    mContainerListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void updateStatus(Status status) {}

        @Override
        public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {}
    }

    @Override
    public void onBackPressed() {
        if (mMadeChanges) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.update_library));
            builder.setMessage(getString(R.string.want_to_update_library));
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Update library...

                    startService(new Intent(AddUpnpSourceActivity.this, LibraryUpdateService.class));
                    finish();
                }
            });
            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        } else {
            super.onBackPressed();
        }
    }
}
