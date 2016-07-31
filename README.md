# WeChatDemo

**Single Activity and Multiple Fragments to implement the social app WeChat client**. The knowledges about this apk are from [my article](https://github.com/gpfduoduo/android-article).  Welcom you guys to star and fork  

[点击查看中文说明](chinese.md)  

# Introduction
1. **To study android Activity and fragment**. you could know how to use fragment in you app developing correctly.
2. **Fragment stack management**. you could know the stack management method, in case of memory leaks. 
3. implement the **friend circle using self define view**
4. **Lazy load fragment** when UI show 
5. study TextView knowledge: **ClickableSpan, SpannableString etc...**  
6. **Animator** using method  
7. **soft input** using   
8. use zbar to **scan QR, adopt lazy load camera**, in case that, poor performance phone get stick  
9. Use** TextureView to play video** in FriendCirlcle(ListView), do not use SurfaceView to play video in Listview or RecycleView  
10. **SwipeBack in Fragment ** 





#Gif
![gif效果](screencapture/out.gif)
 

# Picture  
![通讯录效果](screencapture/device-2016-07-11-151913.png)![发现效果](screencapture/device-2016-07-11-151656.png)![朋友圈效果](screencapture/device-2016-07-19-190441.png) ![输入法切换](screencapture/device-2016-07-19-190320.png)
![朋友圈本地图片浏览效果](screencapture/device-2016-07-11-095328.png)![弹窗口效果](screencapture/device-2016-07-11-095306.png)![好友发现效果](screencapture/device-2016-07-11-095225.png)
![设置的效果](screencapture/device-2016-07-11-151714.png) ![扫一扫的界面](screencapture/device-2016-07-17-021602.png) ![小视频的效果](screencapture/device-2016-07-19-185920.png)

# Thanks  
1. [RadarScanViwe](https://github.com/gpfduoduo/RadarScanView)
2. [YoKeyword/Fragmentation](https://github.com/YoKeyword/Fragmentation)  
　　Welcom to [fork YoKeyword的Fragmentation](https://github.com/YoKeyword/Fragmentation)，very nice repositories 。
3. [PhotoView](https://github.com/chrisbanes/PhotoView)  
4. [ViewPagerFix](https://github.com/chrisbanes/PhotoView/issues/31)    
5. [Glide](https://github.com/bumptech/glide)  
6. [QrCodeScan](https://github.com/SkillCollege/QrCodeScan), notify this project has **memory leak**, i have put issue, but the author doesn't handler this. **In my project, I have solved this problem**.    
7. [VideoPlayerManager](https://github.com/danylovolokh/VideoPlayerManager) A progject to implement play video in ListView or RecycleView, very nice... 





# License  
Copyright 2016 gpfduoduo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at  
   http://www.apache.org/licenses/LICENSE-2.0  
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
