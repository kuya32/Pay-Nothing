"use strict";

const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp(functions.config().firebase);

// eslint-disable-next-line max-len
exports.sendNotification = functions.database.ref("/Notifications/{user_id}/{notification_id}").onWrite((change, context) => {
  console.log("The User Id is: ", context.params.user_id);

  if (!change.after.val()) {
    // eslint-disable-next-line max-len
    return console.log("A notification has been deleted from the database: ", context.params.notification_id);
  }

  // eslint-disable-next-line max-len
  const fromUser = admin.database().ref(`/Notifications/${context.params.user_id}/${context.params.notification_id}`).once("value");

  return fromUser.then((fromUserResult) => {
    // eslint-disable-next-line max-len
    const userQuery = admin.database().ref(`/Users/${fromUserResult.val().from}/username`).once("value");

    return userQuery.then((userResult) => {
      const username = userResult.val();

      // eslint-disable-next-line max-len
      const deviceToken = admin.database().ref(`/Users/${context.params.user_id}/deviceToken`).once("value");

      return deviceToken.then((result) => {
        const tokenId = result.val();

        const payload = {
          notification: {
            title: "New Message",
            body: `New message from ${username}`,
          },
        };

        // eslint-disable-next-line max-len
        return admin.messaging().sendToDevice(tokenId, payload).then((response) => {
          console.log("Notification sent!");
        });
      });
    });
  });
});
