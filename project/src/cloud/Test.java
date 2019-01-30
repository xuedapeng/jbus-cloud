package cloud;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import cloud.jbus.common.helper.DateHelper;
import cloud.jbus.common.helper.GuidHelper;
import cloud.jbus.common.helper.JsonHelper;
import cloud.jbus.db.mongo.MongoUtil;
import cloud.jbus.proxy.DbProxy;

public class Test {

	public static void main(String[] args) {
		
		String json = "{\"a\":1.00, \"b\":[2.30,3], \"c\":\"0\"}";
		Map<String, Object> map = JsonHelper.json2map(json);
		String p = JsonHelper.map2json(map);
		System.out.println(json);
		System.out.println(p);
		
	}
}
