package com.coolweather.app.activity;

import java.security.PublicKey;

import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.LogUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity {

	private LinearLayout weatherInfoLayout;
	//城市名字
	private TextView cityNameText;
	//发布时间
	private TextView publishText;
	//天气描述
	private TextView weatherDespText;
	//气温1
	private TextView temp1Text;
	//气温2
	private TextView temp2Text;
	//当前日期
	private TextView currentDateText;
	
	private Button switchCity;
	private Button refreshWeather;
	
	private static boolean first=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		LogUtil.w("TAG", "into weatherActivity ok");
		
		weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameText=(TextView)findViewById(R.id.city_name);
		publishText=(TextView)findViewById(R.id.publish_text);
		weatherDespText=(TextView)findViewById(R.id.weather_desp);
		temp1Text=(TextView)findViewById(R.id.temp1);
		temp2Text=(TextView)findViewById(R.id.temp2);
		currentDateText=(TextView)findViewById(R.id.current_date);
		
		String countyCode=getIntent().getStringExtra("county_code");
		LogUtil.w("TAG", "get countycode ok "+countyCode);
		if(!TextUtils.isEmpty(countyCode)){
			//有县级代号时查询天气
			LogUtil.w("TAG", "countycode is not null ok");
			publishText.setText("同步中...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			LogUtil.w("TAG", "will queryWeatherCode");
			queryWeatherCode(countyCode);
			LogUtil.w("TAG", "queryWeatherCode finish");
		}else{
			//没有县级代号时显示本地天气
			LogUtil.w("TAG", "will show weather");
			showWeather();
			LogUtil.w("TAG", "show weather finish");
		}
		
		switchCity=(Button)findViewById(R.id.switch_city);
		refreshWeather=(Button)findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(WeatherActivity.this,
						ChooseAreaActivity.class);
				intent.putExtra("from_weather_activity", true);
				first=false;
				startActivity(intent);
				finish();
			}
		});
		refreshWeather.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LogUtil.a("TAG", "into refresh");
				publishText.setText("正在同步中...");
				SharedPreferences prefs=PreferenceManager.
						getDefaultSharedPreferences(WeatherActivity.this);
				String weatherCode=prefs.getString("weather_code", "");
				LogUtil.a("TAG", "weathercode is "+weatherCode);
				if(!TextUtils.isEmpty(weatherCode)){
					LogUtil.a("TAG", "will queryWeatherInfo");
					queryWeatherInfo(weatherCode);
				}
			}
		});
	}
	//丛SharedPreferences文件中读取存储的天气信息，并显示
	private void showWeather() {
		// TODO Auto-generated method stub
		LogUtil.w("TAG", "into show weather ok");
		SharedPreferences prefs=PreferenceManager.
				getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("今天"+prefs.getString("publish_time", "")+"发布");
		currentDateText.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		LogUtil.w("TAG", "showweather is ok");
		//Intent intent=new Intent(this,AutoUpdateService.class);
		//startService(intent);
	}

	private void queryWeatherCode(String countyCode) {
		// TODO Auto-generated method stub
		LogUtil.w("TAG", "into queryWeatherCode");
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+
				".xml";
		LogUtil.w("TAG", "will into queryFromService");
		queryFromService(address,"countyCode");
		LogUtil.w("TAG", "queryFromService finsh");
	}

	private void queryFromService(final String address, final String type) {
		// TODO Auto-generated method stub
		LogUtil.a("TAG", "into queryFromService");
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(final String response) {
				// TODO Auto-generated method stub
				LogUtil.a("TAG", "into onFinish");
				if("countyCode".equals(type)){
					LogUtil.a("TAG", "countycode is equal");
					if(!TextUtils.isEmpty(response)){
						String[] array=response.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode=array[1];
							LogUtil.a("TAG", "will into queryWeatherInfo WC="+
							weatherCode);
							queryWeatherInfo(weatherCode);
							LogUtil.a("TAG", "qWInfo finish");
						}
					}
				}else if("weatherCode".equals(type)){
					LogUtil.a("TAG", "weathercode is equal");
					LogUtil.a("TAG", "will into hanleWR");
					Utility.handleWeatherResponse(WeatherActivity.this, 
							response);
					LogUtil.a("TAG", "handleWR finish");
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							LogUtil.a("TAG", "will showWeather");
							showWeather();
							LogUtil.a("TAG", "show weather finish");
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				LogUtil.a("TAG", "into onError");
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						LogUtil.a("TAG", "失败");
						publishText.setText("同步失败");
					}
				});
			}
		});
	}

	protected void queryWeatherInfo(String weatherCode) {
		// TODO Auto-generated method stub
		LogUtil.a("TAG", "into queryWeatherInfo");
		LogUtil.a("TAG", "weathercode = "+weatherCode);
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode
				+".html";
		LogUtil.a("TAG", "will into queryFromService WC");
		queryFromService(address, "weatherCode");
		LogUtil.a("TAG", "queryFromService WC finish");
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(first){
			super.onBackPressed();
		}
		else{
		Intent intent=new Intent(this,ChooseAreaActivity.class);
		intent.putExtra("from_weather_back", true);
		//intent.putExtra("", value)
		startActivity(intent);
		finish();
		}
	}
}
