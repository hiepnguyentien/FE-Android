package com.example.musicapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.api.ApiClient;
import com.example.musicapp.api.AuthService;
import com.example.musicapp.api.UserService;
import com.example.musicapp.models.AuthResponse;
import com.example.musicapp.models.SignUpModel;
import com.example.musicapp.util.DialogUtil;

import app.rive.runtime.kotlin.RiveAnimationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText edt_username, edt_password, edt_email;
    TextView tv_login;
    Button btn_dangky;
    RiveAnimationView rv_robot;
    AuthService authService = AuthService.authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        edt_email = findViewById(R.id.edt_email);
        btn_dangky = findViewById(R.id.btn_dangky);
        rv_robot = findViewById(R.id.rv_robot);
        tv_login = findViewById(R.id.tv_dangnhap);

        tv_login.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        edt_password.setOnFocusChangeListener((view, isFocus) -> {
            if(isFocus) {
                rv_robot.setBooleanState("robot_login_machine", "is_hands_up", true);
            } else {
                rv_robot.setBooleanState("robot_login_machine", "is_hands_up", false);
            }
        });

        btn_dangky.setOnClickListener(v -> {
            String username = edt_username.getText().toString();
            String email = edt_email.getText().toString();
            String password = edt_password.getText().toString();

            if (TextUtils.isEmpty(username)) {
                DialogUtil.showDialog(RegisterActivity.this, "Error", "username trống");
                rv_robot.fireState("robot_login_machine", "fail");
                return;
            }
            if (TextUtils.isEmpty(email)) {
                DialogUtil.showDialog(RegisterActivity.this, "Error", "email trống");
                rv_robot.fireState("robot_login_machine", "fail");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                DialogUtil.showDialog(RegisterActivity.this, "Error", "password trống");
                rv_robot.fireState("robot_login_machine", "fail");
                return;
            }
            SignUpModel model = new SignUpModel(username, email, password);
            authService.register(model).enqueue(new Callback<AuthResponse>() {
                @Override
                public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                    AuthResponse authResponse = response.body();
                    if (response.isSuccessful() & authResponse.isSucceed()) {
                        rv_robot.fireState("robot_login_machine", "success");
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        DialogUtil.showDialog(RegisterActivity.this, "Error", response.toString());
                        rv_robot.fireState("robot_login_machine", "fail");
                    }
                }

                @Override
                public void onFailure(Call<AuthResponse> call, Throwable throwable) {
                    new AlertDialog.Builder(RegisterActivity.this).setMessage("Error : " + throwable.getMessage()).create().show();
                    rv_robot.fireState("robot_login_machine", "fail");
                }
            });
        });
    }
}