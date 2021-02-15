package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.sharad.brainywood.Utils.GlobalUrlApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ProfileEditActivity extends AppCompatActivity {

    String user_id, full_name, dob, gender, phone, school, classs, city, state;

    EditText name_text, phone_text, school_text, class_text, city_text, state_text;
    TextView age_text;

    RadioButton radioMale, radioFemale;

    String selected_date_of_birht = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_edit);

        name_text = findViewById(R.id.name_text);
        age_text = findViewById(R.id.age_text);
        phone_text = findViewById(R.id.phone_text);
        school_text = findViewById(R.id.school_text);
        class_text = findViewById(R.id.class_text);
        city_text = findViewById(R.id.city_text);
        state_text = findViewById(R.id.state_text);

        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);


        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        full_name = intent.getStringExtra("full_name");
        dob = intent.getStringExtra("dob");
        gender = intent.getStringExtra("gender");
        phone = intent.getStringExtra("phone");
        school = intent.getStringExtra("school");
        classs = intent.getStringExtra("class");
        city = intent.getStringExtra("city");
        state = intent.getStringExtra("state");

        try {


            if (full_name != null && !full_name.equals("") && !full_name.equals("null"))
                name_text.setText(full_name);

            if (dob != null && !dob.equals("") && !dob.equals("null"))
                age_text.setText(dob);

            if (phone != null && !phone.equals("") && !phone.equals("null"))
                phone_text.setText(phone);

            if (school != null && !school.equals("") && !school.equals("null"))
                school_text.setText(school);

            if (classs != null && !classs.equals("") && !classs.equals("null"))
                class_text.setText(classs);

            if (city != null && !city.equals("") && !city.equals("null"))
                city_text.setText(city);

            if (state != null && !state.equals("") && !state.equals("null"))
                state_text.setText(state);

            if (gender != null) {
                if (gender.equals("Male")) {
                    radioMale.setChecked(true);
                    radioFemale.setChecked(false);
                }
                if (gender.equals("Female")) {
                    radioMale.setChecked(false);
                    radioFemale.setChecked(true);
                }
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //  name_text.setText(full_name);


    }

    public void UpdateUser(View view) {

        String full_name = name_text.getText().toString();
        String date_of_birth = age_text.getText().toString();
        String m = phone_text.getText().toString();
        String school = school_text.getText().toString();
        String classs = class_text.getText().toString();
        String city = city_text.getText().toString();
        String state = state_text.getText().toString();

        String gender = "";// name_text.getText().toString();
        if (radioMale.isChecked()) {
            gender = "Male";
        } else if (radioFemale.isChecked()) {
            gender = "Female";
        }

        UpdateOnline(user_id, date_of_birth, gender, school, classs, city, state, full_name);
    }


    private void UpdateOnline(final String user_id, final String age, final String gender, final String school, final String classs, final String city, final String state, final String name) {

        final ProgressDialog pd = new ProgressDialog(ProfileEditActivity.this);
        pd.setMessage("Updating Profile...");
        pd.setCancelable(false);
        pd.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new GlobalUrlApi().getBaseUrl() + "/wp-json/app/v1/update_user_detail/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            //JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (status.equals("true")) {
                                pd.dismiss();
                                pd.cancel();
                                Log.d("UPDATE_USER: ", "Successful");
                                final AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileEditActivity.this, R.style.DialogTheme)
                                        .setTitle("")
                                        .setMessage("Updated Successfully...")
                                        .setCancelable(false)
                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (ProfileActivity.instance != null) {
                                                    try {
                                                        ProfileActivity.instance.finish();
                                                    } catch (Exception e) {
                                                    }
                                                }
                                                Intent intent = new Intent(ProfileEditActivity.this, ProfileActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                dialog.show();
                                // Toast.makeText(ProfileActivity.this, ""+(jsonObject.getJSONObject("data").toString()), Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("UPDATE_USER: ", "Error");
                                pd.dismiss();
                                pd.cancel();
                                final AlertDialog.Builder dialog = new AlertDialog.Builder(ProfileEditActivity.this, R.style.DialogTheme)
                                        .setTitle("")
                                        .setMessage("Not Updated...")
                                        .setCancelable(false)
                                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (ProfileActivity.instance != null) {
                                                    try {
                                                        ProfileActivity.instance.finish();
                                                    } catch (Exception e) {
                                                    }
                                                }
                                                Intent intent = new Intent(ProfileEditActivity.this, ProfileActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                dialog.show();

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
                params.put("age", age);
                params.put("gender", gender);
                params.put("school", school);
                params.put("city", city);
                params.put("state", state);
                params.put("full_name", name);
                params.put("class", classs);
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


    public void Close(View view) {
        finish();
    }

    public void SelectDateTime(View view) {
        final TextView date_time = findViewById(R.id.age_text);

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

                selected_date_of_birht = simpleDateFormat.format(calendar.getTime());

                date_time.setText("" + simpleDateFormat.format(calendar.getTime()));


            }
        };

        new DatePickerDialog(ProfileEditActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();


    }
}