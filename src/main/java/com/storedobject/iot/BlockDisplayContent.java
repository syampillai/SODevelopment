package com.storedobject.iot;

import com.storedobject.core.*;
import com.storedobject.core.annotation.*;
import java.math.BigDecimal;

public class BlockDisplayContent extends StoredObject {

    private Id blockId = Id.ZERO;
    private int x, y;
    private String content = """
            <style>
              .value-text {
                font-weight:bold;
                font-size:small;
              }
              .value-normal {
                color:#FFFFFF;
                background:#66CC66;
              }
              .value-high {
                color:#333333;
                background:#FFCC33;
              }
              .value-higher {
                color:#FFFFFF;
                background:#FF9933;
              }
              .value-highest {
                color:#FFFFFF;
                background:#FF3333;
              }
              .value-low {
                color:#333333;
                background:#FFCC33;
              }
              .value-lower {
                color:#FFFFFF;
                background:#FF9933;
              }
              .value-lowest {
                color:#FFFFFF;
                background:#FF3333;
              }
              .alarm-on {
                color:#FFFFFF;
                background:#66CC66;
              }
              .alarm-off {
                color:#333333;
                background:#FFCC33;
              }
              .label-text {
                color:#FFFFFF;
                background:#333333;
              }
              .label-normal {
                color:#FFFFFF;
                background:#66CC66;
              }
              .label-high {
                color:#333333;
                background:#FFCC33;
              }
              .label-higher {
                color:#FFFFFF;
                background:#FF9933;
              }
              .label-highest {
                color:#FFFFFF;
                background:#FF3333;
              }
              .label-low {
                color:#333333;
                background:#FFCC33;
              }
              .label-lower {
                color:#FFFFFF;
                background:#FF9933;
              }
              .label-lowest {
                color:#FFFFFF;
                background:#FF3333;
              }
            </style>
            """;

    public BlockDisplayContent() {
    }

    public static void columns(Columns columns) {
        columns.add("Block", "id");
        columns.add("Content", "text");
        columns.add("X", "int");
        columns.add("Y", "int");
    }

    public static void indices(Indices indices) {
        indices.add("Block", false);
    }

    public static int hints() {
        return ObjectHint.SMALL_LIST;
    }

    public void setBlock(Id blockId) {
        this.blockId = blockId;
    }

    public void setBlock(BigDecimal idValue) {
        setBlock(new Id(idValue));
    }

    public void setBlock(Block block) {
        setBlock(block == null ? null : block.getId());
    }

    @Column(required = false, order = 100)
    public Id getBlockId() {
        return blockId;
    }

    public Block getBlock() {
        return getRelated(Block.class, blockId);
    }

    public void setX(int x) {
        this.x = x;
    }

    @Column(required = false, order = 200)
    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Column(required = false, order = 300)
    public int getY() {
        return y;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Column(style = "(large2)", order = 400)
    public String getContent() {
        return content;
    }

    @Override
    public void validateData(TransactionManager tm) throws Exception {
        blockId = tm.checkType(this, blockId, Block.class, true);
        if (StringUtility.isWhite(content)) {
            throw new Invalid_Value("Content");
        }
        super.validateData(tm);
    }

    public static ObjectIterator<BlockDisplayContent> list(Block block) {
        return list(BlockDisplayContent.class, "Block=" + block.getId())
                .add(list(BlockDisplayContent.class, "Block=0"));
    }
}
