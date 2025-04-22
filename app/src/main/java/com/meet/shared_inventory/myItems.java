package com.meet.shared_inventory;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.meet.shared_inventory.Helper.FirebaseHelper;
import com.meet.shared_inventory.Models.itemModel;
import com.meet.shared_inventory.adapters.itemAdapter;

import java.util.List;

public class myItems extends AppCompatActivity {

    private RecyclerView recyclerView;
    private itemAdapter itemAdapter;
    private FirebaseHelper firebaseHelper;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String userId = auth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_items);
        recyclerView = findViewById(R.id.rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new itemAdapter();
        recyclerView.setAdapter(itemAdapter);
        firebaseHelper = new FirebaseHelper();
        loadItems();
    }

    private void loadItems() {
        firebaseHelper.fetchAll("Item Details/"+userId, itemModel.class, new FirebaseHelper.FetchListCallback<itemModel>() {
            @Override
            public void onSuccess(List<itemModel> userList) {
                itemAdapter.setItemList(userList);
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("myItems", "Failed to fetch users: " + e.getMessage());
            }
        });


    }
}