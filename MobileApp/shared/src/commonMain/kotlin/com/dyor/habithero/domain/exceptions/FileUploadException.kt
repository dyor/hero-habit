package com.dyor.habithero.domain.exceptions

/**
 * Thrown when an input file (e.g. the milestone selfie) could not be uploaded, so the AI provider
 * would never see it. Generation stops before any credit is spent; the message is shown to the user.
 */
class FileUploadException : Exception("No selfie received. Do you have a good internet connection? If so, please try again.")
