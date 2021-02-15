package com.sharad.brainywood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class ProfileActivity extends AppCompatActivity {

    TextView username_text, name_text, email_text, phone_text, age_text, gender_text, school_text, class_text, city_text, state_text;

    GlobalUrlApi globalUrlApi;
    SessionManager sessionManager;
    String user_id, email, full_name;

    CardView userInfoCard;
    ProgressBar progressBar;

    boolean fetch_user_check = false;


    //String fullname = null; //= data.getString("age");
    String age = null; //= data.getString("age");
    String gender = null;
    String username = null;
    String school = null;
    String classs = null;
    String city = null;
    String state = null;
    String phone = null;


    public static ProfileActivity instance = null;



    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseReference referenceEmail;

    CardView verifyEmailCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_profile);
        instance = this;

        userInfoCard = findViewById(R.id.userInfoCard);
        progressBar = findViewById(R.id.progressBar);

        username_text = findViewById(R.id.username_text);
        name_text = findViewById(R.id.name_text);
        email_text = findViewById(R.id.email_text);
        phone_text = findViewById(R.id.phone_text);

        age_text = findViewById(R.id.age_text);
        gender_text = findViewById(R.id.gender_text);
        school_text = findViewById(R.id.school_text);
        class_text = findViewById(R.id.class_text);
        city_text = findViewById(R.id.city_text);
        state_text = findViewById(R.id.state_text);


        sessionManager = new SessionManager(this);
        sessionManager.checkLoginProfile();

        HashMap<String, String> user = sessionManager.getUserDetail();
        full_name = user.get(sessionManager.NAME);
        email = user.get(sessionManager.EMAIL);
        user_id = user.get(sessionManager.ID);

        username_text.setText(email);
        name_text.setText(full_name);
        email_text.setText(email);



        verifyEmailCard = findViewById(R.id.verifyEmailCard);

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        referenceEmail = rootNode.getReference("verify");


        GetUserDetails(user_id);


        final ImageView profile_settingButton = (ImageView) findViewById(R.id.profile_settingButton);
        profile_settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ProfileActivity.this, profile_settingButton);
                //Inflating the Popup using xml file
                //   popup.getMenuInflater()
                //         .inflate(R.menu.popup_menu, popup.getMenu());

                popup.getMenu().add("Edit Profile");
                popup.getMenu().add("Change Password");
                popup.getMenu().add("My Account");
                popup.getMenu().add("Logout");
                popup.getMenu().add("Cancel");

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        //  Toast.makeText(ProfileActivity.this,"" + menuItem.getTitle(), Toast.LENGTH_SHORT).show();

                        if (menuItem.getTitle().equals("Edit Profile")) {

                            if (fetch_user_check) {
                                Intent intent = new Intent(getApplicationContext(), ProfileEditActivity.class);
                                intent.putExtra("user_id", user_id);
                                intent.putExtra("full_name", full_name);
                                intent.putExtra("dob", age);
                                intent.putExtra("gender", gender);
                                intent.putExtra("phone", phone);
                                intent.putExtra("school", school);
                                intent.putExtra("class", classs);
                                intent.putExtra("city", city);
                                intent.putExtra("state", state);

                                startActivity(intent);
                            } else {
                                Toast.makeText(ProfileActivity.this, "Please Wait...", Toast.LENGTH_SHORT).show();
                            }
                        }

                        else if (menuItem.getTitle().equals("Change Password")) {
                            if (fetch_user_check) {
                                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                                intent.putExtra("user_id", user_id);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ProfileActivity.this, "Please Wait...", Toast.LENGTH_SHORT).show();
                            }
                        }

                        else if (menuItem.getTitle().equals("My Account")) {
                            if (fetch_user_check) {
                                Intent intent = new Intent(getApplicationContext(), ProfileEditActivity.class);
                                intent.putExtra("user_id", user_id);
                                intent.putExtra("full_name", full_name);
                                intent.putExtra("dob", age);
                                intent.putExtra("gender", gender);
                                intent.putExtra("phone", phone);
                                intent.putExtra("school", school);
                                intent.putExtra("class", classs);
                                intent.putExtra("city", city);
                                intent.putExtra("state", state);

                                startActivity(intent);
                            } else {
                                Toast.makeText(ProfileActivity.this, "Please Wait...", Toast.LENGTH_SHORT).show();
                            }
                        } else if (menuItem.getTitle().equals("Logout")) {
                            sessionManager.logoutFromProfileActivity();
                        } else if (menuItem.getTitle().equals("Cancel")) {
                            finish();
                        }
                        return true;
                    }

                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method


        CheckEmailVerification();
    }

    private void CheckEmailVerification() {

        verifyEmailCard.setVisibility(View.GONE);
        Query checkUser = referenceEmail.orderByChild("user_id").equalTo(user_id);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String user_verify = dataSnapshot.child(user_id).child("user_verify").getValue(String.class);


                if (user_verify == null || user_verify.equals("null") ){

                    //setting firebase user values //
                    FirebaseEmailVerify emailVerify = new FirebaseEmailVerify(user_id, "none");
                    referenceEmail.child(user_id).setValue(emailVerify);
                    //Toast.makeText(getActivity(), "None added", Toast.LENGTH_SHORT).show();
                    verifyEmailCard.setVisibility(View.VISIBLE);

                    //----------//
                }
                else if (user_verify.equals("none")){
                    //Toast.makeText(getActivity(), "Not verified", Toast.LENGTH_SHORT).show();
                    verifyEmailCard.setVisibility(View.VISIBLE);
                }
                else if (user_verify.equals("verified")){
                    //Toast.makeText(getActivity(), "Verified", Toast.LENGTH_SHORT).show();

                }

                // Toast.makeText(MainActivity.this, username+"\n"+device_id, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void GetUserDetails(final String user_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, new GlobalUrlApi().getBaseUrl() + "wp-json/app/v1/get_user_detail/" + user_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            //JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (status.equals("true")) {
                                Log.d("STATUS_USER: ", "Successful");
                                // Toast.makeText(ProfileActivity.this, ""+(jsonObject.getJSONObject("data").toString()), Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("STATUS_USER: ", "Error");

                            }
                            JSONObject data = jsonObject.getJSONObject("data");

                            full_name = data.getString("full_name");
                            age = data.getString("dob");
                            gender = data.getString("gender");
                            username = data.getString("name");
                            school = data.getString("eduname");
                            classs = data.getString("class");
                            city = data.getString("city");
                            state = data.getString("state");
                            phone = data.getString("mobile_number");




                            // age_text.setText(age);
                            if (full_name != null && !full_name.equals("") && !full_name.equals("null")) {
                                name_text.setText(full_name);
                            } else {
                                name_text.setText("Empty");
                            }

                            // age_text.setText(age);
                            if (age != null && !age.equals("") && !age.equals("null")) {
                                age_text.setText(age);
                            } else {
                                age_text.setText("Empty");
                            }

                            if (!username.equals("") && !username.equals("null") && username != null) {
                                username_text.setText(username);
                            } else {
                                username_text.setText("Empty");
                            }

                            if (!gender.equals("") && !gender.equals("null") && (gender.equals("Female") || gender.equals("Male")) && gender != null) {
                                gender_text.setText(gender);
                            } else {
                                gender_text.setText("Empty");
                            }
                            // gender_text.setText(gender);

                            if (!school.equals("") && !school.equals("null") && school != null) {
                                school_text.setText(school);
                            } else {
                                school_text.setText("Empty");
                            }

                            if (!classs.equals("") && !classs.equals("null") && classs != null) {
                                class_text.setText(classs);
                            } else {
                                class_text.setText("Empty");
                            }
                            //   school_text.setText(school);

                            if (!city.equals("") && !city.equals("null") && city != null) {
                                city_text.setText(city);
                            } else {
                                city_text.setText("Empty");
                            }
                            //city_text.setText(city);

                            if (!state.equals("") && !state.equals("null") && state != null) {
                                state_text.setText(state);
                            } else {
                                state_text.setText("Empty");
                            }
                            //state_text.setText(state);

                            if (!phone.equals("") && !phone.equals("null") && phone != null) {
                                phone_text.setText(phone);
                            } else {
                                phone_text.setText("Empty");
                            }
                            // phone_text.setText(phone);

                            fetch_user_check = true;

                            progressBar.setVisibility(View.GONE);
                            userInfoCard.setVisibility(View.VISIBLE);


                        } catch (JSONException e) {

                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Server not responding", Toast.LENGTH_SHORT).show();
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
                //   params.put("username", email);
                //   params.put("password", password);
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

    @Override
    public void finish() {
        super.finish();
        instance = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckEmailVerification();
    }

    public void Close(View view) {
        finish();
    }

    public void OpenEmailVerification(View view) {
        startActivity(new Intent(ProfileActivity.this, EmailVerificationActivity.class));
    }

    public void OpenBrainywood(View view) {
        Uri uriUrl = Uri.parse("https://brainywoodindia.com/");
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }
}