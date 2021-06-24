package com.storedobject.job;

/**
 * Daemon job is a {@link Job} that requires only a single instance to be created. The
 * {@link Job#execute()} method will be invoked only once on the {@link Job} instance and it may run for ever in
 * the background.
 *
 * @author Syam
 */
public interface DaemonJob {
}
