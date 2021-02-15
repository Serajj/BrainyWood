package com.sharad.brainywood;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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

public class ChangePasswordActivity extends AppCompatActivity {

    EditText passwordView, passwordViewC;
    String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_password);

        passwordView = findViewById(R.id.passwordView);
        passwordViewC = findViewById(R.id.passwordViewC);


        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");

    }

    public void Close(View view) {
        finish();
    }


    private void UpdateOnline(final String user_id, final String password) {

        final ProgressDialog pd = new ProgressDialog(ChangePasswordActivity.this);
        pd.setMessage("Updating Password...");
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
                                final AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.DialogTheme)
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
                                                Intent intent = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
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
                                final AlertDialog.Builder dialog = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.DialogTheme)
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
                                                Intent intent = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
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

    public void UpdatePassword(View view) {
        if (!passwordView.getText().toString().equals("") && passwordView.getText().toString().equals(passwordViewC.getText().toString())){
            UpdateOnline(user_id, passwordView.getText().toString());
        }else {
            Toast.makeText(this, "Password mismatched, please enter password correctly.", Toast.LENGTH_SHORT).show();
        }
    }
}