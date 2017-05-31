package Util;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.util.JSON;

public class MongodbHelper {
	private Logger logger = LoggerFactory.getLogger(MongodbHelper.class);
	//mongo
	private  Mongo mo=null;
	//DB
	private  DB db=null;
	
	
    private  String dbname=null;
    private  String host=null;
    private  int port=27017;
    
	public MongodbHelper(){}
	
	public MongodbHelper(String host,int port,String dbName){
		
		this.dbname=dbName;
		this.host=host;
		this.port=port;
		createInstence(host, port);
		
	}

	/**
	 * 得到Mongo
	 * @param host
	 * @param port
	 * @return
	 */
	public synchronized Mongo createInstence(String host,int port){
		
		if(mo==null){
			try {
				mo=new Mongo(host, port);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MongoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mo;
	}
	
	/*
	 * 连接数据库
	 */
	private synchronized DB connDb(){
		
		if(mo==null){
			logger.error("Mongo 为空");
			return null;
		}else{
			if(db==null && dbname!=null){
				db=mo.getDB(this.dbname);
			}
		}
		return db;
	}
	
	
    /**
     *  创建collName
     * @param collName
     * @return
     */
	public  boolean createColl(String collName){
		
	    boolean flag=false;
	    if(db==null){
	    	connDb();
	    }
		if(db.collectionExists(collName)){
			logger.error("已经存在");
			return flag;
		}else{
			try{
		       db.createCollection(collName, null);
		       flag=true;
			}catch(Exception e){
				logger.error("创建数据库失败",e);
				return flag;
			}finally{
				closeMongo();
			}
		
		}
		return flag;
	}
	
	
    /**
     * 得到DBCollection
     * @param collName
     * @return
     */
	
	public DBCollection getColl(String collName){
		if(mo==null){
			createInstence(host, port);
		}
	    if(db==null){
	    	connDb();
	    }
		DBCollection dbColl=db.getCollection(collName);
		return dbColl;
	}
	
	/**
	 * 增加单个
	 * @param collName
	 * @param bean
	 * @return
	 */
	public boolean addDbobject(String collName,Object bean){
		Gson gson = new Gson();
		DBObject obj = (DBObject) JSON.parse(gson.toJson(bean));
		DBCollection coll=getColl(collName);
		try{
		   coll.insert(obj,WriteConcern.NONE);
		   return true;
		}catch (Exception e) {
			return false;
		}finally{
			this.closeMongo();
		}
	
	}
	
	/**
	 * 增加多个
	 * @param collName
	 * @param objlist
	 * @return
	 */
	public <T> boolean addDbobject(String collName,List<T> objlist){
		List<DBObject> list=new ArrayList<>();
		Gson gson = new Gson();
		for (T t : objlist) {
			DBObject obj = (DBObject) JSON.parse(gson.toJson(t));
			list.add(obj);
		}
		DBCollection coll=getColl(collName);
		try{
		   coll.insert(list);
		   return true;
		}catch (Exception e) {
			return false;
		}finally{
			this.closeMongo();
		}
	
	}
	

	/**
	 * 查询全部
	 * @param collName
	 * @param bean
	 * @return
	 */
	public <T> List<T> getlist(String collName,Class<T> bean){
		List<T> list=new ArrayList<>();
		DBCollection coll=getColl(collName);
		try{
			Gson gson = new Gson();
			DBCursor cursor=coll.find();
		    while(cursor.hasNext()){
		    	list.add(gson.fromJson(cursor.next().toString(),bean));
		    }
			return list;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("查询失败!",e);
			return null;
		}finally{
			this.closeMongo();
		}
	}
	
	/**
	 * 查询单个
	 * @param collName
	 * @param queryBean
	 * @param bean
	 * @return
	 */
	public <T> T getdbobj(String collName,T queryBean,Class<T> bean){
		DBCollection coll=getColl(collName);
		try{
			Gson gson = new Gson();
			DBObject bobj = (DBObject) JSON.parse(gson.toJson(queryBean));
		    DBObject ob=coll.findOne(bobj);
			return gson.fromJson(ob.toString(),bean);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("查询失败!",e);
			return null;
		}finally{
			this.closeMongo();
		}
	}
	
	/**
	 * 查询多个
	 * @param collName
	 * @param queryBean
	 * @param bean
	 * @return
	 */
	public <T> List<T> getdbobjList(String collName,T queryBean,Class<T> bean){
		List<T> list=new ArrayList<>();
		DBCollection coll=getColl(collName);
		try{
			Gson gson = new Gson();
			DBObject bobj = (DBObject) JSON.parse(gson.toJson(queryBean));
			DBCursor cursor=coll.find(bobj);
		    while(cursor.hasNext()){
		    	list.add(gson.fromJson(cursor.next().toString(),bean));
		    }
			return list;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("查询失败!",e);
			return null;
		}finally{
			this.closeMongo();
		}
	}
    
	/**
	 * 删除
	 * @param collName
	 * @param bean
	 * @return
	 */
	public <T> boolean rvdbobj(String collName,T bean){
		
		DBCollection coll=getColl(collName);
		try{
			Gson gson = new Gson();
			DBObject bobj = (DBObject) JSON.parse(gson.toJson(bean));
			coll.remove(bobj);
			return true;
		}catch (Exception e) {
			logger.error("删除失败!",e);
			return false;
		}finally{
			this.closeMongo();
		}
	}
	
	/**
	 * 更新
	 * @param collName
	 * @param queryBean
	 * @param bean
	 * @return
	 */
	public <T> boolean upbobj(String collName,T queryBean,T bean){
		DBCollection coll=getColl(collName);
		
		try{
			Gson gson = new Gson();
			DBObject update = (DBObject) JSON.parse(gson.toJson(bean));
			DBObject query = (DBObject) JSON.parse(gson.toJson(queryBean));
			coll.update(query, update);
		    return true;
		}catch (Exception e) {
			logger.error("更新失败!",e);
			return false;
		}finally{
			this.closeMongo();
		}
		
	}
	
	
	
	/**
	 * 关闭
	 */
	public  void closeMongo(){
		 mo.close();
	}
	
}
