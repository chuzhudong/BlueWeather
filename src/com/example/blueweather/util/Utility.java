package com.example.blueweather.util;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.example.blueweather.R;
import com.example.blueweather.database.BlueWeatherDB;
import com.example.blueweather.model.City;
import com.example.blueweather.model.County;
import com.example.blueweather.model.Province;
import com.example.blueweather.model.Weather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Utility {
	
	private static final String TAG = "Utility";
	
	private static final int RAWDATA_NOT_LOADED = 0;
	private static final int RAWDATA_LOADED = 1;
	private static final int RAWDATA_NONE = -1;
	
	private static final int PRO = 1;
	private static final int CIT = 2;
	private static final int COU = 3;

	private static int result;
	
	/**
	 * parse jason data in Province to database
	 */
	public synchronized static boolean handleProvincesData(
			BlueWeatherDB blueWeatherDB, String data) {
		if (!TextUtils.isEmpty(data)) {
			JSONArray jsonArray;
			try {
				jsonArray = new JSONArray(data);
				Province province = new Province();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					province.setProId(object.getInt("ProID"));
					province.setProName(object.getString("name"));
					province.setProSort(object.getInt("ProSort"));
					province.setProRemark(object.getString("ProRemark"));
					blueWeatherDB.saveProvince(province);
				}
				return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * parse jason data in city to database
	 */
	public synchronized static boolean handleCitiesData(
			BlueWeatherDB blueWeatherDB, String data) {
			if (!TextUtils.isEmpty(data)) {
				JSONArray jsonArray;
				try {
					jsonArray = new JSONArray(data);
					City city = new City();
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.getJSONObject(i);
						city.setCityId(object.getInt("CityID"));
						city.setCityName(object.getString("name"));
						//Log.d(TAG, object.getString("name"));
						city.setProId(object.getInt("ProID"));
						city.setCitySort(object.getInt("CitySort"));
						blueWeatherDB.saveCity(city);
					}
					return true;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
	}
	
	/**
	 * parse jason data in city to database
	 */
	public synchronized static boolean handleCountiesData(
			BlueWeatherDB blueWeatherDB, String data) {		
		if (!TextUtils.isEmpty(data)) {
			JSONArray jsonArray;
			try {
				jsonArray = new JSONArray(data);
				County county = new County();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.getJSONObject(i);
					county.setCountyId(object.getInt("Id"));
					county.setCountyName(object.getString("DisName"));
					//Log.d(TAG, object.getString("DisName"));
					county.setCityId(object.getInt("CityID"));
					county.setCountySort(object.getInt("DisSort"));
					blueWeatherDB.saveCounty(county);
				}
				return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static String filterString(int dis, String source) {
		if (TextUtils.isEmpty(source) || dis > 3 || dis < 1) {
			return null;
		}
		int lastCharPos = source.length() - 1;
		char lastChar = source.charAt(source.length() - 1);
		String des = null;
		if (dis == CIT) {
			if (lastChar == '��') {
				des = source.substring(0, lastCharPos);
			} else if ("����ر�������".equals(source) || "�����ر�������".equals(source)) {
				des = source.substring(0, 2);
			} else if (source.charAt(lastCharPos) == '��') {
				des = source.substring(0, lastCharPos -1 );
			} else if (lastChar == '��') {
				des = source.substring(0, 2);
			} else if (lastChar == '��') {
				if ("�ӱ߳�����������".equals(source)) {
					des = "�Ӽ�";
				} else if ("��������������������".equals(source)) {
					des = "����";
				} else if ("ǭ�ϲ���������������".equals(source)) {
					des = "����";
				} else if ("ǭ�������嶱��������".equals(source)) {
					des = "����";
				} else if ("ǭ���ϲ���������������".equals(source)) {
					des = "����";
				} else if ("��˫���ɴ���������".equals(source)) {
					des = "��˫����";
				} else if ("�������������".equals(source)) {
					des = "�������";
				} else if ("��������������".equals(source)) {
					des = "����";
				} else if ("���ϲ���������".equals(source)) {
					des = "ͬ��";
				} else if ("�����ɹ������������".equals(source)) {
					des = "�����";
				} else if ("���ϲ���������".equals(source)) {
					des = "������";
				} else if ("�������տ¶�����������".equals(source)) {
					des = "��ͼʲ";
				} else if ("���������ɹ�������".equals(source)) {
					des = "����";
				} else if ("���������ɹ�������".equals(source)) {
					des = "�����";
				} else {
					des = source.substring(0, 2);
				}
			} else {
				des = null;
			}
		} else if (dis == COU) {
			if (lastChar == '��') {
				if ("�����������ɹ���������".equals(source)) {
					des = "��������";
				} else if ("�Ŷ������ɹ���������".equals(source)) {
					des = "����";
				} else if ("�������������".equals(source)) {
					des = "��ˮ";
				} else if ("�żҴ�����������".equals(source)) {
					des = "�żҴ�";
				} else if ("��������������������".equals(source)) {
					des = "��Ȫ";
				} else if ("��ʯɽ�����嶫����������������".equals(source)) {
					des = "����";
				} else if ("�����ɹ���������".equals(source)) {
					des = "ͬ��";
				} else if ("ī�񹤿���".equals(source)) {
					des = "����";
				} else if ("����������".equals(source)) {
					des = "����";
				} else if ("лͨ����".equals(source)) {
					des = "�տ���";
				} else if ("����������".equals(source)) {
					des = "��֥";
				} else if ("��ʲ�����������������".equals(source)) {
					des = "��ʲ";
				} else if ("��������".equals(source)) {
					des = "����";
				} else if ("�Ͳ��������ɹ�������".equals(source)) {
					des = "�Ͳ�������";
				} else if ("������������������".equals(source)) {
					des = "������";
				} else if ("�첼�������������".equals(source)) {
					des = "�첼���";
				} else if ("ľ�ݹ�����������".equals(source)) {
					des = "ľ��";
				} else if ("����".equals(source)) {
					des = "��֥";
				} else if (lastCharPos + 1 == 2) {
					des = source;
				} else if (source.contains("������")){
					des = source.substring(0, 2);
				} else {
					des = source.substring(0, lastCharPos);
				}
			} else if (lastChar == '��') {
				if ("�ֶ�����".equals(source)) {
					des = "�ֶ�";
				} else if ("������".equals(source)) {
					des = "���";
				} else if ("�������".equals(source)) {
					des = "����";
				} else {
					des = source.substring(0, lastCharPos);
				}
			} else if (lastChar == '��') {
				des = source.substring(0, lastCharPos);
			} else if (lastChar == '��') {
				if (source == "") {
					
				} else if ("Ī�����ߴ��Ӷ���������".equals(source)) {
					des = "Ī��������";
				} else if ("���״�������".equals(source)) {
					des = "���״���";
				} else if ("���¿���������".equals(source)) {
					des = "���¿���";
				} else if (source == "") {
					des = "";
				} else {
					des = source;
				}
			} else {
				return null;
			}
		}
		return des;
	}
	
	public static String unicodeToGb2312(String str) {
		String result = null;
		try {
			result = URLEncoder.encode(str, "gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static Weather parseXMLWithPull(String xmlData) {
		//Log.d(TAG, xmlData);
		Weather mWeather = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = factory.newPullParser();
			xmlPullParser.setInput(new StringReader(xmlData));
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = xmlPullParser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG: {
					if ("status1".equals(nodeName)) {
						mWeather = new Weather();
						try {
							mWeather.weather = xmlPullParser.nextText();
						} catch (Exception e) {
							e.printStackTrace();
							mWeather.weather = "��";//������Ϊ�˷�ֹxmlPullPaser.nettext()���쳣
						}
					} else if ("temperature1".equals(nodeName)) {
						try {
							mWeather.temp2 = Integer.parseInt(xmlPullParser.nextText());
						} catch (Exception e) {
							e.printStackTrace();
							mWeather.temp2 = 30;
						}
					} else if ("temperature2".equals(nodeName)) {
						try {
							mWeather.temp1 = Integer.parseInt(xmlPullParser.nextText());
						} catch (Exception e) {
							e.printStackTrace();
							mWeather.temp1 = mWeather.temp2 - 10;
						}
					} else if ("tgd1".equals(nodeName)) {
						try {
							mWeather.current_tem = Integer.parseInt(xmlPullParser.nextText());
						} catch (Exception e) {
							e.printStackTrace();
							mWeather.current_tem = (mWeather.temp1 + mWeather.temp2) / 2;
						}
					} else if ("udatetime".equals(nodeName)) {
						try {
							mWeather.updateTime = xmlPullParser.nextText();
						} catch (Exception e) {
							e.printStackTrace();
							mWeather.updateTime = "���շ���";
						}
					}
					break;
				}
				case XmlPullParser.END_TAG: {
					if ("Weather".equals(nodeName)) {
						int imageId = weatherToImageId(mWeather.weather);
						mWeather.imageId = imageId;
					}
					break;
				}
				default:
					break;
				}
				eventType = xmlPullParser.next();	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mWeather;
	}
	
	private static int weatherToImageId(String weather) {
		int imageId = R.drawable.qing_0;
		if (!TextUtils.isEmpty(weather)) {
			if ("��ѩ".equals(weather)) {
				imageId = R.drawable.baoxue_0;
			} else if ("����".equals(weather)) {
				imageId = R.drawable.baoyu_0;
			} else if ("����".equals(weather)) {
				imageId = R.drawable.bingbao_0;
			} else if ("��ѩ".equals(weather)) {
				imageId = R.drawable.bingbao_0;
			} else if ("����".equals(weather)) {
				imageId = R.drawable.dayu_0;
			} else if ("����".equals(weather)) {
				imageId = R.drawable.dongyu_0;
			} else if ("����".equals(weather)) {
				imageId = R.drawable.duoyun_0;
			} else if ("����".equals(weather)) {
				imageId = R.drawable.dongyu_0;
			} else if ("������".equals(weather)) {
				imageId = R.drawable.leizhenyu_0;
			} else if ("��".equals(weather)) {
				imageId = R.drawable.mai_0;
			} else if ("ǿɳ����".equals(weather)) {
				imageId = R.drawable.qiangshachenbao_0;
			} else if ("��".equals(weather)) {
				imageId = R.drawable.qing_0;
			} else if ("ɳ����".equals(weather)) {
				imageId = R.drawable.shachenbao_0;
			} else if ("�ش�ѩ".equals(weather)) {
				imageId = R.drawable.tedabaoyu_0;
			} else if ("��".equals(weather)) {
				imageId = R.drawable.wu_0;
			} else if ("Сѩ".equals(weather)) {
				imageId = R.drawable.xiaoxue_0;
			} else if ("С��".equals(weather) || "С������".equals(weather)) {
				imageId = R.drawable.xiaoyu_0;
			} else if ("��".equals(weather)) {
				imageId = R.drawable.yin_0;
			} else if ("���ѩ".equals(weather)) {
				imageId = R.drawable.yujiaxue_0;
			} else if ("����".equals(weather)) {
				imageId = R.drawable.zhenyu_0;
			} else if ("��ѩ".equals(weather)) {
				imageId = R.drawable.zhongxue_0;
			} else if ("����".equals(weather)) {
				imageId = R.drawable.zhongyu_0;
			}
		}
		return imageId;
	}
	
	public static int getUpdateInterval(String param) {
		int result = 0;
		if(!TextUtils.isEmpty(param)) {
			int end = param.indexOf('h');
			String des = param.substring(0, end);
			result = Integer.parseInt(des);
		}
		return result;	
	}
	
	public static String getRemoteWeatherUpdateTime(String param) {
		String result = null;
		if(!TextUtils.isEmpty(param)) {
			if ("���շ���".equals(param)) {
				result = param;
			} else {
				try {
					int day = Integer.parseInt(param.substring(8, 10));
					int hour = Integer.parseInt(param.substring(11, 13));
					result = String.valueOf(day) + "��" + String.valueOf(hour) + "ʱ����";
				} catch (Exception e) {
					result = "���շ���";
					e.printStackTrace();
				}
			}
		} else {
			result = "���շ���";
		}
		Log.d(TAG, result);
		return result;
	}
}
