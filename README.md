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

| Command  | Arguments           | Description                                                                | Permission Node   |
|----------|---------------------|----------------------------------------------------------------------------|-------------------|
| start    | \<server>           | Start a server and automatically add it to the network when it goes online | exaroton.start    |
| stop     | \<server>           | Remove a server from the proxy and stop it                                 | exaroton.stop     |
| restart  | \<server>           | Restart a server                                                           | exaroton.restart  |
| add      | \<server>           | Add a server that's already online to your proxy                           | exaroton.add      |
| remove   | \<server>           | Remove a server from your proxy                                            | exaroton.remove   |
| switch   | \<server>           | Start a server (if needed) and transfer the executing player to it         | exaroton.switch   |
| transfer | \<player> \<server> | Start a server (if needed) and transfer the specified player to it         | exaroton.transfer |


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

## Contributing
This project is licensed as MIT. Contributions are welcome but if you plan some larger changes please
create an issue for discussion first, to avoid wasting time on something that might not be merged.

### Setting up the Development Environment
1. Clone the repository
2. Import the project in your IDE (IntelliJ IDEA is recommended)

### Modules

| Module     | Description                                                                    | Parent Module |
|------------|--------------------------------------------------------------------------------|---------------|
| common     | Code shared between all platforms                                              |               |
| adventure  | Code shared between all platforms that use the adventure component library     | common        |
| bukkit     | Bukkit plugin implementation                                                   | adventure     |
| bungeecord | BungeeCord plugin implementation                                               | adventure     |
| velocity   | Velocity plugin implementation                                                 | adventure     |

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
