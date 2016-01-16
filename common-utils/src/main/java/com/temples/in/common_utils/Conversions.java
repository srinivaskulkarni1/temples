package com.temples.in.common_utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Conversions {

	private static Charset charset = Charset.forName("UTF-8");
	private static Logger LOGGER = LoggerFactory.getLogger(Conversions.class);

	public static ByteBuffer str_to_bb(String msg) {
		return ByteBuffer.wrap(msg.getBytes(charset));
	}

	public static String bb_to_str(ByteBuffer buffer) {
		byte[] bytes;
		if (buffer.hasArray()) {
			bytes = buffer.array();
		} else {
			bytes = new byte[buffer.remaining()];
			buffer.get(bytes);
		}
		return new String(bytes, charset);
	}

	public static <T> Object getEntityFromJson(String jsonString, Class<T> clazz) {
		LOGGER.debug("De-serializing Message={}", jsonString);

		Gson gson = new Gson();
		try {
			return gson.fromJson(jsonString, clazz);
		} catch (JsonSyntaxException e) {
			LOGGER.error("Malformed Message={} | Exception Message={}",
					jsonString, e.getLocalizedMessage());
			throw e;
		}
	}

	public static String getJsonFromEntity(Object entity) {
		Gson gson = new Gson();
		return gson.toJson(entity);
	}
}
