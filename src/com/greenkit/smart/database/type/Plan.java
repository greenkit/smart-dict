package com.greenkit.smart.database.type;

import com.greenkit.smart.utils.TextUtils;

public class Plan {

    /**
     * The mode of study plan for book, 'or' means the all words in book1 or
     * book2
     */
    public static final int STUDY_MODE_OR = 0;

    /**
     * The mode of study plan for book, 'xor' means the words just in one of
     * book1 or book2, not both in two of them.
     */
    public static final int STUDY_MODE_XOR = 1;

    public static final String BOOKS_DIVIDER = " | ";
    /**
     * The books you want to study, maybe one or two books. Two books will be
     * saved as "book_one | book_two" with the mark '|'.
     */
    private String mBooks;

    /**
     * The study mode, can be 'or' or 'xor'.
     */
    private int mMode;

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

    public String[] getBooks() {
        if (TextUtils.isEmpty(mBooks)) {
            return null;
        } else {
            int size = TextUtils.contain(mBooks, BOOKS_DIVIDER);
            if (size == 0) {
                return new String[] { mBooks };
            } else {
                return TextUtils.split(mBooks, BOOKS_DIVIDER);
            }
        }
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

    public void setMode(int mode) {
        mMode = mode;
    }

    public int getMode() {
        return mMode;
    }
}
