package com.caik13.musicplayer.util;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * 解析访问百度音乐接口返回的XML，从中抽取音乐URL
 * @author Administrator
 *
 */
public class ParseXML {
	
	public static String parse(String xml) throws XmlPullParserException, IOException{
		String url_encode = "";
		String url_decode = "";
		//构建XmlPullParserFactory  
        XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();  
        //获取XmlPullParser的实例  
        XmlPullParser xmlPullParser = pullParserFactory.newPullParser();  
        xmlPullParser.setInput(new StringReader(xml));
		
		int eventType = xmlPullParser.getEventType();
	
		boolean isStop = false;
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String nodeName = xmlPullParser.getName();
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if ("encode".equals(nodeName)) {
					url_encode = xmlPullParser.nextText();
				}
				if ("decode".equals(nodeName)) {
					url_decode = xmlPullParser.nextText();
				}
				break;
			case XmlPullParser.END_TAG:
				//XML中有两套或者更多的歌曲地址，这里只取第一套，所以在url标签结束时就停止解析
				if ("url".equals(nodeName)) {
					isStop = true;
				}
				break;

			default:
				break;
			}
			if(isStop){
				eventType = xmlPullParser.END_DOCUMENT;
			}else{
				eventType = xmlPullParser.next();
			}
			
		}
		String url = "";
		if(!"".equals(url_encode)){
			url = url_encode.substring(0, url_encode.lastIndexOf('/')) + "/" + url_decode;
		}
		return url;
	}
}
