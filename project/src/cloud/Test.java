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
		
		DbProxy.findForTimeline("BJW401", 1, null, -1, 20, null);
		
	}
}
