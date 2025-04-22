package com.meet.shared_inventory.Helper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meet.shared_inventory.Models.itemModel;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper {

    private final DatabaseReference rootRef;

    public FirebaseHelper() {
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    public void addData(String path, Object data, boolean push) {
        DatabaseReference ref = rootRef.child(path);
        if (push) {
            ref.push().setValue(data);
        } else {
            ref.setValue(data);
        }
    }

    public void updateData(String path, Object updatedData) {
        rootRef.child(path).setValue(updatedData);
    }

    public void deleteData(String path) {
        rootRef.child(path).removeValue();
    }

    /**
     * Fetch data from a specific path.
     * @param path Path to the node (e.g., "users/user1")
     * @param modelClass Your model class to convert the data into
     * @param callback Callback to return the fetched object or error
     * @param <T> Type of the model class
     */
    public <T> void fetchAll(String path, Class<T> modelClass, FetchListCallback<T> callback) {
        rootRef.child(path).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<T> dataList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    T item = child.getValue(modelClass);
                    if (item instanceof itemModel) {
                        ((itemModel) item).setKey(child.getKey());
                    }
                    dataList.add(item);
                }
                callback.onSuccess(dataList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }


    public interface FetchListCallback<T> {
        void onSuccess(List<T> dataList);   // When data is fetched successfully
        void onFailure(Exception e);        // When something goes wrong
    }


}
