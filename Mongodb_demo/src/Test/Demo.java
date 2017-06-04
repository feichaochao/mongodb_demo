package Test;

import java.util.ArrayList;
import java.util.List;

import Util.MongodbHelper;
import pojo.Person;

public class Demo {
	
	private final static  String HOST="127.0.0.1";
    private final static  int PORT=27017;
    private final static String DBNAME="test";
	

	
	public static void main(String[] args) {
		
		 String collName="person";
		 MongodbHelper helper=new MongodbHelper(HOST,PORT,DBNAME);
		 
         //创建
//         boolean flag= helper.createColl(collName);
//         System.out.println(flag?"0":"1");
         //新增
         add(collName, helper);
         addList(collName, helper);
         
         //查询
         Person query= new Person();
         query.setName("lmy");
         Person person = helper.getdbobj(collName, query,Person.class);
         System.err.println(person);
         List<Person> list = helper.getdbobjList(collName, query,Person.class);
         for (Person p : list) {
        	 System.out.println(p);
		 }
         
       //删除
       boolean flag=helper.rvdbobj(collName, query);
       System.err.println(flag);

         
	}



	private static void addList(String collName, MongodbHelper helper) {
		Person p = new Person();
 		p.setAge(18);
 		p.setName("youyou");
 		Person p1 = new Person();
 		p1.setAge(12);
 		p1.setName("立刻有");
 		
 		List<Person> plist=new ArrayList<>();
 		plist.add(p);
 		plist.add(p1);
// 		User u = new User();
// 		u.setPersonList(plist);
// 		u.setIds("double you");
        System.err.println(helper.addDbobject(collName, plist));
	}
	
	private static void add(String collName, MongodbHelper helper) {
		Person p = new Person();
 		p.setAge(18);
// 		p.setName("double kill");
		p.setName("lmy");
        System.err.println(helper.addDbobject(collName, p));
	}

}
