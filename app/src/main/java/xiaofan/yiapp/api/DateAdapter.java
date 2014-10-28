package xiaofan.yiapp.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaoyu on 2014/10/21.
 */
public class DateAdapter implements JsonSerializer<Date>,JsonDeserializer<Date>{
    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd\'T\'hh:ss:mm.SSS\'Z\'");
        Date date = null;
        try {
            date = sf.parse(jsonElement.getAsString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd\'T\'hh:ss:mm.SSS\'Z\'");
        return new JsonPrimitive(sf.format(date));
    }
}
