package com.knight.glview.util;

import android.hardware.Camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * @author l00385426
 * @version 1.0
 * @date 2017/3/1
 */

public class CameraUtil {

    public static boolean equalRate(Camera.Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.03) {
            return true;
        } else {
            return false;
        }
    }

    public static Camera.Size chooseOptimalSize(List<Camera.Size> list, float th, int minWidth) {
        Collections.sort(list, new CameraSizeComparator());

        int mismatchingIndex = 0;
        int i = 0;
        for (Camera.Size s : list) {
            LOG.logI("最终设置 PreviewSize:w = " + s.width + "h = " + s.height);
            if ((s.width >= minWidth) && equalRate(s, th)) {
                LOG.logI("最终设置 final PreviewSize:w = " + s.width + "h = " + s.height);
                break;
            }
            if (s.width >= minWidth && mismatchingIndex == 0) {
                mismatchingIndex = i;
            }
            i++;
        }
        if (i == list.size()) {
            LOG.logE("找不到合适的预览尺寸！！！");
            i = mismatchingIndex;//如果没找到，就选最小的size
        }
        return list.get(i);
    }


    // 为Size定义一个比较器Comparator
    public static class CameraSizeComparator implements Comparator<Camera.Size> {
        public int compare(Camera.Size lhs, Camera.Size rhs) {
            // TODO Auto-generated method stub
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }

    }
}
