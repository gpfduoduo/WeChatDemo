package com.gpfduoduo.wechat.ui.fragment.event;

import java.util.List;

/**
 * Created by gpfduoduo on 2016/7/8.
 */
public class FriendCircleSelectPhotoEvent {

    public static interface PHOTO_TYPE {
        public static final int CIRCLE_SHARE = 0;
        public static final int CIRCLE_BACK = 1;
        public static final int CHAT = 2;
    }

    private int mType;
    private List<String> selectedPhotos;


    public int getType() {
        return mType;
    }


    public void setType(int type) {
        mType = type;
    }


    public void setSelectedPhotos(List<String> photos) {
        selectedPhotos = photos;
    }


    public List<String> getSelectedPhotos() {
        return selectedPhotos;
    }
}
