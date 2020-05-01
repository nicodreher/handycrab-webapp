package de.dhbw.handycrab.server.rest.serialization;

import com.google.gson.Gson;
import de.dhbw.handycrab.api.utils.Serializer;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * The GsonProvider provides a MessageBodyWriter and a MessageBodyReader to serialize and deserialize all incoming json
 * messages
 *
 * @param <T> the type of the java objects
 *
 * @author Nico Dreher
 * @see javax.ws.rs.ext.MessageBodyReader
 * @see javax.ws.rs.ext.MessageBodyWriter
 */
@Provider
@Dependent
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GsonProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {

    @Resource(lookup = Serializer.LOOKUP)
    private Serializer serializer;

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                      MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        try(InputStreamReader reader = new InputStreamReader(entityStream)) {
            try(BufferedReader br = new BufferedReader(reader)) {
                StringBuilder text = new StringBuilder();
                String line = "";
                while((line = br.readLine()) != null) {
                    text.append(line);
                }
                return serializer.restDeserialize(text.toString(), type);
            }
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws
            WebApplicationException {
        try(PrintWriter writer = new PrintWriter(entityStream)) {
            String json = serializer.restSerialize(t);
            writer.write(json);
            writer.flush();
        }
    }
}