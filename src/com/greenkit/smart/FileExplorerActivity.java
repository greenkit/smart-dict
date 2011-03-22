package com.greenkit.smart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.greenkit.smart.database.table.Smart;
import com.greenkit.smart.database.type.Word;

/**
 * The UI for load the books.
 * 
 * @author green
 * 
 */
public class FileExplorerActivity extends Activity {

    private static final String TAG = "LibLoader";
    private static final String FILE_ROOT = "/sdcard/";
    private static final String SUFFIX_LIB = ".lib";
    private static final String PATH_PARENT = "..";
    // private static final String LIB_DEFAULT_PATH = "libs";

    private GridView mGrid;
    private FileAdapter mAdapter;
    private String mCurrentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_lib_loader);
        mGrid = (GridView) findViewById(R.id.grid_files);

        File file = new File(FILE_ROOT);
        mCurrentPath = FILE_ROOT;
        setTitle(FILE_ROOT);

        FileInfo[] infos = getFileInfo(file);
        mAdapter = new FileAdapter(infos, FILE_ROOT);

        mGrid.setAdapter(mAdapter);
        mGrid.setOnItemClickListener(mItemClickListener);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String path = view.getTag().toString();
            File file = new File(path);

            if (file.isDirectory()) {
                FileInfo[] infos = getFileInfo(file);
                if (infos != null) {
                    mAdapter.updateData(infos, path);
                    mCurrentPath = path;
                    setTitle(mCurrentPath);
                }
            } else {
                // Load library file
                if (path.endsWith(SUFFIX_LIB)) {
                    loadLib(file);
                }
                // It is not the library file
                else {
                    Toast.makeText(FileExplorerActivity.this, R.string.toast_not_lib_file,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    /**
     * Load the words from library file.
     */
    private void loadLib(File file) {
        new LoaderThread(this, file).loadLibs();
    }

    private class LoaderThread extends Thread {

        private File file;
        private Activity context;
        private BufferedReader reader;
        private boolean loading;
        private ProgressDialog dialog;

        public LoaderThread(Activity _context, File _file) {
            context = _context;
            file = _file;
        }

        private void showLoadingDialog() {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    dialog = new ProgressDialog(context);
                    dialog.setIcon(R.drawable.lib);
                    dialog.setTitle(getText(R.string.load_word_from_file).toString().replace("%s",
                            file.getName()));
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.setMax(3007);
                    dialog.setCancelable(false);
                    dialog.setButton(getText(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    loading = false;
                                }
                            });

                    dialog.show();
                }
            });
        }

        public void loadLibs() {
            try {
                InputStream in = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(in));
                start();
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        private Word readWord() throws IOException {
            String trans = null;
            String line = readLine();

            if (line.equals(":||")) {
                return null;
            }

            Word word = new Word();

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == '\n') {
                    word.mName = line;
                    break;
                } else if (c == ' ') {
                    word.mName = line.substring(0, i);
                    word.mSoundmark = line.substring(i).trim();
                    break;
                }
            }

            while (!TextUtils.isEmpty(trans = readLine())) {
                word.mTranslation += trans;
            }

            return word;
        }

        private String readLine() throws IOException {
            String line = reader.readLine();

            if (!TextUtils.isEmpty(line)) {
                return line.trim();
            }

            return null;
        }

        public void run() {
            showLoadingDialog();
            loading = true;
            Word word = null;
            try {
                while (loading && (word = readWord()) != null) {
                    insert(word);
                    dialog.incrementProgressBy(1);
                }
                dialog.dismiss();
            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }
            }
        }

        private void insert(final Word word) {
            ContentValues values = new ContentValues();
            values.put(Smart.Words.NAME, word.mName);
            values.put(Smart.Words.SOUNDMARK, word.mSoundmark);
            values.put(Smart.Words.TRANSLATION, word.mTranslation);
            values.put(Smart.Words.EXAMPLE, word.mExample);
            values.put(Smart.Words.PRONUNCIATION, word.mPronunciation);

            getContentResolver().insert(Smart.Words.CONTENT_URI, values);

            context.runOnUiThread(new Runnable() {
                public void run() {
                    dialog.setTitle(word.mName);
                }
            });
        }
    }

    /**
     * The path of directory must be included the suffix '/' If some path don't
     * have the '/' character, we add it.
     */
    private String formatDirectory(String dir) {
        if (dir.endsWith(File.separator)) {
            return dir;
        } else {
            return dir + File.separator;
        }
    }

    /**
     * Get the parent path according to the path string.
     */
    private String getParentPath(String path) {
        if (!path.endsWith(File.separator)) {
            return path.substring(0, path.lastIndexOf(File.separator) + 1);
        } else {
            return path.substring(0, path.lastIndexOf(File.separator, path.length() - 2) + 1);
        }
    }

    private FileInfo[] getFileInfo(File f) {
        if (f != null) {
            File[] files;
            FileInfo[] infos;
            File file = f;

            if (file.exists()) {
                files = file.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.canRead() && !file.isHidden();
                    }
                });

                infos = new FileInfo[files.length];

                for (int i = 0; i < files.length; i++) {
                    infos[i] = new FileInfo();
                    infos[i].name = files[i].getName();
                    String path = files[i].getAbsolutePath();

                    // If it is directory
                    if (files[i].isDirectory()) {
                        infos[i].path = formatDirectory(path);
                        infos[i].icon = R.drawable.folder;
                    }
                    // If it is file
                    else {
                        infos[i].path = path;

                        if (files[i].getName().endsWith(SUFFIX_LIB)) {
                            infos[i].icon = R.drawable.lib;
                        } else {
                            infos[i].icon = R.drawable.unknown_file;
                        }
                    }
                }

                return infos;
            }
        }

        return null;
    }

    private class FileInfo {
        public String name;
        public int icon;
        public String path;

        public FileInfo() {

        }

        public FileInfo(String _name, int _icon, String _path) {
            name = _name;
            icon = _icon;
            path = _path;
        }
    }

    private class FileAdapter extends BaseAdapter {

        private FileInfo[] mList;

        public FileAdapter(FileInfo[] list, String destPath) {
            updateData(list, destPath);
        }

        public int getCount() {
            return mList.length;
        }

        public Object getItem(int position) {
            return mList[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                final LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.file, parent, false);
            }

            TextView text = (TextView) convertView.findViewById(R.id.label);
            FileInfo info = mList[position];
            text.setText(info.name);
            text.setCompoundDrawablesWithIntrinsicBounds(0, info.icon, 0, 0);
            text.setTag(info.path);
            return convertView;
        }

        public void updateData(FileInfo[] list, String destPath) {
            if (destPath.equals(FILE_ROOT)) {
                mList = list;
            } else {
                int length = list.length;
                mList = new FileInfo[length + 1];
                System.arraycopy(list, 0, mList, 0, length);
                mList[length] = new FileInfo(PATH_PARENT, R.drawable.folder,
                        getParentPath(destPath));
            }

            notifyDataSetChanged();
        }
    }
}
