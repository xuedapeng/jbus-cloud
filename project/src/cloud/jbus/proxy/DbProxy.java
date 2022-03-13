package cloud.jbus.proxy;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Sorts.descending;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.common.collect.ImmutableMap;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import cloud.jbus.common.helper.DateHelper;
import cloud.jbus.db.mongo.MongoUtil;
import fw.jbiz.common.conf.ZSystemConfig;

public class DbProxy {

	static Logger log = Logger.getLogger(DbProxy.class);
	
	static String _dbName = MongoUtil._db;
//	static String _collCmd = "cmd";
	static String _collDat = "dat";
//	static String _collSts = "sts";
	static String _collMsglog = "msglog";
	
	final static int MAX_COUNT  = 1000;
	
	public enum TIME_UNIT {
	    MINUTE, TEN_MINUTE, HOUR, DAY, WEEK, MONTH, YEAR;
	}
	
	static Map<TIME_UNIT, String> _unit2FieldMap = new ImmutableMap.Builder<TIME_UNIT, String>()
			.put(TIME_UNIT.MINUTE, "minute")
			.put(TIME_UNIT.TEN_MINUTE, "ten-minute")
			.put(TIME_UNIT.HOUR, "hour")
			.put(TIME_UNIT.DAY, "day")
			.put(TIME_UNIT.WEEK, "week")
			.put(TIME_UNIT.MONTH, "month")
			.put(TIME_UNIT.YEAR, "year")
			.build();
	
	/*
	 * db.getCollection('dat').aggregate([{$match:{"deviceSn":"BJW401", "content.sno":1, "time.second":{$gte:"2018-12-28 09:00:00",$lt:"2018-12-30 00:00:00"}}},{$group:{_id:{time_hour:'$time.hour'}, avgWd:{$avg:'$content.data.wd'},avgSd:{$avg:'$content.data.sd'}}}, {$sort:{"_id":-1}}])
	 */
	public static List<Map<String, Object>> findForAvg(
			String deviceSn, Integer sensorNo, Date from,  Date to, TIME_UNIT unit, Map<String, Map<String, String>> fieldStyle) {
		
		
		MongoCollection<Document> coll = MongoUtil.getCollection(_dbName, _collDat);
		List<Bson> pipeline = Arrays.asList(
				match(gte("time.second", DateHelper.toYmdhms(from))), 
				match(lt("time.second", DateHelper.toYmdhms(to))), 
				match(eq("deviceSn", deviceSn)), 
				match(eq("content.sno", sensorNo)), 
				match(eq("parsed", 1)), 
				group("$time."+_unit2FieldMap.get(unit),
						fieldStyle.entrySet().stream()
							.map(e->avg(e.getKey(), "$content.data."+e.getKey()))
							.collect(Collectors.toList())),
						
						
						
//						Arrays.asList(avg("avgWd", "$content.data.wd"), avg("avgSd", "$content.data.sd"))),
				sort(descending("_id")));
		
		AggregateIterable<Document> iterable = coll.aggregate(pipeline);

		final List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		iterable.forEach(new Block<Document>() {
            public void apply(final Document document) {
            	Map<String, Object> map = new HashMap<String, Object>();
            	map.put("time", document.get("_id"));
            	fieldStyle.forEach((K,V)->{
            		String fieldName = K;
            		Double fieldValue = document.getDouble(fieldName);
            		
            		map.put(fieldName, fieldValue==null?0:new DecimalFormat(V.get("format")).format(fieldValue));
            	});
            	
            	retList.add(map);
            }
        });
		
		Collections.reverse(retList);
		return retList;
		
		
	}


	/*
	 * db.getCollection('dat').find({"deviceSn":"BJW401", "content.sno":1, "time.second":{$lt:"2019-04-12 12:00:00"}}).sort({"_id":-1}).limit(10)
	 */
	public static List<Map<String, Object>> findForTimeline(
			String deviceSn, Integer sensorNo, Date from,  Integer direction, Integer pageSize, Map<String, Map<String, String>> fieldStyle) {
		
		
		MongoCollection<Document> coll = MongoUtil.getCollection(_dbName, _collDat);
		
		Bson filterTime = null;
		if (from != null) {
			filterTime = (direction < 0
						? lt("time.second", DateHelper.toYmdhms(from))
						:gt("time.second", DateHelper.toYmdhms(from)));
		}
		Bson filter = (filterTime==null
						? and(eq("deviceSn", deviceSn), eq("content.sno", sensorNo))
						: and(eq("deviceSn", deviceSn), eq("content.sno", sensorNo), filterTime));
	    //指定查询过滤器查询
	    FindIterable<Document> iterable = coll.find(filter).sort(new Document("_id",direction)).limit(pageSize);

		final List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		iterable.forEach(new Block<Document>() {
            public void apply(final Document document) {
            	Map<String, Object> map = new HashMap<String, Object>();
            	map.put("time", DateHelper.toYmdhms(document.getObjectId("_id").getDate()));
            	fieldStyle.forEach((K,V)->{
            		String fieldName = K;
            		Double dv = getDouble(document, "content.data." + fieldName);
            		String sv = dv==null?"-":new DecimalFormat(V.get("format")).format(dv);
            		map.put(fieldName, sv);
            	});
            	
            	retList.add(map);
            }
        });
		
		return retList;
	}
	
	public static List<Map<String, Object>> findMsgLogForPeriod(String deviceSn, Date fromTime, Date toTime, Integer limit) {

		MongoCollection<Document> coll = MongoUtil.getCollection(_dbName, _collMsglog);

		Bson filterTime = (and(gt("time", fromTime), lt("time", toTime)));
		
		Bson filter = (and(eq("deviceSn", deviceSn), filterTime));
		
	    //指定查询过滤器查询
	    FindIterable<Document> iterable = coll.find(filter).sort(new Document("_id",-1)).limit(limit);

		final List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		iterable.forEach(new Block<Document>() {
            public void apply(final Document document) {
            	Map<String, Object> map = new HashMap<String, Object>();
            	map.put("deviceSn", document.get("deviceSn"));
            	map.put("time", DateHelper.toYmdhmsMs((Date) document.get("time")));
            	map.put("type", document.get("type"));
            	map.put("msg", document.get("msg"));
            	
            	retList.add(map);
            }
        });
		
		return retList;
	}
	
	public static Double getDouble(Document doc, String key) {
		
		String[] keys = key.split("\\.");
		for(int i=0; i<keys.length; i++) {
			
			if (!doc.containsKey(keys[i])) {
				return null;
			}
			
			if (i == keys.length-1) {
				try {
					return Double.valueOf(String.valueOf(doc.get(keys[i])));
				} catch (Exception e) {
					log.error("", e);
					return null;
				}
			}
			
			doc = (Document)doc.get(keys[i]);
			
		}
		
		return null;
	}
}
