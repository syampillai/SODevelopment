package com.storedobject.core;

import java.math.BigDecimal;

public class FileFolderAccessRights extends StoredObject {

    public FileFolderAccessRights() {
    }

    public static void columns(Columns columns) {
    }

    public void setPerson(Id personId) {
    }

    public void setPerson(BigDecimal idValue) {
    }

    public void setPerson(Person person) {
    }

    public Id getPersonId() {
        return null;
    }

    public Person getPerson() {
        return null;
    }

    public void setAccessRights(int accessRights) {
    }

    public int getAccessRights() {
        return 0;
    }

    public static String[] getAccessRightsBitValues() {
        return null;
    }

    public static String getAccessRightsValue(int value) {
        return null;
    }

    public String getAccessRightsValue() {
        return null;
    }

    public boolean canAddFolder() {
        return false;
    }

    public boolean canRenameFolder() {
        return false;
    }

    public boolean canDeleteFolder() {
        return false;
    }

    public boolean canCirculateFolder() {
        return false;
    }

    public boolean canViewCirculationStatus() {
        return false;
    }

    public boolean canViewDetails() {
        return false;
    }

    public boolean canEditDetails() {
        return false;
    }

    public boolean canCreateNewVersion() {
        return false;
    }

    public boolean canDeleteCurrentVersion() {
        return false;
    }

    public boolean canCirculateDocument() {
        return false;
    }

    public boolean canSetFolderPassword() {
        return false;
    }

    public boolean canAccessSecredtFolders() {
        return false;
    }

    public boolean canAddFile() {
        return false;
    }

    public static FileFolderAccessRights get(TransactionManager tm) {
        return null;
    }
}