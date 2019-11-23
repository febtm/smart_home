package com.example.home.ubidots;

import android.app.ActionBar;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ubidots.ApiClient;
import com.ubidots.Value;
import com.ubidots.Variable;


public class MainActivity extends ActionBarActivity {

    //Creating View variables
    private ToggleButton CONNECT_BUTTON;
    private ImageView PRESENCE;
    private Switch MAINSWITCH;
    private Switch DEVICESWITCH;
    private TextView CONNECT_TEXT,PRESENCE_STATUS;
    private ImageView MAIN_SWITCH_IMG,DEVICE_SWITCH_IMG;

    private int mInterval = 10000; // 5 seconds by default, can be changed later
    private Handler mHandler;

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                //Data Retrieval
                new PIRData_ApiUbidots().execute();

                new MainSwitchGet_ApiUbidots().execute();

                new DeviceSwitchGet_ApiUbidots().execute();

                //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.main_actionbar);

        View customActionBarView = actionBar.getCustomView();


        //Assigning Textview and Switches

        CONNECT_BUTTON = (ToggleButton) findViewById(R.id.on_off_button);
        PRESENCE = (ImageView) findViewById(R.id.human_presence_image);
        MAINSWITCH = (Switch)findViewById(R.id.main_switch);
        DEVICESWITCH = (Switch)findViewById(R.id.device_switch);
        CONNECT_TEXT = (TextView)findViewById(R.id.on_off_text);
        PRESENCE_STATUS = (TextView)findViewById(R.id.human_presence_status);
        MAIN_SWITCH_IMG = (ImageView)findViewById(R.id.main_switch_indicator);
        DEVICE_SWITCH_IMG = (ImageView)findViewById(R.id.device_switch_indicator);

        // Calling the classes that get the values and push the values

        //Main Switch Action

        CONNECT_BUTTON.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                    if (CONNECT_BUTTON.isChecked()) {

                        mHandler = new Handler();
                        startRepeatingTask();

                        CONNECT_BUTTON.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));

                        CONNECT_TEXT.setText("ON");

                        MAINSWITCH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    new MainSwitchEnter_ApiUbidots().execute(1);

                                } else {
                                    new MainSwitchEnter_ApiUbidots().execute(0);

                                }
                            }

                        });


                        //Device Switch Action

                        DEVICESWITCH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    new DeviceSwitchEnter_ApiUbidots().execute(1);

                                } else {

                                    new DeviceSwitchEnter_ApiUbidots().execute(0);

                                }
                            }

                        });


                    }else{

                        CONNECT_TEXT.setText("OFF");

                        CONNECT_BUTTON.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));
                    }
                }

        });

    }


//This code is to insert data into the Ubidots Clouds


    public class MainSwitchEnter_ApiUbidots extends AsyncTask<Integer, Void, Void>  {

        private final String API_KEY = "568c685b5e5dbd991c9fc7dfbdd526a594ef355c";
        private final String VARIABLE_ID = "56f673587625427350f62a5e";


        @Override
        protected Void doInBackground(Integer... params) {
            ApiClient apiClient = new ApiClient(API_KEY);
            Variable main_switch = apiClient.getVariable(VARIABLE_ID);

            if(params[0]==1)
                main_switch.saveValue(1);
            else
                main_switch.saveValue(0);

            return null;
        }
    }

    public class DeviceSwitchEnter_ApiUbidots extends AsyncTask<Integer, Void, Void>  {

        private final String API_KEY = "568c685b5e5dbd991c9fc7dfbdd526a594ef355c";
        private final String VARIABLE_ID = "56f673607625427305a7a00a";


        @Override
        protected Void doInBackground(Integer... params) {
            ApiClient apiClient = new ApiClient(API_KEY);
            Variable device_switch = apiClient.getVariable(VARIABLE_ID);

            if(params[0]==1)
                device_switch.saveValue(1);
            else
                device_switch.saveValue(0);

            return null;
        }
    }

    //Human Presence Class

    public class PIRData_ApiUbidots extends AsyncTask<Integer, Void, Value[]> {

        private final String API_KEY = "568c685b5e5dbd991c9fc7dfbdd526a594ef355c";
        private final String PIR_ID = "56f6736f76254273316897b0";

        @Override
        protected Value[] doInBackground(Integer... params) {

            ApiClient apiClient = new ApiClient(API_KEY);
            Variable Pir_data = apiClient.getVariable(PIR_ID);

            Value[] HumanPresence = Pir_data.getValues();

            return HumanPresence;
        }

        @Override
        protected void onPostExecute(Value[] PIRDatavariables) {

            //Getting the latest value pushed by the PIR Sensor
            Double pir_data = PIRDatavariables[0].getValue();

            if (pir_data == 1)
                {
                PRESENCE.setImageDrawable(getResources().getDrawable(R.drawable.green));
                PRESENCE_STATUS.setText("TRUE");
                }
            else
                {
                PRESENCE.setImageDrawable(getResources().getDrawable(R.drawable.red));
                PRESENCE_STATUS.setText("FALSE");
                }
        }

        }


    public class MainSwitchGet_ApiUbidots extends AsyncTask<Integer, Void, Value[]> {

        private final String API_KEY = "568c685b5e5dbd991c9fc7dfbdd526a594ef355c";
        private final String MAIN_SWITCH_ID = "56f673587625427350f62a5e";

        @Override
        protected Value[] doInBackground(Integer... params) {

            ApiClient apiClient = new ApiClient(API_KEY);
            Variable Main_Switch_data = apiClient.getVariable(MAIN_SWITCH_ID);

            Value[] MainSwitch = Main_Switch_data.getValues();

            return MainSwitch;
        }

        @Override
        protected void onPostExecute(Value[] PIRDatavariables) {

            //Getting the latest value pushed by the PIR Sensor
            Double main_switch_data = PIRDatavariables[0].getValue();


            if (main_switch_data == 1.00)
            {
                MAIN_SWITCH_IMG.setBackgroundColor(Color.parseColor("#20bd66"));
            }
            else
            {
                MAIN_SWITCH_IMG.setBackgroundColor(Color.parseColor("#dd2727"));

            }
        }

    }


    public class DeviceSwitchGet_ApiUbidots extends AsyncTask<Integer, Void, Value[]> {

        private final String API_KEY = "568c685b5e5dbd991c9fc7dfbdd526a594ef355c";
        private final String DEVICE_SWITCH_ID = "56f673607625427305a7a00a";
        @Override
        protected Value[] doInBackground(Integer... params) {

            ApiClient apiClient = new ApiClient(API_KEY);
            Variable device_switch_data = apiClient.getVariable(DEVICE_SWITCH_ID);

            Value[] DeviceSwitch = device_switch_data.getValues();

            return DeviceSwitch;
        }

        @Override
        protected void onPostExecute(Value[] PIRDatavariables) {

            //Getting the latest value pushed by the PIR Sensor
            Double device_switch = PIRDatavariables[0].getValue();


            if (device_switch == 1.00)
            {
                DEVICE_SWITCH_IMG.setBackgroundColor(Color.parseColor("#20bd66"));
            }
            else
            {
                DEVICE_SWITCH_IMG.setBackgroundColor(Color.parseColor("#dd2727"));

            }
        }

    }

}
