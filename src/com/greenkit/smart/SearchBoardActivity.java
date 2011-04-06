package com.greenkit.smart;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.greenkit.smart.database.DatabaseHelper;
import com.greenkit.smart.database.table.WordTable;

/**
 * The UI for word search.
 * @author green
 *
 */
public class SearchBoardActivity extends Activity {

    static final int HANDLE_MESSAGE_SEARCH_WORD = 0;
    static final long HANDLE_MESSAGE_SEARCH_DELAY = 500L;

    private ListView mWordList;
    private WordListAdapter mWordAdapter;
    private DatabaseHelper mDatabaseHelper;
    private HandlerThread mSearchThread;
    private SearchHandler mSearchHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDatabaseHelper = DatabaseHelper.getInstance(this);
        initUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSearchThread = new HandlerThread("search");
        mSearchThread.start();
        mSearchHandler = new SearchHandler(mSearchThread.getLooper(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSearchThread != null) {
            mSearchThread.quit();
            mSearchThread = null;
        }
    }

    private void initUi () {
        setContentView(R.layout.screen_search_board);
        mWordList = (ListView) findViewById(R.id.word_list);
        ((EditText) findViewById(R.id.input_search)).addTextChangedListener(mSearchWordWatcher);
        mWordAdapter = new WordListAdapter(this, mDatabaseHelper.queryWordByName(null));
        mWordList.setAdapter(mWordAdapter);
    }

    private final TextWatcher mSearchWordWatcher = new TextWatcher() {

        private String text;
        private Message message;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Do nothing;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            text = s.toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {
            final String key = s.toString().trim();
            if (!key.equalsIgnoreCase(text)) {
                // If the interval of user input is less than the default value, the prior query will be canceled.
                // Otherwise, do query.
                if (message != null && SystemClock.uptimeMillis() - message.getWhen()
                            < HANDLE_MESSAGE_SEARCH_DELAY) {
                        mSearchHandler.removeMessages(HANDLE_MESSAGE_SEARCH_WORD);
                }

                message = mSearchHandler.obtainMessage(HANDLE_MESSAGE_SEARCH_WORD, key);
                mSearchHandler.sendMessageDelayed(message, HANDLE_MESSAGE_SEARCH_DELAY);
            }
        }
    };

    private static final class WordListAdapter extends CursorAdapter {

        private LayoutInflater inflater;
        private int columnWord;
        private int columnSearchCount;
        private int columnStudyCount;
        private int columnMistakeCount;
        private int columnLevel;

        private static class ViewHolder {
            private TextView word;
            private TextView search;
            private TextView study;
            private TextView mistake;
            private TextView level;

            public ViewHolder (View view) {
                word = (TextView) view.findViewById(R.id.word);
                search = (TextView) view.findViewById(R.id.search_count);
                study = (TextView) view.findViewById(R.id.study_count);
                mistake = (TextView) view.findViewById(R.id.mistake_count);
                level = (TextView) view.findViewById(R.id.level);
            }
        }

        public WordListAdapter(Context context, Cursor c) {
            super(context, c);
            inflater = LayoutInflater.from(context);
            storeColumnIndex(c);
        }

        @Override
        public void changeCursor(Cursor cursor) {
            super.changeCursor(cursor);
            storeColumnIndex(cursor);
        }

        private void storeColumnIndex (Cursor c) {
            columnWord = c.getColumnIndex(WordTable.COLUMN_NAME_WORD);
            columnSearchCount = c.getColumnIndex(WordTable.COLUMN_NAME_SEARCH_COUNT);
            columnStudyCount = c.getColumnIndex(WordTable.COLUMN_NAME_STUDY_COUNT);
            columnMistakeCount = c.getColumnIndex(WordTable.COLUMN_NAME_MISTAKE_COUNT);
            columnLevel = c.getColumnIndex(WordTable.COLUMN_NAME_LEVEL);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder(view);
                view.setTag(holder);
            }

            holder.word.setText(cursor.getString(columnWord));
            holder.search.setText(cursor.getString(columnSearchCount));
            holder.study.setText(cursor.getString(columnStudyCount));
            holder.mistake.setText(cursor.getString(columnMistakeCount));
            holder.level.setText(cursor.getString(columnLevel));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup group) {
            return inflater.inflate(R.layout.word_list_item, null, false);
        }
    }

    private static final class SearchHandler extends Handler {

        private final WeakReference<SearchBoardActivity> activityRef;

        public SearchHandler(Looper looper, SearchBoardActivity activity) {
            super(looper);
            activityRef = new WeakReference<SearchBoardActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case HANDLE_MESSAGE_SEARCH_WORD:
                final SearchBoardActivity activity = activityRef.get();
                if (activity != null && !activity.isFinishing()) {
                    final String key = msg.obj.toString().trim();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.mWordAdapter.changeCursor(
                                    activity.mDatabaseHelper.queryWordByName(key));
                        }
                    });
                }

                break;
            }
        }
    }
}
