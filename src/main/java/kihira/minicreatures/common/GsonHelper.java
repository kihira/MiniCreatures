package kihira.minicreatures.common;

import com.google.gson.*;
import kihira.minicreatures.MiniCreatures;
import kihira.minicreatures.common.personality.Mood;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

public class GsonHelper {
    private static Gson instance;

    public static Gson getInstance() {
        if (instance == null) {
            instance = new GsonBuilder()
                    .registerTypeAdapter(Mood.class, new SubClassDeserializer<Mood>())
                    .create();
        }
        return instance;
    }

    public static <T> String toJson(T obj) {
        return getInstance().toJson(obj);
    }

    @Nullable
    public static <T> T fromJson(String json, Class<T> clazz) {
        return getInstance().fromJson(json, clazz);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private static final class SubClassDeserializer<T> implements JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String className = jsonObject.get("clazz").getAsString();
            try {
                Class<T> clazz = (Class<T>) Class.forName(className);
                return new Gson().fromJson(jsonObject.toString(), clazz);
            }
            catch (ClassNotFoundException | ClassCastException e) {
                MiniCreatures.logger.error("Failed to deserialize " + jsonObject.toString(), new JsonParseException(e.getMessage()));
            }
            return null;
        }
    }
}
