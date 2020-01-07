package com.storedobject.core;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.stream.Stream;

/**
 * A structure that encapsulates all the objects (including links) involved in a transaction.
 * {@link TransactionInformation#get(BigInteger)} method is used to create a {@link TransactionInformation} structure
 * populated with all involved objects.
 */
public final class TransactionInformation {

    private TransactionInformation(BigInteger tid, Id userId, Timestamp timestamp) {
    }

    /**
     * Get the {@link TransactionLink} for a particular transaction.
     *
     * @param transactionId Transaction Id
     * @return The Transaction Link if available, otherwise <code>null</code> is returned.
     */
    public static TransactionInformation get(BigInteger transactionId) {
        return null;
    }

    /**
     * Get the transaction Id.
     *
     * @return Transaction Id.
     */
    public BigInteger getTransactionId() {
        return null;
    }

    /**
     * Get the timestamp of this transaction.
     *
     * @return Transaction timestamp.
     */
    public Timestamp getTimestamp() {
        return null;
    }

    /**
     * Get the user who created this transaction.
     *
     * @return User. (This will return <code>null</code> if the transaction was aborted by the system).
     */
    public SystemUser getUser() {
        return null;
    }


    /**
     * Get the user's Id who created this transaction.
     *
     * @return Id of the user.
     */
    public Id getUserId() {
        return null;
    }

    /**
     * Check whether this is an abandoned transaction or not.
     *
     * @return True or false.
     */
    public boolean isAbandoned() {
        return false;
    }

    /**
     * Check whether this transaction is in progress or not.
     *
     * @return True or false.
     */
    public boolean inProgress() {
        return true;
    }

    /**
     * Get the objects involved in this transaction.
     *
     * @return Stream of {@link TransactionObject}s.
     */
    public Stream<TransactionObject<?>> objects() {
        return null;
    }

    /**
     * Get a particular type of objects involved in this transaction.
     *
     * @return Stream of {@link TransactionObject}s.
     */
    public <O extends StoredObject> Stream<TransactionObject<O>> objects(Class<O> objectClass, boolean any) {
        return null;
    }

    /**
     * Get the involved object for the given Id.
     *
     * @param id Id of the object
     * @return Transaction object for the Id passed.
     */
    public TransactionObject<?> getObject(Id id) {
        return null;
    }


    /**
     * Get the involved object for the given object.
     *
     * @param object Object for which involved object to be retrieved
     * @return Transaction object for the object passed.
     */
    public <O extends StoredObject> TransactionObject<O> getObject(O object) {
        return null;
    }

    /**
     * Dump the object and link details to a "String Builder".
     *
     * @param s "String Builder" to which output should be written.
     */
    public void dump(StringBuilder s) {
    }

    /**
     * Class that encapsulates an object involved in the transaction.
     *
     * @param <T> Class type
     */
    public final class TransactionObject<T extends StoredObject> {

        private TransactionObject(Id id, ClassAttribute<T> ca) {
        }

        /**
         * Get the object involved. The object will contain historical information at the time of this transaction
         * happened.
         *
         * @return The object that is involved. (A <code>null</code> may be returned if any error occurs in retrieving
         * the object and in that case, error log may be reviewed).
         */
        public T getObject() {
            return null;
        }

        /**
         * Get the class of the object involved.
         *
         * @return Object's class.
         */
        public Class<T> getObjectClass() {
            return null;
        }

        /**
         * Get the Id of the object involved.
         *
         * @return Id of the object.
         */
        public Id getObjectId() {
            return null;
        }

        /**
         * Get the 'action' of the object in this transaction. It return a character that represents the action
         * ('+': newly created, '*': modified, '-': deleted, '^': referenced, 'X': error).
         *
         * @return Action.
         */
        public char getAction() {
            return 'x';
        }

        /**
         * Get the links of this object involved in this transaction.
         *
         * @return Links of this object involved in this transaction.
         */
        public Stream<TransactionLink<?>> links() {
            return null;
        }

        /**
         * Get the links of this object involved in this transaction.
         *
         * @param linkClass Class of the link
         * @param linkType Type of the link
         * @param <L> Class type of the link
         * @return Links of this object involved in this transaction.
         */
        public <L extends StoredObject> Stream<TransactionLink<L>> links(Class<L> linkClass, int linkType) {
            return null;
        }

        /**
         * Get the links of this object involved in this transaction.
         *
         * @param linkClass Class of the link
         * @param linkType Type of the link
         * @param <L> Class type of the link
         * @param any Whether to retrieve subclasses or not
         * @return Links of this object involved in this transaction.
         */
        public <L extends StoredObject> Stream<TransactionLink<L>> links(Class<L> linkClass, int linkType, boolean any) {
            return null;
        }

        /**
         * Dump the object and link details to a "String Builder".
         *
         * @param s "String Builder" to which output should be written.
         */
        public void dump(StringBuilder s) {
        }
    }

    /**
     * Class that encapsulates a link involved in a transaction.
     *
     * @param <L> Class type of the link
     */
    public final class TransactionLink<L extends StoredObject> {

        private TransactionLink(TransactionObject<?> master, Id id, ClassAttribute<L> ca, int type, boolean removed) {
        }

        /**
         * Get the link object involved.
         *
         * @return Link object. (A <code>null</code> may be returned if any error occurs in retrieving
         * the object and in that case, error log may be reviewed).
         */
        public L getLink() {
            return null;
        }

        /**
         * Get the Id of the link object.
         *
         * @return Id of the link object.
         */
        public Id getLinkId() {
            return null;
        }

        /**
         * Class of the link object.
         *
         * @return Link object's class.
         */
        public Class<L> getLinkClass() {
            return null;
        }

        /**
         * Check whether this is a 'detail link' or not.
         *
         * @return True or false.
         */
        public boolean isDetailLink() {
            return false;
        }


        /**
         * Check whether this is a 'reference link' or not.
         *
         * @return True or false.
         */
        public boolean isReferenceLink() {
            return false;
        }

        /**
         * If this link was removed in the transaction or not.
         *
         * @return True or false.
         */
        public boolean isRemoved() {
            return false;
        }

        /**
         * If this link was newly added in the transaction or not.
         *
         * @return True or false.
         */
        public boolean isAdded() {
            return false;
        }

        /**
         * If this link was just referenced (not added or removed) in the transaction or not.
         *
         * @return True or false.
         */
        public boolean isReferenced() {
            return false;
        }

        /**
         * Dump the link details to a "String Builder".
         *
         * @param s "String Builder" to which output should be written.
         */
        public void dump(StringBuilder s) {
        }
    }
}
