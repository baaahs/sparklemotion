# Proposed Mac Mini Launcher

> [!NOTE]
> Assumptions:
> * Fresh MacOS Install
> * ARM based Mac
> * Data Encryption is off
> * System will restart when power is lost
> * MacOS is autologged in
> * sparklemotion daemon is auto launched

> [!CAUTION]
> DO NOT ENABLE FILEVAULT
>
> If you don't disable this AutoLogin will fail.

## Setup Fresh OS as usual

### Enable AutoLogin
1. System Preferences
2. Users & Groups
3. Select User to Autologin
4. Provide Admin Password
5. Provide Password for Autologin User

### Disable Screen Saver
1. System Preferences
2. Wallpaper
3. Screen Saver
4. Start Screen Saver -> Never

### Enable SSH Access
1. System Preferences
2. General
3. Sharing
4. Remote Login
 
#### Enable VNC Access (optional)
1. System Preferences
2. General
3. Sharing
4. Remote Management
5. VNC Viewers may control screen with password
6. Enter a shared password
7. Done (to save)

#### Ghostty (optional)
[Download](https://ghostty.org/download)
Since LaunchAgent will be spawning a terminal session, if you want to use the MacOS GUI you'll need another Terminal application that you can kill without losing your session or do everything over SSH and VNC

## Software Dependencies

### Download Intellij
[Download Page](https://www.jetbrains.com/idea/download/?section=mac)

### Install an x86 JDK per [link](https://github.com/baaahs/sparklemotion#prerequisites)
```code
# downloading x86 based java
cd ${HOME}
curl -o ~/java.tar.gz https://download.java.net/java/GA/jdk20.0.2/6e380f22cbe7469fa75fb448bd903d8e/9/GPL/openjdk-20.0.2_macos-x64_bin.tar.gz
tar xvfz ~/java.tar.gz

# setting Java environment
echo 'export JAVA_HOME=${HOME}/jdk-20.0.2.jdk/Contents/Home' >> ~/.zshrc
source ~/.zshrc
```

### Installing git dependency & Rosetta for ARM/x86 Translation
```code
# Installing MacOS Dev Tools (git)
xcode-select --install
# Installing x86 to arm translation layer
softwareupdate --install-rosetta --agree-to-license
```

### Clone git repo
```code 
# cloning sparklemotion codebase
cd ${HOME}
git clone https://github.com/baaahs/sparklemotion.git
```

### Enable wake on LAN and AutoRestart on Power Cut
```code
# wake on lan
sudo pmset -a womp 1

# autorestart on power cut
sudo pmset -a autorestart 1
```

### Create directory to put logs of sparklemotion daemon
```code 
# Add log directories
mkdir ${HOME}/logs
```

### Install start.sh and sparkle.sh bash script
```code 
# Translate template files
sed s/{XYZ}/$USER/ ${HOME}/sparklemotion/deployment/start.sh-template > ${HOME}/start.sh
chmod 755 ${HOME}/start.sh
sed s/{XYZ}/$USER/ ${HOME}/sparklemotion/deployment/sparkle.sh-template > ${HOME}/sparkle.sh
chmod 755 ${HOME}/sparkle.sh

mkdir ${HOME}/Library/LaunchAgents
sed s/{XYZ}/$USER/ ${HOME}/sparklemotion/deployment/org.baaahs.sparklemotion.plist-template > ${HOME}/Library/LaunchAgents/org.baaahs.sparklemotion.plist
launchctl load -w ${HOME}/Library/LaunchAgents/org.baaahs.sparklemotion.plist
```
### Useful ZSH Aliases
```code
# Logs
alias std-err='tail -10 ${HOME}/logs/stderr.log'
alias std-out='tail -10 ${HOME}/logs/stdout.log'
alias std-err-f='tail -f ${HOME}/logs/stderr.log'
alias std-out-f='tail -f ${HOME}/logs/stdout.log'

# Kill Associated Process and Launchd will restart sparklemotion
function killsparkle () {
killall java
killall Terminal
}

alias killsparkle=killsparkle
```