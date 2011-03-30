package com.greenkit.smart;

import java.io.File;
import java.io.FileFilter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.greenkit.smart.database.DatabaseHelper;
import com.greenkit.smart.datatype.BookHeader;
import com.greenkit.smart.datatype.Word;
import com.greenkit.smart.engine.BookLoader;
import com.greenkit.smart.engine.BookLoader.OnBookLoadListener;
import com.greenkit.smart.utils.Utils;

/**
 * The UI for load book, it works just like a tiny file explorer.
 * 
 * @author green
 * 
 */
public class FileLoaderActivity extends Activity {

    private static final String PATH_ROOT = "/sdcard/";
    private static final String BOOK_FILE_SUFFIX = ".dict";
    private static final String PATH_PARENT_NAME = "..";

    private GridView mFileGrid;
    private FileAdapter mFileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_file_loader);
        mFileGrid = (GridView) findViewById(R.id.grid_files);
        mFileGrid.setOnItemClickListener(mOnFileClicListener);
        new LoadFileInfoTask().execute(PATH_ROOT);
    }

    private class LoadFileInfoTask extends AsyncTask<String, Void, Boolean>{

        private String path;
        private FileInfo[] list;

        @Override
        protected Boolean doInBackground(String... _path) {
            path = _path[0];
            final File file = new File(path);

            if(file.isFile()) {
                // Load book file
                if (path.endsWith(BOOK_FILE_SUFFIX)) {
                    loadBook(file);
                }

                // It is not the book file
                else {
                    FileLoaderActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(FileLoaderActivity.this, R.string.toast_not_book_file,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                return false;
            } else {
                list = getFileInfo(file);
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean isPath) {
            if(isPath) {
                if(mFileAdapter == null) {
                    mFileAdapter = new FileAdapter(list, path);
                    mFileGrid.setAdapter(mFileAdapter);
                } else {
                    mFileAdapter.updateData(list, path);
                }

                setTitle(path);
                list = null;
                path = null;
            }
        }

        private FileInfo[] getFileInfo(File directory) {
            File[] files;
            FileInfo[] list;

            files = directory.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    return file.canRead() && !file.isHidden();
                }
            });

            if(files == null) {
                return null;
            }

            list = new FileInfo[files.length];

            for (int i = 0; i < files.length; i++) {
                list[i] = new FileInfo();
                list[i].name = files[i].getName();
                String subPath = files[i].getAbsolutePath();

                // If it is a directory
                if (files[i].isDirectory()) {
                    list[i].path = Utils.formatDirectory(subPath);
                    list[i].icon = R.drawable.file_folder;
                }
                // If it is a file
                else {
                    list[i].path = subPath;

                    // Filter book files;
                    if (list[i].name.endsWith(BOOK_FILE_SUFFIX)) {
                        list[i].icon = R.drawable.file_book;
                    } else {
                        list[i].icon = R.drawable.file_unknown;
                    }
                }
            }

            return list;
        }
    }

    private void loadBook(File bookFile) {
        BookLoader loader = new BookLoader(this);
        loader.setOnBookLoadListener(new OnBookLoadListener() {

            private ProgressDialog dialog;

            @Override
            public void onStart() {
                FileLoaderActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        dialog = new ProgressDialog(FileLoaderActivity.this);
                        dialog.setIcon(R.drawable.file_book);
                        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        dialog.setCancelable(false);
                        dialog.setMessage("");
                        dialog.setTitle(getText(R.string.loading_word).toString().
                                replace("%s", ""));

                        dialog.setButton(ProgressDialog.BUTTON_NEUTRAL, getText(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int whichButton) {
                                final CharSequence buttonTxt = dialog.getButton(
                                        ProgressDialog.BUTTON_NEUTRAL).getText();

                                if (buttonTxt.equals(getText(R.string.cancel))) {
                                    // TODO: Cancel loading;
                                } else if (buttonTxt.equals(getText(R.string.ok))) {
                                    dialog.dismiss();
                                }
                            }
                        });

                        dialog.show();
                    }
                });
            }

            @Override
            public void onLoadWord(final Word word) {
//                mDatabaseHelper.insertWord(word);

                FileLoaderActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.setMessage(word.getWord() + ' ' + word.getSymbol());
                        dialog.incrementProgressBy(1);
                    }
                });
            }

            @Override
            public void onLoadHeader(final BookHeader header) {
                FileLoaderActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        dialog.setTitle(header.getBookName());
                        dialog.setMax(header.getWordNumber());
                    }
                });
            }

            @Override
            public void onComplete(int state) {
                if(state == BookLoader.LOAD_STATE_SUCCESS) {
                    FileLoaderActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            dialog.setMessage(getText(R.string.load_word_success));
                            dialog.getButton(ProgressDialog.BUTTON_NEUTRAL).setText(getText(R.string.ok));
                        }
                    });
                }
            }
        });

        loader.startParse(bookFile);
    }

    private AdapterView.OnItemClickListener mOnFileClicListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String path = view.getTag().toString();
            new LoadFileInfoTask().execute(path);
        }
    };

    /**
     * A simple class to hold the file information.
     */
    private static class FileInfo {
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

    /**
     * Adapter for file info grid view;
     */
    private class FileAdapter extends BaseAdapter {

        private FileInfo[] mList;
        private LayoutInflater inflater;

        public FileAdapter(FileInfo[] list, String path) {
            inflater = getLayoutInflater();
            updateData(list, path);
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
                convertView = inflater.inflate(R.layout.file_grid_item, parent, false);
            }

            TextView text = (TextView) convertView.findViewById(R.id.label);
            FileInfo info = mList[position];
            text.setText(info.name);
            text.setCompoundDrawablesWithIntrinsicBounds(0, info.icon, 0, 0);
            text.setTag(info.path);
            return convertView;
        }

        public void updateData(final FileInfo[] list, final String path) {
            // If current path is not root, we have to show one more item for parent path.
            // Otherwise, do not;
            if(path.equals(PATH_ROOT)) {
                mList = list;
            } else {
                // If list is null just parent will be shown;
                if(list == null || list.length < 1) {
                    mList = new FileInfo[1];
                    // Just one item for parent;
                    mList[0] = new FileInfo(PATH_PARENT_NAME, R.drawable.file_folder,
                            Utils.getParentPath(path));
                } else {
                    final int length = list.length;
                    mList = new FileInfo[length + 1];
                    System.arraycopy(list, 1, mList, 0, length);
                    // One more item for parent;
                    // The first item is parent directory;
                    mList[0] = new FileInfo(PATH_PARENT_NAME, R.drawable.file_folder,
                            Utils.getParentPath(path));
                }
            }

            // Notify refresh itself;
            notifyDataSetChanged();
        }
    }
}
