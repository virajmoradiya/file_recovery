package photo.video.recovery.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;

import photo.video.recovery.extension.UtilsKt;
import photo.video.recovery.model.FileModel;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;



public class ScanImage {
    public static ArrayList<FileModel> arrFileScan = new ArrayList<>();
    public static String lastFile = "";
    public static int number = 0;

    public static void checkFileOfDirectoryImage(Context context, File[] fileArr, OnImageScanListener listener) {
        String[] externalStorageDirectories = getExternalStorageDirectories(context);
        if (externalStorageDirectories.length > 0) {
            for (String str : externalStorageDirectories) {
                File file = new File(str);
                if (file.exists()) {
                    checkFileOfDirectoryImage1(context, file.listFiles(), listener);
                }
            }
        }
        for (File value : fileArr) {
            if (value.isDirectory()) {
                File[] fileList = ScanImage.getFileList(value.getPath());
                if (!(fileList == null || fileList.length == 0)) {
                    checkFileOfDirectoryImage1(context, fileList, listener);
                }
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(value.getPath(), options);
                if (!(options.outWidth == -1 || options.outHeight == -1)) {
                    File file = new File(value.getPath());
                    if (UtilsKt.isImageOrGifFile(file)) {
                        arrFileScan.add(new FileModel(file, false,null,null,null));
                        number++;
                        listener.onImageScan(number);
                    }
                }
            }
        }
        listener.onScanCompleted();
    }

    public static void checkFileOfDirectoryImage1(Context context, File[] fileArr, OnImageScanListener listener) {
        String[] externalStorageDirectories = getExternalStorageDirectories(context);
        if (externalStorageDirectories.length > 0) {
            for (String str : externalStorageDirectories) {
                File file = new File(str);
                if (file.exists()) {
                    checkFileOfDirectoryImage1(context, file.listFiles(), listener);
                }
            }
        }
        for (File value : fileArr) {
            if (value.isDirectory()) {
                File[] fileList = ScanImage.getFileList(value.getPath());
                if (!((fileList == null) || (fileList.length == 0))) {
                    checkFileOfDirectoryImage1(context, fileList, listener);
                }
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(value.getPath(), options);
                if (!(options.outWidth == -1 || options.outHeight == -1)) {
                    File file = new File(value.getPath());
                    if (UtilsKt.isImageOrGifFile(file)) {
                        arrFileScan.add(new FileModel(file, false,null,null,null));
                        number++;
                        listener.onImageScan(number);
                    }
                }
            }
        }


    }

    public static File[] getFileList(String str) {
        File file = new File(str);
        if (!file.isDirectory()) {
            return new File[0];
        }
        return file.listFiles();
    }


    public static String[] getExternalStorageDirectories(Context context) {
        File[] externalFilesDirs;
        String[] split;
        boolean z;
        ArrayList<String> arrayList = new ArrayList<>();
        if ((externalFilesDirs = context.getExternalFilesDirs((String) null)) != null && externalFilesDirs.length > 0) {
            for (File file : externalFilesDirs) {
                split = file.getPath().split("/Android");
                if (!(split.length == 0)) {
                    String str = split[0];
                    z = Environment.isExternalStorageRemovable(file);
                    if (z) {
                        arrayList.add(str);
                    }
                }
            }
        }
        if (arrayList.isEmpty()) {
            StringBuilder str2 = new StringBuilder();
            try {
                Process start = new ProcessBuilder().command("mount | grep /dev/block/vold").redirectErrorStream(true).start();
                start.waitFor();
                InputStream inputStream = start.getInputStream();
                byte[] bArr = new byte[1024];
                while (inputStream.read(bArr) != -1) {
                    str2.append(new String(bArr));
                }
                inputStream.close();
            } catch (Exception unused) {
                unused.printStackTrace();
            }
            if (!str2.toString().trim().isEmpty()) {
                String[] split2 = str2.toString().split("\n");
                if (split2.length > 0) {
                    for (String split3 : split2) {
                        arrayList.add(split3.split(" ")[2]);
                    }
                }
            }
        }
        String[] strArr = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            strArr[i] = (String) arrayList.get(i);
        }
        return strArr;
    }

}
