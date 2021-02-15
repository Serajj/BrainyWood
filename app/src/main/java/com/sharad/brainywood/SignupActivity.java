package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
import com.sharad.brainywood.Utils.FirebaseUserModel;
import com.sharad.brainywood.Utils.GlobalUrlApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText editText_fname, editText_lname, editText_email, editText_phone, editText_password;

    ProgressBar progress_bar;
    Button register_button;

    private static String URL_Register;
    GlobalUrlApi globalUrlApi;
    private static String URL_Login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_signup);


        editText_fname = findViewById(R.id.editText_fname);
        editText_lname = findViewById(R.id.editText_lname);
        editText_email = findViewById(R.id.editText_email);
        editText_phone = findViewById(R.id.editText_phone);
        editText_password = findViewById(R.id.editText_password);

        Intent intent = getIntent();
        //     String email = intent.getStringExtra("email");
        String number = intent.getStringExtra("number");

        //   if (email != null)
        //       editText_email.setText(email);

        if (number != null)
            editText_phone.setText(number);

        progress_bar = findViewById(R.id.progress_bar);
        register_button = findViewById(R.id.register_button);

        globalUrlApi = new GlobalUrlApi();
        URL_Register = globalUrlApi.getUrlAppFolder() + "app_reg.php";


    }

    public void finish(View view) {
        finish();
    }

    public void RegisterButtonClick(View view) {
        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
        // finish();


        progress_bar.setVisibility(View.VISIBLE);
        register_button.setVisibility(View.GONE);

        final String fullname = editText_fname.getText().toString();
        ArrayList<String> FullNames = new ArrayList<>();
        for (String ar : fullname.split(" ")) {
            FullNames.add(ar);
        }
        String fname = FullNames.get(0);
        String lname = "";
        try {
            if (FullNames.get(1) != null) {
                lname = FullNames.get(1);
            } else {
                lname = "";
            }
        } catch (IndexOutOfBoundsException e) {
            lname = "";
        }

        //  Toast.makeText(this, fname+"\n"+lname, Toast.LENGTH_LONG).show();


        // final String lname = editText_lname.getText().toString();
        final String email = editText_email.getText().toString();
        final String password = editText_password.getText().toString();

        //Toast.makeText(this, "Email "+email+"\nPass "+password+"\nFname "+fname+"\nLname "+lname, Toast.LENGTH_SHORT).show();
        if (fullname != null && !fullname.equals("") && (lname == null || lname.equals(""))) {
            fname = fullname;
            lname = " ";
        }

        if (
                email != null && !email.equals("") &&
                        password != null && !password.equals("") &&
                        fname != null && !fname.equals("") &&
                        lname != null && !lname.equals("")
        ) {

                  /*  Toast.makeText(SignupActivity.this,
                            "Username: "+username+"\n"+
                                    "fname: "+fname+"\n"+
                                    "lname: "+lname+"\n"+
                                    "email: "+email+"\n"+
                                    "password: "+password+"\n",
                            Toast.LENGTH_LONG).show();

                   */

            Register(fname, lname, email, password);

        } else {
            Toast.makeText(SignupActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();

            progress_bar.setVisibility(View.GONE);
            register_button.setVisibility(View.VISIBLE);
        }

    }

    private void Register(final String fname, final String lname, final String email, final String password) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://www.brainywoodindia.com/app/app_reg.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            //  JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (success.equals("0")) {

                                register_button.setVisibility(View.VISIBLE);
                                progress_bar.setVisibility(View.GONE);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(SignupActivity.this, R.style.DialogTheme)
                                        .setTitle("Info")
                                        .setMessage("This mail already registered")
                                        .setCancelable(false)
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                register_button.setVisibility(View.VISIBLE);
                                                progress_bar.setVisibility(View.GONE);

                                            }
                                        });
                                //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
                                dialog.show();


                            } else if (success.equals("1")) {


                                Login(email, password);


                            } else if (success.equals("2")) {

                                register_button.setVisibility(View.VISIBLE);
                                progress_bar.setVisibility(View.GONE);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(SignupActivity.this, R.style.DialogTheme)
                                        .setTitle("Info")
                                        .setMessage("This email is already registered")
                                        .setCancelable(false)
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                register_button.setVisibility(View.VISIBLE);
                                                progress_bar.setVisibility(View.GONE);

                                            }
                                        });
                                //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
                                dialog.show();


                            } else if (success.equals("3")) {

                                register_button.setVisibility(View.VISIBLE);
                                progress_bar.setVisibility(View.GONE);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(SignupActivity.this, R.style.DialogTheme)
                                        .setTitle("Info")
                                        .setMessage("This username is already registered")
                                        .setCancelable(false)
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                register_button.setVisibility(View.VISIBLE);
                                                progress_bar.setVisibility(View.GONE);

                                            }
                                        });
                                //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
                                dialog.show();


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            //  register_button.setVisibility(View.VISIBLE);
                            // progress_bar.setVisibility(View.GONE);
                            // Toast.makeText(LoginActivity.this, "Error "+e.toString(), Toast.LENGTH_SHORT).show();
                            //   Toast.makeText(SignupActivity.this, "Server not responding", Toast.LENGTH_SHORT).show();
                            Register(fname, lname, email, password);

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
                        //  Toast.makeText(SignupActivity.this, "Server not responding "+error.toString(), Toast.LENGTH_SHORT).show();
                        Register(fname, lname, email, password);

                        //    login_text.setVisibility(View.VISIBLE);
                        //   login_text.setText("Error from php");

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //   params.put("u_name", username);
                params.put("f_name", fname);
                params.put("l_name", lname);
                params.put("email", email);
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

    public void Login(final String email, final String password) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new GlobalUrlApi().getUrlAppFolder() + "app_login.php",
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

                                    UpdateOnline(id, editText_phone.getText().toString(), "um_registered");


                                }

                            } else {

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


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

    private void UpdateOnline(final String user_id, final String number, final String role) {

        //final ProgressDialog pd = new ProgressDialog(ProfileEditActivity.this);
        //    pd.setMessage("Updating Profile...");
        //   pd.setCancelable(false);
        //   pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new GlobalUrlApi().getBaseUrl() + "/wp-json/app/v1/update_user_detail/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            //JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (status.equals("true")) {
                                //pd.dismiss();
                                // pd.cancel();

                                RegistrationSuccessful();

                            } else {


                            }

                            // progressBar.setVisibility(View.GONE);
                            // userInfoCard.setVisibility(View.VISIBLE);


                        } catch (JSONException e) {

                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "JSONException Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Server not responding", Toast.LENGTH_SHORT).show();
                        //    login_text.setVisibility(View.VISIBLE);
                        //   login_text.setText("Error from php");

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("mobile_number", number);
                params.put("role", role);
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


    private void RegistrationSuccessful() {

        register_button.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.GONE);
        AlertDialog.Builder dialog = new AlertDialog.Builder(SignupActivity.this, R.style.DialogTheme)
                .setTitle("Info")
                .setMessage("Registration successful, now login please")
                .setCancelable(false)
                .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        register_button.setVisibility(View.VISIBLE);
                        progress_bar.setVisibility(View.GONE);
                        finish();
                    }
                });
        dialog.show();

    }

}