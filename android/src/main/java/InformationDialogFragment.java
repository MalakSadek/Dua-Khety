package malaksadek.duakhety;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by malaksadek on 2/17/18.
 */

public class InformationDialogFragment extends DialogFragment{


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Information on Sir Gardiner's Classification of Hieroglyphics:");
        alertDialogBuilder.setMessage("Each letter in the code represents a category, and each number represents a specific symbol.\n\nThe letter classification is as follows:\n\n" +
                        "A - Man and his occupations\n\nB - Woman and her occupations\n\nC - Anthropomorphic deities\n\nD - Parts of the human body\n\nE - Mammals\n\nF - Parts of Mammals\n\nG - Birds\n\nH - Parts of Birds\n\nI - Amphibious animals, reptiles, etc.\n\nJ - None\n\nK - Fish and parts of fish\n\nL - Invertebrates and lesser animals\n\nM - Trees and plants\n\nN - Sky, earth, water\n\nO - Buildings, parts of buildings, etc.\n\nP - Ships and parts of ships\n\nQ - Domestics and funerary furniture\n\nR - Temple furniture and sacred emblems\n\nS - Crowns, dress, staves, etc.\n\nT - Warfare, hunting, and butchery\n\nU - Agriculture, crafts, and professions\n\nV - Rope, fiber, baskets, bags, etc.\n\nW - Vessels of stone and earthenware\n\nX - Loaves and cakes\n\nY - Writings, games, music\n\nZ - Strokes, signs derived from Hieratic, geometrical figures\n\nAa - Unclassified\n\n");
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        return alertDialogBuilder.create();
    }
}
