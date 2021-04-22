package com.app.remote_controller_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothService {
    public BluetoothDevice currentDevice;
    BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothDevice> pairedDevices;

    ConnectedThread connectedThread;
    BluetoothSocket bluetoothSocket;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    public BluetoothService() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<>();
        for(BluetoothDevice bt : mBluetoothAdapter.getBondedDevices()){
            pairedDevices.add(bt);
        }
    }

    public ArrayList<String> getAddressesList(){
        ArrayList<String> devicesList = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices)
            devicesList.add(bt.getAddress());
        return devicesList;
    }

    public String[] getNameList(){
        ArrayList<String> devicesList = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices)
            devicesList.add(bt.getName());
        return devicesList.toArray(new String[devicesList.size()]);
    }

    public void setCurrentDevice(int index) {
        if(index != -1)
            this.currentDevice = pairedDevices.get(index);
        else
            this.currentDevice = null;
    }

    public String getCurrentName(){
        return currentDevice.getName();
    }

    public String getCurrentAddress(){
        return currentDevice.getAddress();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void initializeBluetooth(Handler handler){
        Log.v("[BLUETOOTH]", "Initialize bluetooth");
        try {
            bluetoothSocket = currentDevice.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            Log.i("[BLUETOOTH]","Connected to: " + currentDevice.getName());
        }catch(IOException e){
            try{
                bluetoothSocket.close();
            }catch(IOException c){
                return;
            }
        }


        Log.i("[BLUETOOTH]", "Creating and running Thread");
        connectedThread = new ConnectedThread(bluetoothSocket,handler);
        connectedThread.start();
    }

    public void send(String s){
        connectedThread.write(s.getBytes());
    }

    public void closeBluetooth() throws IOException {
        connectedThread.cancel();
        bluetoothSocket.close();
    }
}