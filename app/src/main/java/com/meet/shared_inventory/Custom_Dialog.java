package com.meet.shared_inventory;

import static com.meet.shared_inventory.Constants.KEY_DONT_SHOW_AGAIN;
import static com.meet.shared_inventory.Constants.PREFS_NAME;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class Custom_Dialog {

    private final Context context;

    // Constructor to receive context
    public Custom_Dialog(Context context) {
        this.context = context;
    }

    public void Show(String[] instructions,String t) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean dontShowAgain = preferences.getBoolean(KEY_DONT_SHOW_AGAIN, false);

        if (dontShowAgain) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.custom_alert_box, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();

        CheckBox dontShowAgainCheckBox = dialogView.findViewById(R.id.dont_show_again);
        TextView title = dialogView.findViewById(R.id.title);
        Button skipButton = dialogView.findViewById(R.id.skip_button);
        Button okButton = dialogView.findViewById(R.id.ok_button);
        TextView messageText = dialogView.findViewById(R.id.message);
        title.setText(t);
        final int[] currentStep = {0};

        okButton.setOnClickListener(v -> {
            if (currentStep[0] < instructions.length - 1) {
                currentStep[0]++;
                messageText.setText(instructions[currentStep[0]]);

                // Change button text when reaching the last instruction
                if (currentStep[0] == instructions.length - 1) {
                    okButton.setText("OK");
                }
            } else {
                dialog.dismiss(); // Close the dialog after the last step
            }
        });


        skipButton.setOnClickListener(v -> {
            if (dontShowAgainCheckBox.isChecked()) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(KEY_DONT_SHOW_AGAIN, true);
                editor.apply();
            }
            dialog.dismiss();
        });

        messageText.setText(instructions[0]);
    }
}
