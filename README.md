# Simian: a systematic black-box analysis of collaborative web applications

This repository contains **Simian**, our approach for discovering bugs in collaborative web applications. Simian is packaged as a Java application that uses [Selenium WebDriver](https://github.com/SeleniumHQ/selenium) to exercise browsers.

## Publication

Marina Billes, Anders Møller, and Michael Pradel. Systematic Black-Box Analysis of Collaborative Web Applications. PLDI '17.

## Installation

### 1. Setting up the system environment

#### Ubuntu 16.04

We recommend at least **Ubuntu 16.04** to run Simian. Install build dependencies:

    sudo apt install openjdk-8-jdk openjfx git maven

#### Earlier versions

Ubuntu 14.04 and earlier versions do not offer precompiled packages of `openjfx`, which provides the JavaFX dependency required by Simian. If your Ubuntu distribution does not provide the openjfx package, you can instead [use Oracle's JDK](http://www.webupd8.org/2012/09/install-oracle-java-8-in-ubuntu-via-ppa.html): (this will set Oracle's JDK as the default instead of OpenJDK for all Java software on your computer.)

    sudo add-apt-repository ppa:webupd8team/java
    sudo apt-get update
    sudo apt-get install oracle-java8-installer git maven
    
### 2. Checking out of the code

    git clone

### 3. Installing Firefox

Simian uses *Selenium 2*. Firefox 45 is the last version of Firefox to support this protocol. Please install Firefox ESR 45:

    cd /opt
    sudo wget https://ftp.mozilla.org/pub/firefox/releases/45.8.0esr/firefox-45.8.0esr.linux-x86_64.sdk.tar.bz2
    sudo tar -xjvf firefox-45.8.0esr.linux-x86_64.sdk.tar.bz2

**Warning: Firefox 45 is end-of-life by 2017-04-18.** Please consider the implications of running an end-of-life browser with potentially unpatched security vulnerabilities on your desktop operating system.

Porting Simian to Selenium 3 to make it work on more recent Firefox versions is not currently possible, as the current version of geckodriver does not yet support the `Actions` interface used extensively by Simian.

### 4. Setting up Selenium Grid

#### Downloading Selenium Grid

[Selenium Grid](https://github.com/SeleniumHQ/selenium/wiki/Grid2) is a client-server architecture which allows a host application connect to a grid server running on `localhost:4444`, which forwards commands to remote browsers that are connected to the server.

The latest version of the Selenium Grid server binary that is compatible with Selenium 2 is 2.53.1. Selenium Grid server version >= 3.0 is not compatible.

    cd /opt
    sudo wget https://selenium-release.storage.googleapis.com/2.53/selenium-server-standalone-2.53.1.jar
    
#### Running several X server sessions on the same machine
    
In order to test Simian, you need to run the Selenium Grid server on the same machine as you are executing the Simian, and connect two Grid nodes to the hub. Preferably, you should run the nodes on the same machine as the hub, but in different X server sessions. If you use Ubuntu Unity, you can easily switch to a second user account by clicking the power button symbol in the top left. 

Alternatively, you can use [x2go](http://www.x2go.org) to run additional X server sessions on your machine. This way, you can view what is happening at both web browsers Simian controls at once, without having to switch users. Install the [x2go client](http://wiki.x2go.org/doku.php/doc:installation:x2goclient) and [x2go server](http://wiki.x2go.org/doku.php/doc:installation:x2goserver) on your machine, as well as a simple, low-overhead window manager such as `openbox`:

    sudo apt-get install openbox
    
Create two new user accounts on your machine, and start two instances of x2goclient, then connect to localhost, selecting Openbox as the window manager. Right-click on the empty Openbox desktop to open a terminal.

#### Starting Selenium Grid

We have provided the `selenium/startserver.sh` and `selenium/startnode.sh` scripts in the code repository. In these scripts, make sure that the paths to `selenium-server-standalone-#version#.jar` and the Firefox ESR executable are correct on your system.


In the hub session, run:

    ./selenium/startserver.sh
    
For the client nodes, run
    
    ./selenium/startnode.sh 5566
    
and
    
    ./selenium/startnode.sh 5567
    
Pick a different port (e.g. 5566 and 5567) for each client node.


### 5. Building and running Simian

The `startGUI.sh` script automatically runs `mvn compile` and then starts Simian's GUI application.


## Configuring Simian

Simian is implemented for Firepad, Google Docs and ownCloud Documents. You need to pre-configure the applications in the file `src/main/resources/paths.properties`. After changing this file, make sure to restart Simian's GUI.

### Google Docs

First, create a new Google Docs document and open it for link sharing. Make sure to allow editing for users with the link. Add the link to the property "gDocsUrl":

    gDocsUrl=https://docs.google.com/document/d/...

Make sure that your Google Docs document opens in English by adding the `&hl=en` parameter.

### Firepad

You need a Firebase account to use Firepad for the database backend. Go to https://firebase.google.com/, sign up with your Google account and create a new project. Go to Authentication -> Sign-in Method and enable anonymous sign-up. Go to Database -> Rules and paste the following ruleset:

    {
      "rules": {
        ".read": true,
        ".write": true
      }
    }


Edit `firepad/firepad.json` with your Firebase project's API key and project ID. Your Firebase project is automatically deleted after some months of inactivity.

Start a web server in the `firepad` folder, for example with python:

    python3 -m http.server 8912
    
Configure the URL to `firepad.html` in `paths.properties` with the `firepadUrl` property.

### ownCloud

Install a local ownCloud server. Simian is tested for ownCloud 9. Follow the instructions at https://owncloud.org/install/ to set up an ownCloud server. On Ubuntu, you can use the package repository. The [package repository version](https://download.owncloud.org/download/repositories/stable/owncloud/) provides automatic security updates via apt, which will set your ownCloud installation into maintenance mode each time there is an update.

Install mysql and set up a database called "owncloud":

    sudo apt-get install mariadb-client mariadb-server
    sudo mysql -p
    <Press Enter>
    MariaDB> create database owncloud;
    MariaDB> grant all on owncloud.* to 'multitester'@'localhost' identified by 'password';
    
Put the given username and password (e.g. multitester, 'password') into `paths.properties` as `mysqlUser` and `mysqlPass`. Then run the ownCloud installation at http://localhost/owncloud. Select MySQL as the database instead of the default SQLite.

Go to the ownCloud admin control panel and create a second user, then put the login information for both users (`owncloudUser1 / 2` and `owncloudPass1 / 2`) into `paths.properties`. Now, share the ownCloud example document `/Documents/Example.odt` with the second user. Activate the Documents app under Apps -> Productivity.

With both users, navigate to the Documents app to see the list of available documents. Hovering over a document, you can see its individual link as something like `http://localhost/owncloud/index.php/apps/files/download/Documents/Example.odt`. In `paths.properties`, make sure `owncloudFilePath1` and `owncloudFilePath2` are equal to the part that comes after "download" for each user, i.e. "/Documents/Example.odt" for the first user in this example and "/Example.odt" for the second user.


## Implementation

The main Simian approach is implemented in [GuidedStateSpaceExplorer](src/main/java/de/crispda/sola/multitester/GuidedStateSpaceExplorer.java). The GUI allows the selection of three exploration strategies:

1.  Simian -- the two-phase Simian approach.
2.  Simian with cached sequential phase -- this mode skips the sequential phase for the experiments stored in the folder `cached`.
3.  exhaustive -- performs complete parallel exploration.

Each execution provides the following result data on the file system in the `results` folder on the desktop:

* The `Execution ###.xml` file contains the string log of actions performed during the execution, equal to the "Execution Log" view in the GUI.
* The `Execution ###` sub-folder of `results`. If this folder does not exist, no inconsistencies were detected.
    * `diffmap.zip` and `diffmap.zip.scr`, intermediate files of the algorithm that you can ignore.
    * For each inconsistency, a pair of screenshots of the form `###_0_f.png` (for **f**irst) and `###_0_s.png` (for **s**econd) for the views of the first and second client when the inconsistency was detected.
    * The file `failures.xml`, if inconsistencies were detected. Each `<failure>` contains a `<failureId>` equivalent to the first number of the screenshot pair file name, as well as the `<sequence>` that triggered the inconsistency.
    * The file `errors.xml` in case there was an exception thrown during an execution.
    

In order to show statistics as in Table 2 of the paper, run the `stats.sh` command from the results folder:
```
$ ../stats.sh Execution 1
GDocs AS5
Depth: 3
Sequential count:                              125
Entire sequential exploration took:            PT51M46.3S
Individual sequential exploration averaged at: PT24.8504S
Individual sequential without last:            PT24.271645161S
Inference step:                                PT3.113S
Parallel count:                                34
Entire parallel step took:                     PT33M35.565S
Individual parallel exploration averaged at:   PT59.281323529S
Number of inconsistencies: 1
```
The output lists the number of sequential executions for Phase 1 (sequential count) and the number of parallel executions for Phase 2 (parallel count). Time spans are given using the [ISO-8601 standard](https://en.wikipedia.org/wiki/ISO_8601#Durations). 

In order to evaluate the results of Table 1 of the paper, you can run experiments of your choice two times and then compare the number of overlap between the sequences presented in `failures.xml`. You can use the following bash script in the `results` folder to calculate the number of overlapping sequences in two given executions:

```
$ ../results-overlap.sh Execution\ 1 Execution\ 2
Overlap: 1
```

## Extending Simian

See [Extending Simian](EXTENDING.md).

## License

Licensed under the MIT license.
