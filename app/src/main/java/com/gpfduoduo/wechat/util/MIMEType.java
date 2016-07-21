package com.gpfduoduo.wechat.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

public class MIMEType {
    private static final String tag = MIMEType.class.getSimpleName();
    private static ArrayMap<String, String> map
            = new ArrayMap<String, String>();

    public static final String[] VIDEO_EXTENSIONS = { "264", "3g2", "3gp",
            "3gp2", "3gpp", "3gpp2", "3mm", "3p2", "60d", "aep", "ajp", "amv",
            "amx", "arf", "asf", "asx", "avb", "avd", "avi", "avs", "avs",
            "axm", "bdm", "bdmv", "bik", "bix", "bmk", "box", "bs4", "bsf",
            "byu", "camre", "clpi", "cpi", "cvc", "d2v", "d3v", "dav", "dce",
            "dck", "ddat", "dif", "dir", "divx", "dlx", "dmb", "dmsm", "dmss",
            "dnc", "dpg", "dream", "dsy", "dv", "dv-avi", "dv4", "dvdmedia",
            "dvr-ms", "dvx", "dxr", "dzm", "dzp", "dzt", "evo", "eye", "f4p",
            "f4v", "fbr", "fbr", "fbz", "fcp", "flc", "flh", "fli", "flv",
            "flx", "gl", "grasp", "gts", "gvi", "gvp", "hdmov", "hkm", "ifo",
            "imovi", "imovi", "iva", "ivf", "ivr", "ivs", "izz", "izzy", "jts",
            "lsf", "lsx", "m15", "m1pg", "m1v", "m21", "m21", "m2a", "m2p",
            "m2t", "m2ts", "m2v", "m4e", "m4u", "m4v", "m75", "meta", "mgv",
            "mj2", "mjp", "mjpg", "mkv", "mmv", "mnv", "mod", "modd", "moff",
            "moi", "moov", "mov", "movie", "mp21", "mp21", "mp2v", "mp4",
            "mp4v", "mpe", "mpeg", "mpeg4", "mpf", "mpg", "mpg2", "mpgin",
            "mpl", "mpls", "mpv", "mpv2", "mqv", "msdvd", "msh", "mswmm", "mts",
            "mtv", "mvb", "mvc", "mvd", "mve", "mvp", "mxf", "mys", "ncor",
            "nsv", "nvc", "ogm", "ogv", "ogx", "osp", "par", "pds", "pgi",
            "piv", "playlist", "pmf", "prel", "pro", "prproj", "psh", "pva",
            "pvr", "pxv", "qt", "qtch", "qtl", "qtm", "qtz", "rcproject", "rdb",
            "rec", "rm", "rmd", "rmp", "rmvb", "roq", "rp", "rts", "rts", "rum",
            "rv", "sbk", "sbt", "scm", "scm", "scn", "sec", "seq", "sfvidcap",
            "smil", "smk", "sml", "smv", "spl", "ssm", "str", "stx", "svi",
            "swf", "swi", "swt", "tda3mt", "tivo", "tix", "tod", "tp", "tp0",
            "tpd", "tpr", "trp", "ts", "tvs", "vc1", "vcr", "vcv", "vdo", "vdr",
            "veg", "vem", "vf", "vfw", "vfz", "vgz", "vid", "viewlet", "viv",
            "vivo", "vlab", "vob", "vp3", "vp6", "vp7", "vpj", "vro", "vsp",
            "w32", "wcp", "webm", "wm", "wmd", "wmmp", "wmv", "wmx", "wp3",
            "wpl", "wtv", "wvx", "xfl", "xvid", "yuv", "zm1", "zm2", "zm3",
            "zmv" };

    private static final HashSet<String> mHashVideo = new HashSet<String>(
            Arrays.asList(VIDEO_EXTENSIONS));


