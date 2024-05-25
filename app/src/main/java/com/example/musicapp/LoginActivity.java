package com.example.musicapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.api.AuthService;
import com.example.musicapp.models.AuthResponse;
import com.example.musicapp.models.SignInModel;
import com.example.musicapp.util.DialogUtil;

import app.rive.runtime.kotlin.RiveAnimationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edt_username, edt_password;
    Button btn_dangnhap;
    CheckBox cb_missme;
    RiveAnimationView rv_robot;
    AuthService authService = AuthService.authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_email);
        btn_dangnhap = findViewById(R.id.btn_dangnhap);
        cb_missme = findViewById(R.id.cb_missme);
        rv_robot = findViewById(R.id.rv_robot);

        edt_password.setOnFocusChangeListener((view, isFocus) -> {
            if(isFocus) {
            rv_robot.setBooleanState("robot_login_machine", "is_hands_up", true);
            } else {
                rv_robot.setBooleanState("robot_login_machine", "is_hands_up", false);
            }
        });

        btn_dangnhap.setOnClickListener(v -> {
            String username = edt_username.getText().toString();
            String password = edt_password.getText().toString();

            if (TextUtils.isEmpty(username)) {
                DialogUtil.showDialog(LoginActivity.this, "Error", "username trống");
                rv_robot.fireState("robot_login_machine", "fail");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                DialogUtil.showDialog(LoginActivity.this, "Error", "password trống");
                rv_robot.fireState("robot_login_machine", "fail");
                return;
            }
            SignInModel model = new SignInModel(username, password, cb_missme.isChecked());
            authService.login(model).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    AuthResponse authResponse = response.body();
                    if (response.isSuccessful() & authResponse.isSucceed() & authResponse != null) {
                        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", authResponse.getData());
                        editor.apply();
                        rv_robot.fireState("robot_login_machine", "success");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        DialogUtil.showDialog(LoginActivity.this, "Error", response.toString());
                        rv_robot.fireState("robot_login_machine", "fail");
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                    new AlertDialog.Builder(LoginActivity.this).setMessage("Error : " + throwable.getMessage()).create().show();
                    rv_robot.fireState("robot_login_machine", "fail");
                }
            });
        });
    }
}