package cp.server.common.sina;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import cp.server.common.Comment;
import cp.server.common.SourceType;
import cp.server.util.FileUtils;

public class SinaJsonParser
{
    public static SinaJson parse(String json)
    {
        Gson gson = new Gson();
        return gson.fromJson(json.substring(json.indexOf("{")), SinaJson.class);
    }

    public static List<Comment> toComments(List<SinaComment> sinaComments)
    {
        if (sinaComments == null)
        {
            return null;
        }
        
        List<Comment> cmtList = new ArrayList<Comment>(sinaComments.size());

        for (SinaComment sc : sinaComments)
        {
            Comment cmt = new Comment();
            cmt.setCmtId(sc.getMid());
            cmt.setAgainst(sc.getAgainst());
            cmt.setAgree(sc.getAgree());
            cmt.setArea(sc.getArea());
            cmt.setContent(sc.getContent());
            cmt.setIp(sc.getIp());
            cmt.setNick(sc.getNick());
            cmt.setPageId(sc.getNewsid());
            cmt.setTime(Timestamp.valueOf(sc.getTime()));
            cmt.setSource(SourceType.SINA);
            cmt.setParent(sc.getParent());
            cmtList.add(cmt);
        }

        return cmtList;
    }

    public static void main(String args[]) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, IOException

    {
        String json = FileUtils.readStringFromFile("json.txt");
        Gson gson = new Gson();

        SinaJson sinaJson = gson.fromJson(json.substring(json.indexOf("{")),
                SinaJson.class);
        System.out.println(sinaJson.getResult().getCmntlist());

        List<SinaComment> jsonList = sinaJson.getResult().getCmntlist();

        for (SinaComment sc : jsonList)
        {
            System.out.println("getUid:"+sc.getUid());
            System.out.println("getStatus:"+sc.getStatus());
            System.out.println("getVote:"+sc.getVote());
            System.out.println("getParent:"+sc.getParent());
            System.out.println("getUsertype:"+sc.getUsertype());
            System.out.println("getIp:"+sc.getIp());
            System.out.println("getContent:"+sc.getContent());
            System.out.println("getTime:"+sc.getTime());
            System.out.println("getRank:"+sc.getRank());
            System.out.println("getLevel:"+sc.getLevel());
            System.out.println("getArea"+sc.getArea());
            System.out.println("getNick:"+sc.getNick());
            System.out.println("getAgainst:"+sc.getAgainst());
            System.out.println("getThread:"+sc.getThread());
            System.out.println("getLength:"+sc.getLength());
            System.out.println("getConfig:"+sc.getConfig());
            System.out.println("getMid:"+sc.getMid());
            System.out.println("getNewsid:"+sc.getNewsid());
            System.out.println("getChannel:"+sc.getChannel());
            System.out.println("getAgree:"+sc.getAgree());
        }

        // for (int i = 0; i < jsonArray.length(); i++)
        // {
        // JSONObject jsonObj = jsonArray.getJSONObject(i);
        // System.out.println(jsonObj.get("uid"));
        // System.out.println(jsonObj.get("status"));
        // System.out.println(jsonObj.get("vote"));
        // System.out.println(jsonObj.get("parent"));
        // System.out.println(jsonObj.get("usertype"));
        // System.out.println(jsonObj.get("ip"));
        // System.out.println(jsonObj.get("content"));
        // System.out.println(jsonObj.get("time"));
        // System.out.println(jsonObj.get("rank"));
        // System.out.println(jsonObj.get("level"));
        // System.out.println(jsonObj.get("area"));
        // System.out.println(jsonObj.get("nick"));
        // System.out.println(jsonObj.get("against"));
        // System.out.println(jsonObj.get("thread"));
        // System.out.println(jsonObj.get("length"));
        // System.out.println(jsonObj.get("config"));
        // System.out.println(jsonObj.get("mid"));
        // System.out.println(jsonObj.get("newsid"));
        // System.out.println(jsonObj.get("channel"));
        // System.out.println(jsonObj.get("agree"));
        // }

        // System.out.println(jsonObject);
        // Object bean = JSONObject.toBean( jsonObject );
        // assertEquals( jsonObject.get( "name" ), PropertyUtils.getProperty(
        // bean, "name" ) );
        // assertEquals( jsonObject.get( "bool" ), PropertyUtils.getProperty(
        // bean, "bool" ) );
        // assertEquals( jsonObject.get( "int" ), PropertyUtils.getProperty(
        // bean, "int" ) );
        // assertEquals( jsonObject.get( "double" ), PropertyUtils.getProperty(
        // bean, "double" ) );
        // assertEquals( jsonObject.get( "func" ), PropertyUtils.getProperty(
        // bean, "func" ) );
        // System.out.println(PropertyUtils.getProperty(bean, "result"));
        // System.out.println(PropertyUtils.getProperty(bean,
        // "52200A90-6FC1D54E-53DFB3D5-8A0-809"));
        // System.out.println(PropertyUtils.getProperty(bean, "int"));
        // System.out.println(PropertyUtils.getProperty(bean, "double"));
        // System.out.println(PropertyUtils.getProperty(bean, "func"));
        // System.out.println(PropertyUtils.getProperty(bean, "array"));

        // List arrayList =
        // (List)JSONArray.toCollection(jsonObject.getJSONArray("array"));
        // for(Object object : arrayList){
        // System.out.println(object);
        // }
    }
}
