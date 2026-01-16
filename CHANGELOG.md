# v2.0.6

- Fix caching of server list

---

# v2.0.5

- Fix version number

---

# v2.0.4

- Fix retries if the initial websocket connection fails

--- 

# v2.0.3

- Fix Stop command
- List 1.21.8 support

---

# v2.0.2

- List 1.21.5, .6 and .7 support

---

# v2.0.1

- The bukkit plugin no longer refuses to load if velocities modern forwarding is used
- Proxy names are now also suggested for server arguments

---

# v2.0.0

---

## Breaking Changes

- This plugin now requires Java 11 or newer
- All platforms now use a TOML configuration file and the names of some options have changed. Old configs are migrated
  automatically.
- The API provided for other plugins has been removed. If you were using this API, please contact us with feedback on
  how you were using it so we can work on a replacement.

---

## Additions

- Added a Bukkit integration that forwards commands using plugin messages. This allows using exaroton commands in
  command blocks and NPCs. Check the README for more details.
- Requests are now asynchronous, which should improve performance
