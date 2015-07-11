package co.ryred.userinfo;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 11/07/2015.
 */
public class UserInfoBungee extends Plugin
{

	@Override
	public void onEnable()
	{

		CooldownUtil.getCooldown();
		getProxy().getScheduler().schedule( this, new Runnable()
		{
			@Override
			public void run()
			{
				CooldownUtil.getCooldown().purge();
			}
		}, 5, 5, TimeUnit.MINUTES );
		getProxy().getPluginManager().registerCommand( this, new UserInfoBungeeCommand( this ) );

	}

}

class UserInfoBungeeCommand extends Command
{


	private final UserInfoBungee plugin;

	public UserInfoBungeeCommand( UserInfoBungee plugin )
	{
		super( "userinfo" );
		this.plugin = plugin;
	}

	public static String colour( String str ) { return ChatColor.translateAlternateColorCodes( '&', str ); }

	@Override
	public void execute( final CommandSender sender, String[] args )
	{


		final Callback<User> cb = new Callback<User>()
		{
			@Override
			public void done( User user, Throwable e )
			{

				if ( e != null ) {
					sender.sendMessage( colour( "&cSomething went wrong whilst getting the user..." ) );
					sender.sendMessage( colour( "&c  " + e.getMessage() ) );
					e.printStackTrace();
				}
				else {

					String username = user.getName();
					UUID uuid = user.getUuid();
					User.NameEntry[] nameHistory = user.getNameHistory();

					TextComponent tc = new TextComponent( colour( "&e=== &d" + username + " &e===" ) );
					tc.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new TextComponent[]{ new TextComponent( colour( "&eUUID: &d" + uuid.toString() ) ) } ) );

					TextComponent tc2 = new TextComponent( colour( "&e Name History" ) );

					TextComponent[] textComponents;

					if ( nameHistory == null || nameHistory.length < 1 ) {
						textComponents = new TextComponent[]{
								tc, tc2, new TextComponent( colour( "&c   Unable to fetch name history.." ) )
						};
					}
					else {

						textComponents = new TextComponent[ nameHistory.length + 2 ];
						textComponents[ 0 ] = tc;
						textComponents[ 1 ] = tc2;

						int i = 1;
						for ( User.NameEntry ne : nameHistory ) {
							i++;

							String changedToString;
							if ( ne.getChangedToAt() < 60 ) {
								changedToString = "&9User's original name.";
							}
							else {

								Date date = new Date();
								date.setTime( ne.getChangedToAt() );

								SimpleDateFormat dateFormatGmt = new SimpleDateFormat( "yyyy-MMM-dd HH:mm:ss" );
								dateFormatGmt.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

								changedToString = "&eChange at &9" + dateFormatGmt.format( date );

							}

							TextComponent historyComponent = new TextComponent( colour( "&b" + ne.getName() ) );
							historyComponent.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new TextComponent[]{
									new TextComponent( colour( changedToString ) )
							} ) );

							textComponents[ i ] = historyComponent;
						}
					}

					for ( TextComponent stc : textComponents ) {
						sender.sendMessage( stc );
					}

				}

			}
		};

		if ( args.length != 1 ) {
			sendUsage( sender );
			return;
		}
		else {

			CooldownUtil cdu = CooldownUtil.getCooldown();
			if ( cdu.isChilling( sender.getName() ) ) {
				sender.sendMessage( colour( "&5Please don't spam that.." ) );
				return;
			}
			cdu.chillBabes( sender.getName() );
			cdu = null;

			final String userName = args[ 0 ];
			plugin.getProxy().getScheduler().runAsync( plugin, new Runnable()
			{
				@Override
				public void run()
				{
					try {
						User user = User.getUser( userName );
						cb.done( user, null );
					} catch ( Exception e ) {
						cb.done( null, e );
					}
				}
			} );
		}

	}

	private void sendUsage( CommandSender sender )
	{
		sender.sendMessage( colour( "&eIncorrect usage of that command.." ) );
		sender.sendMessage( colour( "&eUsage:&a /userinfo <username>" ) );
	}

}
