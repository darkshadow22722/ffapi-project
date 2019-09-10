[donate]: https://paypal.me/hexile0

[![Donate](https://img.shields.io/badge/Donate-Paypal-blue.svg)][donate]

# McMod Server

## Requirements
You need to have a working Android phone with root or compatible with
the Janus vulnerability to generate SafetyNet tokens.

## TODO
* Active offers
* Caching
* Redis
* Login and Registration system
* Setup guide
* Config file explanation

## Config file explanation
`"SETTING" - (TYPE) DESCRIPTION`

```
"server"
    "name" - (String) This will be displayed in the index file and sent via the "Server" header.
    "version" - (String) This will be displayed in the index file and sent via the "Server" header.
    "debug" - (Boolean) This will print some debug logs and activate Flask debug mode.
    "host" - (String)
    "port" - (Integer)
    "externalUrl" - (String)
"tokenFactory"
    "postKeys" - (List[Strings])
    "getKeys" - (List[Strings])
```

## Donate
If this repository helped you in any way, feel free to donate [here][donate].
