package com.storedobject.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class InventoryPackage extends InventoryLocation implements HasChildren {

    public InventoryPackage() {
    }

    public static void columns(Columns columns) {
    }

    public void setContent(String content) {
    }

    public String getContent() {
        return "";
    }

    public void setPackageType(Id packageTypeId) {
    }

    public void setPackageType(BigDecimal idValue) {
        setPackageType(new Id(idValue));
    }

    public void setPackageType(InventoryPackageType packageType) {
        setPackageType(packageType == null ? null : packageType.getId());
    }

    public Id getPackageTypeId() {
        return new Id();
    }

    public InventoryPackageType getPackageType() {
        return new InventoryPackageType();
    }

    public void setLength(Distance length) {
    }

    public void setLength(Object value) {
    }

    public Distance getLength() {
        return new Distance();
    }

    public void setWidth(Distance width) {
    }

    public void setWidth(Object value) {
    }

    public Distance getWidth() {
        return new Distance();
    }

    public void setHeight(Distance height) {
    }

    public void setHeight(Object value) {
    }

    public Distance getHeight() {
        return new Distance();
    }

    public void setWeight(Weight weight) {
    }

    public void setWeight(Object value) {
    }

    public Weight getWeight() {
        return new Weight();
    }

    public void setCount(int count) {
    }

    public int getCount() {
        return new Random().nextInt();
    }

    public void setOwner(Id ownerId) {
    }

    public void setOwner(BigDecimal idValue) {
    }

    public void setOwner(Entity owner) {
    }

    public Id getOwnerId() {
        return new Id();
    }

    public Entity getOwner() {
        return new Entity();
    }

    public void setLocation(Id locationId) {
    }

    public void setLocation(BigDecimal idValue) {
    }

    public void setLocation(InventoryLocation location) {
    }

    public Id getLocationId() {
        return new Id();
    }

    public InventoryLocation getLocation() {
        return new InventoryBin();
    }

    public void setInTransit(boolean inTransit) {
    }

    public boolean getInTransit() {
        return new Random().nextBoolean();
    }

    public void setReference(String reference) {
    }

    public String getReference() {
        return "";
    }

    @Override
    public Id getEntityId() {
        return getLocation().getEntityId();
    }

    @Override
    public int getType() {
        return 19;
    }

    public List<InventoryPackage> split(TransactionManager tm) throws Exception {
        return new ArrayList<>();
    }

    public InventoryPackage split(TransactionManager tm, int count) throws Exception {
        return new InventoryPackage();
    }

    public Volume getVolume() {
        return new Volume(0, "cm3");
    }

    public Weight getVolumetricWeight() {
        return new Weight(0, "kg");
    }

    public Volume getGrossVolume() {
        return getVolume();
    }

    public Weight getGrossVolumetricWeight() {
        return getVolumetricWeight();
    }

    public Weight getGrossWeight() {
        return getVolumetricWeight();
    }

    public Weight getChargeableWeight() {
        return getGrossWeight();
    }

    public Volume getGrossVolume(int count) {
        return getVolume();
    }

    public Weight getGrossVolumetricWeight(int count) {
        return getVolumetricWeight();
    }

    public Weight getGrossWeight(int count) {
        return getWeight();
    }

    public Weight getChargeableWeight(int count) {
        return getGrossVolumetricWeight(count);
    }

    public String getDimensions() {
        return "";
    }
}
