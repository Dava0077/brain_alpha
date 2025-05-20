package brain.alpha.utils

/**
 * Expects a platform-specific implementation to return the current time in milliseconds.
 *
 * This function allows shared/common code to get the current epoch time without using
 * platform-specific APIs directly (like System.currentTimeMillis() on JVM or NSDate on iOS).
 *
 * The actual implementation must be provided in platform-specific source sets.
 *
 * @return current timestamp in milliseconds since epoch (UTC).
 */
expect fun currentTimeMillis(): Long
