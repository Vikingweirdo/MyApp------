package com.example.asus.myapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RegisterStudent extends AppCompatActivity{
    private final String userInfo = "user_info";
    private static final int LoginFlag = 2;
    private EditText rguser = null;
    private EditText rgpass = null;
    private EditText checkPass = null;
    private EditText studentId = null;
    private Button regist = null;
    private Button cancel = null;
    private String tempname = "";
    private String temppass = null;
    private String tempCheck = null;
    private String tempID = null;
    private boolean flag = false;
    private UserActivity userActivity = new UserActivity();

    private static String selected = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0 :
                    Toast.makeText(RegisterStudent.this, "注册成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.register_student);

        Resources resources = getResources();
        Drawable drawable = resources.getDrawable(R.drawable.registerWindowBackground);
        this.getWindow().setBackgroundDrawable(drawable);

        this.studentId = (EditText)super.findViewById(R.id.studentId);
        this.rguser = (EditText) super.findViewById(R.id.registeruser);
        this.checkPass = (EditText) super.findViewById(R.id.checkpsd);
        this.rgpass = (EditText) super.findViewById(R.id.registerpsd);
        this.regist = (Button) super.findViewById(R.id.register);
        this.cancel = (Button) super.findViewById(R.id.cancel);


        this.regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempname = rguser.getText().toString();
                temppass = rgpass.getText().toString();
                tempCheck = checkPass.getText().toString();
                tempID = studentId.getText().toString();

                if (tempCheck.equals(temppass) && !tempname.equals("")) {
                        SentInfo sentInfo = new SentInfo(tempname,temppass,tempID);
                        sentInfo.start();
                }else{
                    Toast.makeText(RegisterStudent.this, "注册失败，两次密码不正确或者用户名为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterStudent.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public String getTempname(){
        return this.tempname;
    }

    public String getTemppass(){
        return this.temppass;
    }

    public int getLoginFlag(){
        return this.LoginFlag;
    }

    private class SentInfo extends Thread{
        private String tempname = null;
        private String temppass = null;
        private String tempId = null;
        public SentInfo(String tempname ,String temppass, String tempId){
            this.tempId = tempId;
            this.tempname = tempname;
            this.temppass = temppass;
        }


        @Override
        public void run() {

            final String url = "http://115.28.80.81/app/check.php";

            try {

                HttpPost request = new HttpPost(url);

                List<NameValuePair> params = new ArrayList<>();

                //添加入学生标志 “xs”
                params.add(new BasicNameValuePair("flag", String.valueOf(0)));
                params.add(new BasicNameValuePair("name", tempname));
                params.add(new BasicNameValuePair("password", temppass));
                params.add(new BasicNameValuePair("id",tempID));
                params.add(new BasicNameValuePair("action","register"));
                request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

                HttpResponse response = new DefaultHttpClient().execute(request);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String str = EntityUtils.toString(response.getEntity());
                    System.out.println("JSON1-------->" + str);
                    JSONObject jsonObject = new JSONObject(str);
                    flag = jsonObject.getBoolean("status");

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (flag == true) {

                SharedPreferences sharedPreferences = getSharedPreferences(userInfo, Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Username", tempname);
                editor.putString("Passworld", temppass);
                editor.putString("StudentId", tempID);
                editor.commit();
                //Toast.makeText(RegisterStudent.this, "注册成功", Toast.LENGTH_SHORT).show();
                RegisterStudent.this.handler.sendEmptyMessage(0);
                userActivity.setUserID(tempId);
                Intent intent = new Intent(RegisterStudent.this, Login.class);
                startActivity(intent);
                finish();
            } else {
                //Toast.makeText(RegisterStudent.this, "注册失败，网络异常", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void setSelected(String selected){
        this.selected = selected;
    }

    public String getSelected(){return this.selected;}
}
