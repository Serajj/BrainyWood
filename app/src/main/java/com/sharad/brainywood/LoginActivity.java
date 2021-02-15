package com.sharad.brainywood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sharad.brainywood.Utils.FirebaseUserModel;
import com.sharad.brainywood.Utils.GlobalUrlApi;
import com.sharad.brainywood.Utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText editText_email, editText_password;

    ProgressBar progress_bar;
    Button login_button;

    private static String URL_Login;
    GlobalUrlApi globalUrlApi;
    SessionManager sessionManager;

    boolean passwordShow = false;


    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseReference referenceEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        referenceEmail = rootNode.getReference("verify");

        editText_email = findViewById(R.id.editText_email);
        editText_password = findViewById(R.id.editText_password);
        progress_bar = findViewById(R.id.progress_bar);
        login_button = findViewById(R.id.login_button);

        globalUrlApi = new GlobalUrlApi();
        URL_Login = globalUrlApi.getUrlAppFolder() + "app_login.php";


        sessionManager = new SessionManager(this);

    }

    public void LoginButtonClick(View view) {
        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
        // finish();


        progress_bar.setVisibility(View.VISIBLE);
        login_button.setVisibility(View.GONE);

        final String email = editText_email.getText().toString();
        final String password = editText_password.getText().toString();


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (email != null && !email.equals("") && password != null && !password.equals("")) {


                    Login(email, password);

                } else {
                    Toast.makeText(LoginActivity.this, "Please enter Email and Password.", Toast.LENGTH_SHORT).show();

                    progress_bar.setVisibility(View.GONE);
                    login_button.setVisibility(View.VISIBLE);
                }
            }
        }, 10);

    }


    private void Login(final String email, final String password) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_Login,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (success.equals("1")) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String name = object.getString("display_name").trim();
                                    String email = object.getString("user_email").trim();
                                    String id = object.getString("ID").trim();
                                    String username = object.getString("username").trim();

                                    sessionManager.createSession(name, email, id, username);

                                    //Toast.makeText(LoginActivity.this, name + "\n" + email + "\n" + id, Toast.LENGTH_LONG).show();

                                    CheckDeviceLogin(id, name, email);


                                }

                            }

                            if (success.equals("0")) {

                                login_button.setVisibility(View.VISIBLE);
                                progress_bar.setVisibility(View.GONE);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this, R.style.DialogTheme)
                                        .setTitle("Warning!")
                                        .setMessage("Incorrect Email or Password")
                                        .setCancelable(false)
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                login_button.setVisibility(View.VISIBLE);
                                                progress_bar.setVisibility(View.GONE);

                                            }
                                        });
                                //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
                                dialog.show();


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            login_button.setVisibility(View.VISIBLE);
                            progress_bar.setVisibility(View.GONE);
                            // Toast.makeText(LoginActivity.this, "Error "+e.toString(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Server not responding", Toast.LENGTH_SHORT).show();
                            //  login_text.setVisibility(View.VISIBLE);
                            // login_text.setText("JSON Error");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //  login_button.setVisibility(View.VISIBLE);
                        //  progress_bar.setVisibility(View.GONE);
                        //  Toast.makeText(LoginActivity.this, "Error "+error.toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginActivity.this, "Trying login please wait", Toast.LENGTH_SHORT).show();
                        Login(email, password);
                        //    login_text.setVisibility(View.VISIBLE);
                        //   login_text.setText("Error from php");

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", email);
                params.put("password", password);
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

    private void CheckDeviceLogin(final String user_id, final String name, final String email) {

        Query checkUser = reference.orderByChild("user_id").equalTo(user_id);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String device_id = dataSnapshot.child(user_id).child("user_device_id").getValue(String.class);
                String username = dataSnapshot.child(user_id).child("user_name").getValue(String.class);
                @SuppressLint("HardwareIds") final String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

               // Toast.makeText(LoginActivity.this, "User_ID " + user_id + "\nAndroid ID " + device_id, Toast.LENGTH_SHORT).show();

                if (device_id == null){


                    //setting firebase user values //
                    @SuppressLint("HardwareIds")
                    FirebaseUserModel userModel = new FirebaseUserModel(user_id, name, email, android_id);
                    reference.child(user_id).setValue(userModel);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();

                }
                else if (device_id.equals(android_id)) {

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    //Toast.makeText(LoginActivity.this, "Same\n\n"+device_id+"\n\n"+android_id, Toast.LENGTH_SHORT).show();
                } else {


                    login_button.setVisibility(View.VISIBLE);
                    progress_bar.setVisibility(View.GONE);

                    final AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this, R.style.DialogTheme)
                            .setTitle("")
                            .setMessage("This account has been logged out of your other device, thank you.")
                            .setCancelable(false)
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {


                                    //setting firebase user values //
                                    @SuppressLint("HardwareIds")
                                    FirebaseUserModel userModel = new FirebaseUserModel(user_id, name, email, android_id);
                                    reference.child(user_id).setValue(userModel);

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    dialog.show();

                    // Toast.makeText(MainActivity.this, "Not Same", Toast.LENGTH_SHORT).show();
                }


                // Toast.makeText(MainActivity.this, username+"\n"+device_id, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public void OpenRegistration(View view) {
        startActivity(new Intent(LoginActivity.this, PhoneVerificationActivity.class));
    }

    public void ForgetPassword(View view) {


        AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this, R.style.DialogTheme)
                .setTitle("Reset Password")
                .setMessage("Please reset your password using your email and login back in app")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String url = "https://brainywoodindia.com/password-reset/";
                        try {
                            Uri uri = Uri.parse("googlechrome://navigate?url=" + url);
                            Intent ii = new Intent(Intent.ACTION_VIEW, uri);
                            ii.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(ii);
                        } catch (ActivityNotFoundException e) {
                            // Chrome is probably not installed
                        }

                    }
                });
        //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
        dialog.show();

    }

    public void ShowPassword(View view) {
        if (!passwordShow) {
            editText_password.setInputType(InputType.TYPE_CLASS_TEXT);
            passwordShow = true;
        } else {
            editText_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordShow = false;
        }
    }
}