# **Things** - Udacity Android Nanodegree Capstone Project
-----------------------------------------------------
This was my final project for Udacity's Android Nanodegree. It's a relatively simple note taking app built to incorporate the numerous techniques, and Android APIs learned. 

## App features
* Persistent Notification can be enabled as a reminder
* Home screen widget for quick access to lists
* Ability to change the background color of notes
* FirebaseAuth-UI integration used for Google and Email sign in
* Firebase Crash Reporting
* Content Provider used to access local SQL Database

## Screenshots

## Libraries Used
* Firebase Crash reporting - com.google.firebase:firebase-crash:9.0.2
* FirebaseAuth UI - com.firebaseui:firebase-ui-auth:0.4.0
* Color picker dialog - com.thebluealliance:spectrum:0.5.0
* Image loading - com.github.bumptech.glide:glide:3.7.0
* Swipe functionality on item view - com.daimajia.swipelayout:library:1.2.0@aar

## Issues
* When you delete a note from the main list, and then create a new note with the _same_ title, the long-press buttons are shown when it's created. 
* Notifcation Icon in the tool bar doesn't change when clicked. 

## License

  Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
