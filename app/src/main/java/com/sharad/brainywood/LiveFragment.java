package com.sharad.brainywood;


import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.sharad.brainywood.Utils.GlobalUrlApi;
import com.sharad.brainywood.Utils.SessionManager;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import us.zoom.sdk.JoinMeetingOptions;
import us.zoom.sdk.JoinMeetingParams;
import us.zoom.sdk.MeetingService;
import us.zoom.sdk.ZoomAuthenticationError;
import us.zoom.sdk.ZoomSDK;
import us.zoom.sdk.ZoomSDKAuthenticationListener;
import us.zoom.sdk.ZoomSDKInitParams;
import us.zoom.sdk.ZoomSDKInitializeListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class LiveFragment extends Fragment {

    private ZoomSDKAuthenticationListener authListener = new ZoomSDKAuthenticationListener() {
        /**
         * This callback is invoked when a result from the SDK's request to the auth server is
         * received.
         */
        @Override
        public void onZoomSDKLoginResult(long result) {
            if (result == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                //  startMeeting(MainActivity.this);
            }
        }

        @Override
        public void onZoomSDKLogoutResult(long l) {
        }

        @Override
        public void onZoomIdentityExpired() {
        }

        @Override
        public void onZoomAuthIdentityExpired() {
        }
    };

    ViewPager viewPager;
    ViewFlipper viewFlipper;


    View ChildView;
    int GetItemPosition;


    GlobalUrlApi globalUrlApi;
    SessionManager sessionManager;
    String user_id;

    private ProgressBar progressBar;
    //  LinearLayout loadingLayouot;


    View view;

    ImageView downloadButton, playButton;

    String url1;


    ArrayList<String> ID_PASS;


    public LiveFragment() {
        // Required empty public constructorcategoryPrefRecycler
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_live, container, false);
        viewPager = getActivity().findViewById(R.id.viewPager);
        sessionManager = new SessionManager(getActivity());
        //   loadingLayouot = view.findViewById(R.id.loadingLayout);
        //   loadingLayouot.setVisibility(View.VISIBLE);
        progressBar = view.findViewById(R.id.progress_bar);


        /*
        String link = ("https://us05web.zoom.us/j/86935016027?pwd=L29WR3Arell4anZiQ1lGNTdZQURzQT09".replace("/j/", "/j/id="));
        ID_PASS = new ArrayList<>();
        for (String pair : link.split("\\?(?!\\?)")) {
            String[] split = pair.split("=", 2);
            System.out.println(split[0] + " is " + split[1]);
            ID_PASS.add(split[1]);
        }

         */
         //  Toast.makeText(getActivity(), "ID=  "+ID_PASS.get(0)+"\n\nPASS=  "+ID_PASS.get(1), Toast.LENGTH_LONG).show();


        initializeSdk(getActivity());

        downloadButton = view.findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getActivity(), url1, Toast.LENGTH_SHORT).show();

                String name = null, exe = null;
                try {
                    URL url = new URL(url1);
                    name = FilenameUtils.getBaseName(url.getPath());
                    exe = FilenameUtils.getExtension(url.getPath());

                    //    Toast.makeText(getActivity(), name + "\n" + exe, Toast.LENGTH_SHORT).show();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url1));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setTitle("Fundamentals of Brain Science" + "." + exe);
                request.setDescription("Download Video Lectures from BrainyWood");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setDestinationInExternalFilesDir(getActivity(), "/BrainyWood/", "Fundamentals of Brain Science" + "." + exe + "");

                DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);

                Toast.makeText(getActivity(), "Downloading...", Toast.LENGTH_SHORT).show();

            }
        });

        playButton = view.findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //online
                Intent intent = new Intent(getActivity(), OnlinePlayerActivity.class);
                intent.putExtra("url", url1);
                // Toast.makeText(SplashActivity.this, ""+(videoLink.get(GetItemPosition)), Toast.LENGTH_SHORT).show();
                //    startActivity(intent);




                createJoinMeetingDialog();

            }
        });


        //   progressBar.setVisibility(View.VISIBLE);


        HashMap<String, String> user = sessionManager.getUserDetail();
        //name = user.get(sessionManager.NAME);
        String email = user.get(sessionManager.EMAIL);
        user_id = user.get(sessionManager.ID);

        initializeURLs();
        //  getUrl("https://player.vimeo.com/video/303968333/config"); //not brainy video
        getUrl("https://player.vimeo.com/video/462061589/config"); // brainy video


        setRetainInstance(true);

        Map<String, String> map = null;


        return view;
    }


    public void initializeSdk(Context context) {
        ZoomSDK sdk = ZoomSDK.getInstance();
        // TODO: For the purpose of this demo app, we are storing the credentials in the client app itself. However, you should not use hard-coded values for your key/secret in your app in production.
        ZoomSDKInitParams params = new ZoomSDKInitParams();
        params.appKey = "0YHumbbIaZ2HRvmneTxus617JxYLhcOzO8yi"; // TODO: Retrieve your SDK key and enter it here
        params.appSecret = "Z0Jwzw0qbP8quAwOiEqeJxa1bvPh5s1FrC6O"; // TODO: Retrieve your SDK secret and enter it here
        params.domain = "zoom.us";
        params.enableLog = true;
        // TODO: Add functionality to this listener (e.g. logs for debugging)
        ZoomSDKInitializeListener listener = new ZoomSDKInitializeListener() {
            /**
             * @param errorCode {@link us.zoom.sdk.ZoomError#ZOOM_ERROR_SUCCESS} if the SDK has been initialized successfully.
             */
            @Override
            public void onZoomSDKInitializeResult(int errorCode, int internalErrorCode) {
            }

            @Override
            public void onZoomAuthIdentityExpired() {
            }
        };
        sdk.initialize(context, listener, params);

    }


    private void createJoinMeetingDialog() {
        new AlertDialog.Builder(getActivity()).setView(R.layout.join_layout).setPositiveButton("Join", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog dialog = (AlertDialog) dialogInterface;
                TextInputEditText numberInput = dialog.findViewById(R.id.meeting_no_input);
              //  TextInputEditText passwordInput = dialog.findViewById(R.id.password_input);

                String link = ((numberInput.getText().toString()).replace("/j/", "/j/id="));
                ID_PASS = new ArrayList<>();
                for (String pair : link.split("\\?(?!\\?)")) {
                    String[] split = pair.split("=", 2);
                    System.out.println(split[0] + " is " + split[1]);
                    ID_PASS.add(split[1]);
                }

                if (ID_PASS.get(0) != null && ID_PASS.get(1) != null) {
                    Toast.makeText(getActivity(), "ID=  " + ID_PASS.get(0) + "\n\nPASS=  " + ID_PASS.get(1), Toast.LENGTH_LONG).show();
                    try {
                        joinMeeting(getActivity(), ID_PASS.get(0), ID_PASS.get(1));

                    }catch (NullPointerException e){
                        // Toast.makeText(getActivity(), ""+e.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }).show();



    }


    private void joinMeeting(Context context, String meetingNumber, String password) {
        MeetingService meetingService = ZoomSDK.getInstance().getMeetingService();
        JoinMeetingOptions options = new JoinMeetingOptions();
        JoinMeetingParams params = new JoinMeetingParams();
        params.displayName = "hariss"; // TODO: Enter your name
        params.meetingNo = meetingNumber;
        params.password = password;
        Toast.makeText(getActivity(), meetingNumber + "\n" + password, Toast.LENGTH_SHORT).show();

        meetingService.joinMeetingWithParams(context, params, options);
    }


    public void getUrl(String url) {

        StringRequest str = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //   Toast.makeText(CheckActivity.this, ""+response, Toast.LENGTH_SHORT).show();

                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject request = object.getJSONObject("request");
                    JSONObject files = request.getJSONObject("files");
                    JSONArray progressive = files.getJSONArray("progressive");

                    JSONObject array1 = progressive.getJSONObject(0);

                    url1 = array1.getString("url");

                    // Toast.makeText(CheckActivity.this, ""+progressive.toString(), Toast.LENGTH_SHORT).show();


                    //Setting thumbnail from url
                    /*
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    //give YourVideoUrl below
                    retriever.setDataSource(url1, new HashMap<String, String>());
                    // this gets frame at 2nd second
                    Bitmap image = retriever.getFrameAtTime(1500000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    //use this bitmap image
                    ImageView imageView = view.findViewById(R.id.thumbnail);
                 //   imageView.setImageBitmap(image);

                     */


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "" + e.toString(), Toast.LENGTH_SHORT).show();
                    //  result.setText(e.toString());

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(str);
    }


    private void initializeURLs() {
        globalUrlApi = new GlobalUrlApi();
        //  Categories_URL = globalUrlApi.getGlobalVariable() + "categories_json.php";
        // Cat_pref_count_URL = globalUrlApi.getGlobalVariable() + "get_pref_count_cat.php?user_id=" + user_id;


    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {
            ((MainActivity) getActivity()).courseLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).homeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).liveLayout.setBackgroundColor(Color.parseColor("#80BAFA"));
            ((MainActivity) getActivity()).qaLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).downloadLayout.setBackgroundColor(Color.parseColor("#ffffff"));

            ((MainActivity) getActivity()).courseText.setTextColor(Color.parseColor("#232323"));
            ((MainActivity) getActivity()).homeText.setTextColor(Color.parseColor("#232323"));
            ((MainActivity) getActivity()).liveText.setTextColor(Color.parseColor("#ffffff"));
            ((MainActivity) getActivity()).qaText.setTextColor(Color.parseColor("#232323"));
            ((MainActivity) getActivity()).downloadText.setTextColor(Color.parseColor("#232323"));

        } catch (NullPointerException e) {
            // Toast.makeText(getActivity(), ""+e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
