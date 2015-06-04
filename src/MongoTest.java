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
			// ʵ����Mongo��������27017�˿�
			MongoClient mongoClient = new MongoClient("localhost", 27017);
			// ������Ϊyourdb�����ݿ⣬�������ݿⲻ���ڵĻ���mongodb���Զ�����
			List<String> dbNames = mongoClient.getDatabaseNames();
			for (String name : dbNames)
				System.out.println(name);
			 MongoDatabase db = mongoClient.getDatabase("test");
			 // Get collection from MongoDB, database named "yourDB"
			 //��Mongodb�л����ΪyourColleection�����ݼ��ϣ���������ݼ��ϲ����ڣ�Mongodb��Ϊ���½���
			 MongoCollection collection = db.getCollection("test");
			 // ʹ��BasicDBObject���󴴽�һ��mongodb��document,�����踳ֵ��
			 Document document = new Document();
			 document.put("id", 1001);
			 document.put("msg", "hello world mongoDB in Java");
			 //���½�����document���浽collection��ȥ
			 //collection.insertOne(document);
			 // ����Ҫ��ѯ��document
			 BasicDBObject searchQuery = new BasicDBObject();
			 searchQuery.put("a", "test");
			 // ʹ��collection��find��������document
			 FindIterable it = collection.find(searchQuery);
			 MongoCursor cursor = it.iterator() ;
			 //ѭ��������
			 while (cursor.hasNext()) {
			 System.out.println(cursor.next());
			 }
			 System.out.println("Done");
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
}