package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
//import android.arch.lifecycle.ViewModelProvider;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.series.DataPoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    BluetoothAdapter myBluetoothAdapter;
    String TAG = "MainActivity";
    Button connectButton;
    Button pressureButton;
    TextView status;
    Boolean bluetoothOn = false;
    Button b5min;
    Button b10min;
    Button b15min;
    Switch modeSelect;
    Switch deviceSwitch;
    SeekBar vibrationLevel;

//    LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
//            // on below line we are adding
//            // each point on our x and y axis.
//            new DataPoint(0, 0)
//    });

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    private static final String APP_NAME = "My Application";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //  UUID for redmi: 62baece8-4c41-4fe6-8cf2-d1db8648f4e6
//    UUID for samsung = ac43a296-4e44-4ffc-88bd-200c70fadc16
    BluetoothDevice[] btArray = new BluetoothDevice[10];

    SendReceive sendReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        layoutInstantiation();
        layoutFunctioning();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            Log.i(TAG, "ALl Devices:" + myBluetoothAdapter.getBondedDevices());
//        }
//        BluetoothDevice hc06 = myBluetoothAdapter.getRemoteDevice("00:14:03:05:59:30");
//
//        Log.i(TAG, "name:" + hc06.getName());
//
//        try {
//            BluetoothSocket socket = hc06.createRfcommSocketToServiceRecord(MY_UUID);
//            Log.i(TAG, "Socket:" + socket);
//            socket.connect();
//            Log.i(TAG, "Connected:" + socket.isConnected());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void layoutInstantiation() {
        status = findViewById(R.id.status);
        connectButton = findViewById(R.id.b_connect);
        pressureButton = findViewById(R.id.b_pressure);
        b5min = findViewById(R.id.b_5min);
        b10min = findViewById(R.id.b_10min);
        b15min = findViewById(R.id.b_15min);
        modeSelect = findViewById(R.id.modeSwitch);
        vibrationLevel = findViewById(R.id.seekBar);
        deviceSwitch = findViewById(R.id.deviceSwitch);
        b5min.setEnabled(false);
        b15min.setEnabled(false);
        b10min.setEnabled(false);
        modeSelect.setEnabled(false);
        pressureButton.setEnabled(false);
        vibrationLevel.setEnabled(false);
        deviceSwitch.setEnabled(false);
    }

    private void layoutFunctioning() {

        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                checkBluetooth();

                if(bluetoothOn) {

                    BluetoothDevice hc06 = myBluetoothAdapter.getRemoteDevice("00:14:03:05:59:30");

//                    scanBluetooth();

                    ServerClass serverClass = new ServerClass();
                    serverClass.start();

                    ClientClass clientClass = new ClientClass(hc06);
                    clientClass.start();

                    status.setText("Connecting...");
                } else {
                    Toast.makeText(getApplicationContext(), "Turn your bluetooth ON sanskar", Toast.LENGTH_LONG).show();
                }
            }
        });

        pressureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                String string = "1";
