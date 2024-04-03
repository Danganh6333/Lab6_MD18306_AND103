package com.dangchph33497.fpoly.lab6_and103_ph33497.Views;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.dangchph33497.fpoly.lab6_and103_ph33497.Adapter.FruitAdapter;
import com.dangchph33497.fpoly.lab6_and103_ph33497.Model.Fruit;
import com.dangchph33497.fpoly.lab6_and103_ph33497.Model.Page;
import com.dangchph33497.fpoly.lab6_and103_ph33497.Model.Response;
import com.dangchph33497.fpoly.lab6_and103_ph33497.Services.HttpRequest;
import com.dangchph33497.fpoly.lab6_and103_ph33497.databinding.ActivityHomeBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeActivity extends AppCompatActivity implements FruitAdapter.FruitClick {
    ActivityHomeBinding binding;
    private HttpRequest httpRequest;
    private SharedPreferences sharedPreferences;
    private String token;
    private FruitAdapter adapter;
    private ArrayList<Fruit> ds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("INFO",MODE_PRIVATE);

        token = sharedPreferences.getString("token","");
        httpRequest = new HttpRequest(token);
        httpRequest.callAPI().getListFruit("Bearer " + token).enqueue(getListFruitResponse);


        userListener();

    }
    private void userListener () {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this , AddFruitActivity.class));
            }
        });
    }

    Callback<Response<ArrayList<Fruit>>> getListFruitResponse = new Callback<Response<ArrayList<Fruit>>>() {
        @Override
        public void onResponse(Call<Response<ArrayList<Fruit>>> call, retrofit2.Response<Response<ArrayList<Fruit>>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() ==200) {
                    ArrayList<Fruit> ds = response.body().getData();
                    getData(ds);
//                    Toast.makeText(HomeActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<ArrayList<Fruit>>> call, Throwable t) {

        }
    };

    private void getData (ArrayList<Fruit> ds) {
            adapter = new FruitAdapter(this, ds,this );
            binding.rcvFruit.setAdapter(adapter);
    }
    Callback<Response<Fruit>> responseFruitAPI = new Callback<Response<Fruit>>() {
        @Override
        public void onResponse(Call<Response<Fruit>> call, retrofit2.Response<Response<Fruit>> response) {
            if (response.isSuccessful()) {
                if (response.body().getStatus() == 200) {
                    ds.clear();
                    Toast.makeText(HomeActivity.this, response.body().getMessenger(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onFailure(Call<Response<Fruit>> call, Throwable t) {
            Log.e("zzzzzzzz", "onFailure: "+t.getMessage() );
        }
    };


    @Override
    public void delete(Fruit fruit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("yes", (dialog, which) -> {
            httpRequest.callAPI()
                    .deleteFruits(fruit.get_id())
                    .enqueue(responseFruitAPI);
        });
        builder.setNegativeButton("no", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();


    }

    @Override
    public void edit(Fruit fruit) {
        Intent intent =new Intent(HomeActivity.this, UpdateFruitActivity.class);
        intent.putExtra("fruit", fruit);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        httpRequest.callAPI().getListFruit("Bearer "+token).enqueue(getListFruitResponse);
    }
}