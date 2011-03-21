package com.greenkit.smart.database.table;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Smart{

	public static final String AUTHORITY = "com.green.provider.Smart";

	private Smart() {}

	public static final class Words implements BaseColumns {

		public static final Uri CONTENT_URI
				= Uri.parse("content://" + AUTHORITY + "/words");

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single word.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.green.word";

		public static final String DEFAULT_SORT_ORDER = "name DESC";

        /**
         * The word text.
         * <P>Type: TEXT</P>
         */
		public static final String NAME = "name";

        /**
         * The word sound mark.
         * <P>Type: TEXT</P>
         */
		public static final String SOUNDMARK = "soundmark";

        /**
         * The word meaning.
         * <P>Type: TEXT</P>
         */
		public static final String TRANSLATION = "translation";

        /**
         * The word pronunciation.
         * <P>Type: TEXT</P>
         */
		public static final String PRONUNCIATION = "pronunciation";

        /**
         * The word example sentence.
         * <P>Type: TEXT</P>
         */
		public static final String EXAMPLE = "example";
	}
}
