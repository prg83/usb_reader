package com.example.usb_reader;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    PendingIntent mPermissionIntent;
    UsbManager usbManager;
    TextView mytext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView mytext = findViewById(R.id.MytextView);
        setSupportActionBar(toolbar);

        verifyStoragePermissions(this);

       /* try {
            USB();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }*/

        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        final String ACTION_USB_PERMISSION =
                "com.android.example.USB_PERMISSION";

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                try {
                    USB();
                } catch (IOException | InterruptedException e) {

                    Toast toast = Toast.makeText(getApplicationContext(),
                            e.getMessage(),
                            Toast.LENGTH_LONG);

                    toast.show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void USB() throws IOException, InterruptedException {
        UsbManager usbManager;
        UsbDevice clef;
        ArrayList<File> images;
        Toast toast;

        mytext = findViewById(R.id.MytextView);


        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        /*String ACTION_USB_PERMISSION =   "com.android.example.USB_PERMISSION";

        PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_UPDATE_CURRENT);
       // IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);*/



        clef = null;

        if (usbManager != null)
        {
            HashMap<String,UsbDevice> deviceList = usbManager.getDeviceList();
            if (deviceList != null)
            {
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                while (deviceIterator.hasNext()) {
                    clef = deviceIterator.next();
                }
            }
        }

        if (clef != null)
        {

            usbManager.requestPermission(clef, mPermissionIntent);
        toast = Toast.makeText(getApplicationContext(),
                clef.getDeviceName(),
        Toast.LENGTH_LONG);

        mytext.setText(clef.toString());
            /*toast = Toast.makeText(getApplicationContext(),
                    Environment.getExternalStorageDirectory().toString(),
                    Toast.LENGTH_LONG);*/


            toast.show();


            ///clef.

            /*UsbDeviceConnection connection = usbManager.openDevice(clef);

            mytext.setText(connection.toString());

            UsbInterface intf = clef.getInterface(0);
            UsbEndpoint endpoint = intf.getEndpoint(0);
            connection.claimInterface(intf, true);

            toast = Toast.makeText(getApplicationContext(),
                    connection.toString(),
                    Toast.LENGTH_LONG);

            toast.show();

            byte[] bytes = new byte[64];
            connection.bulkTransfer(endpoint, bytes, 64, 1);*/


          /*  File directory  = new File(clef.getDeviceName()+ "/TEST.txt");
            if (directory != null) {


                toast = Toast.makeText(getApplicationContext(),
                        " in directory",
                        Toast.LENGTH_LONG);

                toast.show();

                RandomAccessFile file = new RandomAccessFile(directory, "r");

                file.seek(0x13);


                int low = file.readUnsignedByte();
                int high = file.readUnsignedByte();

                int sectorsOnDisk = low + (high * 256);

                toast = Toast.makeText(getApplicationContext(),
                        Integer.toString(sectorsOnDisk) + " sectors",
                        Toast.LENGTH_LONG);

                toast.show();
                if (directory.canRead()) {

                    toast = Toast.makeText(getApplicationContext(),
                            "hello flash",
                            Toast.LENGTH_LONG);

                    toast.show();


                }else{

                    toast = Toast.makeText(getApplicationContext(),
                            "Can't read dir",
                            Toast.LENGTH_LONG);

                    toast.show();

                }
            }*/
        }else{

            toast = Toast.makeText(getApplicationContext(),
                    "NO flash here",
                    Toast.LENGTH_LONG);

            toast.show();
        }

    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private static final String ACTION_USB_PERMISSION =
            "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){

                            UsbDeviceConnection connection = usbManager.openDevice(device);

                            mytext.setText(connection.toString());

                            UsbInterface intf = device.getInterface(0);
                            UsbEndpoint endpoint = intf.getEndpoint(0);
                            connection.claimInterface(intf, true);

                            Toast toast = Toast.makeText(getApplicationContext(),
                                    connection.toString(),
                                    Toast.LENGTH_LONG);

                            toast.show();

                            byte[] bytes = new byte[64];
                            connection.bulkTransfer(endpoint, bytes, 64, 1);

                            toast = Toast.makeText(getApplicationContext(),
                                    device.getDeviceName(),
                                    Toast.LENGTH_LONG);
                            toast.show();


                            File directory  = new File(device.getDeviceName()+ "/TEST.txt");
                            if (directory != null) {


                                toast = Toast.makeText(getApplicationContext(),
                                        " in directory",
                                        Toast.LENGTH_LONG);

                                toast.show();

                                RandomAccessFile file = null;

                                try {

                                    file = new RandomAccessFile(directory, "r");

                                    file.seek(0x13);

                                int low = 0;

                                    low = file.readUnsignedByte();

                                int high = file.readUnsignedByte();

                                int sectorsOnDisk = low + (high * 256);

                                toast = Toast.makeText(getApplicationContext(),
                                        Integer.toString(sectorsOnDisk) + " sectors",
                                        Toast.LENGTH_LONG);

                                } catch (IOException e) {
                                    toast = Toast.makeText(getApplicationContext(),
                                            e.getMessage(),
                                            Toast.LENGTH_LONG);

                                    toast.show();
                                    e.printStackTrace();
                                }

                                toast.show();
                                if (directory.canRead()) {

                                    toast = Toast.makeText(getApplicationContext(),
                                            "hello flash",
                                            Toast.LENGTH_LONG);

                                    toast.show();


                                }else{

                                    toast = Toast.makeText(getApplicationContext(),
                                            "Can't read dir",
                                            Toast.LENGTH_LONG);

                                    toast.show();

                                }
                            }

                        }
                    }
                    else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "permission denied for device " + device,
                                Toast.LENGTH_LONG);

                        toast.show();
                        //Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };
}