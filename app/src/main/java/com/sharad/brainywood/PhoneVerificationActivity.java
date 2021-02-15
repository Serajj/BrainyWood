package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sharad.brainywood.Utils.GlobalUrlApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PhoneVerificationActivity extends AppCompatActivity {


    EditText editText_number;

    private static String URL_Register;
    GlobalUrlApi globalUrlApi;


    ProgressBar progress_bar;
    Button send_code_button;

    String verification_code;

    String number;
    //String email;

    LinearLayout verifyLayout, numberLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_phone_verification);

        editText_number = findViewById(R.id.editText_number);
        progress_bar = findViewById(R.id.progress_bar);
        send_code_button = findViewById(R.id.send_verification_button);

        verifyLayout = findViewById(R.id.verifyLayout);
        numberLayout = findViewById(R.id.numberLayout);

        Random random = new Random();

        verification_code = String.format("%04d", random.nextInt(10000));


      //  Intent intent = getIntent();
      //  email = intent.getStringExtra("email");
        // Toast.makeText(this, ""+verification_code, Toast.LENGTH_LONG).show();
    }


    public void SendVerificationCode(View view) {

        progress_bar.setVisibility(View.VISIBLE);
        send_code_button.setVisibility(View.GONE);

        number = editText_number.getText().toString();

        if (number != null && !number.equals("")
        ) {


            SendCodeFunc(number);

        } else {
            Toast.makeText(PhoneVerificationActivity.this, "Please enter a valid number", Toast.LENGTH_SHORT).show();

            progress_bar.setVisibility(View.GONE);
            send_code_button.setVisibility(View.VISIBLE);
        }
    }


    private void SendCodeFunc(final String number) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://mysms.sms7.biz/rest/services/sendSMS/sendGroupSms?AUTH_KEY=9bda9717481b611e08984f5449c939&message=Your Brainywood app verification code is: "+verification_code+"&senderId=aesweb&routeId=1&mobileNos="+number+"&smsContentType=english",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String responseCode = jsonObject.getString("responseCode");
                            //  JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (responseCode.equals("3001")) {

                               // Toast.makeText(PhoneVerificationActivity.this, ""+verification_code, Toast.LENGTH_SHORT).show();
                                verifyLayout.setVisibility(View.VISIBLE);
                                numberLayout.setVisibility(View.GONE);
                                send_code_button.setVisibility(View.VISIBLE);
                                progress_bar.setVisibility(View.GONE);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(PhoneVerificationActivity.this, R.style.DialogTheme)
                                        .setTitle("Info")
                                        .setMessage("Check your Phone for verification code")
                                        .setCancelable(false)
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                send_code_button.setVisibility(View.VISIBLE);
                                                progress_bar.setVisibility(View.GONE);

                                            }
                                        });
                                //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
                                dialog.show();


                            } else {

                                send_code_button.setVisibility(View.VISIBLE);
                                progress_bar.setVisibility(View.GONE);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(PhoneVerificationActivity.this, R.style.DialogTheme)
                                        .setTitle("Info")
                                        .setMessage("Code Not Sent")
                                        .setCancelable(false)
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                send_code_button.setVisibility(View.VISIBLE);
                                                progress_bar.setVisibility(View.GONE);

                                                verifyLayout.setVisibility(View.GONE);
                                                numberLayout.setVisibility(View.VISIBLE);

                                            }
                                        });
                                //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
                                dialog.show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            //  register_button.setVisibility(View.VISIBLE);
                            // progress_bar.setVisibility(View.GONE);
                            Toast.makeText(PhoneVerificationActivity.this, "Error " + e.toString(), Toast.LENGTH_LONG).show();
                            send_code_button.setVisibility(View.VISIBLE);
                            progress_bar.setVisibility(View.GONE);

                            verifyLayout.setVisibility(View.GONE);
                            numberLayout.setVisibility(View.VISIBLE);
                            //   Toast.makeText(SignupActivity.this, "Server not responding", Toast.LENGTH_SHORT).show();
                            //SendCodeFunc(fname, lname, email, password);

                            //  login_text.setVisibility(View.VISIBLE);
                            // login_text.setText("JSON Error");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // register_button.setVisibility(View.VISIBLE);
                        // progress_bar.setVisibility(View.GONE);
                        Toast.makeText(PhoneVerificationActivity.this, "Server not responding " + error.toString(), Toast.LENGTH_LONG).show();
                        send_code_button.setVisibility(View.VISIBLE);
                        progress_bar.setVisibility(View.GONE);

                        verifyLayout.setVisibility(View.GONE);
                        numberLayout.setVisibility(View.VISIBLE);
                        // SendCodeFunc(fname, lname, email, password);

                        //    login_text.setVisibility(View.VISIBLE);
                        //   login_text.setText("Error from php");

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //   params.put("u_name", username);
             /*   params.put("apikey", "32208CADAFA43C1537CBFF99936C6C16375F0243200AE5BDF896889058EC27D72E2C1A57CD2A759A943CAEE8B22A0519");
                params.put("subject", "Verification Code BrainyWood App");
                params.put("from", "rhunar96@gmail.com");
                params.put("msgTo", email);
                params.put("bodyHtml", "Your verification code for BrainyWood App is \n\nCODE:  "+verification_code);
                params.put("bodyText", "Your verification code for BrainyWood App is \n\nCODE:  "+verification_code);
                params.put("isTransactional", "True");

              */
                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 10000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0; //retry turn off
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    public void finish(View view) {
        finish();
    }

    public void VerifyCode(View view) {

        EditText editText_code = findViewById(R.id.editText_code);
        if (verification_code.equals(editText_code.getText().toString())){
            Toast.makeText(this, "Verified", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(PhoneVerificationActivity.this, R.style.DialogTheme)
                    .setTitle("Info")
                    .setMessage("Number verified, now fill remaining fields for registration.")
                    .setCancelable(false)
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                         //   intent.putExtra("email", email);
                            intent.putExtra("number", number);
                            startActivity(intent);
                            finish();

                        }
                    });
            //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
            dialog.show();
        }
        else {

            Toast.makeText(this, "Wrong Code", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(PhoneVerificationActivity.this, R.style.DialogTheme)
                    .setTitle("Info")
                    .setMessage("Wrong Code, Number Not Verified")
                    .setCancelable(false)
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {



                        }
                    });
            //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
            dialog.show();

        }

    }

}