package me.jeffrey.open.utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class ObjectIdSerializer extends JsonSerializer<Object> {
    
    
    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(value==null){
            gen.writeNull();
            return;
        }
        gen.writeString(value.toString());
    }
}