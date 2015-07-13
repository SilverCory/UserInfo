package co.ryred.userinfo;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by rissa on 01/07/2015.
 */
public class CooldownUtil
{

	private static CooldownUtil __INSTANCE__ = null;

	private static long cooldownTime = TimeUnit.MINUTES.toMillis( 1 );

	private final ConcurrentHashMap<String, Long> cooldownMap;

	private CooldownUtil()
	{
		this.cooldownMap = new ConcurrentHashMap<String, Long>();
	}

	public synchronized static CooldownUtil getCooldown()
	{
		return __INSTANCE__ == null ? ( __INSTANCE__ = new CooldownUtil() ) : __INSTANCE__;
	}

	;

	public synchronized static void setCooldownTime( TimeUnit timeUnit, long cooldownTime )
	{
		CooldownUtil.cooldownTime = timeUnit.toMillis( cooldownTime );
	}

	public synchronized void chillBabes( String sender )
	{
		long time = System.currentTimeMillis() + cooldownTime;
		cooldownMap.put( sender, time );
	}

	public synchronized boolean isChilling( String sender )
	{

		long time = System.currentTimeMillis();
		long userTime;

		if ( cooldownMap.containsKey( sender ) ) {
			userTime = cooldownMap.get( sender );
		}
		else {
			userTime = time - 300;
		}

		return ( time - userTime ) < 0;

	}

	public synchronized void purge()
	{

		long time = System.currentTimeMillis();

		Iterator<Map.Entry<String, Long>> iterator = cooldownMap.entrySet().iterator();

		while ( iterator.hasNext() ) { if ( ( time - iterator.next().getValue() ) < 0 ) iterator.remove(); }

	}

}
