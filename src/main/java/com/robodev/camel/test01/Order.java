package com.robodev.camel.test01;

import org.apache.camel.component.jpa.Consumed;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@NamedQuery(name = "new-orders", query = "select order from Order order where order.processed = false")
public class Order {

    @Id
    @GeneratedValue
    private int id;

    private int amount;

    @ManyToOne
    private Book book;

    private boolean processed;

    @Consumed
    public void setProcessed() {
        processed = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
