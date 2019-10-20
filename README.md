# Disaster Alerter

Due to the lack of awareness/information among the general public regarding warnings and alerts of possible future natural disaster near the location of the people, they don't get much time to prepare themselves when the disaster strikes and thus in this case there is more economic as well as human losses, with the help of this app, users can be alerted in advance about the possibilities of a natural disaster near their location and thus get more time to prepare themselves accordingly. 

### Prerequisites

Nothing much, as almost most of the people today use it. Thus a wide range of public can be alerted of the future disaster before hand.

```
1) An Android Device
2) Working Internet Connectivity on the device
3) GPS Acess on the device
```

## How the App works and the Implementation of the App

The location data of the android device which has the app installed can be accessed by taking the location permission from the user, so the app will have access to the exact latitude and longitude coordinates of the user, and with the help of the real time data from a realiable source (Government or Authorized), like for example **The Global Disaster Alert and Coordination System**, the exact latitude and longitude coordinates of the places where there is a possibility of future disaster can be known, now the Android App downloads the data in the background from this reliable authenticated source every 15 minutes to get the updated and close to real time information.
Then by creating a Geofence or a imaginary boundary taking center as the coordinates of the user and with any specified radius of the boundary, if the location coordinates of the place with the possibiltiy of any future natural disaster falls within the radius of our user specific Geofence then the user of the app will be alerted immediately with a Notification, which will be having the nature of the disaster along with other details.

This app can also be used to send emergency information and the important steps to be taken by the users who are at the risk of facing any natural disaster. 

## About the Author

* **Rohit K Bharadwaj**, Second Year Student At Birla Institue of Technology and Science Pilani, Pilani Campus
* [Mail](mailto:f20170633@pilani.bits-pilani.ac.in)

## Acknowledgments

* I thank Microsoft for giving me this oppourtunity to do this project
