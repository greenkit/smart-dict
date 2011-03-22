package com.greenkit.smart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.greenkit.smart.database.table.Smart;

public class WelcomeActivity extends Activity {

	private ListView mList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.screen_search);
        mList = (ListView)findViewById(R.id.list);
        mList.setAdapter(new CursorAdapter(this, getContentResolver().query(
        		Smart.Words.CONTENT_URI, new String[]{Smart.Words._ID,
        				Smart.Words.NAME, Smart.Words.SOUNDMARK,
        				Smart.Words.TRANSLATION}, null, null, null)) {

			@Override
			public View newView(Context context, Cursor cursor, ViewGroup parent) {
				return new TextView(WelcomeActivity.this);
			}

			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				String text = cursor.getString(cursor.getColumnIndexOrThrow(Smart.Words.NAME));
				((TextView)view).setText(text);
			}
		});

        Intent intent = new Intent(this, FileExplorerActivity.class);
        startActivity(intent);
    }
}