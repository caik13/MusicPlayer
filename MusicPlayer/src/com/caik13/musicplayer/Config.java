package com.caik13.musicplayer;

public class Config {
	/**
	 * 歌曲数量
	 */
	public static int MUSIC_COUNT = 0;

	/**
	 * 歌手列表--华语
	 */
	public static final String SINGER_LIST_HUAYU = "http://mobilecdn.kugou.com/api/v3/singer/recommend?type=1&with_res_tag=1";
	/**
	 * 歌手列表--欧美
	 */
	public static final String SINGER_LIST_OUMEI = "http://mobilecdn.kugou.com/api/v3/singer/recommend?type=2&with_res_tag=1";
	/**
	 * 歌手列表--日韩
	 */
	public static final String SINGER_LIST_RIHAN = "http://mobilecdn.kugou.com/api/v3/singer/recommend?type=3&with_res_tag=1";
	/**
	 * 歌曲列表
	 * 使用时需要在后面加上singerid参数
	 */
	public static final String SINGER_SONGS_LIST = "http://mobilecdn.kugou.com/api/v3/singer/song?page=1&pagesize=20&with_res_tag=1&singerid=";
	/**
	 * 获取网络歌曲
	 * 使用时需要在后面加上title的参数，例：歌曲名$$歌手名$$$$
	 */
	public static final String BAIDU_MUSIC = "http://box.zhangmen.baidu.com/x?op=12&count=1&title=";
}
