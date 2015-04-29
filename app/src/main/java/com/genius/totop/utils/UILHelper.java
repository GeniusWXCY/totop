package com.genius.totop.utils;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.totop.genius.R;

public class UILHelper {

    /**
     * 初始化图片加载控件
     * 
     * @param context
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2)
                                                                                       .denyCacheImageMultipleSizesInMemory()
                                                                                       .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                                                                                       .tasksProcessingOrder(QueueProcessingType.LIFO)
                                                                                       .build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 常规图片显示设置
     */
    static DisplayImageOptions options        = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_item_list_default_bg)
                                                                                 .showImageForEmptyUri(R.drawable.ic_item_list_default_bg)
                                                                                 .showImageOnFail(R.drawable.ic_item_list_default_bg)
                                                                                 .cacheInMemory(true)
                                                                                 .cacheOnDisk(true)
                                                                                 .considerExifParams(true)
                                                                                 .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                                                                                 .build();

    /**
     * 圆角图片显示设置
     */
    static DisplayImageOptions roundedOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_item_list_default_bg)
                                                                                 .showImageForEmptyUri(R.drawable.ic_item_list_default_bg)
                                                                                 .showImageOnFail(R.drawable.ic_item_list_default_bg)
                                                                                 .cacheInMemory(false)
                                                                                 .cacheOnDisk(true)
                                                                                 .considerExifParams(true)
                                                                                 .displayer(new RoundedBitmapDisplayer(20))
                                                                                 .build();

    /**
     * 显示常规图片
     * 
     * @param uri
     * @param imageView
     */
    public static void displayImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView, options);
    }

    /**
     * 显示圆角图片
     * 
     * @param uri
     * @param imageView
     */
    public static void displayRoundedImage(String uri, ImageView imageView) {
        ImageLoader.getInstance().displayImage(uri, imageView, roundedOptions);
    }

}
