package ru.dithard.dithardroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v4.content.LocalBroadcastManager;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BT";
    TextView text_X, text_Y;
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothSocket mBluetoothSocket = null;
    BluetoothDevice mBluetoothDevice = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private OutputStream outStream = null;

    List<String> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Magic???
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        Log.d(TAG, "Started TankUI");

        // Init textboxes
        text_Y = (TextView) findViewById(R.id.textY);
        text_X = (TextView) findViewById(R.id.textX);

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "joystick-event".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_X,
                new IntentFilter("joystick-motion-x"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver_Y,
                new IntentFilter("joystick-motion-y"));

        //Мы хотим использовать тот bluetooth-адаптер, который задается по умолчанию
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }

        //Включаем bluetooth. Если он уже включен, то ничего не произойдет
        String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
        startActivityForResult(new Intent(enableBT), 0);
        enumerateBTdevices();
        btListDialog();
    }

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "joystick-event" is broadcasted.
    private BroadcastReceiver mMessageReceiver_X = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: X=" + message);
            text_X.setText(message);
            sendData(message);
        }
    };

    private BroadcastReceiver mMessageReceiver_Y = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: Y=" + message);
            text_Y.setText(message);
            sendData(message);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                return true;
            case R.id.settings:
                //
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_X);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver_Y);
        super.onDestroy();
    }

    protected void enumerateBTdevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mArrayAdapter = new ArrayList<String>();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
        Log.e(TAG, mArrayAdapter.toString());
    }

    //    protected void findDevices() {
//        // Create a BroadcastReceiver for ACTION_FOUND
//        private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                // When discovery finds a device
//                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                    // Get the BluetoothDevice object from the Intent
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    // Add the name and address to an array adapter to show in a ListView
//                    mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//                }
//            }
//        };
//        // Register the BroadcastReceiver
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
//    }
    private void btListDialog() {
        final String[] arr = mArrayAdapter.toArray(new String[mArrayAdapter.size()]);
        if (arr[0] == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.setTitle("BT List");
        builder.setItems(arr, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String macaddr = mArrayAdapter.get(which).split("\n")[1];
                Toast.makeText(getApplicationContext(),
                        "Выбранное устройство: " + macaddr,
                        Toast.LENGTH_LONG).show();
                connect(macaddr);

            }
        });
            // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void connect(String macAddress){
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(macAddress);
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        mBluetoothAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Соединяемся...");
        try {
            mBluetoothSocket.connect();
            Log.d(TAG, "...Соединение установлено и готово к передачи данных...");
            // Create a data stream so we can talk to server.
            Log.d(TAG, "...Создание Socket...");

            try {
                outStream = mBluetoothSocket.getOutputStream();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
            }
        } catch (IOException e) {
            try {
                mBluetoothSocket.close();
                Log.e(TAG, "...Соединение  не установлено... " + e.getMessage());
                Log.e(TAG, "*** device = " + mBluetoothDevice.getName() + "***");
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        Log.e(TAG, title + " - " + message);
        finish();
    }
    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Посылаем данные: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            errorExit("Fatal Error", msg);
        }
    }
}