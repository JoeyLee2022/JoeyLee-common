package com.joeylee.common.utils;


import cn.hutool.core.io.file.FileNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.FlacFileWriter;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mp3 工具类
 *
 * @author joeylee
 * @date 2022年4月16日15:56:33
 */
@Slf4j
public class Mp3Utils {
    private static final String MP3_SUFFIX = "mp3";

    static {
        //关闭日志打印
        Logger[] pin = new Logger[]{Logger.getLogger("org.jaudiotagger")};
        for (Logger l : pin) {
            l.setLevel(Level.OFF);
        }
    }

    /**
     * 是否是 mp3 类型
     *
     * @return
     */
    public static boolean isMp3Type(File file) {
        String suffix = FileNameUtil.getSuffix(file);
        if (suffix.equalsIgnoreCase(MP3_SUFFIX)) {
            return true;
        }
        return false;
    }


    public static void updateTag(AudioFile audioFile, String[] array) throws Exception {
        Tag tag = audioFile.getTag();
        tag.setField(FieldKey.TITLE, array[1].substring(0, array[1].indexOf(".")).trim());// 单曲名
        tag.setField(FieldKey.ARTIST, array[0].trim());// 单曲艺术家
        FlacFileWriter ffw = new FlacFileWriter();
        ffw.write(audioFile);
    }

    /**
     * 获取MP3歌曲名、歌手、时长、照片信息
     *
     * @param file
     * @return
     */
    private static Object getInfo(File file)
            throws ReadOnlyFileException, TagException, InvalidAudioFrameException, IOException, CannotReadException {
        MP3File mp3File = (MP3File) AudioFileIO.read(file);
        AbstractID3v2Tag v2tag = mp3File.getID3v2Tag();

        String artist = v2tag.getFirst(FieldKey.ARTIST);// 歌手名
        String album = v2tag.getFirst(FieldKey.ALBUM);// 專輯名
        String songName = v2tag.getFirst(FieldKey.TITLE);// 歌名
        System.out.println("歌名 songName: " + songName); // 歌名
        System.out.println("歌手 singer: " + artist); // 歌手名
        System.out.println("专辑 album: " + album); // 專輯名

        //MP3AudioHeader header = mp3File.getMP3AudioHeader(); // mp3文件頭部信息
        //int length = header.getTrackLength();
        //System.out.println("Length: " + length / 60 + ":" + length % 60 + "sec"); // 歌曲時長
        //
        ////AbstractID3v2Tag tag = mp3File.getID3v2Tag();
        //AbstractID3v2Frame frame = (AbstractID3v2Frame) v2tag.getFrame("APIC");
        //if (ObjectUtil.isNotEmpty(frame)) {
        //    FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
        //    byte[] imageData = body.getImageData();
        //    //System.out.println(imageData);
        //    Image img = Toolkit.getDefaultToolkit().createImage(imageData, 0, imageData.length);
        //    System.out.println("img----" + imageData);
        //    ImageIcon icon = new ImageIcon(img);
        //    FileOutputStream fos = new FileOutputStream("D://test1.jpg");
        //    fos.write(imageData);
        //    fos.close();
        //    getImg(icon);
        //}

        return v2tag;
    }

    /**
     * 获取MP3歌曲名、歌手、时长、照片信息
     *
     * @param url
     * @return
     */
    public static void getMP3Info(String url)
            throws ReadOnlyFileException, TagException, InvalidAudioFrameException, IOException, CannotReadException {
        url = "D:\\CloudMusic\\Ada - new sou.mp3";//测试数据**

        MP3File mp3File = (MP3File) AudioFileIO.read(new File(url));
        AbstractID3v2Tag v2tag = mp3File.getID3v2Tag();

        String artist = v2tag.getFirst(FieldKey.ARTIST);// 歌手名
        String album = v2tag.getFirst(FieldKey.ALBUM);// 專輯名
        String songName = v2tag.getFirst(FieldKey.TITLE);// 歌名
        System.out.println("album: " + album); // 專輯名
        System.out.println("singer: " + artist); // 歌手名
        System.out.println("songName: " + songName); // 歌名

        MP3AudioHeader header = mp3File.getMP3AudioHeader(); // mp3文件頭部信息
        int length = header.getTrackLength();
        System.out.println("Length: " + length / 60 + ":" + length % 60 + "sec"); // 歌曲時長
        AbstractID3v2Frame frame = (AbstractID3v2Frame) v2tag.getFrame("APIC");

        FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
        byte[] imageData = body.getImageData();
        //System.out.println(imageData);
        Image img = Toolkit.getDefaultToolkit().createImage(imageData, 0, imageData.length);
        System.out.println("img----" + imageData);
        ImageIcon icon = new ImageIcon(img);
        FileOutputStream fos = new FileOutputStream("D://test1.jpg");
        fos.write(imageData);
        fos.close();
        getImg(icon);
    }

    private static void getImg(ImageIcon img) {
        JFrame f = new JFrame();
        JLabel l = new JLabel();
        l.setIcon(img);
        l.setVisible(true);
        f.add(l);
        f.setSize(500, 500);
        f.setVisible(true);
    }


}


