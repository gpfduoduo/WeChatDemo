package com.gpfduoduo.wechat.util;

import android.widget.SectionIndexer;
import com.gpfduoduo.wechat.entity.Contact;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by gpfduoduo on 2016/6/29.
 */
public class MediaIndexer implements SectionIndexer {

    private static int OTHER_INDEX = 0; // index of other in the mSections array
    private int[] mPositions;
    // store the list of starting position index for each section
    // e.g. A start at index 0, B start at index 20, C start at index 41 and so on
    private int mCount; // this is the count for total number of contacts

    private static String OTHER = "#";
    private static String[] mSections = { OTHER, "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z" };


    public MediaIndexer(ArrayList<Contact> cityList) {
        mCount = cityList.size();
        initPositions(cityList);
    }


    public MediaIndexer(List<Contact> contentList) {
        mCount = contentList.size();
        initPositions(contentList);
    }


    private void initPositions(List<Contact> contentList) {
        int sectionCount = mSections.length;
        mPositions = new int[sectionCount];

        Arrays.fill(mPositions, -1); // initialize everything to -1

        // Assumption: list of items have already been sorted by the prefer names
        int itemIndex = 0;

        for (int i = 0; i < contentList.size(); i++) {

            String indexItem = contentList.get(i).sortKey;
            int sectionIndex = getSectionIndex(
                    indexItem); // find out which section this item belong to

            if (mPositions[sectionIndex] ==
                    -1) // if not set before, then do this, otherwise just ignore
            {
                mPositions[sectionIndex] = itemIndex;
            }

            itemIndex++;
        }

        int lastPos = -1;

        // now loop through, for all the ones not found, set position to the one before them
        // this is to make sure the array is sorted for binary search to work
        for (int i = 0; i < sectionCount; i++) {
            if (mPositions[i] == -1) mPositions[i] = lastPos;
            lastPos = mPositions[i];
        }
    }


    private void initPositions(ArrayList<Contact> contentList) {

        int sectionCount = mSections.length;
        mPositions = new int[sectionCount];

        Arrays.fill(mPositions, -1); // initialize everything to -1

        // Assumption: list of items have already been sorted by the prefer names
        int itemIndex = 0;

        for (int i = 0; i < contentList.size(); i++) {

            String indexItem = contentList.get(i).sortKey;
            int sectionIndex = getSectionIndex(
                    indexItem); // find out which section this item belong to

            if (mPositions[sectionIndex] ==
                    -1) // if not set before, then do this, otherwise just ignore
            {
                mPositions[sectionIndex] = itemIndex;
            }

            itemIndex++;
        }

        int lastPos = -1;

        // now loop through, for all the ones not found, set position to the one before them
        // this is to make sure the array is sorted for binary search to work
        for (int i = 0; i < sectionCount; i++) {
            if (mPositions[i] == -1) mPositions[i] = lastPos;
            lastPos = mPositions[i];
        }
    }


    // return which section this item belong to
    public int getSectionIndex(String indexItem) {
        if (indexItem == null) {
            return OTHER_INDEX;
        }

        indexItem = indexItem.trim();
        String firstLetter = OTHER;

        if (indexItem.length() == 0) {
            return OTHER_INDEX;
        }
        else {
            // get the first letter
            firstLetter = String.valueOf(indexItem.charAt(0))
                                .toUpperCase(Locale.US);
        }

        int sectionCount = mSections.length;
        for (int i = 0; i < sectionCount; i++) {
            if (mSections[i].equals(firstLetter)) {
                return i;
            }
        }

        return OTHER_INDEX;
    }


    @Override public int getPositionForSection(int section) {
        if (section < 0 || section >= mSections.length) {
            return -1;
        }

        return mPositions[section];
    }


    @Override public int getSectionForPosition(int position) {
        if (position < 0 || position >= mCount) {
            return -1;
        }
        int index = Arrays.binarySearch(mPositions, position);

        return index >= 0 ? index : -index - 2;
    }


    @Override public Object[] getSections() {
        return mSections;
    }
}
