const {initializeApp} = require("firebase-admin/app");

// Initialize Firebase app
initializeApp();

// Enable only the AI providers you have keys for.
const enabledProviders = (process.env.AI_PROVIDERS || "replicate")
  .split(",")
  .map((provider) => provider.trim().toLowerCase())
  .filter(Boolean);

// Replicate API Function exports
if (enabledProviders.includes("replicate")) {
  const replicateFunctions = require("./api/replicate");
  exports.replicateCreatePrediction = replicateFunctions.createPrediction;
  exports.replicateCreateModelPrediction = replicateFunctions.createModelPrediction;
  exports.replicateGetPredictionStatus = replicateFunctions.getPredictionStatus;
  exports.replicateCancelPrediction = replicateFunctions.cancelPrediction;
}

// OpenAI API Function exports
if (enabledProviders.includes("openai")) {
  const openAiFunctions = require("./api/openai");
  exports.openAiCreateTextCompletion = openAiFunctions.createTextCompletion;
  exports.openAiCreateImage = openAiFunctions.createImage;
}
