package com.example.shubham.socketchat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.engineio.client.Socket;
import com.github.nkzawa.socketio.client.IO;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import me.pushy.sdk.Pushy;
import me.pushy.sdk.exceptions.PushyException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String pushyId;
    Button join;
    Button send;
    Button sendNotification;
    EditText sendNumber;
    EditText myNumber;
    EditText message;
    TextView pushyIdText;
    EditText recieverId;
    private com.github.nkzawa.socketio.client.Socket mSocket;

    {
        try {
            mSocket = IO.socket("http://ec2-52-37-49-130.us-west-2.compute.amazonaws.com:8080");
        } catch (URISyntaxException e) {

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        join = (Button) findViewById(R.id.join);
        send = (Button) findViewById(R.id.send);
        sendNumber = (EditText) findViewById(R.id.sendNumber);
        myNumber = (EditText) findViewById(R.id.myNumber);
        message = (EditText) findViewById(R.id.message);
        pushyIdText = (TextView) findViewById(R.id.pushyId);
        sendNotification = (Button) findViewById(R.id.sendNotification);
        recieverId = (EditText) findViewById(R.id.recieverId);
        join.setOnClickListener(this);
        send.setOnClickListener(this);
        sendNotification.setOnClickListener(this);
        mSocket.connect();
        mSocket.on("new_message", onNewMessage);
        registerinPushy();
    }

    private void registerinPushy() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    pushyId = Pushy.register(MainActivity.this);
                } catch (PushyException e) {
                    e.printStackTrace();
                }
                return pushyId;
            }

            @Override
            protected void onPostExecute(String s) {
                if (s != null) {
                    Log.d("TAG", "The pushyId  is " + s);
                    pushyIdText.setText(s);
                }
            }
        }.execute(null, null, null);

    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("TAG", "The basic chat app");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    try {
                        String message = data.getString("msg");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    };


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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.join:
                String phone = myNumber.getText().toString();
                mSocket.emit("join", phone);
                break;

            case R.id.send:
                String sendPhone = sendNumber.getText().toString();
                String msg = message.getText().toString();
                JSONObject object = new JSONObject();
                try {
                    object.put("sendPhone", sendPhone);
                    object.put("msg", msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("send", object);
                break;


            case R.id.sendNotification:
                mSocket.emit("sendNotification", recieverId.getText().toString());
                break;
        }
    }
}
