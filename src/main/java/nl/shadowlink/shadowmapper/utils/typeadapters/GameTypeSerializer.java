package nl.shadowlink.shadowmapper.utils.typeadapters;

import com.google.gson.*;
import nl.shadowlink.shadowgtalib.utils.Constants.GameType;
import java.lang.reflect.Type;

/**
 * Takes care of (de)serializing a GameType<br/>
 *
 * @author kilian<br/>
 * @date 12 Apr 2015.
 */
public class GameTypeSerializer implements JsonSerializer<GameType>, JsonDeserializer<GameType> {

	@Override
	public GameType deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		return GameType.valueOf(json.getAsString());
	}

	@Override
	public JsonElement serialize(final GameType pGameType, final Type typeOfSrc, final JsonSerializationContext context) {
		return new JsonPrimitive(pGameType.getGameName());
	}
}
