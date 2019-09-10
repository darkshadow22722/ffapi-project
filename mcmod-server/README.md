[donate]: https://paypal.me/hexile0

[![Donate](https://img.shields.io/badge/Donate-Paypal-blue.svg)][donate]

# McMod Server

## Requirements
You need to have a working Android phone with root or compatible with
the Janus vulnerability to generate SafetyNet tokens.

## Supported countries
There are 50 supported countries!
* Austria
* Azerbaijan
* Bahrain
* Belarus
* Belgium
* Bosnia and Herzegovina
* Bulgaria
* Croatia
* Czech Republic
* Denmark
* Egypt
* El Salvador
* Estonia
* Finland
* Georgia
* Greece
* Guatemala
* Honduras
* Hungary
* India
* Indonesia
* Italy
* Jordan
* Kuwait
* Latvia
* Lebanon
* Lithuania
* Luxemburg
* Malaysia
* Malta
* Morocco
* Netherlands
* Norway
* Oman
* Pakistan
* Paraguay
* Poland
* Qatar
* Romania
* Saudi Arabia
* Serbia
* Singapore
* Slovakia
* Slovenia
* South Africa
* South Korea
* Sweden
* Switzerland
* Turkey
* United Arab Emirates
* Vietnam

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
