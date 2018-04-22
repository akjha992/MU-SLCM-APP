package com.example.akjha.mytest;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.view.KeyEvent;
import java.util.Calendar;
import java.util.Date;
public class MainActivity extends Activity {
    TextView doc;
    int mon = 4;
    int day=22;
    String id="not_set",pass="not_set";
    String student_reg_no="",student_name="";
    String[][] trtd;
    AnimationDrawable loading_animation;
    AnimationDrawable refresh_animation;
    ImageView refresh_image;
    ImageView loading_image;
    EditText id_edit;
    EditText pass_edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_page);
        loading_image = (ImageView)findViewById(R.id.loading_image);
        loading_animation = (AnimationDrawable)loading_image.getDrawable();
        id_edit = (EditText)findViewById(R.id.loginTextbox);
        pass_edit = (EditText)findViewById(R.id.passTextbox);
        //id_edit.clearFocus();
        if(id_present())
        {
            Button butt = (Button)findViewById(R.id.loginButton);
            id_edit.setText(id);
            pass_edit.setText(pass);
            Button b = (Button)findViewById(R.id.loginButton);
            b.setVisibility(View.GONE);
            start_loading_animation();
            id_edit.setFocusable(false);
            pass_edit.setFocusable(false);
            TextView errortxt = (TextView)findViewById(R.id.error_textbox);
            errortxt.setVisibility(View.INVISIBLE);
            TextView signing_in = (TextView)findViewById(R.id.signing_in);
            signing_in.setText("Singing in...");
            new doit().execute();
        }

        pass_edit.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("key123",keyCode+" "+KeyEvent.KEYCODE_ENTER+ " " + event.getAction() + " "+KeyEvent.ACTION_DOWN);
                return false;
            }
        });
            Button butt = (Button)findViewById(R.id.loginButton);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                id=id_edit.getText().toString();
                pass=pass_edit.getText().toString();
                Button b = (Button)findViewById(R.id.loginButton);
                b.setVisibility(View.GONE);
                start_loading_animation();
                id_edit.setFocusable(false);
                pass_edit.setFocusable(false);
                TextView errortxt = (TextView)findViewById(R.id.error_textbox);
                errortxt.setVisibility(View.INVISIBLE);
                new doit().execute();
            }
        });
        // doc = (TextView)findViewById(R.id.doc);

    }
    public boolean authentication()
    {
        String sday = new SimpleDateFormat("dd", Locale.getDefault()).format(new Date());
        String smon = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        if(Integer.parseInt(sday)>day||Integer.parseInt(smon)>mon)
        {
            Log.d("khataam","bhaiya");
            return false;
        }
        return true;
    }
    public void start_loading_animation()
    {
        loading_image.setVisibility(View.VISIBLE);
        loading_animation.start();
    }
    public void stop_loading_animation()
    {
        loading_animation.stop();
        loading_image.setVisibility(View.GONE);
    }
    public void start_refresh_animation()
    {
        refresh_image.setVisibility(View.VISIBLE);
        refresh_animation.start();
    }
    public void stop_refresh_animation()
    {
        refresh_animation.stop();
        refresh_image.setVisibility(View.GONE);
    }
    public void set_login_pref(String login_id,String pass)
    {
        SharedPreferences sp = getSharedPreferences("login_details",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("login_id",login_id);
        editor.putString("pass",pass);
        editor.apply();
    }
    public void clear_login_pref()
    {
        SharedPreferences sp = getSharedPreferences("login_details",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
    public boolean id_present()
    {
        SharedPreferences sp = getSharedPreferences("login_details",MODE_PRIVATE);
        if(sp.getString("login_id","")=="")
            return false;
        else
        {
            id=sp.getString("login_id","");
            pass=sp.getString("pass","");
            return true;
        }

    }
    public class refresh extends AsyncTask<Void,Void,Void>{
        String data;
        boolean error;
        @Override
        protected Void doInBackground(Void... params) {

            Connection.Response response2 = null;
            try {
                response2 = Jsoup.connect("https://slcm.manipal.edu/loginForm.aspx")
                        .method(Connection.Method.GET)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Document responseDocument = null;
            try {
                if(response2==null)
                {
                    data="Please check your internet connection!";
                    return null;
                }
                responseDocument = response2.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String viewState=responseDocument.select("#__VIEWSTATE").attr("value");
            String eventValidation=responseDocument.select("#__EVENTVALIDATION").attr("value");
            String viewStateGen=responseDocument.select("#__VIEWSTATEGENERATOR").attr("value");

            Connection.Response response = null;
            try {
                response = Jsoup.connect("https://slcm.manipal.edu/loginForm.aspx")
                        .data("ScriptManager1", "updpnl%7CbtnLogin")
                        .data("__EVENTTARGET", "")
                        .data("__EVENTARGUMENT", "")
                        .data("__VIEWSTATE", viewState)
                        .data("__VIEWSTATEGENERATOR", viewStateGen)
                        .data("__EVENTVALIDATION", eventValidation)
                        .data("txtUserid", id)
                        .data("txtpassword", pass)
                        .data("__ASYNCPOST", "true")
                        .data("btnLogin", "Sign%20in")
                        .userAgent("Mozilla")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Map<String, String> cookies = response.cookies();
            Document homePage = null;
            try {
                homePage = Jsoup.connect("https://slcm.manipal.edu/Academics.aspx")
                        .cookies(cookies)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(homePage==null)
            {
                data = "Wrong Id or Password";
                error=true;
            }
            else
            {
                Elements reg_no = homePage.select("#ContentPlaceHolder1_lblAttenEnrollNo");
                Elements name = homePage.select("#ContentPlaceHolder1_lblAttenName");
                student_reg_no = reg_no.text().toString();
                student_name = name.text().toString();
                Log.d("reg123",reg_no.text().toString());
                Log.d("name123",name.text().toString());
                Elements info = homePage.select("table#tblAttendancePercentage");
                if(!info.isEmpty())
                {
                    error=false;
                    Elements trs = info.select("tr");
                    trtd = new String[trs.size()][];
                    for (int i = 0; i < trs.size(); i++) {
                        //data+=trs.get(i).text();
                        Elements tds = trs.get(i).select("td");
                        trtd[i] = new String[tds.size()];
                        for (int j = 0; j < tds.size(); j++) {
                            trtd[i][j] = tds.get(j).text();
                        }
                    }


                    //data=info.html();
                }

                else{
                    data = "Wrong Id or Password";
                    error=true;
                }
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //doc.setText("Working....");

            error=true;
            //b.setClickable(false);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //doc.setText(data);

            // b.setClickable(true);
            stop_refresh_animation();
            Button refresh_button = (Button)findViewById(R.id.refresh_button);
            refresh_button.setVisibility(View.VISIBLE);
            if(!error)
            {
                //clear_login_pref();
                //setContentView(R.layout.start_page);
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout parent = (LinearLayout)findViewById(R.id.attendance_list);
                parent.removeAllViews();
                TextView student_reg_no_textbox = (TextView)findViewById(R.id.reg_no);
                student_reg_no_textbox.setText("Reg No: "+student_reg_no);
                for(int i=1;i<trtd.length;i++)
                {
                    View myLayout = inflater.inflate(R.layout.one_box, parent, false);
                    TextView subject = (TextView)myLayout.findViewById(R.id.subject);
                    TextView percentage = (TextView)myLayout.findViewById(R.id.percentage);
                    TextView total_classes = (TextView)myLayout.findViewById(R.id.total_classes);
                    subject.setText(trtd[i][1]);
                    total_classes.setText("Total Classes:  "+trtd[i][4]+"\nClasses Attended:  "+trtd[i][5]+"\nClasses Bunked:  "+(trtd[i][6]));
                    percentage.setText(trtd[i][7]+"%");
                    parent.addView(myLayout,parent.getChildCount());
                }
                Toast.makeText(getApplicationContext(),"Attendance Data Refreshed!",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Error Fetching Data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class doit extends AsyncTask<Void,Void,Void>{
        String data;
        boolean error;
        @Override
        protected Void doInBackground(Void... params) {
            if(!authentication())
            {
                data="Please Update the App!";
                error=true;
                return null;
            }
            Connection.Response response2 = null;
            try {
                    response2 = Jsoup.connect("https://slcm.manipal.edu/loginForm.aspx")
                    .method(Connection.Method.GET)
                    .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Document responseDocument = null;
            try {
                if(response2==null)
                {
                    data="Please check your internet connection!";
                    return null;
                }
                responseDocument = response2.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String viewState=responseDocument.select("#__VIEWSTATE").attr("value");
            String eventValidation=responseDocument.select("#__EVENTVALIDATION").attr("value");
            String viewStateGen=responseDocument.select("#__VIEWSTATEGENERATOR").attr("value");

            Connection.Response response = null;
            try {
                response = Jsoup.connect("https://slcm.manipal.edu/loginForm.aspx")
                        .data("ScriptManager1", "updpnl%7CbtnLogin")
                        .data("__EVENTTARGET", "")
                        .data("__EVENTARGUMENT", "")
                        .data("__VIEWSTATE", viewState)
                        .data("__VIEWSTATEGENERATOR", viewStateGen)
                        .data("__EVENTVALIDATION", eventValidation)
                        .data("txtUserid", id)
                        .data("txtpassword", pass)
                        .data("__ASYNCPOST", "true")
                        .data("btnLogin", "Sign%20in")
                        .userAgent("Mozilla")
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Map<String, String> cookies = response.cookies();
            Document homePage = null;
            try {
                homePage = Jsoup.connect("https://slcm.manipal.edu/Academics.aspx")
                        .cookies(cookies)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(homePage==null)
            {
                data = "Wrong Id or Password";
                error=true;
            }
            else
            {
                Elements reg_no = homePage.select("#ContentPlaceHolder1_lblAttenEnrollNo");
                Elements name = homePage.select("#ContentPlaceHolder1_lblAttenName");
                student_reg_no = reg_no.text().toString();
                student_name = name.text().toString();
                Log.d("reg123",reg_no.text().toString());
                Log.d("name123",name.text().toString());
                Elements info = homePage.select("table#tblAttendancePercentage");
                if(!info.isEmpty())
                {
                    error=false;
                    Elements trs = info.select("tr");
                    trtd = new String[trs.size()][];
                    for (int i = 0; i < trs.size(); i++) {
                        //data+=trs.get(i).text();
                        Elements tds = trs.get(i).select("td");
                        trtd[i] = new String[tds.size()];
                        for (int j = 0; j < tds.size(); j++) {
                            trtd[i][j] = tds.get(j).text();
                        }
                    }


                    //data=info.html();
                }

                else{
                    data = "Wrong Id or Password";
                    error=true;
                }
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //doc.setText("Working....");

            error=true;
            //b.setClickable(false);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //doc.setText(data);

           // b.setClickable(true);
            if(!error)
            {
                //clear_login_pref();
                set_login_pref(id,pass);
                setContentView(R.layout.start_page);
                Button logout_button = (Button)findViewById(R.id.logout_button);
                logout_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clear_login_pref();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });
                Button refresh_button = (Button)findViewById(R.id.refresh_button);
                refresh_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button b = (Button)findViewById(R.id.refresh_button);
                        refresh_image = (ImageView)findViewById(R.id.refresh_image);
                        refresh_animation = (AnimationDrawable)refresh_image.getDrawable();
                        b.setVisibility(View.GONE);
                        start_refresh_animation();
                        new refresh().execute();
                    }
                });
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout parent = (LinearLayout)findViewById(R.id.attendance_list);
                TextView student_reg_no_textbox = (TextView)findViewById(R.id.reg_no);
                student_reg_no_textbox.setText("Reg No: "+student_reg_no);
                for(int i=1;i<trtd.length;i++)
                {
                    View myLayout = inflater.inflate(R.layout.one_box, parent, false);
                    TextView subject = (TextView)myLayout.findViewById(R.id.subject);
                    TextView percentage = (TextView)myLayout.findViewById(R.id.percentage);
                    TextView total_classes = (TextView)myLayout.findViewById(R.id.total_classes);


                    subject.setText(trtd[i][1]);
                    total_classes.setText("Total Classes:  "+trtd[i][4]+"\nClasses Attended:  "+trtd[i][5]+"\nClasses Bunked:  "+(trtd[i][6]));
                    percentage.setText(trtd[i][7]+"%");
                    parent.addView(myLayout,parent.getChildCount());
                }
            }

            else
            {
                Button b = (Button)findViewById(R.id.loginButton);
                stop_loading_animation();
                id_edit.setFocusableInTouchMode(true);
                pass_edit.setFocusableInTouchMode(true);
                id_edit.setFocusable(true);
                pass_edit.setFocusable(true);
                b.setVisibility(View.VISIBLE);
                TextView errortxt = (TextView)findViewById(R.id.error_textbox);
                errortxt.setVisibility(View.VISIBLE);
                errortxt.setText(data);
            }
        }
    }
}