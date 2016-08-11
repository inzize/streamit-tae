package comparetrace;

import java.util.*;
import java.io.*;
import java.util.regex.*;


public class CompareTrace 
{
    static void Main(String[] args) throws IOException
    {
        File root = new File("/home/coreva/coreva_dev/qut-stream-ubi-mpsoc/QUTtests/generated_src/");
        for (File dir : root.listFiles())
            if (dir.isDirectory())
                Test(dir);
    }

    static void Test(File dir) throws IOException
    {
        System.out.println(dir);
        Map<Integer, Filter> QUTS = new LinkedHashMap<>();
        Map<Integer, Filter> QUTP = new LinkedHashMap<>();
        Map<Integer, Filter> MIT = new LinkedHashMap<>();

        Process(MIT, dir + "/java/MIT.trace");
        Process(QUTS, dir + "/c/QUTtrace.txt");

        File coreva = new File(dir.getAbsolutePath() + "/coreva");

        for (String filename : coreva.list())
            if (filename.startsWith("QUTtrace_core"))
            Process(QUTP, filename);

        Compare("MIT", MIT, "QUTS", QUTS);
        //Compare("QUTS", QUTS, "QUTP", QUTP);
        //Compare("MIT", MIT, "QUTP", QUTP);
    }

    static void Compare(String aname, Map<Integer, Filter> a, String bname, Map<Integer, Filter> b)
    {
        if (a.size() < b.size())
        {
            for (Integer k : b.keySet())
                if (!a.containsKey(k))
                {
                    System.out.println(bname + " produces trace for filter " + k + ", " + aname + " does not");
                    return;
                }
        }
        else if (b.size() < a.size())
        {
            for (Integer k : a.keySet())
                if (!b.containsKey(k))
                {
                     System.out.println(aname + " produces trace for filter " + k + ", " + bname + " does not");

                    return;
                }
        }
        else // a.Count == b.Count
        {
            for (Integer filter : a.keySet())
            {
                Filter f1 = a.get(filter);
                Filter f2 = b.get(filter);

                if (!Same(f1.prework, f2.prework))
                    System.out.println("  " + aname + " vs " +  bname + ", prework differs");

                for (int j = 0; j < f1.work.size() && j < f2.work.size(); j++)
                {
                    if (!Same(f1.work.get(j), f2.work.get(j)))
                    {
                         System.out.println("  Filter " + filter + ":");
                        for (int k = 0; k < j; k++)
                            Print("", f1.work.get(k));
                        Print(aname + ": ", f1.work.get(j));
                        Print(bname + ": ", f2.work.get(j));
                        return;
                    }
                }
            }
        }
    }

    static void Print(String label, Work w)
    {
        System.out.print("    " + label + "Pop(");
        for (Object p : w.pop)
            System.out.print(p + ",");
        System.out.print(") Push(");
        for (Object p : w.push)
            System.out.print(p + ",");
         System.out.println(")");
    }

    static boolean Same(Work w1, Work w2)
    {
        return (w1 == null && w2 == null) || 
               (w1 != null && w2 != null && Same(w1.push, w2.push) && Same(w1.pop, w2.pop));
    }

    static boolean Same(List<Object> l1, List<Object> l2)
    {
        if (l1.size() != l2.size())
            return false;
        for (int i = 0; i < l1.size(); i++)
            if (!l1.get(i).toString().equals(l2.get(i).toString()))
                return false;
        return true;
    }

    static void Process(Map<Integer, Filter> filters, String filename) throws IOException
    {
        if (!new File(filename).exists())
            return;

        BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));

        Pattern pattern = Pattern.compile("(\\d+).(\\w+)\\(([0-9a-f]*)\\)");

        String line;
        while ((line = file.readLine()) != null)
        {
            Matcher match = pattern.matcher(line);
            if (!match.matches())
                break;
            String num = match.group(1);
            int filter_number = Integer.parseInt(num);

            if (!filters.containsKey(filter_number))
                filters.put(filter_number, new Filter());

            Filter filter = filters.get(filter_number);

            String proc = match.group(2);
            String arg = match.group(3);

            switch (proc)
            {
                case "prework":
                    filter.current = new Work();
                    filter.prework = filter.current;
                    break;
                case "work":
                    filter.current = new Work();
                    filter.work.add(filter.current);
                    break;
                case "push":
                    filter.current.push.add(arg);
                    break;
                case "pop":
                    filter.current.pop.add(arg);
                    break;
            }
        }

        for (Filter f : filters.values())
            f.work.remove(f.current);

        file.close();
    }
}