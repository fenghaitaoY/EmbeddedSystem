package com.android.blue.smarthomefunc.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.WindowManager;

import com.android.blue.smarthomefunc.R;
import com.android.blue.smarthomefunc.entity.LogUtils;
import com.android.blue.smarthomefunc.model.Music;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * 音乐专辑封面图片加载工具类
 * 分为本地加载和网络加载方式
 * LruCache 内存存储，　后面优化结合使用DiskLruCache
 * Created by root on 1/25/18.
 */

public class MusicCoverLoaderUtils {
    public static final int THUMBNAIL_MAX_LENGTH = 500;
    private static final String MUSIC_KEY_NULL = "null";

    private LruCache<String, Bitmap> mMusicCoverCache;
    private Context mContext;

    enum Type {
        THUMBNAIL(""),
        BLUR("#BLUR"),
        ROUND("#ROUND");

        private String value;

        Type(String value) {
            this.value = value;
        }
    }

    private static MusicCoverLoaderUtils LoaderInstance = null;

    public static MusicCoverLoaderUtils getInstance() {
        if (LoaderInstance == null) {
            synchronized (MusicCoverLoaderUtils.class) {
                if (LoaderInstance == null) {
                    LoaderInstance = new MusicCoverLoaderUtils();
                }
            }
        }
        return LoaderInstance;
    }

    private MusicCoverLoaderUtils() {
        //获取当前进程的最大内存
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024); //kB
        //设置缓存大小
        int cacheSize = maxMemory / 8;
        mMusicCoverCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return value.getAllocationByteCount() / 1024;
                } else {
                    return value.getByteCount() / 1024; //此单位与设置缓存大小单位一致
                }
            }
        };
    }


    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public Bitmap loadThumbnail(Music music) {
        return loadCover(music, Type.THUMBNAIL);
    }

    public Bitmap loadBlur(Music music) {
        return loadCover(music, Type.BLUR);
    }

    public Bitmap loadRound(Music music) {
        return loadCover(music, Type.ROUND);
    }

    private Bitmap loadCover(Music music, Type type) {
        if (music != null)
            LogUtils.i("music = "+music.getTitle()+" , type = "+type.value+" , path ="+music.getCoverPath());
        Bitmap bitmap;
        String key = getKey(music, type);
        LogUtils.i(" fht loadCover key :" + key);
        if (TextUtils.isEmpty(key)) {
            bitmap = mMusicCoverCache.get(MUSIC_KEY_NULL.concat(type.value));
            LogUtils.i(" ---key is empty 11 ");
            if (bitmap != null) {
                return bitmap;
            }
            LogUtils.i(" ---key is empty 22");
            bitmap = getDefaultCover(type);
            LogUtils.i("put lrucache default");
            mMusicCoverCache.put(MUSIC_KEY_NULL.concat(type.value), bitmap);
            return bitmap;
        }

        bitmap = mMusicCoverCache.get(key);
        LogUtils.i("key = "+key+" ,bitmap = "+bitmap);
        if (bitmap != null){
            return bitmap;
        }

        bitmap = loadCoverByType(music, type);
        LogUtils.i(" loadCoverByType "+bitmap);
        if (bitmap != null) {
            LogUtils.i("put lrucache loadcoverbytype key ="+key);
            mMusicCoverCache.put(key, bitmap);
            return bitmap;
        }
        return loadCover(null, type);
    }

    private Bitmap loadCoverByType(Music music, Type type) {
        Bitmap bitmap;
        LogUtils.i("music type ="+music.getType()+" cover path ="+music.getCoverPath());
        if (music.getType() == Music.Type.LOCAL) {
            bitmap = loadCoverFromMediaStore(music.getAlbumId());
        } else {
            bitmap = loadCoverFromFile(music.getCoverPath());
        }
        switch (type) {
            case ROUND:
                WindowManager vm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                int width = vm.getDefaultDisplay().getWidth();
                bitmap = ImageUtils.resizeImage(bitmap, width / 2, width / 2);
                return ImageUtils.createCircleImage(bitmap);
            case BLUR:
                return ImageUtils.blur(bitmap);
            default:
                return bitmap;
        }
    }

    /**
     * 从系统加载本地音乐封面
     * @param albumId
     * @return
     */
    private Bitmap loadCoverFromMediaStore(long albumId) {
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = MusicUtils.getMediaStoreAlbumCoverUri(albumId);
        InputStream is;
        try {
            is = resolver.openInputStream(uri);
        } catch (FileNotFoundException ignored) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeStream(is, null, options);
    }

    /**
     * 从下载的图片加载封面<br>
     * 网络音乐
     */
    private Bitmap loadCoverFromFile(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(path, options);
    }

    private Bitmap getDefaultCover(Type type) {
        switch (type) {
            case BLUR:
                return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.smart_login_suc_bg2);
            case ROUND:
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.play_page_default_cover);
                WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                int width = manager.getDefaultDisplay().getWidth();
                bitmap = ImageUtils.resizeImage(bitmap, width / 2, width / 2);
                return bitmap;
            default:
                return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_cover);
        }
    }

    private String getKey(Music music, Type type) {
        if (music == null) {
            return null;
        }

        if (music.getType() == Music.Type.LOCAL && music.getAlbumId() > 0) {
            return String.valueOf(music.getAlbumId()).concat(type.value);
        } else if (music.getType() == Music.Type.ONLINE && !TextUtils.isEmpty(music.getCoverPath())) {
            return music.getCoverPath().concat(type.value);
        } else {
            return null;
        }

    }

}
