package com.greenkit.smart.datatype;

public class BookType {

    private String mName;
    private int mWordNumber;

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setWordNumber(int number) {
        if(number > 0) {
            mWordNumber = number;
        }
    }

    public int getWordNumber() {
        return mWordNumber;
    }
}
