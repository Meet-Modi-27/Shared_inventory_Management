package com.meet.shared_inventory.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.meet.shared_inventory.Models.itemModel;
import com.meet.shared_inventory.R;
import com.meet.shared_inventory.selectedItem;

import java.util.ArrayList;
import java.util.List;

public class itemAdapter extends RecyclerView.Adapter<itemAdapter.itemViewModel> {
    private List<itemModel> itemList = new ArrayList<>();

    public void setItemList(List<itemModel> list) {
        this.itemList = list;
    }

    @NonNull
    @Override
    public itemAdapter.itemViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        return new itemViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemViewModel holder, int position) {
        itemModel itemModel = itemList.get(position);
        holder.name.setText(itemModel.getItemName());
        holder.qty.setText(itemModel.getQty());
        if (itemModel.getKey() != null) {
            generateQRCode(itemModel.getKey(), holder.qr);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), selectedItem.class);
            v.getContext().startActivity(intent);
        });
    }
    private void generateQRCode(String key, ImageView qr) {
        int qrColor = Color.BLACK;
        int backgroundColor = Color.WHITE;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(key, BarcodeFormat.QR_CODE, 200, 200);
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
            e.printStackTrace();
            throw new RuntimeException("QR code generation failed", e);
        }
    }



    @Override
    public int getItemCount() {
        return itemList.size();
    }
    static class itemViewModel extends RecyclerView.ViewHolder{
        public TextView name,qty;
        public ImageView qr;


        public itemViewModel(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            qty = itemView.findViewById(R.id.qty);
            qr = itemView.findViewById(R.id.qrCode);
        }
    }

}
