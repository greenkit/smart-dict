package com.greenkit.smart.database.type;

public class Word {

    private String mWord;
    private String mSymbol;
    private String mTranslation;
    private String mPronunciation;
    private String mExample;
    private String mBooks;
    private int mStudyCount;
    private int mMistakeCount;
    private int mSearchCount;

    public void setName(String name) {
        mWord = name;
    }

    public String getWord() {
        return mWord;
    }

    public void setSymbol(String symbol) {
        mSymbol = symbol;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setTranslation(String translation) {
        mTranslation = translation;
    }

    public String getTranslation() {
        return mTranslation;
    }

    public void setPronunciation(String pronunciation) {
        mPronunciation = pronunciation;
    }

    public String getPronunciation() {
        return mPronunciation;
    }

    public void setExample(String example) {
        mExample = example;
    }

    public String getExample() {
        return mExample;
    }

    public void setBookRef(String ref) {
        mBooks = ref;
    }

    public String getBookRef() {
        return mBooks;
    }

    public void setStudyCount(int count) {
        if (count > 0) {
            mStudyCount = count;
        }
    }

    public int getStudyCount() {
        return mStudyCount;
    }

    public void setMistakeCoumt(int count) {
        if (count > 0) {
            mMistakeCount = count;
        }
    }

    public int getMistakeCount() {
        return mMistakeCount;
    }

    public void setSearchCount(int count) {
        if (count > 0) {
            mSearchCount = count;
        }
    }

    public int getSearchCount() {
        return mSearchCount;
    }
}
