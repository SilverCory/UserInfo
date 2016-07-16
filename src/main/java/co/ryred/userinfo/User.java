package co.ryred.userinfo;

import com.google.gson.Gson;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 11/07/2015.
 */
@Data
@AllArgsConstructor
public class User
{

	public static final Gson GSON = new Gson();

	private final UUID uuid;
	private final String name;
	private final NameEntry[] nameHistory;

	public static User getUser( UUID uuid ) throws Exception
	{

		// Name.
		String nameHisStr = getContents( new URL( "https://api.minepay.net/mojang/v1/profile/" + uuid.toString() + "/history" ) );
		NameEntry[] names = GSON.fromJson( nameHisStr, NameEntry[].class );
		return new User( uuid, names[ names.length - 1 ].getName(), names );

	}

	public static User getUser( String name ) throws Exception
	{

		// UUID.
		String uuidStr = getContents( new URL( "https://api.minepay.net/mojang/v1/name/" + name ) );
		return getUser( GSON.fromJson( uuidStr, NameUUIDPair.class ).getUUID() );

	}

	public static String getContents( URL url ) throws Exception {
		HttpURLConnection urlCon = ( (HttpURLConnection) url.openConnection() );
		urlCon.setRequestProperty( "user-agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36" );
		urlCon.connect();
		if( urlCon.getResponseCode() != 200 ) {
			throw new Exception( "User isn't premium" );
		}

		return new Scanner( urlCon.getInputStream(), "UTF-8" ).useDelimiter( "\\A" ).next();
	}

	public static UUID getUUID( String uuid ) throws Exception
	{
		return UUID.fromString( uuid.substring( 0, 8 ) + "-" + uuid.substring( 8, 12 ) + "-" + uuid.substring( 12, 16 ) + "-" + uuid.substring( 16, 20 ) + "-" + uuid.substring( 20, 32 ) );
	}

	@Data
	@Setter( AccessLevel.PRIVATE )
	public static class NameEntry
	{

		private String name;
		private long changedToAt;

	}

	@Data
	@Setter( AccessLevel.PRIVATE )
	private static class NameUUIDPair {

		private String name;
		private String id;

		public UUID getUUID() {
			try {
				return User.getUUID( id.replace( "-", "" ) );
			} catch ( Exception e ) {
				return null;
			}
		}

	}
}
