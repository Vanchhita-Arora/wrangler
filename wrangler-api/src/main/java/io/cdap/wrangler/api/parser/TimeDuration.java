package io.cdap.wrangler.api.parser;
import io.cdap.wrangler.api.annotations.PublicEvolving;
import com.google.gson.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

@PublicEvolving
public class TimeDuration implements Token {
    private static final Pattern TIME_PATTERN=Pattern.compile("([0-9]+(?:\\.[0-9+]+)?)\\s*([a-zA-Z]+)");

    private final String value;
    private final long nanoseconds;

    public TimeDuration(String value){
        this.value=value;
        Matcher matcher=TIME_PATTERN.matcher(value.trim());
        if(!matcher.matches()){
            throw new IllegalArgumentException("Invalid Time duration format: "+value);
        }

        double amount=Double.parseDouble(matcher.group(1));
        String unit=matcher.group(2).toLowerCase();

        switch(unit){
            case "ns":
                nanoseconds=(long)amount;
                break;
            case "us":
                nanoseconds=(long)(amount*1_000);
                break;
            case "ms":
                nanoseconds=(long)(amount*1_000_000);
                break;
            case "s":
                nanoseconds=(long)(amount*1_000_000_000);
                break;
            case "m":
                nanoseconds=(long)(amount*60*1_000_000_000L);
                break;
            case "h":
                nanoseconds=(long)(amount*60*60*1_000_000_000L);
                break;
            case "d":
                nanoseconds=(long)(amount*24*60*60*1_000_000_000);
            default:
            throw new IllegalArgumentException("Unsupported time unit: "+unit);
        }
    }

    @Override
    public Object value(){
        return value;
    }

    @Override
    public TokenType type(){
        return TokenType.TIME_DURATION;
    }

    @Override
    public JsonElement toJson(){
        JsonObject json=new JsonObject();
        json.addProperty("type",type().name());
        json.addProperty("value",value);
        json.addProperty("nanoseconds",nanoseconds);
        return json;
    }

    public long getNanoseconds(){
        return nanoseconds;
    }

    public double getMilliseconds(){
        return nanoseconds/1_000_000.0;
    }

    public double getSeconds(){
        return nanoseconds/1_000_000_000.0;
    }

    public double getMinutes(){
        return nanoseconds/(60*1_000_000_000.0);
    }

    public double getHours(){
        return nanoseconds/(60*60*1_000_000_000.0);
    }
}
