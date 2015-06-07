# Pollr Readme

## Points of Contact

**Primary Contact**  
Kyle Buzsaki  
(510) 378-2696  
kbuzsaki@ucsd.edu  

**Secondary Contact**  
Kyle Huynh  
(510) 566-9019  
kdhuynh@ucsd.edu  

## Installation Instructions

#### Prerequisites for Installation:

1. You must have an android device running *Android 4.4 KitKat or later*, preferably Lollipop.
2. You must have a stable internet connection, preferably wifi.

Note: To make full use of the application, it's a good idea to have multiple devices
at a time so that you can create and join polls separately.

#### APK Download Link

You can download a pre-built APK
[here](https://github.com/kbuzsaki/cse110-android/releases/download/pollr-0.1/pollr.apk).  
If you wish to instead, you can clone our source repository and build the APK yourself using Android Studio.

#### Steps for Installation

1. Download the APK from the link above.
2. Connect your android device to your computer.
3. Load the APK onto your device.
4. Navigate to and install the APK using a tool like [ES File Explorer](https://play.google.com/store/apps/details?id=com.estrongs.android.pop&hl=en).
5. Launch the application, 'Pollr'.

## Information for Testing

Our application does not use "login" for managing users. Instead, an "account" 
is created for each user the first time they launch the application.  
This "account" is usually hidden from the user and cannot be changed or deleted.

For the purposes of testing, we have provided a way to switch between user "accounts"
and generate fresh new user accounts from the settings screen.

#### Switching to a Different User

1. Navigate to the home screen by pressing the back button until you see the
   application name, Pollr, in the upper left.
2. Open the "Settings" panel by clicking the menu button in the upper right and then
   selecting the Settings option in the dropdown menu.
3. Press the next to last option on the preferences panel, "User Id".
4. Enter the User Id that you would like to switch to in the dialog box.
5. Press the "OK" button.
6. Wait until you see the success notification, which should say 
   "Successfully switched to user id: $user\_id".
7. Hit the back button to return to the main screen.

#### Generating a Fresh Account

1. Navigate to the home screen by pressing the back button until you see the
   application name, Pollr, in the upper left.
2. Open the "Settings" panel by clicking the menu button in the upper right and then
   selecting the Settings option in the dropdown menu.
3. Press the last option on the preferences panel, "Generate New User".
4. Wait until you see the success notification, which should say "Hi, $user\_name"
5. Hit the back button to return to the main screen.

## Source Code Repositories

#### Android Application

The repository for the Android application can be found 
[here](https://github.com/kbuzsaki/cse110-android).
The most up to date code is located on the `master` branch.

The primary source code for our application is in this subdirectory:  
`/studentpoll/src/main/`

Our package structure from the `main` directory is as follows:

- `java`
  - `edu`
    - `ucsd`
      - `studentpoll` - *classes directly in this package form our controllers*
        - `misc`
        - `models` - *contains our front end model classes*
        - `rest` - *contains dispatch classes that communicate with the web backend*
        - `view` - *contains java components for custom view classes*
- `res`
  - `layouts` - *contains the bulk of our views*
  - `menu` - *contains additional components for our views*
  - `...`

#### iOS Application - Deferred 

The repository for the iOS application can be found
[here](https://github.com/kbuzsaki/cse110-ios).
The most up to date code is located on the `develop` branch.

The iOS application is not complete and has been deferred for this release.
As such, there are no testing instructions for the iOS application.

#### Web Backend

The repository for the web application can be found
[here](https://github.com/kbuzsaki/cse110-web).
The most up to date code is located on the `fullcircle` branch.

The web backend for our application is not user facing. Instead, the 
Android and iOS applications communicate with the web backend to synchronize
data across clients.  
As such, there are no testing instructions for the web backend.

