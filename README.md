<div align="center">
    <a href="https://exaroton.com">
        <img src="https://exaroton.com/panel/img/exaroton.svg" alt="Logo" width="288" height="55">
    </a>
    <h3>Proxy Plugin</h3>
    <p>
        A proxy plugin designed to use exaroton servers in your proxy network.
        <br />
        This plugin automatically updates exaroton servers in your proxy and allows you to manage them.
        <br />
        <br />
        Installing this plugin on proxies that do not run on exaroton but use exaroton servers is also supported.
    </p>
</div>


## About exaroton

<a href="https://exaroton.com" target="_blank">exaroton</a> provides high-end on demand game servers. This is very
useful for proxy networks as you can turn off servers which aren't used right now to save money and start them
automatically when you need them.

## Commands

The Plugin provides the `/exaroton` command with the following subcommands:

| Command  | Arguments              | Description                                                                                                                                              | Permission Node   |
|----------|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------|
| start    | \<server>              | Start a server and automatically add it to the network when it goes online                                                                               | exaroton.start    |
| stop     | \<server>              | Remove a server from the proxy and stop it                                                                                                               | exaroton.stop     |
| restart  | \<server>              | Restart a server                                                                                                                                         | exaroton.restart  |
| add      | \<server>              | Add a server that's already online to your proxy                                                                                                         | exaroton.add      |
| remove   | \<server>              | Remove a server from your proxy                                                                                                                          | exaroton.remove   |
| switch   | \<server> [\<players>] | Start a server (if needed) and transfer the selected players to it. If no players are specified the player who executed the command will be transferred. | exaroton.switch   |

### Permissions

Commands are not available to players by default. You can give players access to the commands by giving them the 
required permission nodes. The `exaroton` permission node is required for all commands.

## Configuration

Since this plugin uses the exaroton API to manage servers, you need to provide an API token, which can be obtained
on https://exaroton.com/account/. You can also configure this plugin to start servers alongside your proxy (e.g. for
your main lobby) and stop servers when the proxy stops.

It's also possible to disable the watch servers feature which automatically removes servers from the proxy when they
go offline.

### Default configuration
```toml
# exaroton API token. You can generate this on https://exaroton.com/account/
apiToken = 'example-token'

# Watch servers in the proxy config and automatically remove them when they go offline
# Note that this only works if you use .exaroton.me addresses in your velocity config.
watch-servers = true

# Automatically start servers when the proxy starts
[auto-start]
enabled = false
servers = ["example.exaroton.me"]

# Automatically stop servers when the proxy stops
[auto-stop]
enabled = false
servers = ["example.exaroton.me"]
```

## Bukkit plugin
The bukkit plugin forwards commands from your backend servers to the proxy using plugin messages. This makes it possible
to use these commands in command blocks or other plugins (e.g. for NPCs).

Since plugin messages are sent using a player connection, at least one player has to be connected to the backend server.
If the plugin is missing from one of your proxies, the player could read the command that would have been executed.

Selectors like `@a`, `@p` and `@r` will automatically be replaced on the backend server. `@a` therefore refers to all
players on the backend server, not all players on the proxy. These selectors are not supported on the proxy directly.


## Using multiple Proxies
Since this plugin does not synchronize the proxy servers with each other, there are some limitations when you try to
use it with multiple proxies. Starting a server using `/exaroton start <server>` only adds the server to the proxy you
are connected to right now. Stopping/Restarting works across proxies, assuming that all proxies have the plugin 
installed as they will automatically remove the server when it is no longer online and will automatically add it back
once it comes back online.

If all servers you intend to use with the proxy are already in your proxy configuration, starting a server should also
work across proxies because the other proxies are already watching the status of this server.

## Contributing
This project is licensed as MIT. Contributions are welcome but if you plan some larger changes please
create an issue for discussion first, to avoid wasting time on something that might not be merged.

### Setting up the Development Environment
1. Clone the repository
2. Import the project in your IDE (IntelliJ IDEA is recommended)

### Modules

| Module     | Description                         | Parent Module |
|------------|-------------------------------------|---------------|
| common     | Code shared between all platforms   |               |
| proxy      | Code shared between proxy platforms | common        |
| bukkit     | Bukkit plugin implementation        | common        |
| bungeecord | BungeeCord plugin implementation    | proxy         |
| velocity   | Velocity plugin implementation      | proxy         |

### Building
To build all modules run `./gradlew buildAll`. Unless specified below all other modules use the `build` task.

Modules with special build task:

| Module     | Task        |
|------------|-------------|
| bukkit     | `shadowJar` |
| bungeecord | `shadowJar` |
| velocity   | `shadowJar` |

### Running in development environments
If you're using IntelliJ IDEA you should already see run configurations for most platforms.
For other platforms or other IDEs run their respective gradle tasks:

| Platform   | Client Task                     | Server Task                           |
|------------|---------------------------------|---------------------------------------|
| Bukkit     |                                 | `./gradlew :bukkit:runServer`         |
| BungeeCord |                                 | `./gradlew :bungeecord:runBungeeCord` |
| Velocity   |                                 | `./gradlew :velocity:runVelocity`     |