    public static final ArrayMap<String, String> MimeTypeTab() {
        map.put(".mpc", "application/vnd.mpohun.certificate");
        map.put(".apk", "application/vnd.android.package-archive");
        map.put(".bin", "application/octet-stream");
        map.put(".class", "application/octet-stream");
        map.put(".pdf", "application/pdf");
        map.put(".doc", "application/msword");
        map.put(".exe", "application/octet-stream");
        map.put(".gtar", "application/x-gtar");
        map.put(".gz", "application/x-gzip");
        map.put(".jar", "application/java-archive");
        map.put(".js", "application/x-javascript");
        map.put(".msg", "application/vnd.ms-outlook");
        map.put(".pps", "application/vnd.ms-powerpoint");
        map.put(".ppt", "application/vnd.ms-powerpoint");
        map.put(".rar", "application/x-rar-compressed");
        map.put(".rtf", "application/rtf");
        map.put(".tar", "application/x-tar");
        map.put(".tgz", "application/x-compressed");
        map.put(".wps", "application/vnd.ms-works");
        map.put(".z", "application/x-compress");
        map.put(".zip", "application/zip");

        map.put(".c", "text/plain");
        map.put(".conf", "text/plain");
        map.put(".cpp", "text/plain");
        map.put(".h", "text/plain");
        map.put(".htm", "text/html");
        map.put(".html", "text/html");
        map.put(".java", "text/plain");
        map.put(".log", "text/plain");
        map.put(".prop", "text/plain");
        map.put(".rc", "text/plain");
        map.put(".sh", "text/plain");
        map.put(".txt", "text/plain");
        map.put(".xml", "text/plain");

        map.put(".bmp", "image/bmp");
        map.put(".gif", "image/gif");
        map.put(".jpeg", "image/jpeg");
        map.put(".jpg", "image/jpeg");
        map.put(".png", "image/png");
        map.put(".pcx", "image/pcx");
        map.put(".tif", "image/tiff");
        map.put(".tiff", "image/tiff");

        map.put(".m3u", "audio/x-mpegurl");
        map.put(".m4a", "audio/mp4a-latm");
        map.put(".m4b", "audio/mp4a-latm");
        map.put(".m4p", "audio/mp4a-latm");
        map.put(".mp2", "audio/x-mpeg");
        map.put(".mp3", "audio/x-mpeg");
        map.put(".ape", "audio/*");
        map.put(".mpga", "audio/mpeg");
        map.put(".wav", "audio/x-wav");
        map.put(".wma", "audio/x-ms-wma");
        map.put(".wmv", "audio/x-ms-wmv");
        map.put(".ogg", "audio/ogg");
        map.put(".rmvb", "audio/x-pn-realaudio");
        map.put(".flac", "audio/*");
        map.put(".mid", "audio/midi");
        map.put(".aac", "audio/aac");
        map.put(".amr", "audio/amr");

        map.put(".mpe", "video/mpeg");
        map.put(".mp4", "video/mp4");
        map.put(".m4u", "video/vnd.mpegurl");
        map.put(".m4v", "video/x-m4v");
        map.put(".asf", "video/x-ms-asf");
        map.put(".3gp", "video/3gpp");
        map.put(".avi", "video/x-msvideo");
        map.put(".mov", "video/quicktime");
        map.put(".rmvb", "video/quicktime");
        map.put(".mpeg", "video/mpeg");
        map.put(".mpg", "video/mpeg");
        map.put(".mpg4", "video/mp4");
        map.put(".flv", "video/x-flv");
        map.put(".f4v", "video/x-f4v");
        map.put(".rmvb", "video/*");

        map.put("", "*/*");

        return map;
    }


    public static boolean isVideo(String url) {
        final String ext = getUrlExtension(url);
        return mHashVideo.contains(ext);
    }


    public static boolean isVideoByExt(String ext) {
        return mHashVideo.contains(ext);
    }


    public static String getUrlExtension(String url) {
        if (!TextUtils.isEmpty(url)) {
            int i = url.lastIndexOf('.');
            if (i > 0 && i < url.length() - 1) {
                return url.substring(i + 1).toLowerCase(Locale.US);
            }
        }
        return "";
    }


    /** get file MIME type */
    public static String getMIMEType(File file) {
        MimeTypeTab();
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex, fName.length())
                          .toLowerCase(Locale.getDefault());
        if (end.equals("")) return type;

        for (int i = 0; i < map.size(); i++) {
            if (map.containsKey(end)) type = map.get(end);
        }
        return type;
    }


    public static String getFileExtention(String name) {
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex < 0) {
            return null;
        }
        /* 获取文件的后缀名 */
        String end = name.substring(dotIndex, name.length())
                         .toLowerCase(Locale.getDefault());

        return end;
    }


    /**
     * 根据后缀获取MIME
     */
    public static String getMIMEType(String fileType) {
        String type = "*/*";
        MimeTypeTab();
        for (int i = 0; i < map.size(); i++) {
            if (map.containsKey(fileType)) type = map.get(fileType);
        }

        return type;
    }


    /**
     * open file according to the MIME
     */
    public static void openFile(File file, Context context) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = MIMEType.getMIMEType(file);
            intent.setDataAndType(Uri.fromFile(file), type);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    public static void openUrlFile(String url, String mime, Context ctx) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(mime)) return;
        try {
            Log.d(tag, "url = " + url + "; mime type = " + mime);
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = mime;
            intent.setDataAndType(Uri.parse(url), type);
            //跳转
            ctx.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
