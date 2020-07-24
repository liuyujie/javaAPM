package com.app.manager.controller;

import com.dd.plist.NSDictionary;
import com.dd.plist.NSString;
import com.dd.plist.PropertyListParser;

import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class IPAUtil {

    public static Map<String,String> getIpaInfoMap(File ipaPath) throws Exception{

        Map<String,String> map = new HashMap();
        // 第三方jar包提供
        NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(ipaPath);
        // 应用包名
        NSString parameters = (NSString) rootDict.objectForKey("CFBundleIdentifier");
        map.put("CFBundleIdentifier", parameters.toString());
        // 应用名称
        parameters = (NSString) rootDict.objectForKey("CFBundleName");
        map.put("CFBundleName", parameters.toString());
        // 应用版本
        parameters = (NSString) rootDict.objectForKey("CFBundleVersion");
        map.put("CFBundleVersion", parameters.toString());

        parameters = (NSString) rootDict.objectForKey("CFBundleShortVersionString");
        map.put("CFBundleShortVersionString", parameters.toString());
        // 应用展示的名称
        parameters = (NSString) rootDict.objectForKey("CFBundleName");
        map.put("CFBundleName", parameters.toString());
        // 应用所需IOS最低版本
        parameters = (NSString) rootDict.objectForKey("MinimumOSVersion");
        map.put("MinimumOSVersion", parameters.toString());

        // 如果有必要，应该删除解压的结果文件
        ipaPath.delete();
        ipaPath.getParentFile().delete();

        return map;
    }

    /**
     * IPA文件的拷贝，把一个IPA文件复制为Zip文件,同时返回Info.plist文件
     * 参数 oldfile 为 IPA文件
     */
    public static File getIpaInfoPath(File ipaFilePath) throws IOException {
        try{
            int byteread = 0;
            String filename = ipaFilePath.getAbsolutePath().replaceAll(".ipa", ".zip");
            File newfile = new File(filename);
            if (ipaFilePath.exists()){
                // 创建一个Zip文件
                InputStream inStream = new FileInputStream(ipaFilePath);
                FileOutputStream fs = new FileOutputStream(newfile);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1){
                    fs.write(buffer,0,byteread);
                }
                if(inStream != null){
                    inStream.close();
                }
                if(fs != null){
                    fs.close();
                }
                // 解析Zip文件
                return unzip(newfile, newfile.getParent());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解压IPA文件，只获取IPA文件的Info.plist文件存储指定位置
     * @param file
     * zip文件
     * @param unzipDirectory
     * 解压到的目录
     * @throws Exception
     */
    private static File unzip(File file, String unzipDirectory)
            throws Exception {
        // 定义输入输出流对象
        InputStream input = null;
        OutputStream output = null;
        File result = null;
        File unzipFile = null;
        ZipFile zipFile = null;
        try {
            // 创建zip文件对象
            zipFile = new ZipFile(file);
            // 创建本zip文件解压目录
            String name = file.getName().substring(0,file.getName().lastIndexOf("."));
            unzipFile = new File(unzipDirectory + "/" + name);
            if (unzipFile.exists()){
                unzipFile.delete();
            }
            unzipFile.mkdir();
            // 得到zip文件条目枚举对象
            Enumeration<ZipEntry> zipEnum = (Enumeration<ZipEntry>) zipFile.entries();
            // 定义对象
            ZipEntry entry = null;
            String entryName = null;
            String names[] = null;
            int length;
            // 循环读取条目
            while (zipEnum.hasMoreElements()) {
                // 得到当前条目
                entry = zipEnum.nextElement();
                entryName = new String(entry.getName());
                // 用/分隔条目名称
                names = entryName.split("\\/");
                length = names.length;
                for (int v = 0; v < length; v++) {
                    if(entryName.endsWith(".app/Info.plist")){ // 为Info.plist文件,则输出到文件
                        input = zipFile.getInputStream(entry);
                        result = new File(unzipFile.getAbsolutePath()+ "/Info.plist");
                        output = new FileOutputStream(result);
                        byte[] buffer = new byte[1024 * 8];
                        int readLen = 0;
                        while ((readLen = input.read(buffer, 0, 1024 * 8)) != -1){
                            output.write(buffer, 0, readLen);
                        }
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (input != null)
                input.close();
            if (output != null) {
                output.flush();
                output.close();
            }
            // 必须关流，否则文件无法删除
            if(zipFile != null){
                zipFile.close();
            }
        }
        // 如果有必要删除多余的文件
        if(file.exists()){
            file.delete();
        }
        return result;
    }
}
