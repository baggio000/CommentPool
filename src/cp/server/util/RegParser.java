package cp.server.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegParser
{
    public static Set<String> parseReg(String source, String reg)
    {
        Set<String> set = new HashSet<String>();
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(source);

        while (matcher.find())
        {
            set.add(new String(matcher.group()));
        }

        matcher = null;
        pattern = null;

        return set;
    }

    public static String findOne(String source, String reg)
    {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(source);

        if (matcher.find())
        {
            return new String(matcher.group());
        }

        return null;
    }

    public static void main(String args[])
    {
        String url = "<meta name=keywords content=\"单节10分！三战令人刮目 浪子涅槃比斯利兑现承诺,比斯利,热火,浪子\">";
        
        String pageKeywordsReg = "<meta name=[\"]?keywords[\"]? content=\"[^\"]+\"";
        
        String set = RegParser.findOne(url, pageKeywordsReg);

        System.out.println(set);

    }
}
