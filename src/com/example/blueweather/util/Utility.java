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
			if (lastChar == '市') {
				des = source.substring(0, lastCharPos);
			} else if ("香港特别行政区".equals(source) || "澳门特别行政区".equals(source)) {
				des = source.substring(0, 2);
			} else if (source.charAt(lastCharPos) == '区') {
				des = source.substring(0, lastCharPos -1 );
			} else if (lastChar == '县') {
				des = source.substring(0, 2);
			} else if (lastChar == '州') {
				if ("延边朝鲜族自治州".equals(source)) {
					des = "延吉";
				} else if ("湘西土家族苗族自治州".equals(source)) {
					des = "吉首";
				} else if ("黔南布依族苗族自治州".equals(source)) {
					des = "都匀";
				} else if ("黔东南苗族侗族自治州".equals(source)) {
					des = "凯里";
				} else if ("黔西南布依族苗族自治州".equals(source)) {
					des = "兴义";
				} else if ("西双版纳傣族自治州".equals(source)) {
					des = "西双版纳";
				} else if ("迪庆藏族自治州".equals(source)) {
					des = "香格里拉";
				} else if ("海北藏族自治州".equals(source)) {
					des = "海晏";
				} else if ("黄南藏族自治州".equals(source)) {
					des = "同仁";
				} else if ("海西蒙古族藏族自治州".equals(source)) {
					des = "德令哈";
				} else if ("海南藏族自治州".equals(source)) {
					des = "共和县";
				} else if ("克孜勒苏柯尔克孜自治州".equals(source)) {
					des = "阿图什";
				} else if ("博尔塔拉蒙古自治州".equals(source)) {
					des = "博乐";
				} else if ("巴音郭楞蒙古自治州".equals(source)) {
					des = "库尔勒";
				} else {
					des = source.substring(0, 2);
				}
			} else {
				des = null;
			}
		} else if (dis == COU) {
			if (lastChar == '县') {
				if ("喀喇沁左翼蒙古族自治县".equals(source)) {
					des = "喀喇沁旗";
				} else if ("杜尔伯特蒙古族自治县".equals(source)) {
					des = "大庆";
				} else if ("景宁畲族自治县".equals(source)) {
					des = "丽水";
				} else if ("张家川回族自治县".equals(source)) {
					des = "张家川";
				} else if ("阿克塞哈萨克族自治县".equals(source)) {
					des = "酒泉";
				} else if ("积石山保安族东乡族撒拉族自治县".equals(source)) {
					des = "临夏";
				} else if ("河南蒙古族自治县".equals(source)) {
					des = "同仁";
				} else if ("墨竹工卡县".equals(source)) {
					des = "拉萨";
				} else if ("堆龙德庆县".equals(source)) {
					des = "拉萨";
				} else if ("谢通门县".equals(source)) {
					des = "日喀则";
				} else if ("工布江达县".equals(source)) {
					des = "林芝";
				} else if ("塔什库尔干塔吉克自治县".equals(source)) {
					des = "喀什";
				} else if ("类乌齐县".equals(source)) {
					des = "昌都";
				} else if ("和布克赛尔蒙古自治县".equals(source)) {
					des = "和布克赛尔";
				} else if ("巴里坤哈萨克自治县".equals(source)) {
					des = "巴里坤";
				} else if ("察布查尔锡伯自治县".equals(source)) {
					des = "察布查尔";
				} else if ("木垒哈萨克自治县".equals(source)) {
					des = "木垒";
				} else if ("朗县".equals(source)) {
					des = "林芝";
				} else if (lastCharPos + 1 == 2) {
					des = source;
				} else if (source.contains("自治县")){
					des = source.substring(0, 2);
				} else {
					des = source.substring(0, lastCharPos);
				}
			} else if (lastChar == '区') {
				if ("浦东新区".equals(source)) {
					des = "浦东";
				} else if ("峰峰矿区".equals(source)) {
					des = "峰峰";
				} else if ("井陉矿区".equals(source)) {
					des = "井陉";
				} else {
					des = source.substring(0, lastCharPos);
				}
			} else if (lastChar == '市') {
				des = source.substring(0, lastCharPos);
			} else if (lastChar == '旗') {
				if (source == "") {
					
				} else if ("莫力达瓦达斡尔族自治旗".equals(source)) {
					des = "莫力达瓦旗";
				} else if ("鄂伦春自治旗".equals(source)) {
					des = "鄂伦春旗";
				} else if ("鄂温克族自治旗".equals(source)) {
					des = "鄂温克旗";
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
							mWeather.weather = "晴";//纯粹是为了防止xmlPullPaser.nettext()抛异常
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
							mWeather.updateTime = "今日发布";
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
			if ("暴雪".equals(weather)) {
				imageId = R.drawable.baoxue_0;
			} else if ("暴雨".equals(weather)) {
				imageId = R.drawable.baoyu_0;
			} else if ("冰雹".equals(weather)) {
				imageId = R.drawable.bingbao_0;
			} else if ("大雪".equals(weather)) {
				imageId = R.drawable.bingbao_0;
			} else if ("大雨".equals(weather)) {
				imageId = R.drawable.dayu_0;
			} else if ("冻雨".equals(weather)) {
				imageId = R.drawable.dongyu_0;
			} else if ("多云".equals(weather)) {
				imageId = R.drawable.duoyun_0;
			} else if ("浮尘".equals(weather)) {
				imageId = R.drawable.dongyu_0;
			} else if ("雷阵雨".equals(weather)) {
				imageId = R.drawable.leizhenyu_0;
			} else if ("霾".equals(weather)) {
				imageId = R.drawable.mai_0;
			} else if ("强沙尘暴".equals(weather)) {
				imageId = R.drawable.qiangshachenbao_0;
			} else if ("晴".equals(weather)) {
				imageId = R.drawable.qing_0;
			} else if ("沙尘暴".equals(weather)) {
				imageId = R.drawable.shachenbao_0;
			} else if ("特大暴雪".equals(weather)) {
				imageId = R.drawable.tedabaoyu_0;
			} else if ("雾".equals(weather)) {
				imageId = R.drawable.wu_0;
			} else if ("小雪".equals(weather)) {
				imageId = R.drawable.xiaoxue_0;
			} else if ("小雨".equals(weather) || "小到中雨".equals(weather)) {
				imageId = R.drawable.xiaoyu_0;
			} else if ("阴".equals(weather)) {
				imageId = R.drawable.yin_0;
			} else if ("雨夹雪".equals(weather)) {
				imageId = R.drawable.yujiaxue_0;
			} else if ("阵雨".equals(weather)) {
				imageId = R.drawable.zhenyu_0;
			} else if ("中雪".equals(weather)) {
				imageId = R.drawable.zhongxue_0;
			} else if ("中雨".equals(weather)) {
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
			if ("今日发布".equals(param)) {
				result = param;
			} else {
				try {
					int day = Integer.parseInt(param.substring(8, 10));
					int hour = Integer.parseInt(param.substring(11, 13));
					result = String.valueOf(day) + "日" + String.valueOf(hour) + "时发布";
				} catch (Exception e) {
					result = "今日发布";
					e.printStackTrace();
				}
			}
		} else {
			result = "今日发布";
		}
		Log.d(TAG, result);
		return result;
	}
}
