import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * Java + MongoDB Hello world Example
 * 
 */
public class MongoTest {
	public static void main(String[] args) {
		try {
			// 实例化Mongo对象，连接27017端口
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			// 连接名为yourdb的数据库，假如数据库不存在的话，mongodb会自动建立
			List<String> dbNames = mongoClient.getDatabaseNames();
			for (String name : dbNames)
				System.out.println(name);
			 MongoDatabase db = mongoClient.getDatabase("test");
			 // Get collection from MongoDB, database named "yourDB"
			 //从Mongodb中获得名为yourColleection的数据集合，如果该数据集合不存在，Mongodb会为其新建立
			 MongoCollection collection = db.getCollection("test");
			 // 使用BasicDBObject对象创建一个mongodb的document,并给予赋值。
			 Document document = new Document();
			 document.put("id", 1001);
			 document.put("msg", "hello world mongoDB in Java");
			 //将新建立的document保存到collection中去
			 //collection.insertOne(document);
			 // 创建要查询的document
			 BasicDBObject searchQuery = new BasicDBObject();
			 searchQuery.put("a", "test");
			 // 使用collection的find方法查找document
			 FindIterable it = collection.find(searchQuery);
			 MongoCursor cursor = it.iterator() ;
			 //循环输出结果
			 while (cursor.hasNext()) {
			 System.out.println(cursor.next());
			 }
			 System.out.println("Done");
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
}