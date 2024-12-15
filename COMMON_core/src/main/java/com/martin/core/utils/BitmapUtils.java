package com.martin.core.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import com.martin.core.callback.MyCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by DingJinZhu on 2023/9/25.
 * Description:
 */
public class BitmapUtils {
    public static final String BASEPATH = Environment.getExternalStorageDirectory() + File.separator + "kylin";

    /**
     * 返回图片宽高数组，第0个是宽，第1个是高
     */
    public static int[] getBitmapWidthHeight(final String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);// 此时返回的bitmap为nul
        int[] size = new int[2];
        size[0] = options.outWidth;
        size[1] = options.outHeight;
        return size;
    }

    public static String getPathByUri(Context context, Uri uri) {
        Activity activity = null;
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
        if (activity == null) {
            return null;
        }
        String path = null;
        if (uri == null) {
            return path;
        }
        if ("content".equals(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        } else if ("file".equals(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }

    /**
     * 此方法仅仅用于计算createScaledBitmap() 方法获取Bitmap 时的缩放比例。目的是为了防止OOM
     */
    private static int getOptionSize(final float size) {
        int optionSize = 1;
        if (size >= 2f && size < 4f) {
            optionSize = 2;
        } else if (size >= 4f && size < 8f) {
            optionSize = 4;
        } else if (size >= 8f && size < 16) {
            optionSize = 8;
        } else if (size >= 16f) {
            optionSize = 16;
        }
        return optionSize;
    }

    public static void saveBitmap(final Bitmap bmp, final MyCallback<String> callback) {
        if (callback != null) {
            callback.onPrepare();
        }

        ThreadUtil.INST.excute(new Runnable() {
            @Override
            public void run() {
                String fileName = System.currentTimeMillis() + ".jpg";
                File file = new File(BASEPATH + "/", fileName);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onError();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onError();
                    }
                }
                String path = file.getAbsolutePath();
                if (callback != null) {
                    callback.onSuccess(path);
                }
            }
        });
    }

    /**
     * 按原图比例缩放裁剪图片
     * @param activity
     * @param imagePath 图片的路径
     * @param imageWidth 想要被缩放到的target图片宽度，根据此宽度和原图的比例，去计算target图片高度
     * @param callback
     */
    public static void createScaledBitmap(final Activity activity, final String imagePath,
                                          final int imageWidth, final MyCallback callback) {
        if (callback == null) {
            throw new NullPointerException("MyCallback must not null");
        }
        callback.onPrepare();
        if (TextUtils.isEmpty(imagePath) || !(new File(imagePath).exists())) {
            callback.onError();
            return;
        }

        //原图宽度大于传入的宽度才压缩，否则不压缩，直接返回原图路径
        final int[] size = getBitmapWidthHeight(imagePath);
        if (size[0] <= imageWidth) {
            callback.onSuccess(imagePath);
            return;
        }

        ThreadUtil.INST.excute(new Runnable() {
            @Override
            public void run() {
                int imageHeight = (int) (size[1] * 1f * imageWidth * 1f / size[0] * 1f);
                float scale = size[0] * 1f / imageWidth * 1f;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = getOptionSize(scale);
                Bitmap targetBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(imagePath, options), imageWidth, imageHeight, true);
                saveBitmap(targetBitmap, new MyCallback<String>() {
                    @Override
                    public void onPrepare() {
                    }

                    @Override
                    public void onSuccess(String path) {
                        if (activity != null && !activity.isFinishing()) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess(path);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError() {
                        if (activity != null && !activity.isFinishing()) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError();
                                }
                            });
                        }
                    }
                });

            }
        });
    }

    public static void createScaledBitmap(final Activity activity, final Uri imageUri,
                                          int imageWidth, final MyCallback callback) {
        final String imagePath = getPathByUri(activity, imageUri);
        createScaledBitmap(activity, imagePath, imageWidth, callback);
    }

    /**
     * 返回bitmap 所占内存的大小
     */
    public static int getBitmapBytes(Bitmap bitmap) {
        return bitmap.getByteCount();
    }


    /*
    //测试程序
    public static void main(){
        BitmapUtils.createScaledBitmap(getContext(),"/scard/images/13343222.png",700, new MyCallback<String>() {
            @Override
            public void onPrepare() {
            }

            @Override
            public void onSuccess(String imgPath) {
                ToastUtils.showToastOnce(imgPath);
            }

            @Override
            public void onError() {
            }
        });
    }*/


}
