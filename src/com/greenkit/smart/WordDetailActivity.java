package com.greenkit.smart;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.greenkit.smart.database.DatabaseHelper;
import com.greenkit.smart.database.table.WordTable;
import com.greenkit.smart.datatype.Word;

/**
 * The UI for word detail.
 * @author green
 *
 */
public class WordDetailActivity extends Activity {

    private TextView mTextWord;
    private TextView mTextSymbol;
    private TextView mTextTranslation;
    private TextView mTextSearchCount;
    private TextView mTextStudyCount;
    private TextView mTextMistakeCount;
    private TextView mTextLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initUi();
        Intent intent = getIntent();
        long id = intent.getLongExtra(SearchBoardActivity.INTENT_EXTRA_WORD_ID, -1);
        if (id >= 0) {
            new LoadWordTask(this).execute(id);
        }
    }

    private void initUi () {
        setContentView(R.layout.screen_word_detail);
        mTextWord = (TextView) findViewById(R.id.name);
        mTextSymbol = (TextView) findViewById(R.id.symbol);
        mTextTranslation = (TextView) findViewById(R.id.translation);
        mTextSearchCount = (TextView) findViewById(R.id.search_count);
        mTextStudyCount = (TextView) findViewById(R.id.study_count);
        mTextMistakeCount = (TextView) findViewById(R.id.mistake_count);
        mTextLevel = (TextView) findViewById(R.id.level);
    }

    private void setData (Word word) {
        mTextWord.setText(word.getWord());
        mTextSymbol.setText(word.getSymbol());
        mTextTranslation.setText(word.getTranslation());
        mTextSearchCount.setText(String.valueOf(word.getSearchCount()));
        mTextStudyCount.setText(String.valueOf(word.getStudyCount()));
        mTextMistakeCount.setText(String.valueOf(word.getMistakeCount()));
        mTextLevel.setText(String.valueOf(word.getLevel()));
    }

    private static final class LoadWordTask extends AsyncTask<Long, Void, Word> {

        private final WeakReference<WordDetailActivity> activityRef;

        public LoadWordTask (WordDetailActivity activity) {
            activityRef = new WeakReference<WordDetailActivity>(activity);
        }

        @Override
        protected Word doInBackground(Long... id) {
            final WordDetailActivity activity = activityRef.get();
            if (activity != null) {
                DatabaseHelper helper = DatabaseHelper.getInstance(activity);
                Cursor cursor = helper.queryWordDetailById(id[0]);
                if (cursor.moveToFirst()) {
                    Word word = new Word();
                    word.setWord(cursor.getString(
                            cursor.getColumnIndexOrThrow(WordTable.COLUMN_NAME_WORD)));
                    word.setSymbol(cursor.getString(
                            cursor.getColumnIndexOrThrow(WordTable.COLUMN_NAME_SYMBOL)));
                    word.setTranslation(cursor.getString(
                            cursor.getColumnIndexOrThrow(WordTable.COLUMN_NAME_TRANSLATION)));
                    word.setExample(cursor.getString(
                            cursor.getColumnIndexOrThrow(WordTable.COLUMN_NAME_EXAMPLES)));
                    word.setBookRef(cursor.getString(
                            cursor.getColumnIndexOrThrow(WordTable.COLUMN_NAME_BOOKS)));
                    word.setLevel(cursor.getInt(
                            cursor.getColumnIndexOrThrow(WordTable.COLUMN_NAME_LEVEL)));
                    word.setMistakeCoumt(cursor.getInt(
                            cursor.getColumnIndexOrThrow(WordTable.COLUMN_NAME_MISTAKE_COUNT)));
                    word.setPronunciation(cursor.getString(
                            cursor.getColumnIndexOrThrow(WordTable.COLUMN_NAME_PRONUNCIATION)));
                    // Every time search one word should increase its search count;
                    word.setSearchCount(helper.increaseWordSeachCountById(id[0]));
                    word.setStudyCount(cursor.getInt(
                            cursor.getColumnIndexOrThrow(WordTable.COLUMN_NAME_STUDY_COUNT)));

                    return word;
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Word word) {
            final WordDetailActivity activity = activityRef.get();
            if (activity != null && word != null) {
                activity.setData(word);
            }
        }

    }
}
