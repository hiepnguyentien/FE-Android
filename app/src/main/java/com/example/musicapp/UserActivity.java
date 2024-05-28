package com.example.musicapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.musicapp.api.ApiClient;
import com.example.musicapp.api.UserService;
import com.example.musicapp.models.UserResponse;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {
    TextView tv_dang_xuat, doimk;
    TabLayout tabLayout;
    EditText etUserName, etEmail, etPhoneNumber, etFullName;
    ImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users);

        tv_dang_xuat = findViewById(R.id.tv_dang_xuat);
        tabLayout = findViewById(R.id.tab_layout);
        doimk = findViewById(R.id.doimk);
        etUserName = findViewById(R.id.edt_uName);
        etEmail = findViewById(R.id.edt_email);
        etPhoneNumber = findViewById(R.id.edt_phoneNo);
        etFullName = findViewById(R.id.edt_fullName);
        ivAvatar = findViewById(R.id.avatar);

        doimk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this, ResetPWActivity.class));
            }
        });

        // Set the selected tab to User tab (index 3, considering zero-based index)
        TabLayout.Tab userTab = tabLayout.getTabAt(3);
        if (userTab != null) {
            userTab.select();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    startActivity(new Intent(UserActivity.this, MainActivity.class));
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        tv_dang_xuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the token from SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token");
                editor.apply();

                // Navigate to LoginActivity
                Intent intent = new Intent(UserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Fetch user data
        fetchUserData();
    }

    private void fetchUserData() {
        // Assume userId is stored in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1); // Adjust the key as needed

        if (userId == -1) {
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        UserService userService = ApiClient.getClient(this).create(UserService.class);
        Call<List<UserResponse>> call = userService.getUserById(userId);

        call.enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    UserResponse user = response.body().get(0);
                    populateUserData(user);
                } else {
                    Toast.makeText(UserActivity.this, "Failed to get user data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                Toast.makeText(UserActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUserData(UserResponse user) {
        etUserName.setText(user.getUserName());
        etEmail.setText(user.getEmail());
        etPhoneNumber.setText(user.getPhoneNumber());
        etFullName.setText(user.getFullName());

        // Load avatar image using Glide
        Glide.with(this)
                .load(user.getAvatar())
                .into(ivAvatar);
    }
}