package io.cdap.wrangler.api.parser;
import io.cdap.wrangler.api.annotations.PublicEvolving;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@PublicEvolving
public class ByteSize implements Token {
    private static final Pattern BYTE_PATTERN=Pattern.compile("([0-9]+(?:\\.[0-9]+)?)\\s*([kKmMgG][bB])");

    private final long bytes;
    private final String originalValue;

    public ByteSize(String value){
        this.originalValue=value;
        Matcher matcher=BYTE_PATTERN.matcher(value.trim());
        if(!matcher.matches()){
            throw new IllegalArgumentException("Invalid byte size format: "+value);
        }

        double size=Double.parseDouble(matcher.group(1));
        String unit=matcher.group(2).toUpperCase();

        switch(unit){
            case "KB":
                bytes=(long)(size*1024);
                break;
            case "MB":
                bytes=(long)(size*1024*1024);
                break;
            case "GB":
                bytes=(long)(size*1024*1024*1024);
                break;
            case "decimal":
                bytes=(long)(size*1024*1024);
                break;
            default:
                throw new IllegalArgumentException("Unsupported byte unit: "+unit);
        }
    }

    @Override
    public Object value(){
        return bytes;
    }
    @Override
    public TokenType type(){
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonElement toJson(){
        JsonObject object=new JsonObject();
        object.addProperty("type",type().name());
        object.addProperty("value", originalValue);
        object.addProperty("bytes", bytes);
        return object;
    }

    public long getBytes(){
        return bytes;
    }

    public double getKB(){
        return bytes/1024.0;
    }

    public double getMB(){
        return bytes/(1024.0*1024);
    }

    public double getGB(){
        return bytes/(1024.0*1024*1024);
    }
}
