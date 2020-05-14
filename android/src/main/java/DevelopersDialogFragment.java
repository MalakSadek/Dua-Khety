package malaksadek.duakhety;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by malaksadek on 2/17/18.
 */

public class DevelopersDialogFragment extends DialogFragment{


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("This application and all underlying processes was developed by:");
        alertDialogBuilder.setMessage("Malak Sadek\nMohamed Badreldin\nAhmed El Agha\nMohamed Ghoneim");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Contact Us", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendEmail();
            }
        });

        alertDialogBuilder.setNegativeButton("Thanks Guys!", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }

    void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"malaksadek@aucegypt.edu","mohapard@aucegypt.edu","agha@aucegypt.edu","mohamady996@aucegypt.edu"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Dua-Khety Feedback");

        try {
            startActivity(Intent.createChooser(i, "Send email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getContext(), "Feedback Received, thank you!", Toast.LENGTH_LONG).show();

    }
}
