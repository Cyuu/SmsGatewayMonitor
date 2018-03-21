package com.thdz.sgm.util;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import com.thdz.sgm.MyApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * FileUtil.writeFile
 */
public class FileUtil {
    public static String TAG = "FileUtil";


//    /**
//     * 创建本地目录
//     */
//    public static void createLocalPath(String path) {
//        try {
//            String[] str = {"mkdir", path};
//            Process ps = Runtime.getRuntime().exec(path);
//            try {
//                ps.waitFor();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public static boolean createDirectory(String path) {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            File file = new File(path);
            if (!file.exists()) {
                boolean isCreated = file.mkdir();
                if (!isCreated) {
                    Log.i(TAG, path + "目录创建失败");
                    // TsUtil.showToast("缓存目录创建失败");
                }
                return isCreated;
            }
        }
        return false;
    }


    public static boolean createFile(String path) {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            File file = new File(path);
            if (!file.exists()) {
                boolean isCreated = false;
                try {
                    isCreated = file.createNewFile();
                    if (!isCreated) {
                        Log.i(TAG, path + "文件创建失败");
                        // TsUtil.showToast("缓存目录创建失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return isCreated;
            }
        }
        return false;
    }


    // ************** 一行一行地读文件 ************
//    public static List<String> readFileByLines(String fileName) {
//        File file = new File(fileName);
//        try {
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//        } catch (IOException e2) {
//            // TODO Auto-generated catch block
//            e2.printStackTrace();
//        }
//
//        List<String> list = new ArrayList<String>();
//        BufferedReader reader = null;
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            String tempString = null;
//
//            while ((tempString = reader.readLine()) != null) {
//                // Log.i(TAG, "read^^^^" );
//                list.add(tempString);
//            }
//
//            reader.close();
//        } catch (IOException e) {
//            Log.i(TAG, "can't find file " + fileName);
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e1) {
//                }
//            }
//        }
//
//        return list;
//    }


    public static String readFile(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;

            while ((tempString = reader.readLine()) != null) {
                // Log.i(TAG, "read^^^^" );
                sb.append(tempString + "\r\n");
            }

            reader.close();
        } catch (IOException e) {
            Log.i(TAG, "can't find file " + fileName);
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

        return sb.toString();
    }


    // **************写文件*****************
//    public static void writeFile(String fileName, InputStream in) {
//        File file = new File(fileName);
//
//        File fileParent = file.getParentFile();
//        if (!fileParent.exists()) {
//            fileParent.mkdirs();
//        }
//
//        try {
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//        } catch (IOException e2) {
//            // TODO Auto-generated catch block
//            e2.printStackTrace();
//        }
//
//        OutputStream os = null;
//        try {
//            os = new FileOutputStream(file);
//            byte buffer[] = new byte[4 * 1024];
//
//            int len = 0;
//            while ((len = in.read(buffer)) != -1) {
//                os.write(buffer, 0, len);
//            }
//            os.flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                os.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            try {
//                in.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public static void writeFile(String fileName, String message) {
        try {
            File file = new File(fileName);

            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }

            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

//            FileOutputStream fout = new FileOutputStream(fileName, true);
            // 2018.3.16 zhangjunning要求日志只保留最后一条告警,1 不写非告警日志；2 不追加写日志， 3 注释掉备份日志的代码
            FileOutputStream fout = new FileOutputStream(fileName, false);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            Log.i(TAG, "can't find file " + fileName);
            e.printStackTrace();
        }

    }


    // 日志文件的最大容量 （M）
    public static int MAX_LOG_SIZE = 2;

    public static long lastCheckLogSizeTime = System.currentTimeMillis();

    /**
     * 备份,超过xM,则备份<br/>
     * 每个月备份一次<br/>
     * 每次页面可见就启动是否检查的判断
     */
    public static void checkBackupLog() {
        int druings = (int) ((System.currentTimeMillis() - lastCheckLogSizeTime) / 1000 / 60 / 60 / 24 / 30);
        if (druings >= 1) { // 超过1个月没检查
            backupLog();
        } else {
            if (Finals.IS_TEST) {
                toast("启动检查， 距离上次检查不足一个月，不做日志备份");
            }
        }
    }


    public static void backupLog() {
        try {
            File file = new File(Finals.LogPath);
            long size = 0;
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
                if (Finals.IS_TEST) {
                    toast("日志文件大小：" + size);
                }
                fis.close();
                if (size > MAX_LOG_SIZE * 1048576) {
                    String tmpPath = Finals.LogPathHead + "_" + DataUtils.getFormatToday() + ".log.bak";
                    FileUtil.copyFile(Finals.LogPath, tmpPath);
                    writeFileAppendORCover(Finals.LogPath, "", false);
                    lastCheckLogSizeTime = System.currentTimeMillis();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void toast(String msg) {
        Toast.makeText(MyApplication.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    public static int writeFileAppendORCover(String fileName, String content, Boolean append) {
        try {
            File file = new File(fileName);

            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }

            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException e2) {
                // TODO Auto-generated catch block
                e2.printStackTrace();
            }

            FileWriter writer = new FileWriter(fileName, append);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.e(TAG, " ------- can't find file " + fileName + " ---------");
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public static void bytes2File(byte[] w, String fileName) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            out.write(w);
            out.close();
        } catch (IOException e) {
            if (out != null)
                out.close();
            throw e;
        }
    }


    public static byte[] File2Bytes(String path) throws IOException {
        FileInputStream in = null;
        try {
            in = new FileInputStream(path);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] temp = new byte[1024];
            int size = 0;
            while ((size = in.read(temp)) != -1) {
                bos.write(temp, 0, size);
            }
            in.close();
            byte[] bytes = bos.toByteArray();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            if (in != null)
                in.close();
            throw e;
        }

    }


    // *****************************************************备份文件*******************************************************************************
    public static void backupFile(String fileName, String fileName_bak) {
        File file = new File(fileName);
        File file_bak = new File(fileName_bak);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            if (!file_bak.exists()) {
                file_bak.createNewFile();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        if (file.length() >= file_bak.length()) {
            Log.d(TAG, "back up log file");
            if (!copyFile(fileName, fileName_bak)) {
                Log.e(TAG, "log file bak fail ");
            }
        } else {
            Log.e(TAG, "log lost!!! ");
        }
    }

    public static boolean copyFile(String from, String to) {

        File fromFile, toFile;

        fromFile = new File(from);
        toFile = new File(to);

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            if (!toFile.exists()) {
                toFile.createNewFile();
            }

            fis = new FileInputStream(fromFile);
            fos = new FileOutputStream(toFile);

            int bytesRead;
            byte[] buf = new byte[10 * 1024 * 1024];// 10M buffer

            while ((bytesRead = fis.read(buf)) != -1) {
                fos.write(buf, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                Log.i(TAG, "copy file stream close failed");
            }
        }

        return true;

    }

    // *****************************************************************删除文件*********************************************************
    public static boolean delDirectory(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delDirectory(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }

    private static void delFolder(String folderPath) {
        try {
            delDirectory(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //*************************************************************判断文件是否存在******************************************************************
    public static boolean isFileExists(String filename) {
        if (filename == null)
            return false;

        File file = new File(filename);
        return file.exists();
    }

    //*************************************************************获取存储空间大小******************************************************************
    public static long getMemorySize(String path) {
        if (path == null) {
            return 0;
        }
        StatFs statfs = new StatFs(path);
        long blockSize = statfs.getBlockSize();
        long availableBlocks = statfs.getAvailableBlocks();
        return blockSize * availableBlocks / (1024 * 1024);
    }

    // ************************************************************修改文件属性**********************************************************************
    public static void execCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
//			ProcessBuilder builder = new ProcessBuilder("su");
//			builder.redirectErrorStream(false);
//			process = builder.start();
            process = Runtime.getRuntime().exec("su hzliyutai");
            InputStream inputstream = process.getInputStream();
            InputStreamReader inputstreamreader = new InputStreamReader(
                    inputstream);
            BufferedReader bufferedreader = new BufferedReader(
                    inputstreamreader);
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();

            String line = "";
            StringBuilder sb = new StringBuilder(line);
            while ((line = bufferedreader.readLine()) != null) {

                sb.append(line);

                sb.append('\n');

            }
            process.waitFor();
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "
                    + e.getMessage());
            Log.i("why1", e.getMessage());
        }

    }

    // *************************************************************获取文件的MIMEType属性*************************************************************
    public static String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "")
            return type;

        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    private final static String[][] MIME_MapTable = {{".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"}, {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"}, {".bmp", "image/bmp"},
            {".c", "text/plain"}, {".class", "application/octet-stream"},
            {".conf", "text/plain"}, {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".exe", "application/octet-stream"}, {".gif", "image/gif"},
            {".gtar", "application/x-gtar"}, {".gz", "application/x-gzip"},
            {".h", "text/plain"}, {".htm", "text/html"},
            {".html", "text/html"}, {".jar", "application/java-archive"},
            {".java", "text/plain"}, {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"}, {".js", "application/x-javascript"},
            {".log", "text/plain"}, {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"}, {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"}, {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"}, {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"}, {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"}, {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"}, {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"}, {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"}, {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".prop", "text/plain"},
            {".rar", "application/x-rar-compressed"},
            {".rc", "text/plain"}, {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"}, {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"}, {".txt", "text/plain"},
            {".wav", "audio/x-wav"}, {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            // {".xml", "text/xml"},
            {".xml", "text/plain"}, {".z", "application/x-compress"},
            {".zip", "application/zip"}, {"", "*/*"}};
}
