package cp.server.common.netease;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import cp.server.common.Comment;
import cp.server.common.SourceType;
import cp.server.util.FileUtils;

public class NeteaseJsonParser
{
    public static NeteaseJson parse(String json)
    {
        Gson gson = new Gson();

        if (json.substring(json.length() - 1).equals(";"))
        {
            json = json.substring(0, json.length() - 1);
        }

        JsonReader reader = new JsonReader(new StringReader(json.substring(json
                .indexOf("{"))));
        reader.setLenient(true);

        return gson.fromJson(reader, NeteaseJson.class);
    }

    public static List<Comment> toComments(List<NeteaseComment> neteaseComments)
    {
        if (neteaseComments == null)
        {
            return null;
        }

        List<Comment> cmtList = new ArrayList<Comment>(neteaseComments.size());

        for (NeteaseComment nc : neteaseComments)
        {
            Comment cmt = new Comment();
            //comment id = page id + _ + comment id
            cmt.setCmtId(nc.getD() + "_" + nc.getP());
            cmt.setAgainst(0);
            cmt.setAgree(nc.getV());
            cmt.setArea(nc.getF());
            cmt.setContent(nc.getB());
            cmt.setIp(nc.getIp());
            cmt.setNick(nc.getN());
            cmt.setPageId(nc.getD());
            cmt.setTime(Timestamp.valueOf(nc.getT()));
            cmt.setSource(SourceType.NETEASE);
            cmt.setParent(nc.getParaent());
            cmtList.add(cmt);
        }

        return cmtList;
    }

    public static void main(String args[]) throws IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, IOException

    {
        String json = FileUtils.readStringFromFile("json.txt");
        Gson gson = new Gson();

        if (json.substring(json.length() - 1).equals(";"))
        {
            json = json.substring(0, json.length() - 1);
        }

        NeteaseJson n = gson.fromJson(json.substring(json.indexOf("{")),
                NeteaseJson.class);

        // System.out.println(neteaseJson.getResult().getCmntlist());
        //
        // List<NeteaseComment> jsonList =
        // neteaseJson.getResult().getCmntlist();

        // for (NeteaseComment nc : jsonList)
        // {
        // System.out.println("getUid:"+nc.getUid());
        // System.out.println("getStatus:"+nc.getStatus());
        // System.out.println("getVote:"+nc.getVote());
        // System.out.println("getParent:"+nc.getParent());
        // System.out.println("getUsertype:"+nc.getUsertype());
        // System.out.println("getIp:"+nc.getIp());
        // System.out.println("getContent:"+nc.getContent());
        // System.out.println("getTime:"+nc.getTime());
        // System.out.println("getRank:"+nc.getRank());
        // System.out.println("getLevel:"+nc.getLevel());
        // System.out.println("getArea"+nc.getArea());
        // System.out.println("getNick:"+nc.getNick());
        // System.out.println("getAgainst:"+nc.getAgainst());
        // System.out.println("getThread:"+nc.getThread());
        // System.out.println("getLength:"+nc.getLength());
        // System.out.println("getConfig:"+nc.getConfig());
        // System.out.println("getMid:"+nc.getMid());
        // System.out.println("getNewsid:"+nc.getNewsid());
        // System.out.println("getChannel:"+nc.getChannel());
        // System.out.println("getAgree:"+nc.getAgree());
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
    // }
}
