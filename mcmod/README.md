[![Donate](https://img.shields.io/badge/Donate-Paypal-blue.svg)][donate]

# McMod

## Requirements
You need to have the McDonald's app APK file. You can download it in three ways:

1. Using the [Yalp Store](https://github.com/yeriomin/YalpStore/releases)
2. Using an external site like 
[APK Mirror](https://www.apkmirror.com/apk/mcdonalds-apps/)
3. Using an external site like 
[Evozi APK Downloader](https://apps.evozi.com/apk-downloader/)

You can run the patch on Linux, macOS and Windows WSL.

## Setup
### Windows
#### Requirements
1. Enable WSL by following the guide [here](https://aka.ms/wslinstall).
2. Download Debian or Ubuntu from the store.
3. Run the installed WSL and run these command:
`sudo apt update && sudo apt install git default-jdk`

### Linux
#### Requirements
* git
* openjdk

### macOS
#### Requirements
To get the following two popups, simple run the [commands](#how-to-patch) below, then
follow the instructions provided by the popups.

* git

    [![](../.images/git_macos.png)](#)

* JDK (you can download it
[here](https://www.oracle.com/technetwork/java/javase/downloads/index.html))

	[![](../.images/jdk_macos.png)](#)

## How to patch
1. First clone this repository using
`git clone https://github.com/giacomoferretti/ffapi-project`.
2. Now cd in the `ffapi-project/mcmod` folder.
3. Now patch using the `./patch.sh` script. 

Example: (For the URL you can use this: `https://mcmod.hexile.xyz/`)

```bash
git clone https://github.com/giacomoferretti/ffapi-project
cd ffapi-project/mcmod
./patch.sh <APK_PATH> <TARGET_URL>
```

_Only if you are using WSL:_ 

To copy the modded APK to your Desktop, use this command:
`cp *.apk /mnt/c/Users/[YOUR_USER]/Desktop`

## Donate
If this repository helped you in any way, feel free to donate [here][donate].

## Disclaimer
This repository is not affiliated with McDonald's Corp in any way. 
"McDonald's" is a registered trademark of McDonald's Corp.

[donate]: https://paypal.me/hexile0