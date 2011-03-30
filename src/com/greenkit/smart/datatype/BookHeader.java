package com.greenkit.smart.datatype;

public class BookHeader {

    private String mBookName;
    private int mWordNumber;

    public void setBookName(String name) {
        mBookName = name;
    }

    public String getBookName() {
        return mBookName;
    }

    public void setWordNumber(int number) {
        if(number >= 0) {
            mWordNumber = number;
        }
    }

    public int getWordNumber() {
        return mWordNumber;
    }
}