//                sendReceive.write(string.getBytes());
//                DataRepo.getInstance().seriesDataRepo = series;
                callSecondActivity(view);
            }
        });

        b5min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b5min.setEnabled(false);
                b10min.setEnabled(true);
                b15min.setEnabled(true);
                Toast.makeText(getApplicationContext(), "TURN VIBRATION MODE ON TO START THE THERAPY", Toast.LENGTH_SHORT).show();
                String string = "A";
                sendReceive.write(string.getBytes());
            }
        });
        b10min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b5min.setEnabled(true);
                b10min.setEnabled(false);
                b15min.setEnabled(true);
                Toast.makeText(getApplicationContext(), "TURN VIBRATION MODE ON TO START THE THERAPY", Toast.LENGTH_SHORT).show();
                String string = "B";
                sendReceive.write(string.getBytes());
            }
        });
        b15min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b5min.setEnabled(true);
                b10min.setEnabled(true);
                b15min.setEnabled(false);
                Toast.makeText(getApplicationContext(), "TURN VIBRATION MODE ON TO START THE THERAPY", Toast.LENGTH_SHORT).show();
                String string = "C";
                sendReceive.write(string.getBytes());
            }
        });

        modeSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    b5min.setEnabled(false);
                    b15min.setEnabled(false);
                    b10min.setEnabled(false);
                    vibrationLevel.setEnabled(true);
                    String string = "V";
                    sendReceive.write(string.getBytes());
                } else {
                    b5min.setEnabled(true);
                    b15min.setEnabled(true);
                    b10min.setEnabled(true);
                    vibrationLevel.setEnabled(false);
                    String string = "T";
                    sendReceive.write(string.getBytes());
                }
            }
        });

        vibrationLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int speed;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //TODO
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String string = Integer.toString(speed);
                sendReceive.write(string.getBytes());
            }
        });

        deviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    buttonView.setText("TURN DEVICE OFF");
                    pressureButton.setEnabled(true);
                    b5min.setEnabled(true);
                    b10min.setEnabled(true);
                    b15min.setEnabled(true);
                    modeSelect.setEnabled(true);
                    String string = "O";
                    sendReceive.write(string.getBytes());
                } else {
                    buttonView.setText("TURN DEVICE ON");
                    b5min.setEnabled(false);
                    b15min.setEnabled(false);
                    b10min.setEnabled(false);
                    modeSelect.setChecked(false);
                    modeSelect.setEnabled(false);
                    pressureButton.setEnabled(false);
                    vibrationLevel.setProgress(0);
                    vibrationLevel.setEnabled(false);
                    String string = "F";
                    sendReceive.write(string.getBytes());
                }
            }
        });
    }

    public void callSecondActivity(View view){
        Intent i = new Intent(getApplicationContext(), PressureGraph.class);
        startActivity(i);
    }

    private void checkBluetooth() {
        if (myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not supported on this device!!", Toast.LENGTH_LONG).show();
        } else {
            if (!myBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(), "Please turn ON your bluetooth!!", Toast.LENGTH_LONG).show();
            } else {
//                Log.i(TAG, "Bluetooth is ON sanskar");
                bluetoothOn = true;
            }
        }
    }

    private void scanBluetooth() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Set<BluetoothDevice> bt = myBluetoothAdapter.getBondedDevices();
            String[] strings = new String[bt.size()];
            int index = 0;

            if (bt.size() > 0) {
                for (BluetoothDevice device : bt) {
//                    if (Objects.equals(device.getName(), "STLP-0525")) {
//                        Log.i(TAG, "Device Found sanskar!!");
//                        myDevice = device;
//                    }
                    strings[index] = device.getName();
                    btArray[index] = device;
                    index++;
                }
            }
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {

                case STATE_LISTENING:
                    status.setText("Listening...");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting...");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    connectButton.setEnabled(false);
                    deviceSwitch.setEnabled(true);
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed!!");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff,0, msg.arg1);
                    char ch = tempMsg.charAt(0);
                    double time = (double) (System.currentTimeMillis() / 1000);
                    DataRepo.getInstance().seriesDataRepo.appendData(new DataPoint(time, (int)ch), true, 100);
//                    System.out.println(series.isEmpty());
                    System.out.println((int)ch);
                    break;
            }
            return true;
        }
    });

    private class ServerClass extends Thread {

        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    serverSocket = myBluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
                    Log.i(TAG, "I am here99");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            BluetoothSocket socket;

            while (true) {

                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                    Log.i(TAG, "I am here100");
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    Log.i(TAG, "I am here1");
                    handler.sendMessage(message);
                    break;
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    Log.i(TAG, "I am here4");
                    handler.sendMessage(message);
                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread {

        BluetoothDevice device;
        BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {

            device = device1;
            try {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            try {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.i(TAG, "I am here");
                    socket.connect();
                    Log.i(TAG, "Connected:"+socket.isConnected());
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                }

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread {

        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket) {
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            do {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (true);
        }

        public void write(byte[] bytes) {

            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}