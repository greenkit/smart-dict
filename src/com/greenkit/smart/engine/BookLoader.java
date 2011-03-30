package com.greenkit.smart.engine;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

import com.greenkit.smart.database.DatabaseHelper;
import com.greenkit.smart.datatype.BookHeader;
import com.greenkit.smart.datatype.Word;

public class BookLoader extends Thread {

    public static final int LOAD_STATE_SUCCESS = 0;
    public static final int LOAD_STATE_ERROR = 1;
    public static final int LOAD_STATE_CANCEL = 3;

    private static final String TAG = "BookLoader";

    private boolean mCancel;
    private BookParser mParser;
    private File mBookFile;
    private OnBookLoadListener mOnBookLoadListener;
    private Context mContext;

    public static interface OnBookLoadListener {
        public void onStart();
        public void onLoadHeader(BookHeader header);
        public void onComplete(int state);
        public void onLoadWord(Word word);
    }

    public BookLoader(Context context) {
        mContext = context;
    }

    public void setOnBookLoadListener(OnBookLoadListener l) {
        mOnBookLoadListener = l;
    }

    public void startParse(File bookFile) {
        if(mOnBookLoadListener == null) {
            throw new RuntimeException("The OnBookLoadListener is null, " +
                    "you have to call setOnBookLoadListener() method first!");
        }

        mBookFile = bookFile;
        start();
        mOnBookLoadListener.onStart();
    }

    @Override
    public void run() {
        mParser = new BookParser(mBookFile);
        mOnBookLoadListener.onLoadHeader(mParser.getBookHeader());
        final DatabaseHelper mDatabaseHelper = DatabaseHelper.getInstance(mContext);
        int counter = 0;

        try {
            while (true) {
                if(mCancel) {
                    mOnBookLoadListener.onComplete(LOAD_STATE_CANCEL);
                    return;
                }

                Word word = mParser.getNextWord();
                // If word is null, load finished.
                if(word == null) {
                    if(mParser.getBookHeader().getWordNumber() == counter) {
                        mOnBookLoadListener.onComplete(LOAD_STATE_SUCCESS);
                    } else {
                        mOnBookLoadListener.onComplete(LOAD_STATE_ERROR);
                    }
                    return;
                } else {
                    mOnBookLoadListener.onLoadWord(word);
                    counter++;
                    // Insert word to database;
                    mDatabaseHelper.insertWord(word);
                }

                // Give chance for UI thread change its state;
                Thread.sleep(10);
            }
        } catch (IOException e) {
            Log.d(TAG, e.getLocalizedMessage());
            mOnBookLoadListener.onComplete(LOAD_STATE_ERROR);
        } catch (InterruptedException e) {
            Log.d(TAG, e.getLocalizedMessage());
            mOnBookLoadListener.onComplete(LOAD_STATE_ERROR);
        } finally {
            if(mParser != null) {
                mParser.close();
                mParser = null;
            }
        }
    }
}
