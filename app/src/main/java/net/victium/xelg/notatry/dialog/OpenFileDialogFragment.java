package net.victium.xelg.notatry.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OpenFileDialogFragment extends DialogFragment {

    private File mDefaultPath;
    public File mSelectedFileName;

    public interface OpenFileDialogListener {
        void onDialogFileSelected(DialogFragment dialogFragment);
    }

    OpenFileDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OpenFileDialogListener) context;
        } catch (ClassCastException exception) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        mDefaultPath = new File(Environment.getExternalStorageDirectory() + "/notatry");

        if (!mDefaultPath.exists()) {
            mDefaultPath.mkdirs();
        }

        builder.setTitle("Выберете файл для импорта")
                .setNegativeButton("отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .setAdapter(new FileAdapter(getActivity(), getFiles(mDefaultPath)), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSelectedFileName = getFiles(mDefaultPath).get(which).getAbsoluteFile();
                        mListener.onDialogFileSelected(OpenFileDialogFragment.this);
                    }
                });

        return builder.create();
    }

    private List<File> getFiles(File directoryPath) {
        List<File> fileList = Arrays.asList(directoryPath.listFiles());
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile()) {
                    return -1;
                } else if (o1.isFile() && o2.isDirectory()) {
                    return 1;
                } else {
                    return o1.getPath().compareTo(o2.getPath());
                }
            }
        });

        return fileList;
    }

    private class FileAdapter extends ArrayAdapter<File> {

        public FileAdapter(@NonNull Context context, List<File> files) {
            super(context, android.R.layout.simple_list_item_1, files);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            File file = getItem(position);
            view.setText(file.getName());
            return view;
        }
    }
}
