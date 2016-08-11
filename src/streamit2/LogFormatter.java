package streamit2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
 
    @Override
    public String format(LogRecord record) {
        return  new SimpleDateFormat("kk:mm:ss").format(new Date(record.getMillis())) + " " +
                String.format("%1$-7s",record.getLevel().getName()) + " "
                +record.getSourceMethodName()+" :: "
                +formatMessage(record)+"\n";
    }
 
}