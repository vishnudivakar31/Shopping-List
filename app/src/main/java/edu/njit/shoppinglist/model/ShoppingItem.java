package edu.njit.shoppinglist.model;

public class ShoppingItem {
    private long id;
    private String name;
    private Integer qty;
    private String color;
    private Integer size;

    public ShoppingItem() {
    }

    public ShoppingItem(long id, String name, Integer qty, String color, Integer size) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.color = color;
        this.size = size;
    }

    public ShoppingItem(String name, Integer qty, String color, Integer size) {
        this.name = name;
        this.qty = qty;
        this.color = color;
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ShoppingItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", qty=" + qty +
                ", color='" + color + '\'' +
                ", size=" + size +
                '}';
    }
}
