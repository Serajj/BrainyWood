package com.sharad.brainywood.offline;

import android.content.Context;

import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.sharad.brainywood.offline.Constants.DIR_NAME;
import static com.sharad.brainywood.offline.Constants.FILE_EXT;
import static com.sharad.brainywood.offline.Constants.FILE_NAME;
import static com.sharad.brainywood.offline.Constants.TEMP_FILE_NAME;


/**
 * Created by Seraj Alam 9140327455 on 10/02/21.
 */

public class FileUtils {

    public static void saveFile(byte[] encodedBytes, String path) {
        try {
            File file = new File(path);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(encodedBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static byte[] readFile(String filePath) {
        byte[] contents;
        File file = new File(filePath);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

    @NonNull
    public static File createTempFile(Context context, byte[] decrypted) throws IOException {
        File tempFile = File.createTempFile("cvideo", ".mp4", context.getCacheDir());
        tempFile.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(decrypted);
        fos.close();
        Log.d("seraj","Temp file created");
        return tempFile;
    }

    public static FileDescriptor getTempFileDescriptor(Context context, byte[] decrypted) throws IOException {
        File tempFile = FileUtils.createTempFile(context, decrypted);
        FileInputStream fis = new FileInputStream(tempFile);
        return fis.getFD();
    }

    public static final String getDirPath(Context context) {
        return Environment
                .getExternalStorageDirectory().toString()+"/Android/data/com.sharad.brainywood/files/.nomedia";
    }

    public static final String getFilePath(Context context,String filename) {
        return getDirPath(context) + File.separator + filename;
    }

    public static final void deleteDownloadedFile(Context context,String filename) {
        File file = new File(getFilePath(context,filename));
        if (null != file && file.exists()) {
            if (file.delete()) Log.i("FileUtils", "File Deleted.");
        }
    }


}
