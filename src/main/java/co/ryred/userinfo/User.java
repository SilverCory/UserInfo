package co.ryred.userinfo;

import com.google.gson.Gson;
import lombok.Getter;

import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 11/07/2015.
 */
public class User
{

	public static final Gson GSON = new Gson();
	@Getter
	private final NameEntry[] nameHistory;
	@Getter
	private final String name;
	@Getter
	private final UUID uuid;

	public User( UUID uuid, String name, NameEntry[] nameHistory )
	{
		this.uuid = uuid;
		this.name = name;
		this.nameHistory = nameHistory;
	}

	public static User getUser( UUID uuid ) throws Exception
	{

		// Name.
		URL nameHisGet = new URL( "http://api.mcusername.net/pastuuid/" + uuid.toString().replace( "-", "" ) );
		String nameHisStr = new Scanner( nameHisGet.openStream(), "UTF-8" ).useDelimiter( "\\A" ).next();

		// Name History.
		if ( nameHisStr.toLowerCase().contains( "not premium" ) ) throw new Exception( "User isn't premium" );
		NameEntry[] names = GSON.fromJson( nameHisStr, NameEntry[].class );

		return new User( uuid, names[ names.length - 1 ].getName(), names );

	}

	public static User getUser( String name ) throws Exception
	{

		// UUID.
		URL uuidGet = new URL( "http://api.mcusername.net/playertouuid/" + name );
		String uuidStr = new Scanner( uuidGet.openStream(), "UTF-8" ).useDelimiter( "\\A" ).next();

		if ( uuidStr.toLowerCase().contains( "not premium" ) ) throw new Exception( "User isn't premium" );
		UUID uuid = getUUID( uuidStr );

		// Name History.
		URL nameHisGet = new URL( "http://api.mcusername.net/pastuser/" + name );
		String nameHisStr = new Scanner( nameHisGet.openStream(), "UTF-8" ).useDelimiter( "\\A" ).next();

		if ( nameHisStr.toLowerCase().contains( "not premium" ) ) throw new Exception( "User isn't premium" );
		NameEntry[] names = GSON.fromJson( nameHisStr, NameEntry[].class );

		// Return user.
		return new User( uuid, name, names );

	}

	public static UUID getUUID( String uuid ) throws Exception
	{
		return UUID.fromString( uuid.substring( 0, 8 ) + "-" + uuid.substring( 8, 12 ) + "-" + uuid.substring( 12, 16 ) + "-" + uuid.substring( 16, 20 ) + "-" + uuid.substring( 20, 32 ) );
	}

	public static class NameEntry
	{

		@Getter
		private String name;

		@Getter
		private long changedToAt;

	}

}
