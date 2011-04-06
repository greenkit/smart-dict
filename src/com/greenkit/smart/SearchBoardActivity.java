package com.greenkit.smart;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
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

    private ListView mWordList;
    private WordListAdapter mWordAdapter;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDatabaseHelper = DatabaseHelper.getInstance(this);
        initUi();
    }

    private void initUi () {
        setContentView(R.layout.screen_search_board);
        mWordList = (ListView) findViewById(R.id.word_list);
        ((EditText) findViewById(R.id.input_search)).addTextChangedListener(mSearchWordWatcher);
        mWordAdapter = new WordListAdapter(this, mDatabaseHelper.queryWordByName(null));
        mWordList.setAdapter(mWordAdapter);
    }

    private TextWatcher mSearchWordWatcher = new TextWatcher() {

        private String text = null;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Do nothing;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            text = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {
            String key = s.toString();
            if (!key.trim().equalsIgnoreCase(text.trim())) {
                mWordAdapter.changeCursor(mDatabaseHelper.queryWordByName(s.toString().trim()));
            }
        }
    };

    private static class WordListAdapter extends CursorAdapter {

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
            holder.search.setText(String.valueOf(cursor.getInt(columnSearchCount)));
            holder.study.setText(String.valueOf(cursor.getInt(columnStudyCount)));
            holder.mistake.setText(String.valueOf(cursor.getInt(columnMistakeCount)));
            holder.level.setText(cursor.getString(columnLevel));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup group) {
            return inflater.inflate(R.layout.word_list_item, null, false);
        }
    }
}
