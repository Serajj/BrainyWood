package com.sharad.brainywood;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sharad.brainywood.Utils.FirebaseEmailVerify;
import com.sharad.brainywood.Utils.GlobalUrlApi;
import com.sharad.brainywood.Utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EmailVerificationActivity extends AppCompatActivity {

    EditText editText_email;

    private static String URL_Register;
    GlobalUrlApi globalUrlApi;


    ProgressBar progress_bar;
    Button send_code_button;

    String verification_code;

    String email;

    LinearLayout verifyLayout, emailLayout;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseReference referenceEmail;


    SessionManager sessionManager;
    String user_id, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_email_verification);
        sessionManager = new SessionManager(EmailVerificationActivity.this);

        HashMap<String, String> user = sessionManager.getUserDetail();
        name = user.get(sessionManager.NAME);
        email = user.get(sessionManager.EMAIL);
        user_id = user.get(sessionManager.ID);


        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        referenceEmail = rootNode.getReference("verify");

        editText_email = findViewById(R.id.editText_email);
        editText_email.setText(email);
        progress_bar = findViewById(R.id.progress_bar);
        send_code_button = findViewById(R.id.send_verification_button);

        verifyLayout = findViewById(R.id.verifyLayout);
        emailLayout = findViewById(R.id.emailLayout);

        Random random = new Random();

        verification_code = String.format("%04d", random.nextInt(10000));
        // Toast.makeText(this, ""+verification_code, Toast.LENGTH_LONG).show();


    }

    public void SendVerificationCode(View view) {


        progress_bar.setVisibility(View.VISIBLE);
        send_code_button.setVisibility(View.GONE);
        SendCodeFunc(email);
    }

    public void SendVerificationCodee(View view) {

        progress_bar.setVisibility(View.VISIBLE);
        send_code_button.setVisibility(View.GONE);

        email = editText_email.getText().toString();

        if (email != null && !email.equals("")
        ) {

            CheckEmailAlready(email);


        } else {
            Toast.makeText(EmailVerificationActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();

            progress_bar.setVisibility(View.GONE);
            send_code_button.setVisibility(View.VISIBLE);
        }
    }

    private void CheckEmailAlready(final String email) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://brainywoodindia.com/app/check_email.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            //  JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (success.equals("1")) {

                               // Toast.makeText(EmailVerificationActivity.this, ""+verification_code, Toast.LENGTH_SHORT).show();
                                SendCodeFunc(email);


                            } else if (success.equals("2")) {

                                AlertDialog.Builder dialog = new AlertDialog.Builder(EmailVerificationActivity.this, R.style.DialogTheme)
                                        .setTitle("Info")
                                        .setMessage("This email is already registered")
                                        .setCancelable(false)
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {


                                                progress_bar.setVisibility(View.GONE);
                                                send_code_button.setVisibility(View.VISIBLE);

                                            }
                                        });
                                //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
                                dialog.show();

                            } else if (success.equals("3")) {

                                AlertDialog.Builder dialog = new AlertDialog.Builder(EmailVerificationActivity.this, R.style.DialogTheme)
                                        .setTitle("Info")
                                        .setMessage("This email is already registered")
                                        .setCancelable(false)
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {


                                                progress_bar.setVisibility(View.GONE);
                                                send_code_button.setVisibility(View.VISIBLE);

                                            }
                                        });
                                //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
                                dialog.show();


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(EmailVerificationActivity.this, "Json Exception", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(EmailVerificationActivity.this, "VolleyError Exception", Toast.LENGTH_SHORT).show();


                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //   params.put("u_name", username);
                params.put("email", email);
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

    private void SendCodeFunc(final String email) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://api.elasticemail.com/v2/email/send",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            //  JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (success.equals("true")) {

                                verifyLayout.setVisibility(View.VISIBLE);
                                emailLayout.setVisibility(View.GONE);
                                send_code_button.setVisibility(View.VISIBLE);
                                progress_bar.setVisibility(View.GONE);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(EmailVerificationActivity.this, R.style.DialogTheme)
                                        .setTitle("Info")
                                        .setMessage("Check your Email for verification code")
                                        .setCancelable(false)
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                send_code_button.setVisibility(View.VISIBLE);
                                                progress_bar.setVisibility(View.GONE);
                                                //Toast.makeText(EmailVerificationActivity.this, ""+verification_code, Toast.LENGTH_SHORT).show();


                                            }
                                        });
                                //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
                                dialog.show();


                            } else {

                                send_code_button.setVisibility(View.VISIBLE);
                                progress_bar.setVisibility(View.GONE);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(EmailVerificationActivity.this, R.style.DialogTheme)
                                        .setTitle("Info")
                                        .setMessage("Email Not Sent")
                                        .setCancelable(false)
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                send_code_button.setVisibility(View.VISIBLE);
                                                progress_bar.setVisibility(View.GONE);

                                                verifyLayout.setVisibility(View.GONE);
                                                emailLayout.setVisibility(View.VISIBLE);

                                            }
                                        });
                                //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
                                dialog.show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            //  register_button.setVisibility(View.VISIBLE);
                            // progress_bar.setVisibility(View.GONE);
                            Toast.makeText(EmailVerificationActivity.this, "Error " + e.toString(), Toast.LENGTH_LONG).show();
                            send_code_button.setVisibility(View.VISIBLE);
                            progress_bar.setVisibility(View.GONE);

                            verifyLayout.setVisibility(View.GONE);
                            emailLayout.setVisibility(View.VISIBLE);
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
                        Toast.makeText(EmailVerificationActivity.this, "Server not responding " + error.toString(), Toast.LENGTH_LONG).show();
                        send_code_button.setVisibility(View.VISIBLE);
                        progress_bar.setVisibility(View.GONE);

                        verifyLayout.setVisibility(View.GONE);
                        emailLayout.setVisibility(View.VISIBLE);
                        // SendCodeFunc(fname, lname, email, password);

                        //    login_text.setVisibility(View.VISIBLE);
                        //   login_text.setText("Error from php");

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //   params.put("u_name", username);
                params.put("apikey", "32208CADAFA43C1537CBFF99936C6C16375F0243200AE5BDF896889058EC27D72E2C1A57CD2A759A943CAEE8B22A0519");
                params.put("subject", "Verification Code BrainyWood App");
                params.put("from", "no-reply@brainywoodindia.com");
                params.put("msgTo", email);
                params.put("bodyHtml", "Your verification code for BrainyWood App is \n\nCODE:  " + verification_code);
                params.put("bodyText", "Your verification code for BrainyWood App is \n\nCODE:  " + verification_code);
                params.put("isTransactional", "True");
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

        //Toast.makeText(EmailVerificationActivity.this, ""+verification_code, Toast.LENGTH_SHORT).show();


        EditText editText_code = findViewById(R.id.editText_code);
        if (verification_code.equals(editText_code.getText().toString())) {
          //  Toast.makeText(this, "Verified", Toast.LENGTH_SHORT).show();
            CheckEmailVerification();

            AlertDialog.Builder dialog = new AlertDialog.Builder(EmailVerificationActivity.this, R.style.DialogTheme)
                    .setTitle("Info")
                    .setMessage("Email verified, thank you.")
                    .setCancelable(false)
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            finish();

                        }
                    });
            //      dialog.show().getWindow().setBackgroundDrawableResource(R.drawable.backgroud_alertbox_round);
            dialog.show();
        } else {

            Toast.makeText(this, "Wrong Code", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(EmailVerificationActivity.this, R.style.DialogTheme)
                    .setTitle("Info")
                    .setMessage("Wrong Code, Email Not Verified")
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

    private void CheckEmailVerification() {

        //verifyEmailCard.setVisibility(View.GONE);
        Query checkUser = referenceEmail.orderByChild("user_id").equalTo(user_id);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String user_verify = dataSnapshot.child(user_id).child("user_verify").getValue(String.class);


                if (user_verify == null || user_verify.equals("null") ){

                    //setting firebase user values //
                    FirebaseEmailVerify emailVerify = new FirebaseEmailVerify(user_id, "verified");
                    referenceEmail.child(user_id).setValue(emailVerify);
                    //Toast.makeText(getActivity(), "None added", Toast.LENGTH_SHORT).show();
                    //verifyEmailCard.setVisibility(View.VISIBLE);

                    //----------//
                }
                else if (user_verify.equals("none")){
                    //Toast.makeText(getActivity(), "Not verified", Toast.LENGTH_SHORT).show();
                  //  verifyEmailCard.setVisibility(View.VISIBLE);

                    FirebaseEmailVerify emailVerify = new FirebaseEmailVerify(user_id, "verified");
                    referenceEmail.child(user_id).setValue(emailVerify);
                }
                else if (user_verify.equals("verified")){
                    //Toast.makeText(getActivity(), "Verified", Toast.LENGTH_SHORT).show();
                    //FirebaseEmailVerify emailVerify = new FirebaseEmailVerify(user_id, "verified");
                   // referenceEmail.child(user_id).setValue(emailVerify);

                }

                // Toast.makeText(MainActivity.this, username+"\n"+device_id, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}