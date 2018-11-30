const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.notifyNewPost = functions.firestore
    .document('posts/{postId}')
    .onCreate((change, context) => {
      
    });