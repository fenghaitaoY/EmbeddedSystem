package com.android.blue.smarthomefunc.utils.id3;

import com.android.blue.smarthomefunc.entity.LogUtils;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

import java.io.File;

/**
 * ID3 工具类
 * 基于<a href="https://blinkenlights.org/jid3/">JID3</a>编写<br>
 * 学习PonyMusic音乐播放器，感谢hzwangchenyan
 * Created by fht on 3/28/18.
 */

public class ID3TagUtils {
    public static boolean setID3Tags(File sourceFile, ID3Tags id3Tags, boolean clearOriginal){
        if (sourceFile == null || !sourceFile.exists()){
            LogUtils.i("source file is not find");
            return false;
        }

        if ( id3Tags == null){
            LogUtils.i("id3 tag is illegal");
            return false;
        }

        MediaFile mediaFile = new MP3File(sourceFile);
        ID3V2_3_0Tag mID3V2_3_0Tag = null;
        if (clearOriginal){
            mID3V2_3_0Tag = new ID3V2_3_0Tag();
        }else {
            try {
                mID3V2_3_0Tag = (ID3V2_3_0Tag) mediaFile.getID3V2Tag();
            } catch (ID3Exception e) {
                e.printStackTrace();
            }
            if (mID3V2_3_0Tag == null){
                mID3V2_3_0Tag = new ID3V2_3_0Tag();
            }
        }
        try {
            id3Tags.fillID3Tag(mID3V2_3_0Tag);
            mediaFile.setID3Tag(mID3V2_3_0Tag);
            mediaFile.sync();
            return true;
        } catch (ID3Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
