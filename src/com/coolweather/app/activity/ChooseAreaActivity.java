package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.LogUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList=new ArrayList<String>();
	//省市县列表
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	//选中的省市县
	private Province selectedProvince;
	private City selectedCity;
	private County selectedCounty;
	//当前选中级别
	private int currentLevel;
	
	private boolean isFromWeatherActivity;
	private boolean isFromWeatherBack;
	private static int cityId;
	private static String cityCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView=(ListView)findViewById(R.id.list_view);
		titleText=(TextView)findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				dataList);
		listView.setAdapter(adapter);
		coolWeatherDB=CoolWeatherDB.getInstance(this);
		
		
		isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity",
				false);
		isFromWeatherBack=getIntent().getBooleanExtra("from_weather_back", false);
		LogUtil.e("TAG", "from_weather_back is "+isFromWeatherBack);
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		//选择了市且不是从WeatherActivity中跳过来的才会直接进入WeatherActivity;
		/*
		if(prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity){
			if(!isFromWeatherBack){
				LogUtil.w("TAG", "city_selected true ok");
				Intent intent=new Intent(this,WeatherActivity.class);
				LogUtil.w("TAG", "intent to weather ok");
				startActivity(intent);
				LogUtil.w("TAG", "intent to weather over ok");
				finish();
				return;
			}
			else{
				LogUtil.e("TAG", "isFWBack is true");
				LogUtil.e("TAG", "cityId is "+cityId);
				LogUtil.e("TAG", "citycode is "+cityCode);
				countyList=coolWeatherDB.loadCounties(cityId);
				if(countyList.size()>0){
					LogUtil.e("TAG", "size>0");
					dataList.clear();
					for(County county:countyList){
						dataList.add(county.getCountyName());
						LogUtil.e("TAG", "name is "+county.getCountyName());
					}
					adapter.notifyDataSetChanged();
					listView.setSelection(0);
					for(String n:dataList){
						LogUtil.e("TAG", "DataList name is "+n);
					}
					titleText.setText("");
					currentLevel=LEVEL_COUNTY;
					//*****************************************
					//次数需要改进，到这个界面后还可以继续 onClick
					return;
					
				}else{
					LogUtil.e("TAG", "will queryFromService");
					queryFromService(cityCode,"county");
					LogUtil.e("TAG", "qFS finish");
				}
			}
		}
		*/
		/*
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView=(ListView)findViewById(R.id.list_view);
		titleText=(TextView)findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				dataList);
		listView.setAdapter(adapter);
		
		LogUtil.d("TAG", "kongjian ok");
		coolWeatherDB=CoolWeatherDB.getInstance(this);
		*/
		LogUtil.d("TAG", " ok");
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				if(currentLevel==LEVEL_PROVINCE){
					//当前选中省
					LogUtil.w("TAG", "select province");
					selectedProvince=provinceList.get(index);
					queryCities();
				}else if(currentLevel==LEVEL_CITY){
					//当前选中市
					LogUtil.w("TAG", "select city");
					selectedCity=cityList.get(index);
					cityId=cityList.get(index).getId();
					cityCode=selectedCity.getCityCode();
					LogUtil.e("TAG", "cityCode get is "+cityCode);
					queryCounties();
				}else if(currentLevel==LEVEL_COUNTY){
					//LogUtil.w("TAG", "currentlevel==county ok");
					String countyCode=countyList.get(index).getCountyCode();
					//LogUtil.w("TAG", "countycode is "+countyCode);
					Intent intent=new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					//LogUtil.w("TAG", "intent to wea2 ok");
					intent.putExtra("county_code", countyCode);
					//intent.putExtra("CITY_code", cityCode);
					startActivity(intent);
					LogUtil.w("TAG", "intent to wea2 finish ok");
					finish();
				}
			}
		});
		
		
		if(prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity){
			if(!isFromWeatherBack){
				LogUtil.w("TAG", "city_selected true ok");
				Intent intent=new Intent(this,WeatherActivity.class);
				LogUtil.w("TAG", "intent to weather ok");
				startActivity(intent);
				LogUtil.w("TAG", "intent to weather over ok");
				finish();
				//return;
			}
			else{
				LogUtil.e("TAG", "isFWBack is true");
				LogUtil.e("TAG", "cityId is "+cityId);
				LogUtil.e("TAG", "citycode is "+cityCode);
				countyList=coolWeatherDB.loadCounties(cityId);
				if(countyList.size()>0){
					LogUtil.e("TAG", "size>0");
					dataList.clear();
					for(County county:countyList){
						dataList.add(county.getCountyName());
						LogUtil.e("TAG", "name is "+county.getCountyName());
					}
					adapter.notifyDataSetChanged();
					listView.setSelection(0);
					for(String n:dataList){
						LogUtil.e("TAG", "DataList name is "+n);
					}
					titleText.setText("");
					currentLevel=LEVEL_COUNTY;
					//*****************************************
					//次数需要改进，到这个界面后还可以继续 onClick
					//return;
					
				}else{
					LogUtil.e("TAG", "will queryFromService");
					queryFromService(cityCode,"county");
					LogUtil.e("TAG", "qFS finish");
				}
			}
		}
		
		if(!isFromWeatherBack){
		queryProvinces();
		//
		LogUtil.d("TAG", "queryProvince ok"); 
		}
	}
	//查询全国所有省，优先从数据库中查询，若没有查询到则再从服务器上查询
	private void queryProvinces() {
		// TODO Auto-generated method stub
		provinceList=coolWeatherDB.loadProvinces();
		//
		LogUtil.d("TAG", "loadProvince ok");
		if(provinceList.size()>0){
			//
			LogUtil.d("TAG", "size>0 ok");
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
				//
				LogUtil.d("TAG", "add ok");
			}
			adapter.notifyDataSetChanged();
			//默认选择第0项
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel=LEVEL_PROVINCE;
		}else{
			//
			LogUtil.d("TAG", "into queryFromService");
			queryFromService(null,"province");
			//
			LogUtil.d("TAG", "queryFromService  ok");
		}
	}
	//查询所有县信息，优先数据库，后服务器
	private void queryCounties() {
		// TODO Auto-generated method stub
		countyList=coolWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else{
			queryFromService(selectedCity.getCityCode(),"county");
		}
	}
	//查询所有市的信息，优先数据库，后服务器
	private void queryCities() {
		// TODO Auto-generated method stub
		LogUtil.w("TAG", "into queryCity");
		cityList=coolWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else{
			queryFromService(selectedProvince.getProvinceCode(),"city");
		}
	}
	//根据传入的代号和类型，从服务器上查询省市县信息
	private void queryFromService(final String code,final String type){
		String address;
		//
		LogUtil.d("TAG", "into service ok");
		if(!TextUtils.isEmpty(code)){
			//
			LogUtil.d("TAG", "code not null ok");
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			//
			LogUtil.d("TAG", "code is null ok");
			address="http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		//发送Http请求
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			//服务器响应的数据返回到onFinish
			@Override
			public void onFinish(String response) {
				//
				LogUtil.d("TAG", "into onFinish ok");
				// TODO Auto-generated method stub
				boolean result=false;
				//解析和处理服务器返回数据
				if("province".equals(type)){
					LogUtil.d("TAG", "equal province ok");
					result=Utility.handleProvincesResponse(coolWeatherDB, response);
				}else if("city".equals(type)){
					LogUtil.d("TAG", "equal city ok");
					result=Utility.handleCitiesResponse(coolWeatherDB, response, 
							selectedProvince.getId());
				}else if("county".equals(type)){
					LogUtil.d("TAG", "equal county ok");
					result=Utility.handleCountiesResponse(coolWeatherDB, response, 
							selectedCity.getId());
				}
				if(result){
					//切换到主线程
					LogUtil.d("TAG", "result is true ok");
					runOnUiThread(new Runnable() {
						//重新加载省级数据库，由于涉及UI，所以必须主线程中操作
						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type)){
								LogUtil.d("TAG", "P ok");
								queryProvinces();
							}else if("city".equals(type)){
								LogUtil.d("TAG", "C ok");
								queryCities();
							}else if("county".equals(type)){
								LogUtil.d("TAG", "C2 ok");
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				//
				LogUtil.d("TAG", "into onError ok");
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", 
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("正在加载ing...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	//捕获back按键，判断下一步
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(currentLevel==LEVEL_COUNTY){
			queryCities();
		}else if (currentLevel==LEVEL_CITY) {
			queryProvinces();
		}else{
			if(isFromWeatherActivity){
				Intent intent=new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}
