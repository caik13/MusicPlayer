package com.caik13.musicplayer;

public class Config {
	/**
	 * ��������
	 */
	public static int MUSIC_COUNT = 0;

	/**
	 * �����б�--����
	 */
	public static final String SINGER_LIST_HUAYU = "http://mobilecdn.kugou.com/api/v3/singer/recommend?type=1&with_res_tag=1";
	/**
	 * �����б�--ŷ��
	 */
	public static final String SINGER_LIST_OUMEI = "http://mobilecdn.kugou.com/api/v3/singer/recommend?type=2&with_res_tag=1";
	/**
	 * �����б�--�պ�
	 */
	public static final String SINGER_LIST_RIHAN = "http://mobilecdn.kugou.com/api/v3/singer/recommend?type=3&with_res_tag=1";
	/**
	 * �����б�
	 * ʹ��ʱ��Ҫ�ں������singerid����
	 */
	public static final String SINGER_SONGS_LIST = "http://mobilecdn.kugou.com/api/v3/singer/song?page=1&pagesize=20&with_res_tag=1&singerid=";
	/**
	 * ��ȡ�������
	 * ʹ��ʱ��Ҫ�ں������title�Ĳ���������������$$������$$$$
	 */
	public static final String BAIDU_MUSIC = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=";
}
