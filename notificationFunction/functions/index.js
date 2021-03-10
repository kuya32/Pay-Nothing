"use strict";

const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

// eslint-disable-next-line max-len
exports.sendNotification = functions.database.ref("/Notifications/{user_id}/{notification_id}").onWrite((change, context) => {
  console.log("The User Id is: ", context.params.user_id);

  if (!context.data.val()) {
    // eslint-disable-next-line max-len
    return console.log("A notification has been deleted from the database: ", context.params.notification_id);
  }

  const userId = context.params.user_id;

  // eslint-disable-next-line max-len
  const deviceToken = admin.database().ref(`/Users/${userId}/deviceToken`).once("value");

  return deviceToken.then((result) => {
    const tokenId = result.val();

    const payload = {
      notification: {
        title: "Message",
        body: "You've recieved a new message!",
        icon: "default",
      },
    };

    // eslint-disable-next-line max-len
    return admin.messaging().sendToDevice(tokenId, payload).then((response) => {
      console.log("Hello World");
    });
  });
});
