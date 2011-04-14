package com.greenkit.smart;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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

    static final String INTENT_EXTRA_WORD_ID = "word-id";

    private ListView mWordList;
    private WordListAdapter mWordAdapter;
    private DatabaseHelper mDatabaseHelper;
    private HandlerThread mSearchThread;
    private SearchHandler mSearchHandler;
    private String mSearchKey;

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
        mSearchHandler.obtainMessage(HANDLE_MESSAGE_SEARCH_WORD, mSearchKey).sendToTarget();
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
        mWordAdapter = new WordListAdapter(this, mDatabaseHelper.queryWordBySearchKey(null));
        mWordList.setAdapter(mWordAdapter);
        mWordList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> list, View view, int position, long id) {
                Intent intent = new Intent(SearchBoardActivity.this, WordDetailActivity.class);
                intent.putExtra(INTENT_EXTRA_WORD_ID, id);
                startActivity(intent);
            }
        });
    }

    private final TextWatcher mSearchWordWatcher = new TextWatcher() {

        private String mText;
        private Message mMessage;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Do nothing;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mText = s.toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {
            final String key = s.toString().trim();
            if (!key.equalsIgnoreCase(mText)) {
                // If the interval of user input is less than the default value,
                // the prior query will be canceled.
                // Otherwise, do query.
                if (mMessage != null && SystemClock.uptimeMillis() - mMessage.getWhen()
                            < HANDLE_MESSAGE_SEARCH_DELAY) {
                        mSearchHandler.removeMessages(HANDLE_MESSAGE_SEARCH_WORD);
                }

                mMessage = mSearchHandler.obtainMessage(HANDLE_MESSAGE_SEARCH_WORD, key);
                mSearchHandler.sendMessageDelayed(mMessage, HANDLE_MESSAGE_SEARCH_DELAY);
            }
        }
    };

    private static final class WordListAdapter extends CursorAdapter {

        private LayoutInflater mInflater;
        private int mColumnWord;
        private int mColumnTranslation;
        private int mColumnSearchCount;
        private int mColumnStudyCount;
        private int mColumnMistakeCount;
        private int mColumnLevel;

        private static class ViewHolder {
            private TextView mWord;
            private TextView mTranslation;
            private TextView mSearch;
            private TextView mStudy;
            private TextView mMistake;
            private TextView mLevel;

            public ViewHolder (View view) {
                mWord = (TextView) view.findViewById(R.id.word);
                mTranslation = (TextView) view.findViewById(R.id.translation);
                mSearch = (TextView) view.findViewById(R.id.search_count);
                mStudy = (TextView) view.findViewById(R.id.study_count);
                mMistake = (TextView) view.findViewById(R.id.mistake_count);
                mLevel = (TextView) view.findViewById(R.id.level);
            }
        }

        public WordListAdapter(Context context, Cursor c) {
            super(context, c);
            mInflater = LayoutInflater.from(context);
            storeColumnIndex(c);
        }

        @Override
        public void changeCursor(Cursor cursor) {
            super.changeCursor(cursor);
            storeColumnIndex(cursor);
        }

        private void storeColumnIndex (Cursor c) {
            mColumnWord = c.getColumnIndex(WordTable.COLUMN_NAME_WORD);
            mColumnTranslation = c.getColumnIndex(WordTable.COLUMN_NAME_TRANSLATION);
            mColumnSearchCount = c.getColumnIndex(WordTable.COLUMN_NAME_SEARCH_COUNT);
            mColumnStudyCount = c.getColumnIndex(WordTable.COLUMN_NAME_STUDY_COUNT);
            mColumnMistakeCount = c.getColumnIndex(WordTable.COLUMN_NAME_MISTAKE_COUNT);
            mColumnLevel = c.getColumnIndex(WordTable.COLUMN_NAME_LEVEL);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder(view);
                view.setTag(holder);
            }

            holder.mWord.setText(cursor.getString(mColumnWord));
            holder.mTranslation.setText(cursor.getString(mColumnTranslation));
            holder.mSearch.setText(cursor.getString(mColumnSearchCount));
            holder.mStudy.setText(cursor.getString(mColumnStudyCount));
            holder.mMistake.setText(cursor.getString(mColumnMistakeCount));
            holder.mLevel.setText(cursor.getString(mColumnLevel));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup group) {
            return mInflater.inflate(R.layout.word_list_item, null, false);
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
                    final String key = msg.obj == null ? null : msg.obj.toString().trim();
                    activity.mSearchKey = key;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.mWordAdapter.changeCursor(
                                    activity.mDatabaseHelper.queryWordBySearchKey(key));
                        }
                    });
                }

                break;
            }
        }
    }
}
