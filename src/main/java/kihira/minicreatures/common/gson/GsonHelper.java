/*
 * Copyright (C) 2014  Kihira
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package kihira.minicreatures.common.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kihira.minicreatures.common.personality.Mood;

/**
 * A helper class for when using {@link com.google.gson.Gson}
 */
public class GsonHelper {

    /**
     * Creates a {@link com.google.gson.Gson} instance that has some common {@link com.google.gson.TypeAdapter} registered
     * @return A Gson instance
     */
    public static Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Mood.class, new SubClassDeserializer<Mood>());

        return gsonBuilder.create();
    }

    /**
     * Converts the Object to a Json String using {@link #createGson()} instance
     *
     * @param object the object
     * @return the string
     */
    public static String toJson(Object object) {
        return createGson().toJson(object);
    }

    /**
     * Returns a new instance of the Type specified from the json provided
     *
     * @param <T>  the type parameter
     * @param json the json
     * @param clazz the class
     * @return the instance
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return createGson().fromJson(json, clazz);
    }
}
