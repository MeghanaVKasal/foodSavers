const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Initialize the Firebase Admin SDK
admin.initializeApp(functions.config().firebase);

const ONE_KM_DEG = 0.01270486596

//Send a notification to user near newly posted food
exports.notifyNewPost = functions.firestore
  .document('posts/{postId}')
  .onCreate((snap, context) => {
    const { location } = snap.data()

    console.log("new post location:")
    console.log(location)

    return admin.firestore()
      .collection('users').get()
      .then(snap => snap.docs.filter(userSnap => {
        const user = userSnap.data()
        return Math.abs(location._latitude - user.location._latitude) < ONE_KM_DEG 
              || Math.abs(location._longitude - user.location._longitude) < ONE_KM_DEG
      }).map(u => {
        const token = u.get('fcmToken')
        console.log(`FCMToken= ${token}`)
        return token
      }))
      .then(nearByTokens => {
        const payload = {
          notification: {
            title: 'New Food Posted Nearby',
            body: 'Help save some food'
          }
        };
        // Send notifications to all tokens.
        console.log("sending to tokens:", nearByTokens);
        return admin.messaging().sendToDevice(nearByTokens, payload)
      })
      .catch(err => {
        console.log('Error getting documents', err);
      });
  });