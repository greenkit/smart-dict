package com.greenkit.smart.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;

import com.greenkit.smart.datatype.BookHeader;
import com.greenkit.smart.datatype.Word;
import com.greenkit.smart.utils.Utils;

public class BookParser {

    private static final String TAG = "BookParser";
    private BookHeader mBookHeader;
    private BufferedReader mReader;

    public BookParser(File file) {
        try {
            InputStream in = new FileInputStream(file);
            mReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            // Parse book header;
            String line = mReader.readLine();
            if(line.startsWith(ParseScheme.HEADER_TOKEN_START)) {
                mBookHeader = new BookHeader();
                String name = mReader.readLine();
                String number = mReader.readLine();
                mBookHeader.setBookName(name);
                mBookHeader.setWordNumber(Integer.valueOf(number));
            }

            // Move the cursor to the first word;
            while((line = mReader.readLine()) == null ||
                    !ParseScheme.WORD_TOKEN_START.equalsIgnoreCase(line.trim())) {
                continue;
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getLocalizedMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    public void close() {
        if(mReader != null) {
            try {
                mReader.close();
                mReader = null;
                mBookHeader = null;
            } catch (IOException e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
        }
    }

    public BookHeader getBookHeader() {
        return mBookHeader;
    }

    /**
     * The format like:
     * word [symbol] translations
     */
    public Word getNextWord() throws IOException {
        String line = mReader.readLine().trim();
        if(!Utils.isEmpty(line) && !ParseScheme.WORD_TOKEN_END.equalsIgnoreCase(line)) {
            Word word = new Word();
            int offset = line.indexOf('[');
            if(offset < 0) {
                offset = line.indexOf(" ");
                if(offset < 0) {
                    offset = line.length();
                }
            }

            word.setWord(line.substring(0, offset).trim());
            word.setSymbol(line.substring(offset).trim());

            line = mReader.readLine();
            String trans = line;
            while(!Utils.isEmpty(line)) {
                trans = trans + "\n" + line;
                line = mReader.readLine();
            }
            word.setTranslation(trans);

            return word;
        }

        return null;
    }
}
