const functions = require("firebase-functions");
const admin = require("firebase-admin");
const dotenv = require("dotenv");
dotenv.config();
const stripe = require("stripe")(process.env.STRIPE_SECRET_KEY);


admin.initializeApp();

exports.createPaymentIntent = functions.https.onRequest(async (req, res) => {
  try {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith("Bearer ")) {
      return res.status(401).send({error: "Unauthorized: Missing token"});
    }

    const idToken = authHeader.split("Bearer ")[1];
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    const uid = decodedToken.uid;
    const email = decodedToken.email;
    const name = decodedToken.name || "Anonymous";

    const {amount, currency} = req.body;
    if (!amount || !currency) {
      return res.status(400).send({error: "Missing amount or currency"});
    }

    const userRef = admin.firestore().collection("users").doc(uid);
    const userDoc = await userRef.get();

    let customerId;

    if (userDoc.exists && userDoc.data().stripeCustomerId) {
      customerId = userDoc.data().stripeCustomerId;
    } else {
      const customer = await stripe.customers.create({
        email: email,
        name: name,
        metadata: {firebaseUID: uid},
      });

      customerId = customer.id;
      await userRef.set({stripeCustomerId: customerId}, {merge: true});
    }

    const ephemeralKey = await stripe.ephemeralKeys.create(
        {customer: customerId},
        {apiVersion: "2022-11-15"},
    );

    const paymentIntent = await stripe.paymentIntents.create({
      amount,
      currency,
      customer: customerId,
      payment_method_types: ["card"],
    });

    res.status(200).send({
      paymentIntentClientSecret: paymentIntent.client_secret,
      customer: customerId,
      ephemeralKey: ephemeralKey.secret,
    });
  } catch (err) {
    console.error("Error creating payment intent:", err);
    return res.status(500).send({error: err.message});
  }
});
