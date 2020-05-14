package malaksadek.duakhety;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;


public class LogoutDialogFragment extends DialogFragment{
    Context mContext;
    public LogoutDialogFragment() {
        mContext = getActivity();
    }

    void updateSharedPreferences() {
        SharedPreferences firsttime = getContext().getSharedPreferences("DuaKhetyPrefs", 0);
        SharedPreferences.Editor editor = firsttime.edit();
        editor.putBoolean("First", true);
        editor.putString("Email", "None");
        editor.putString("Password", "None");
        editor.putString("Name", "None");
        editor.commit();
    }

    void Logout() {
        updateSharedPreferences();
        Intent i = new Intent(getContext(), OpeningActivity.class);
        FirebaseAuth.getInstance().signOut();
        startActivity(i);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Gone so soon?");
        alertDialogBuilder.setMessage("Are you sure you want to log out?");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
           Logout();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }
}
