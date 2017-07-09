package udacity.project.summer.chatme;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by azeddine on 03/07/17.
 */

public class ImportImageDialogFragment extends DialogFragment {
    public static final String TAG = "ImportImageDialogFragment";
    public static final String DIALOG_TITLE="dialog title";

    public static final int IMPORT_FROM_GALLERY = 0;
    public static final int TAKE_PHOTO = 1;

    public interface ImportOnClickListener {
      void onClick(int value);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(savedInstanceState != null){
            int dialogTitleResourceID = savedInstanceState.getInt(DIALOG_TITLE);
            builder.setTitle(dialogTitleResourceID);
        }
        builder.setItems(R.array.import_image_dialog_items,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((ImportOnClickListener)getContext()).onClick(which);
                    }
                }
        );
        return builder.create();
    }

}
