package com.meet.shared_inventory;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.meet.shared_inventory.Models.itemModel;

public class itemDetails extends AppCompatActivity {

    TextInputEditText _itemName,_itemQty;
    TextInputLayout _nameLayout,_qtyLayout;
    ImageView qr;
    FirebaseAuth auth;
    FirebaseUser user;
    String userId;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbref = db.getReference().child("Item Details");
    Button send;
    String[] instructions = {
            "Step 1: Welcome!",
            "Step 2: Add all the details asked. \nPlease make sure Item name is not repeated.",
            "Step 3: Once Qr Code is generated please save the QR Code for further usage.\nYou can see the qr code in the items section for later use.",
            "Step 4: You're all set! Click OK to close."
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_details);
        _itemName = findViewById(R.id.itemName);
        _itemQty = findViewById(R.id.itemQty);
        _nameLayout = findViewById(R.id.itemNameLayout);
        _qtyLayout = findViewById(R.id.itemQtyLayout);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid().toString();
        qr = findViewById(R.id.qrCode);
        send = findViewById(R.id.generate);
        Custom_Dialog dialog = new Custom_Dialog(this);

        dialog.Show(instructions,"Add Items");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchDetails();
            }
        });
    }

    private void fetchDetails() {
        String Name = _itemName.getText().toString();
        String Qty = _itemQty.getText().toString();

        if (Name.isEmpty()) {
            _nameLayout.setError("Please Enter Name");
        } else if (Qty.isEmpty()) {
            _qtyLayout.setError("Please Enter Quantity");
        } else {
            check(Name, new FirebaseCallback() {
                @Override
                public void onCallback(boolean result) {
                    if (!result) {
                        _nameLayout.setError("Name already exists");
                    } else {
                        _nameLayout.setError(null);
                        _qtyLayout.setError(null);
                        updateData(Name, Qty);
                    }
                }
            });
        }


    }

    public interface FirebaseCallback {
        void onCallback(boolean result);
    }

    private void check(String Name, FirebaseCallback callback) {
        dbref.child(userId).child(Name.toUpperCase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onCallback(false);
                } else {
                    callback.onCallback(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCallback(false);
            }
        });
    }



    private void updateData(String Name,String Qty) {
        itemModel item = new itemModel(Name,Qty);
        dbref.child(userId).child(Name.toUpperCase()).setValue(item);
        generateQRCode(Name.toUpperCase());
    }

    private void generateQRCode(String key) {
        int qrColor = Color.BLACK;
        int backgroundColor = Color.WHITE;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(key, BarcodeFormat.QR_CODE, 500, 500);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? qrColor : backgroundColor);
                }
            }
            qr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }
}