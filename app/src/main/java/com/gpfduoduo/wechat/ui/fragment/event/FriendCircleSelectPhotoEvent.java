package com.gpfduoduo.wechat.ui.fragment.event;

import java.util.List;

/**
 * Created by gpfduoduo on 2016/7/8.
 */
public class FriendCircleSelectPhotoEvent {

    private List<String> selectedPhotos;


    public void setSelectedPhotos(List<String> photos) {
        selectedPhotos = photos;
    }


    public List<String> getSelectedPhotos() {
        return selectedPhotos;
    }
}
