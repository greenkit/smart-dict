package com.greenkit.smart.datatype;


public class PlanType {

    /**
     * The books you want to study, maybe one or two books. Two books will be
     * saved as "book_one | book_two" with the mark '|'.
     */
    private String mBooks;

    /**
     * How many chapters the words will be organized. How many words in one
     * chapter will be determined after this number.
     */
    private int mChapterNumber;

    /**
     * The current chapter the user studied.
     */
    private int mCurrentChapter;

    public void setBooks(String books) {
        mBooks = books;
    }

    public String getBooks() {
        return mBooks;
    }

    public void setChapterNumber(int number) {
        mChapterNumber = number;
    }

    public int getChapterNumber() {
        return mChapterNumber;
    }

    public void setCurrentChapter(int current) {
        mCurrentChapter = current;
    }

    public int getCurrentChapter() {
        return mCurrentChapter;
    }
}
